/**
 * Location-based Healthcare Services Client Script
 * Handles browser GPS auto-detection, city selection, and proximity reloading
 */

const CITY_PRESETS = {
    'Bhubaneswar': { lat: 20.2961, lon: 85.8245 },
    'New Delhi': { lat: 28.6139, lon: 77.2090 },
    'Delhi': { lat: 28.6139, lon: 77.2090 },
    'Mumbai': { lat: 19.0760, lon: 72.8777 },
    'Bengaluru': { lat: 12.9716, lon: 77.5946 },
    'Bangalore': { lat: 12.9716, lon: 77.5946 },
    'Kolkata': { lat: 22.5726, lon: 88.3639 },
    'Budge Budge': { lat: 22.4820, lon: 88.1812 },
    'South 24 Parganas': { lat: 22.4820, lon: 88.1812 },
    'Salt Lake': { lat: 22.5867, lon: 88.4178 },
    'New Town': { lat: 22.5958, lon: 88.4795 },
    'Howrah': { lat: 22.5958, lon: 88.2636 },
    'Siliguri': { lat: 26.7271, lon: 88.3953 },
    'Durgapur': { lat: 23.5204, lon: 87.3119 },
    'Asansol': { lat: 23.6889, lon: 86.9661 },
    'Kharagpur': { lat: 22.3460, lon: 87.2320 },
    'Chennai': { lat: 13.0827, lon: 80.2707 },
    'Hyderabad': { lat: 17.3850, lon: 78.4867 },
    'Pune': { lat: 18.5204, lon: 73.8567 }
};

function findClosestPresetCity(lat, lon) {
    if (!lat || !lon) return 'Kolkata';
    let bestCity = 'Kolkata';
    let minDistance = Infinity;
    const ALIASES = ['Bangalore', 'New Delhi', 'South 24 Parganas'];
    for (const [cityName, coords] of Object.entries(CITY_PRESETS)) {
        if (ALIASES.includes(cityName)) continue;
        const dLat = coords.lat - lat;
        const dLon = coords.lon - lon;
        const distSq = dLat * dLat + dLon * dLon;
        if (distSq < minDistance) {
            minDistance = distSq;
            bestCity = cityName;
        }
    }
    return bestCity;
}

function mapToKnownCity(cityName, lat, lon) {
    if (cityName) {
        const c = cityName.toLowerCase().trim();
        if (c.includes('delhi') || c.includes('noida') || c.includes('gurgaon') || c.includes('gurugram') || c.includes('ghaziabad') || c.includes('faridabad')) return 'Delhi';
        if (c.includes('mumbai') || c.includes('thane') || c.includes('navi mumbai')) return 'Mumbai';
        if (c.includes('bengaluru') || c.includes('bangalore')) return 'Bengaluru';
        if (c.includes('bhubaneswar') || c.includes('cuttack') || c.includes('khordha')) return 'Bhubaneswar';
        if (c.includes('chennai') || c.includes('madras')) return 'Chennai';
        if (c.includes('hyderabad') || c.includes('secunderabad')) return 'Hyderabad';
        if (c.includes('pune') || c.includes('pimpri') || c.includes('chinchwad')) return 'Pune';
        if (c.includes('howrah')) return 'Howrah';
        if (c.includes('salt lake') || c.includes('bidhannagar')) return 'Salt Lake';
        if (c.includes('new town') || c.includes('rajarhat')) return 'New Town';
        if (c.includes('siliguri') || c.includes('darjeeling')) return 'Siliguri';
        if (c.includes('durgapur') || c.includes('asansol') || c.includes('bardhaman') || c.includes('burdwan')) return 'Durgapur';
        if (c.includes('budge budge') || c.includes('pujali') || c.includes('nangi') || c.includes('maheshtala')) return 'Budge Budge';
        if (c.includes('kolkata') || c.includes('calcutta')) return 'Kolkata';
    }
    if (lat && lon) {
        return findClosestPresetCity(lat, lon);
    }
    return 'Kolkata';
}

async function detectUserLocation(isSilent = false, isExplicit = false) {
    const locStatus = document.getElementById('location-status-badge');
    const localPill = document.getElementById('header-locality-badge');
    if (locStatus) {
        locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span><span>🎯 Locating...</span>`;
        locStatus.className = "text-xs bg-amber-950 text-amber-300 border border-amber-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 animate-pulse whitespace-nowrap cursor-pointer";
    }

    if (!isSilent && typeof showToast === 'function') {
        showToast('🎯 Detecting your exact location...', 'info');
    }

    let gpsResolved = false;

    // Fast client-side IP lookup running in parallel with 1.5s timeout
    const fastIpPromise = (async () => {
        // 1. Try geojs
        try {
            const c = new AbortController();
            const tid = setTimeout(() => c.abort(), 1500);
            const resp = await fetch('https://get.geojs.io/v1/ip/geo.json', { signal: c.signal });
            clearTimeout(tid);
            if (resp.ok) {
                const data = await resp.json();
                if (data && (data.city || data.region)) {
                    const lat = parseFloat(data.latitude);
                    const lon = parseFloat(data.longitude);
                    const city = mapToKnownCity(data.city || data.region, lat, lon);
                    return {
                        city: city,
                        locality: `${data.city || city}, ${data.region || 'India'}`,
                        lat: lat,
                        lon: lon
                    };
                }
            }
        } catch (e) {}

        // 2. Try ipwho.is
        try {
            const c = new AbortController();
            const tid = setTimeout(() => c.abort(), 1500);
            const resp = await fetch('https://ipwho.is/', { signal: c.signal });
            clearTimeout(tid);
            if (resp.ok) {
                const data = await resp.json();
                if (data && data.success !== false && data.city) {
                    const city = mapToKnownCity(data.city, data.latitude, data.longitude);
                    return {
                        city: city,
                        locality: `${data.city}, ${data.region || 'India'}`,
                        lat: data.latitude,
                        lon: data.longitude
                    };
                }
            }
        } catch (e) {}

        // 3. Try internal /api/location/detect
        try {
            const c = new AbortController();
            const tid = setTimeout(() => c.abort(), 1200);
            const resp = await fetch('/api/location/detect', { signal: c.signal });
            clearTimeout(tid);
            if (resp.ok) {
                const data = await resp.json();
                if (data && data.city) {
                    return {
                        city: data.city,
                        locality: data.locality || `${data.city}, ${data.state || 'India'}`,
                        lat: data.latitude,
                        lon: data.longitude
                    };
                }
            }
        } catch (e) {}

        return null;
    })();

    // When fast IP finishes, apply ONLY to DOM if GPS hasn't completed yet and never force reload
    fastIpPromise.then(ipResult => {
        if (ipResult && !gpsResolved && localStorage.getItem('user_location_mode') !== 'GPS') {
            localStorage.setItem('user_lat', ipResult.lat);
            localStorage.setItem('user_lon', ipResult.lon);
            localStorage.setItem('user_city', ipResult.city);
            localStorage.setItem('user_locality', ipResult.locality);
            localStorage.setItem('user_location_mode', 'IP');
            sessionStorage.setItem('user_device_located', 'true');

            if (locStatus) {
                locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>📍 ${ipResult.city}</span>`;
                locStatus.className = "text-xs bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 font-medium whitespace-nowrap cursor-pointer";
            }
            if (localPill) {
                localPill.innerText = `📍 ${ipResult.locality}`;
                localPill.classList.remove('hidden');
            }

            const cityDesktop = document.getElementById('header-city-select');
            const cityMobile = document.getElementById('header-city-select-mobile');
            if (cityDesktop) cityDesktop.value = ipResult.city;
            if (cityMobile) cityMobile.value = ipResult.city;

            // Update in-place only - zero automatic reload
            updatePageWithLocation(ipResult.lat, ipResult.lon, ipResult.city, ipResult.locality, false);
        }
    });

    if (!navigator.geolocation) {
        const ipRes = await fastIpPromise;
        if (ipRes && !isSilent && typeof showToast === 'function') {
            showToast(`📍 Location: ${ipRes.locality}`, 'success');
        }
        return;
    }

    // High accuracy device GPS with 3-second timeout and 5-min cache to prevent any hanging
    navigator.geolocation.getCurrentPosition(
        async (pos) => {
            gpsResolved = true;
            const lat = pos.coords.latitude;
            const lon = pos.coords.longitude;
            localStorage.setItem('user_lat', lat);
            localStorage.setItem('user_lon', lon);
            localStorage.setItem('user_location_mode', 'GPS');
            localStorage.setItem('user_device_detected', 'true');
            sessionStorage.setItem('user_device_located', 'true');

            let rawLocality = '';
            let rawCity = '';

            try {
                if (locStatus) locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-sky-400 animate-pulse"></span><span>Resolving Area...</span>`;
                const controller = new AbortController();
                const timeoutId = setTimeout(() => controller.abort(), 2000);
                const resp = await fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lon}`, {
                    headers: { 'Accept': 'application/json' },
                    signal: controller.signal
                });
                clearTimeout(timeoutId);
                if (resp.ok) {
                    const data = await resp.json();
                    const addr = data.address || {};
                    rawLocality = addr.neighbourhood || addr.suburb || addr.subdistrict || addr.residential || addr.road || addr.village || addr.town || addr.city_district || addr.county || addr.city || '';
                    rawCity = addr.city || addr.town || addr.state_district || addr.state || '';
                }
            } catch (err) {
                console.warn("Reverse geocode failed or timed out, using coordinates:", err);
            }

            const targetCity = mapToKnownCity(rawCity, lat, lon);
            const locality = rawLocality ? `${rawLocality}, ${targetCity}` : targetCity;

            localStorage.setItem('user_locality', locality);
            localStorage.setItem('user_city', targetCity);

            if (locStatus) {
                locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>GPS: ${targetCity}</span>`;
                locStatus.className = "text-xs bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 font-medium whitespace-nowrap cursor-pointer";
            }
            if (localPill) {
                localPill.innerText = `📍 ${locality}`;
                localPill.classList.remove('hidden');
            }

            const cityDesktop = document.getElementById('header-city-select');
            const cityMobile = document.getElementById('header-city-select-mobile');
            if (cityDesktop) cityDesktop.value = targetCity;
            if (cityMobile) cityMobile.value = targetCity;

            if (typeof showToast === 'function' && !isSilent) {
                showToast(`📍 Exact Location: ${locality}`, 'success');
            }

            // Only reload if user explicitly requested it (isExplicit = true) AND the city differs
            const citySelect = document.getElementById('header-city-select');
            const currentDisplayedCity = citySelect ? citySelect.value : '';
            const isCityChanged = currentDisplayedCity && targetCity.trim().toLowerCase() !== currentDisplayedCity.trim().toLowerCase();
            const shouldReload = isExplicit && isCityChanged;

            updatePageWithLocation(lat, lon, targetCity, locality, shouldReload);
        },
        async (err) => {
            console.warn("Browser GPS unavailable or timed out:", err.message);
            if (!gpsResolved) {
                await fallbackToIpLocation(isSilent, isExplicit);
            }
        },
        { timeout: 3000, enableHighAccuracy: true, maximumAge: 300000 }
    );
}

async function fallbackToIpLocation(isSilent = false, isExplicit = false) {
    const locStatus = document.getElementById('location-status-badge');
    const localPill = document.getElementById('header-locality-badge');
    if (locStatus) {
        locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-sky-400 animate-pulse"></span><span>Detecting City...</span>`;
    }

    let detectedCity = null;
    let detectedLocality = null;
    let detectedLat = null;
    let detectedLon = null;

    // 1. Fast attempt: geojs.io with AbortController
    try {
        const c = new AbortController();
        const tid = setTimeout(() => c.abort(), 1500);
        const r = await fetch('https://get.geojs.io/v1/ip/geo.json', { signal: c.signal });
        clearTimeout(tid);
        if (r.ok) {
            const d = await r.json();
            if (d && (d.city || d.region)) {
                detectedLat = parseFloat(d.latitude);
                detectedLon = parseFloat(d.longitude);
                detectedCity = mapToKnownCity(d.city || d.region, detectedLat, detectedLon);
                detectedLocality = `${d.city || detectedCity}, ${d.region || 'India'}`;
            }
        }
    } catch (e) {}

    // 2. Second attempt: ipwho.is with AbortController
    if (!detectedCity) {
        try {
            const c = new AbortController();
            const tid = setTimeout(() => c.abort(), 1500);
            const ipResp = await fetch('https://ipwho.is/', { signal: c.signal });
            clearTimeout(tid);
            if (ipResp.ok) {
                const ipData = await ipResp.json();
                if (ipData && ipData.success !== false && ipData.city) {
                    detectedCity = mapToKnownCity(ipData.city, ipData.latitude, ipData.longitude);
                    detectedLocality = `${ipData.city}, ${ipData.region || 'India'}`;
                    detectedLat = ipData.latitude;
                    detectedLon = ipData.longitude;
                }
            }
        } catch (e) {}
    }

    // 3. Third attempt: internal /api/location/detect with AbortController
    if (!detectedCity) {
        try {
            const c = new AbortController();
            const tid = setTimeout(() => c.abort(), 1200);
            const resp = await fetch('/api/location/detect', { signal: c.signal });
            clearTimeout(tid);
            if (resp.ok) {
                const data = await resp.json();
                if (data && data.city) {
                    detectedCity = data.city;
                    detectedLocality = data.locality || `${data.city}, ${data.state || 'India'}`;
                    detectedLat = data.latitude;
                    detectedLon = data.longitude;
                }
            }
        } catch (e) {}
    }

    // 4. Default fallback to Kolkata if all network detection fails
    if (!detectedCity) {
        detectedCity = 'Kolkata';
        detectedLocality = 'Kolkata, West Bengal';
        const def = CITY_PRESETS['Kolkata'] || { lat: 22.5726, lon: 88.3639 };
        detectedLat = def.lat;
        detectedLon = def.lon;
    }

    localStorage.setItem('user_lat', detectedLat);
    localStorage.setItem('user_lon', detectedLon);
    localStorage.setItem('user_city', detectedCity);
    localStorage.setItem('user_locality', detectedLocality);
    localStorage.setItem('user_location_mode', 'IP');
    localStorage.setItem('user_device_detected', 'true');
    sessionStorage.setItem('user_device_located', 'true');

    if (locStatus) {
        locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>${detectedCity}</span>`;
        locStatus.className = "text-xs bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 font-medium whitespace-nowrap cursor-pointer";
    }
    if (localPill) {
        localPill.innerText = `📍 ${detectedLocality || detectedCity}`;
        localPill.classList.remove('hidden');
    }

    const cityDesktop = document.getElementById('header-city-select');
    const cityMobile = document.getElementById('header-city-select-mobile');
    if (cityDesktop) cityDesktop.value = detectedCity;
    if (cityMobile) cityMobile.value = detectedCity;

    // Never auto reload in fallback unless explicit user action requested it
    const citySelect = document.getElementById('header-city-select');
    const currentDisplayedCity = citySelect ? citySelect.value : '';
    const isCityChanged = currentDisplayedCity && detectedCity.trim().toLowerCase() !== currentDisplayedCity.trim().toLowerCase();
    const shouldReload = isExplicit && isCityChanged;

    updatePageWithLocation(detectedLat, detectedLon, detectedCity, detectedLocality, shouldReload);
}

function handleCityChange(selectedCity) {
    if (!selectedCity) {
        localStorage.removeItem('user_city');
        localStorage.removeItem('user_lat');
        localStorage.removeItem('user_lon');
        localStorage.removeItem('user_locality');
        localStorage.removeItem('user_manual_city');
        sessionStorage.removeItem('user_device_located');
        sessionStorage.removeItem('last_reloaded_city');
        window.location.href = window.location.pathname;
        return;
    }
    if (selectedCity === 'AUTO_GPS') {
        localStorage.removeItem('user_manual_city');
        sessionStorage.removeItem('user_device_located');
        sessionStorage.removeItem('last_reloaded_city');
        detectUserLocation(false, true);
        return;
    }
    const clean = selectedCity.trim();
    let coords = CITY_PRESETS[clean];
    if (!coords) {
        for (const k in CITY_PRESETS) {
            if (k.toLowerCase().includes(clean.toLowerCase()) || clean.toLowerCase().includes(k.toLowerCase())) {
                coords = CITY_PRESETS[k];
                break;
            }
        }
    }
    if (!coords) {
        coords = { lat: 22.5726, lon: 88.3639 };
    }
    localStorage.setItem('user_lat', coords.lat);
    localStorage.setItem('user_lon', coords.lon);
    localStorage.setItem('user_city', selectedCity);
    localStorage.setItem('user_locality', selectedCity);
    localStorage.setItem('user_location_mode', 'CITY');
    localStorage.setItem('user_manual_city', 'true');
    sessionStorage.setItem('user_device_located', 'true');

    // Keep both dropdowns in sync
    const cityDesktop = document.getElementById('header-city-select');
    const cityMobile = document.getElementById('header-city-select-mobile');
    if (cityDesktop) cityDesktop.value = selectedCity;
    if (cityMobile) cityMobile.value = selectedCity;

    updatePageWithLocation(coords.lat, coords.lon, selectedCity, selectedCity, true);
}

function selectProximityCity(cityName) {
    handleCityChange(cityName);
}

function updatePageWithLocation(lat, lon, city, locality, forceReload = false) {
    if (!city) return;

    // Update locality pill text silently in DOM
    const localPill = document.getElementById('header-locality-badge');
    if (localPill && locality) {
        localPill.innerText = `📍 ${locality}`;
        localPill.classList.remove('hidden');
    }

    // Keep dropdowns in sync in DOM
    const cityDesktop = document.getElementById('header-city-select');
    const cityMobile = document.getElementById('header-city-select-mobile');
    if (cityDesktop && cityDesktop.value !== city) cityDesktop.value = city;
    if (cityMobile && cityMobile.value !== city) cityMobile.value = city;

    // Save location to storage
    localStorage.setItem('user_city', city);
    if (locality) localStorage.setItem('user_locality', locality);
    if (lat) localStorage.setItem('user_lat', lat);
    if (lon) localStorage.setItem('user_lon', lon);

    // CRITICAL: NEVER RELOAD AUTOMATICALLY!
    // Only proceed to navigation if forceReload is explicitly true (i.e. manual user selection)
    if (!forceReload) {
        return;
    }

    // Guard against rapid repetitive reloads
    const lastReloadCity = sessionStorage.getItem('last_reloaded_city');
    if (lastReloadCity && lastReloadCity.toLowerCase() === city.toLowerCase()) {
        return;
    }
    sessionStorage.setItem('last_reloaded_city', city);
    sessionStorage.setItem('user_device_located', 'true');

    const targetUrl = new URL(window.location.href);
    targetUrl.searchParams.set('city', city);
    targetUrl.searchParams.delete('state');
    if (lat) targetUrl.searchParams.set('lat', lat); else targetUrl.searchParams.delete('lat');
    if (lon) targetUrl.searchParams.set('lon', lon); else targetUrl.searchParams.delete('lon');
    if (locality) targetUrl.searchParams.set('locality', locality); else targetUrl.searchParams.delete('locality');

    window.location.href = targetUrl.toString();
}

// Custom Locality Search (Neighborhood / Street / Suburb modal)
function openLocalitySearchModal() {
    const modal = document.getElementById('locality-search-modal');
    if (modal) {
        modal.classList.remove('hidden');
        setTimeout(() => document.getElementById('locality-search-input')?.focus(), 100);
    }
}

function closeLocalitySearchModal() {
    const modal = document.getElementById('locality-search-modal');
    if (modal) modal.classList.add('hidden');
}

let localitySearchTimeout = null;
async function onLocalitySearchInput(query) {
    clearTimeout(localitySearchTimeout);
    const container = document.getElementById('locality-search-results');
    if (!container) return;

    if (!query || query.trim().length < 2) {
        container.innerHTML = '<p class="text-xs text-slate-400 text-center py-4">Type your locality, area, colony, or pin code (e.g. "Patia", "Whitefield", "Salt Lake")...</p>';
        return;
    }

    localitySearchTimeout = setTimeout(async () => {
        container.innerHTML = '<p class="text-xs text-slate-500 text-center py-3"><i class="fa-solid fa-spinner animate-spin mr-1"></i> Searching local places...</p>';
        try {
            const resp = await fetch(`https://nominatim.openstreetmap.org/search?format=jsonv2&q=${encodeURIComponent(query)}&limit=6&addressdetails=1`, {
                headers: { 'Accept': 'application/json' }
            });
            if (!resp.ok) throw new Error("Search failed");
            const places = await resp.json();

            if (!places || places.length === 0) {
                container.innerHTML = `<p class="text-xs text-slate-500 text-center py-4">No locations found for "${query}". Try adding your city name.</p>`;
                return;
            }

            let html = '<div class="divide-y divide-slate-100">';
            places.forEach(p => {
                const addr = p.address || {};
                const localName = addr.neighbourhood || addr.suburb || addr.residential || addr.road || addr.village || addr.town || addr.city_district || p.name || 'Local Area';
                const city = addr.city || addr.town || addr.state_district || 'India';
                const displayName = p.display_name;

                html += `
                    <button type="button" onclick="selectCustomPlace(${p.lat}, ${p.lon}, '${encodeURIComponent(city)}', '${encodeURIComponent(localName)}')"
                            class="w-full text-left p-2.5 hover:bg-sky-50 transition rounded-lg flex items-start gap-2.5 group">
                        <i class="fa-solid fa-location-dot text-sky-600 mt-1 flex-shrink-0"></i>
                        <div class="overflow-hidden">
                            <div class="text-xs font-bold text-slate-900 group-hover:text-sky-700">${localName} <span class="text-slate-500 font-normal">(${city})</span></div>
                            <div class="text-[11px] text-slate-500 truncate">${displayName}</div>
                        </div>
                    </button>
                `;
            });
            html += '</div>';
            container.innerHTML = html;
        } catch (e) {
            console.error(e);
            container.innerHTML = '<p class="text-xs text-red-500 text-center py-3">Could not fetch places. Please check your internet connection.</p>';
        }
    }, 350);
}

function selectCustomPlace(lat, lon, encodedCity, encodedLocality) {
    const city = decodeURIComponent(encodedCity);
    const locality = decodeURIComponent(encodedLocality);

    localStorage.setItem('user_lat', lat);
    localStorage.setItem('user_lon', lon);
    localStorage.setItem('user_city', city);
    localStorage.setItem('user_locality', locality);
    localStorage.setItem('user_location_mode', 'CUSTOM_LOCALITY');

    closeLocalitySearchModal();
    updatePageWithLocation(lat, lon, city, locality, true);
}

// Restore locality badge and auto-detect on load
document.addEventListener('DOMContentLoaded', () => {
    const savedLocality = localStorage.getItem('user_locality');
    const savedLat = localStorage.getItem('user_lat');
    const savedLon = localStorage.getItem('user_lon');
    const savedCity = localStorage.getItem('user_city');
    const hasManualCity = localStorage.getItem('user_manual_city') === 'true';

    const localPill = document.getElementById('header-locality-badge');
    if (savedLocality && localPill) {
        localPill.innerText = `📍 ${savedLocality}`;
        localPill.classList.remove('hidden');
    }

    const url = new URL(window.location.href);
    const urlCity = url.searchParams.get('city');

    // 1. Clean address bar on home page (/) so any link shared by copying address bar is clean
    if (window.history && window.history.replaceState && window.location.pathname === '/') {
        if (url.searchParams.has('city') || url.searchParams.has('lat') || url.searchParams.has('lon') || url.searchParams.has('locality')) {
            window.history.replaceState(null, '', window.location.origin + '/');
        }
    }

    // 2. Set dropdown active city
    const activeCity = (hasManualCity ? (urlCity || savedCity) : (savedCity || urlCity));
    if (activeCity) {
        const cityDesktop = document.getElementById('header-city-select');
        const cityMobile = document.getElementById('header-city-select-mobile');
        if (cityDesktop) cityDesktop.value = activeCity;
        if (cityMobile) cityMobile.value = activeCity;
    }

    const currentPath = window.location.pathname.replace(/\/$/, '') || '/';
    const LOCATION_PAGES = ['/', '/hospitals', '/doctors', '/emergency', '/blood-bank', '/diagnostics', '/pharmacy', '/home-healthcare'];
    const isLocationPage = LOCATION_PAGES.includes(currentPath);

    if (isLocationPage) {
        // Auto-detect ONLY ONCE per session to prevent any page refresh loops
        const alreadyLocated = sessionStorage.getItem('user_device_located') === 'true';
        if (!hasManualCity && !alreadyLocated) {
            sessionStorage.setItem('user_device_located', 'true');
            if (window.history && window.history.replaceState && urlCity) {
                const cleanParams = new URLSearchParams(window.location.search);
                cleanParams.delete('city');
                cleanParams.delete('lat');
                cleanParams.delete('lon');
                cleanParams.delete('locality');
                const newSearch = cleanParams.toString() ? '?' + cleanParams.toString() : '';
                window.history.replaceState(null, '', window.location.pathname + newSearch);
            }
            detectUserLocation(true, false);
        }
    }
});

// Universal Search Spotlight Toggle (Ctrl+K or Command+K)
document.addEventListener('keydown', (e) => {
    if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        toggleUniversalSearch();
    }
});

function toggleUniversalSearch() {
    const modal = document.getElementById('universal-search-modal');
    if (modal) {
        modal.classList.toggle('hidden');
        if (!modal.classList.contains('hidden')) {
            setTimeout(() => document.getElementById('universal-search-input')?.focus(), 50);
        }
    }
}

async function performUniversalSearch(query) {
    const container = document.getElementById('universal-search-results');
    if (!container) return;

    if (!query || query.trim().length < 2) {
        container.innerHTML = '<p class="text-sm text-slate-400 text-center py-4">Type at least 2 characters to search across all 38 healthcare categories...</p>';
        return;
    }

    try {
        const resp = await fetch(`/api/search/universal?q=${encodeURIComponent(query)}`);
        const data = await resp.json();

        let html = '';

        // Doctors
        if (data.doctors && data.doctors.length > 0) {
            html += `<div class="mb-4"><h4 class="text-xs font-bold uppercase tracking-wider text-sky-600 mb-2">🩺 Doctors & Specialists</h4><div class="space-y-1">`;
            data.doctors.forEach(d => {
                html += `<a href="/doctors?specialty=${encodeURIComponent(d.specialty)}" class="flex justify-between items-center p-2 rounded hover:bg-sky-50 text-sm">
                    <div><strong>${d.name}</strong> <span class="text-slate-500">(${d.specialty})</span></div>
                    <span class="text-xs bg-sky-100 text-sky-800 px-2 py-0.5 rounded">₹${d.consultationFee}</span>
                </a>`;
            });
            html += `</div></div>`;
        }

        // Hospitals
        if (data.hospitals && data.hospitals.length > 0) {
            html += `<div class="mb-4"><h4 class="text-xs font-bold uppercase tracking-wider text-emerald-600 mb-2">🏥 Hospitals & Admissions</h4><div class="space-y-1">`;
            data.hospitals.forEach(h => {
                html += `<a href="/hospitals" class="flex justify-between items-center p-2 rounded hover:bg-emerald-50 text-sm">
                    <div><strong>${h.name}</strong> <span class="text-slate-500">(${h.city})</span></div>
                    <span class="text-xs bg-emerald-100 text-emerald-800 px-2 py-0.5 rounded">${h.icuBedsAvailable} ICU Beds Open</span>
                </a>`;
            });
            html += `</div></div>`;
        }

        // Medicines
        if (data.medicines && data.medicines.length > 0) {
            html += `<div class="mb-4"><h4 class="text-xs font-bold uppercase tracking-wider text-indigo-600 mb-2">💊 Medicines</h4><div class="space-y-1">`;
            data.medicines.forEach(m => {
                html += `<a href="/medicine-timer" class="flex justify-between items-center p-2 rounded hover:bg-indigo-50 text-sm">
                    <div><strong>${m.name}</strong> (${m.dosage}) - <span class="text-xs text-slate-500">${m.foodRelation}</span></div>
                    <span class="text-xs bg-indigo-100 text-indigo-800 px-2 py-0.5 rounded">${m.remainingPills} pills</span>
                </a>`;
            });
            html += `</div></div>`;
        }

        // Labs
        if (data.labs && data.labs.length > 0) {
            html += `<div class="mb-4"><h4 class="text-xs font-bold uppercase tracking-wider text-purple-600 mb-2">🧪 Diagnostic Tests</h4><div class="space-y-1">`;
            data.labs.forEach(l => {
                html += `<a href="/diagnostics" class="flex justify-between items-center p-2 rounded hover:bg-purple-50 text-sm">
                    <div><strong>${l.testName}</strong> <span class="text-slate-500">(${l.centerName})</span></div>
                    <span class="text-xs bg-purple-100 text-purple-800 px-2 py-0.5 rounded">₹${l.price}</span>
                </a>`;
            });
            html += `</div></div>`;
        }

        // Records
        if (data.records && data.records.length > 0) {
            html += `<div class="mb-4"><h4 class="text-xs font-bold uppercase tracking-wider text-amber-600 mb-2">📄 Health Records</h4><div class="space-y-1">`;
            data.records.forEach(r => {
                html += `<a href="/medical-records" class="flex justify-between items-center p-2 rounded hover:bg-amber-50 text-sm">
                    <div><strong>${r.title}</strong> <span class="text-slate-500">(${r.recordDate})</span></div>
                    <span class="text-xs bg-amber-100 text-amber-800 px-2 py-0.5 rounded">${r.recordType}</span>
                </a>`;
            });
            html += `</div></div>`;
        }

        if (!html) {
            html = '<p class="text-sm text-slate-500 text-center py-4">No matching results found for "' + query + '".</p>';
        }

        container.innerHTML = html;
    } catch (e) {
        console.error(e);
        container.innerHTML = '<p class="text-sm text-red-500 text-center py-4">Search failed. Please try again.</p>';
    }
}

// Share platform link cleanly without locking recipient to sender's GPS coordinates
function shareCarePulseLink() {
    const cleanUrl = window.location.origin + (window.location.pathname === '/' ? '' : window.location.pathname);
    if (navigator.share) {
        navigator.share({
            title: 'CarePulse Healthcare Platform',
            text: 'CarePulse Universal Healthcare - Find nearby hospitals, doctors, blood banks, and medicine alerts:',
            url: cleanUrl
        }).catch(() => {});
    } else {
        navigator.clipboard.writeText(cleanUrl).then(() => {
            if (typeof showToast === 'function') {
                showToast('📋 Link copied to clipboard! Share with anyone.', 'success');
            }
        }).catch(() => {
            prompt('Copy platform link:', cleanUrl);
        });
    }
}

