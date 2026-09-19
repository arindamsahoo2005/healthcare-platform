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

async function detectUserLocation(isSilent = false) {
    const locStatus = document.getElementById('location-status-badge');
    const localPill = document.getElementById('header-locality-badge');
    if (locStatus) {
        locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-amber-400 animate-pulse"></span><span>Locating...</span>`;
        locStatus.className = "text-xs bg-amber-950 text-amber-300 border border-amber-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 animate-pulse whitespace-nowrap";
    }

    if (!navigator.geolocation) {
        await fallbackToIpLocation(isSilent);
        return;
    }

    // Try fast device network/GPS position (maximumAge 5m for instantaneous cached mobile resolution)
    navigator.geolocation.getCurrentPosition(
        async (pos) => {
            const lat = pos.coords.latitude;
            const lon = pos.coords.longitude;
            localStorage.setItem('user_lat', lat);
            localStorage.setItem('user_lon', lon);
            localStorage.setItem('user_location_mode', 'GPS');

            let locality = 'My Exact Location';
            let city = 'Local Area';

            try {
                if (locStatus) locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-sky-400 animate-pulse"></span><span>Resolving Area...</span>`;
                const resp = await fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lon}`, {
                    headers: { 'Accept': 'application/json' }
                });
                if (resp.ok) {
                    const data = await resp.json();
                    const addr = data.address || {};
                    locality = addr.neighbourhood || addr.suburb || addr.subdistrict || addr.residential || addr.road || addr.village || addr.town || addr.city_district || addr.county || addr.city || 'Exact Spot';
                    city = addr.city || addr.town || addr.state_district || addr.state || 'Local';
                }
            } catch (err) {
                console.warn("Reverse geocode failed, using coordinates:", err);
            }

            localStorage.setItem('user_locality', locality);
            localStorage.setItem('user_city', city);

            if (locStatus) {
                locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-emerald-400 animate-pulse"></span><span>GPS Active</span>`;
                locStatus.className = "text-xs bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 font-medium whitespace-nowrap";
            }
            if (localPill) {
                localPill.innerText = `📍 ${locality}`;
                localPill.classList.remove('hidden');
            }

            if (typeof showToast === 'function' && !isSilent) {
                showToast(`📍 GPS Active: ${locality}`, 'success');
            }

            // Reload current page with exact GPS parameters and neighborhood locality
            updatePageWithLocation(lat, lon, city, locality);
        },
        async (err) => {
            console.warn("Browser GPS unavailable or timed out:", err.message);
            // Seamlessly fall back to IP-based location with ZERO permission popups
            await fallbackToIpLocation(isSilent);
        },
        { timeout: 6000, enableHighAccuracy: false, maximumAge: 300000 }
    );
}

async function fallbackToIpLocation(isSilent = false) {
    const locStatus = document.getElementById('location-status-badge');
    const localPill = document.getElementById('header-locality-badge');
    if (locStatus) {
        locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-sky-400 animate-pulse"></span><span>Network Area...</span>`;
    }

    try {
        const res = await fetch('https://ipapi.co/json/', { headers: { 'Accept': 'application/json' } });
        if (res.ok) {
            const data = await res.json();
            if (data && data.latitude && data.longitude) {
                const city = data.city || 'Kolkata';
                const region = data.region || 'West Bengal';
                const locality = `${city}, ${region}`;
                localStorage.setItem('user_lat', data.latitude);
                localStorage.setItem('user_lon', data.longitude);
                localStorage.setItem('user_city', city);
                localStorage.setItem('user_locality', locality);
                localStorage.setItem('user_location_mode', 'IP');

                if (locStatus) {
                    locStatus.innerHTML = `<span class="w-1.5 h-1.5 rounded-full bg-emerald-400"></span><span>${city} (Network)</span>`;
                    locStatus.className = "text-xs bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded-full inline-flex items-center gap-1 font-medium whitespace-nowrap";
                }
                if (localPill) {
                    localPill.innerText = `📍 ${city}`;
                    localPill.classList.remove('hidden');
                }
                if (typeof showToast === 'function' && !isSilent) {
                    showToast(`📍 Location detected: ${city}, ${region}`, 'success');
                }
                updatePageWithLocation(data.latitude, data.longitude, city, locality);
                return;
            }
        }
    } catch (e) {
        console.warn("IP geolocation fallback error:", e);
    }

    // Default to Kolkata, West Bengal gracefully
    const def = CITY_PRESETS['Kolkata'] || { lat: 22.5726, lon: 88.3639 };
    localStorage.setItem('user_lat', def.lat);
    localStorage.setItem('user_lon', def.lon);
    localStorage.setItem('user_city', 'Kolkata');
    localStorage.setItem('user_locality', 'Kolkata, WB');
    localStorage.setItem('user_location_mode', 'DEFAULT');

    if (locStatus) {
        locStatus.innerHTML = `<span>📍 Kolkata, WB</span>`;
        locStatus.className = "text-xs bg-slate-800 text-slate-300 border border-slate-700 px-2 py-0.5 rounded-full whitespace-nowrap";
    }
    if (localPill) {
        localPill.innerText = `📍 Kolkata`;
        localPill.classList.remove('hidden');
    }

    if (!isSilent) {
        if (typeof openLocalitySearchModal === 'function') {
            openLocalitySearchModal();
        }
        if (typeof showToast === 'function') {
            showToast("Defaulted to Kolkata. You can pick your local neighborhood below!", "info");
        }
    }
}

function handleCityChange(selectedCity) {
    if (!selectedCity) {
        localStorage.removeItem('user_city');
        localStorage.removeItem('user_lat');
        localStorage.removeItem('user_lon');
        localStorage.removeItem('user_locality');
        window.location.href = window.location.pathname;
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
        coords = { lat: 22.4820, lon: 88.1812 };
    }
    localStorage.setItem('user_lat', coords.lat);
    localStorage.setItem('user_lon', coords.lon);
    localStorage.setItem('user_city', selectedCity);
    localStorage.setItem('user_locality', selectedCity);
    localStorage.setItem('user_location_mode', 'CITY');

    updatePageWithLocation(coords.lat, coords.lon, selectedCity, selectedCity);
}

function selectProximityCity(cityName) {
    handleCityChange(cityName);
}

function updatePageWithLocation(lat, lon, city, locality) {
    const url = new URL(window.location.href);
    if (city) {
        url.searchParams.set('city', city);
        url.searchParams.delete('state'); // Remove conflicting state parameter so city drives exact regional results
    } else {
        url.searchParams.delete('city');
    }
    if (lat) url.searchParams.set('lat', lat); else url.searchParams.delete('lat');
    if (lon) url.searchParams.set('lon', lon); else url.searchParams.delete('lon');
    if (locality) url.searchParams.set('locality', locality); else url.searchParams.delete('locality');
    window.location.replace(url.toString());
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
    updatePageWithLocation(lat, lon, city, locality);
}

// Restore locality badge and auto-detect on load
document.addEventListener('DOMContentLoaded', () => {
    const savedLocality = localStorage.getItem('user_locality');
    const savedLat = localStorage.getItem('user_lat');
    const savedLon = localStorage.getItem('user_lon');
    const savedCity = localStorage.getItem('user_city');

    const localPill = document.getElementById('header-locality-badge');
    if (savedLocality && localPill) {
        localPill.innerText = `📍 ${savedLocality}`;
        localPill.classList.remove('hidden');
    }

    const currentPath = window.location.pathname.replace(/\/$/, '') || '/';
    const LOCATION_PAGES = ['/', '/hospitals', '/doctors', '/emergency', '/blood-bank', '/diagnostics', '/pharmacy', '/home-healthcare'];
    const isLocationPage = LOCATION_PAGES.includes(currentPath);

    const url = new URL(window.location.href);
    const hasUrlLat = url.searchParams.has('lat');
    const hasUrlCity = url.searchParams.has('city');

    if (isLocationPage && !hasUrlLat && !hasUrlCity) {
        if (savedLat && savedLon) {
            updatePageWithLocation(savedLat, savedLon, savedCity || '', savedLocality || '');
        } else {
            // Auto detect device GPS silently on first visit
            detectUserLocation(true);
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
