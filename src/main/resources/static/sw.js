// ==========================================================
// CarePulse Healthcare PWA Service Worker
// Offline Support, Fast Shell Caching, and App Experience
// ==========================================================

const CACHE_NAME = "carepulse-pwa-v2";
const STATIC_ASSETS = [
  "/manifest.json",
  "/css/custom.css",
  "/icons/icon-192.png",
  "/icons/icon-512.png",
  "/icons/apple-touch-icon.png",
  "/icons/favicon.png"
];

// 1. Install: Pre-cache core app shell
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(STATIC_ASSETS).catch((err) => {
        console.warn("Some assets failed to precache:", err);
      });
    })
  );
  self.skipWaiting();
});

// 2. Activate: Clear older caches
self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.filter((key) => key !== CACHE_NAME).map((key) => caches.delete(key))
      );
    })
  );
  self.clients.claim();
});

// 3. Fetch: Network-first for dynamic data with cache fallback for offline
self.addEventListener("fetch", (event) => {
  const req = event.request;
  const url = new URL(req.url);

  // Ignore non-GET requests or chrome-extension URLs
  if (req.method !== "GET" || !url.protocol.startsWith("http")) return;

  // Static images/icons/css: Cache-first
  if (url.pathname.startsWith("/icons/") || url.pathname.endsWith(".css") || url.pathname.endsWith(".png")) {
    event.respondWith(
      caches.match(req).then((cached) => {
        return (
          cached ||
          fetch(req).then((response) => {
            if (response.ok) {
              const clone = response.clone();
              caches.open(CACHE_NAME).then((cache) => cache.put(req, clone));
            }
            return response;
          })
        );
      })
    );
    return;
  }

  // HTML Page Navigation: Network-first, fallback to cache (offline mode)
  event.respondWith(
    fetch(req)
      .then((response) => {
        if (response.ok && req.mode === "navigate") {
          const clone = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(req, clone));
        }
        return response;
      })
      .catch(() => {
        return caches.match(req).then((cached) => {
          if (cached) return cached;
          if (req.mode === "navigate") {
            return caches.match("/");
          }
          return new Response("Offline - CarePulse Healthcare", {
            status: 503,
            headers: { "Content-Type": "text/plain" }
          });
        });
      })
  );
});

