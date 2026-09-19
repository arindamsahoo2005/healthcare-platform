/**
 * Firebase Authentication & Session Sync Module
 * Supports Google Sign-In, Email/Password login, registration, and Spring Boot session sync
 */

// Default Firebase Web configuration
const DEFAULT_FIREBASE_CONFIG = {
    apiKey: "AIzaSyDTFmHn7P3soIYa_m_qjtSs16y8e3Slf-0",
    authDomain: "carepulse-a7fe5.firebaseapp.com",
    projectId: "carepulse-a7fe5",
    storageBucket: "carepulse-a7fe5.firebasestorage.app",
    messagingSenderId: "979439835553",
    appId: "1:979439835553:web:e2e1e2c6b417211fe153e0",
    measurementId: "G-WZYJC4M0GR"
};

let firebaseInitialized = false;

function initFirebase() {
    if (typeof firebase === 'undefined') {
        console.warn("Firebase SDK not loaded");
        return;
    }

    try {
        let config = DEFAULT_FIREBASE_CONFIG;
        const customConfig = localStorage.getItem('firebase_config');
        if (customConfig) {
            try { config = JSON.parse(customConfig); } catch (e) {}
        }

        if (!firebase.apps.length) {
            firebase.initializeApp(config);
        }
        firebaseInitialized = true;
    } catch (e) {
        console.warn("Firebase initialization note:", e.message);
    }
}

async function autoConnectFirebase() {
    if (!firebaseInitialized) initFirebase();

    // Check if user signed in with Google
    const savedGoogleUser = localStorage.getItem('google_auth_user');
    if (savedGoogleUser) {
        try {
            const user = JSON.parse(savedGoogleUser);
            if (user && user.uid) {
                if (sessionStorage.getItem('firebase_session_synced') !== user.uid) {
                    const checkResp = await fetch('/api/auth/current-user');
                    if (checkResp.ok) {
                        const data = await checkResp.json();
                        if (!data.authenticated || data.email !== user.email) {
                            await syncUserWithBackend(user);
                            return;
                        } else {
                            sessionStorage.setItem('firebase_session_synced', user.uid);
                        }
                    }
                }
                return;
            }
        } catch (e) {
            console.warn("Error restoring Google auth session:", e);
        }
    }

    if (typeof firebase === 'undefined' || !firebase.auth) return;

    try {
        // Handle redirect result if redirected for Google sign in
        firebase.auth().getRedirectResult().then(async (result) => {
            if (result && result.user) {
                console.log("Firebase redirect auth success:", result.user.uid);
                await syncUserWithBackend({
                    uid: result.user.uid,
                    displayName: result.user.displayName || result.user.email?.split('@')[0] || 'Google User',
                    email: result.user.email,
                    photoURL: result.user.photoURL || '',
                    phone: result.user.phoneNumber || ''
                });
            }
        }).catch((err) => {
            console.warn("Firebase redirect auth note:", err);
        });

        firebase.auth().onAuthStateChanged(async (user) => {
            if (user) {
                console.log("Firebase auto-connected:", user.uid);
                await ensureSessionSynced(user);
            }
        });
    } catch (e) {
        console.warn("autoConnectFirebase note:", e);
    }
}

async function ensureSessionSynced(user) {
    if (sessionStorage.getItem('firebase_session_synced') === user.uid) {
        return;
    }

    try {
        const checkResp = await fetch('/api/auth/current-user');
        if (checkResp.ok) {
            const data = await checkResp.json();
            if (data.authenticated) {
                sessionStorage.setItem('firebase_session_synced', user.uid);
                return;
            }
        }

        const formData = new URLSearchParams();
        formData.append('uid', user.uid);
        formData.append('email', user.email || '');
        formData.append('displayName', user.displayName || user.email?.split('@')[0] || 'Universal Patient');
        formData.append('photoUrl', user.photoURL || '');
        formData.append('phone', user.phoneNumber || '');

        const resp = await fetch('/api/auth/firebase-login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        if (resp.ok) {
            sessionStorage.setItem('firebase_session_synced', user.uid);
            localStorage.setItem('auth_user_name', user.displayName || 'Universal Patient');
            window.location.href = '/';
        }
    } catch (err) {
        console.warn("ensureSessionSynced error:", err);
    }
}

// Direct Google Sign-In via Firebase
async function signInWithGoogle() {
    if (typeof showToast === 'function') {
        showToast("Opening Google Sign-In...", "info");
    }

    if (typeof firebase === 'undefined' || !firebase.auth) {
        initFirebase();
    }

    try {
        if (!firebaseInitialized) initFirebase();
        const provider = new firebase.auth.GoogleAuthProvider();
        provider.addScope('profile');
        provider.addScope('email');
        provider.setCustomParameters({ prompt: 'select_account' });

        const result = await firebase.auth().signInWithPopup(provider);
        if (result && result.user) {
            if (typeof showToast === 'function') {
                showToast("Google account verified! Connecting...", "success");
            }
            await syncUserWithBackend({
                uid: result.user.uid,
                displayName: result.user.displayName || result.user.email?.split('@')[0] || 'Google User',
                email: result.user.email,
                photoURL: result.user.photoURL || '',
                phone: result.user.phoneNumber || ''
            });
        }
    } catch (err) {
        console.warn("Firebase Google Sign-In note:", err);
        if (err.code === 'auth/popup-blocked') {
            if (typeof showToast === 'function') {
                showToast("Popup blocked. Redirecting to Google Sign-In...", "info");
            }
            const provider = new firebase.auth.GoogleAuthProvider();
            provider.addScope('profile');
            provider.addScope('email');
            provider.setCustomParameters({ prompt: 'select_account' });
            await firebase.auth().signInWithRedirect(provider);
        } else if (err.code === 'auth/popup-closed-by-user') {
            if (typeof showToast === 'function') {
                showToast("Google sign-in window was closed.", "info");
            }
        } else if (err.code === 'auth/cancelled-popup-request') {
            // Concurrent popup suppressed
        } else if (err.code === 'auth/unauthorized-domain') {
            const domain = window.location.hostname;
            const domainMsg = "Google OAuth requires adding '" + domain + "' to Firebase Console Authorized Domains. Please use the instant 1-Tap Sign-In or Email/Password below to log in right now!";
            if (typeof showAuthAlert === 'function') {
                showAuthAlert(domainMsg, 'error');
            }
            if (typeof showPageLoginAlert === 'function') {
                showPageLoginAlert(domainMsg, 'error');
            }
            if (typeof showToast === 'function') {
                showToast("Firebase: Add '" + domain + "' to Authorized Domains, or use 1-Tap Sign-In.", "warning");
            }
        } else {
            if (typeof showToast === 'function') {
                showToast("Google Sign-In: " + (err.message || err.code), "error");
            }
        }
    }
}

// Authentication Modal State & Tab Management
function openAuthModal(initialTab) {
    const modal = document.getElementById('unified-auth-modal');
    if (!modal) {
        signInWithGoogle();
        return;
    }
    modal.classList.remove('hidden');
    switchAuthTab(initialTab === 'register' ? 'register' : 'login');
}

function closeAuthModal() {
    const modal = document.getElementById('unified-auth-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
    const alertBox = document.getElementById('auth-modal-alert');
    if (alertBox) {
        alertBox.className = 'hidden mb-4 p-3 rounded-xl text-xs font-semibold flex items-center gap-2';
        alertBox.innerHTML = '';
    }
}

function switchAuthTab(tab) {
    const loginPanel = document.getElementById('login-tab-panel');
    const registerPanel = document.getElementById('register-tab-panel');
    const tabLoginBtn = document.getElementById('tab-login-btn');
    const tabRegisterBtn = document.getElementById('tab-register-btn');
    const modalTitle = document.getElementById('auth-modal-title');
    const modalSubtitle = document.getElementById('auth-modal-subtitle');
    const alertBox = document.getElementById('auth-modal-alert');

    if (alertBox) {
        alertBox.className = 'hidden mb-4 p-3 rounded-xl text-xs font-semibold flex items-center gap-2';
        alertBox.innerHTML = '';
    }

    if (tab === 'register') {
        if (loginPanel) loginPanel.classList.add('hidden');
        if (registerPanel) registerPanel.classList.remove('hidden');
        if (tabRegisterBtn) {
            tabRegisterBtn.className = 'flex-1 py-1.5 text-xs font-bold rounded-lg transition text-white bg-emerald-600 shadow-sm cursor-pointer';
        }
        if (tabLoginBtn) {
            tabLoginBtn.className = 'flex-1 py-1.5 text-xs font-bold rounded-lg transition text-slate-600 hover:text-slate-900 cursor-pointer';
        }
        if (modalTitle) modalTitle.textContent = 'Create New Account';
        if (modalSubtitle) modalSubtitle.textContent = 'Join CarePulse for connected universal healthcare';
        const nameInput = document.getElementById('reg-fullname');
        if (nameInput) setTimeout(() => nameInput.focus(), 100);
    } else {
        if (registerPanel) registerPanel.classList.add('hidden');
        if (loginPanel) loginPanel.classList.remove('hidden');
        if (tabLoginBtn) {
            tabLoginBtn.className = 'flex-1 py-1.5 text-xs font-bold rounded-lg transition text-white bg-sky-600 shadow-sm cursor-pointer';
        }
        if (tabRegisterBtn) {
            tabRegisterBtn.className = 'flex-1 py-1.5 text-xs font-bold rounded-lg transition text-slate-600 hover:text-slate-900 cursor-pointer';
        }
        if (modalTitle) modalTitle.textContent = 'Welcome to CarePulse';
        if (modalSubtitle) modalSubtitle.textContent = 'Sign in to access your digital healthcare account';
        const loginInput = document.getElementById('login-identifier');
        if (loginInput) setTimeout(() => loginInput.focus(), 100);
    }
}

function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);
    if (!input || !icon) return;

    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

function showAuthAlert(message, type) {
    const alertBox = document.getElementById('auth-modal-alert');
    if (!alertBox) return;

    let iconClass = 'fa-solid fa-circle-exclamation';
    let colorClasses = 'bg-red-50 text-red-700 border border-red-200';

    if (type === 'success') {
        iconClass = 'fa-solid fa-circle-check text-emerald-600';
        colorClasses = 'bg-emerald-50 text-emerald-800 border border-emerald-200';
    } else if (type === 'info') {
        iconClass = 'fa-solid fa-circle-info text-sky-600';
        colorClasses = 'bg-sky-50 text-sky-800 border border-sky-200';
    }

    alertBox.className = `mb-4 p-3 rounded-xl text-xs font-semibold flex items-center gap-2 ${colorClasses}`;
    alertBox.innerHTML = `<i class="${iconClass}"></i><span>${message}</span>`;
}

// Log In handler (Username or Email + Password)
async function handleLoginSubmit(event) {
    if (event) event.preventDefault();
    const loginInput = document.getElementById('login-identifier');
    const passInput = document.getElementById('login-password');
    const submitBtn = document.getElementById('login-submit-btn');

    const identifier = loginInput ? loginInput.value.trim() : '';
    const password = passInput ? passInput.value : '';

    if (!identifier || !password) {
        showAuthAlert('Please enter both your email/username and password.', 'error');
        return;
    }

    const origBtnHtml = submitBtn ? submitBtn.innerHTML : '';
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i><span>Logging In...</span>';
    }

    try {
        const resp = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                emailOrUsername: identifier,
                password: password
            })
        });

        const data = await resp.json();
        if (resp.ok && data.success) {
            showAuthAlert(data.message || 'Login successful! Redirecting...', 'success');
            localStorage.setItem('auth_user_name', data.name || 'User');
            if (data.uid) {
                sessionStorage.setItem('firebase_session_synced', data.uid);
            }
            setTimeout(() => {
                window.location.href = '/';
            }, 400);
        } else {
            showAuthAlert(data.message || 'Login failed. Please check your credentials.', 'error');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = origBtnHtml;
            }
        }
    } catch (err) {
        console.error('Login error:', err);
        showAuthAlert('Network error occurred during login. Please try again.', 'error');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = origBtnHtml;
        }
    }
}

// 1-Tap Instant Sign-in for demo / guest / testing
function quickFillModalDemoLogin() {
    switchAuthTab('login');
    const loginInput = document.getElementById('login-identifier');
    const passInput = document.getElementById('login-password');
    if (loginInput) loginInput.value = 'arindam.sahoo@gmail.com';
    if (passInput) passInput.value = 'arindam123';
    handleLoginSubmit();
}

// Create Account / Register handler (FullName, Email, Phone, Password, Confirm)
async function handleRegisterSubmit(event) {
    if (event) event.preventDefault();
    const nameInput = document.getElementById('reg-fullname');
    const emailInput = document.getElementById('reg-email');
    const phoneInput = document.getElementById('reg-phone');
    const passInput = document.getElementById('reg-password');
    const confirmInput = document.getElementById('reg-confirm');
    const submitBtn = document.getElementById('reg-submit-btn');

    const fullName = nameInput ? nameInput.value.trim() : '';
    const email = emailInput ? emailInput.value.trim() : '';
    const phone = phoneInput ? phoneInput.value.trim() : '';
    const password = passInput ? passInput.value : '';
    const confirm = confirmInput ? confirmInput.value : '';

    if (!fullName) {
        showAuthAlert('Please enter your full name.', 'error');
        return;
    }
    if (!email) {
        showAuthAlert('Please enter a valid email address.', 'error');
        return;
    }
    if (!password || password.length < 4) {
        showAuthAlert('Password must be at least 4 characters.', 'error');
        return;
    }
    if (password !== confirm) {
        showAuthAlert('Passwords do not match. Please re-enter identical passwords.', 'error');
        return;
    }

    const origBtnHtml = submitBtn ? submitBtn.innerHTML : '';
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i><span>Creating Account...</span>';
    }

    try {
        const resp = await fetch('/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                fullName: fullName,
                email: email,
                phone: phone,
                password: password
            })
        });

        const data = await resp.json();
        if (resp.ok && data.success) {
            showAuthAlert(data.message || 'Account created successfully! Welcome to CarePulse.', 'success');
            localStorage.setItem('auth_user_name', data.name || fullName);
            if (data.uid) {
                sessionStorage.setItem('firebase_session_synced', data.uid);
            }
            setTimeout(() => {
                window.location.href = '/';
            }, 500);
        } else {
            showAuthAlert(data.message || 'Registration failed. Please check your information.', 'error');
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = origBtnHtml;
            }
        }
    } catch (err) {
        console.error('Register error:', err);
        showAuthAlert('Network error occurred during registration. Please try again.', 'error');
        if (submitBtn) {
            submitBtn.disabled = false;
            submitBtn.innerHTML = origBtnHtml;
        }
    }
}

// Aliases for compatibility
async function signInWithGooglePopup() { await signInWithGoogle(); }
function openGoogleAuthModal() { openAuthModal('login'); }
function closeGoogleAuthModal() { closeAuthModal(); }
function openGoogleChooser() { openAuthModal('login'); }
function closeGoogleChooser() { closeAuthModal(); }

// Synchronize authenticated user with Spring Boot backend and MongoDB
async function syncUserWithBackend(user) {
    try {
        const payload = {
            uid: user.uid,
            email: user.email || '',
            displayName: user.displayName || user.email?.split('@')[0] || 'Universal Patient',
            photoUrl: user.photoURL || user.photoUrl || '',
            phone: user.phoneNumber || user.phone || ''
        };

        // 1. Post JSON payload to /api/auth/firebase-login
        let resp = await fetch('/api/auth/firebase-login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        // 2. Fallback to Form Data if needed
        if (!resp.ok) {
            const formData = new URLSearchParams();
            formData.append('uid', payload.uid);
            formData.append('email', payload.email);
            formData.append('displayName', payload.displayName);
            formData.append('photoUrl', payload.photoUrl);
            formData.append('phone', payload.phone);

            resp = await fetch('/api/auth/firebase-login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: formData.toString()
            });
        }

        if (resp.ok) {
            localStorage.setItem('google_auth_user', JSON.stringify(user));
            localStorage.setItem('auth_user_name', payload.displayName);
            sessionStorage.setItem('firebase_session_synced', user.uid);
            if (typeof showToast === 'function') {
                showToast("Signed in as " + payload.displayName + "! Going to Dashboard...", "success");
            }
            setTimeout(() => {
                window.location.href = '/';
            }, 350);
        } else {
            if (typeof showToast === 'function') {
                showToast("Server returned status " + resp.status + ". Please retry.", "error");
            }
        }
    } catch (err) {
        console.error("Backend auth sync error:", err);
        if (typeof showToast === 'function') {
            showToast("Network sync error: " + err.message, "error");
        }
    }
}

async function signOutUser() {
    sessionStorage.removeItem('firebase_session_synced');
    localStorage.removeItem('auth_user_name');
    localStorage.removeItem('google_auth_user');
    localStorage.removeItem('firebase_auto_uid');
    try {
        if (firebaseInitialized && firebase.auth) {
            await firebase.auth().signOut().catch(() => {});
        }
    } catch (e) {}

    try {
        await fetch('/api/auth/logout', { method: 'POST' });
    } catch (e) {}

    window.location.reload();
}

// Initialize on DOM ready and automatically connect with Firebase
document.addEventListener('DOMContentLoaded', () => {
    autoConnectFirebase();

    // Close auth modal on backdrop click
    const authModal = document.getElementById('unified-auth-modal');
    if (authModal) {
        authModal.addEventListener('click', (e) => {
            if (e.target === authModal) {
                closeAuthModal();
            }
        });
    }

    // Close auth modal on Escape key press
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            closeAuthModal();
        }
    });
});

