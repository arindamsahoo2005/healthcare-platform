/**
 * Smart Medicine Timer Engine
 * Features: Live countdown clock, Taken/Snooze/Skip workflows, Audio alerts, Safety enforcement
 */

let countdownInterval = null;
let currentRemainingSeconds = 0;
let currentScheduleId = null;

// Synthesize pleasant clinical audio chime using Web Audio API
function playAlertChime() {
    try {
        const AudioContext = window.AudioContext || window.webkitAudioContext;
        if (!AudioContext) return;
        const ctx = new AudioContext();
        
        const now = ctx.currentTime;
        const osc1 = ctx.createOscillator();
        const osc2 = ctx.createOscillator();
        const gain = ctx.createGain();
        
        osc1.type = 'sine';
        osc1.frequency.setValueAtTime(587.33, now); // D5
        osc1.frequency.exponentialRampToValueAtTime(880.00, now + 0.3); // A5
        
        osc2.type = 'sine';
        osc2.frequency.setValueAtTime(440.00, now + 0.15); // A4
        osc2.frequency.exponentialRampToValueAtTime(659.25, now + 0.45); // E5

        gain.gain.setValueAtTime(0.15, now);
        gain.gain.exponentialRampToValueAtTime(0.01, now + 0.8);

        osc1.connect(gain);
        osc2.connect(gain);
        gain.connect(ctx.destination);

        osc1.start(now);
        osc2.start(now + 0.15);
        osc1.stop(now + 0.8);
        osc2.stop(now + 0.8);
    } catch (e) {
        console.log('Audio chime error:', e);
    }
}

function initMedicineCountdown(seconds, scheduleId) {
    currentRemainingSeconds = parseInt(seconds, 10);
    currentScheduleId = scheduleId;

    if (isNaN(currentRemainingSeconds) || currentRemainingSeconds <= 0) {
        updateCountdownUI(0);
        return;
    }

    if (countdownInterval) clearInterval(countdownInterval);

    updateCountdownUI(currentRemainingSeconds);

    countdownInterval = setInterval(() => {
        currentRemainingSeconds--;
        if (currentRemainingSeconds <= 0) {
            clearInterval(countdownInterval);
            updateCountdownUI(0);
            playAlertChime();
            showDoseDueAlert();
        } else {
            updateCountdownUI(currentRemainingSeconds);
        }
    }, 1000);
}

function updateCountdownUI(seconds) {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    const display = `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    
    document.querySelectorAll('.medicine-countdown-clock').forEach(el => {
        el.innerText = display;
    });

    // Update individual time digit blocks if present
    const hEl = document.getElementById('timer-hours');
    const mEl = document.getElementById('timer-minutes');
    const sEl = document.getElementById('timer-seconds');
    if (hEl) hEl.innerText = String(hours).padStart(2, '0');
    if (mEl) mEl.innerText = String(minutes).padStart(2, '0');
    if (sEl) sEl.innerText = String(secs).padStart(2, '0');

    // Smart notification banner in header
    const smartBanner = document.getElementById('smart-reminder-banner');
    if (smartBanner) {
        if (seconds > 0 && seconds <= 1800) { // Under 30 minutes
            const minsLeft = Math.ceil(seconds / 60);
            smartBanner.classList.remove('hidden');
            document.getElementById('smart-reminder-text').innerHTML = 
                `🔔 <strong>Your next scheduled medicine is in ${minsLeft} minutes.</strong>`;
        } else if (seconds === 0) {
            smartBanner.classList.remove('hidden');
            document.getElementById('smart-reminder-text').innerHTML = 
                `⚠️ <strong>Your scheduled medicine dose is DUE NOW!</strong>`;
        }
    }
}

function speakMedicineReminder(medicineName, dosage, foodAdvice) {
    try {
        if ('speechSynthesis' in window) {
            window.speechSynthesis.cancel();
            const med = medicineName || 'Metformin';
            const dose = dosage || '500 mg';
            const advice = foodAdvice || 'Please take 15 to 30 minutes after your meal with plenty of water.';
            const text = `Attention: It is time to take your scheduled medicine, ${med}, ${dose}. ${advice}`;
            const utterance = new SpeechSynthesisUtterance(text);
            utterance.rate = 0.92;
            utterance.pitch = 1.05;
            utterance.lang = 'en-IN';
            window.speechSynthesis.speak(utterance);
        }
    } catch (e) {
        console.warn('Speech synthesis note:', e);
    }
}

function triggerLiveMedicineAlert(medicineName, dosage, foodAdvice) {
    playAlertChime();
    speakMedicineReminder(medicineName, dosage, foodAdvice);
    showDoseDueAlert(medicineName, dosage);
    showDoseModalOverlay(medicineName, dosage, foodAdvice);
}

function testMedicineAlert() {
    const medName = document.getElementById('next-med-name')?.innerText || 'Metformin (Glycomet 500 SR)';
    const dosage = document.getElementById('next-med-dosage')?.innerText || '500 mg';
    const food = document.getElementById('next-med-food')?.innerText || 'Take 15-30 minutes after your meal with plenty of water.';
    
    showToast("🔔 Live Alarm Triggered: Audio chime, voice reminder & desktop alert active!", "info");
    triggerLiveMedicineAlert(medName, dosage, food);
}

function showDoseModalOverlay(medName, dosage, foodAdvice) {
    let modal = document.getElementById('live-medicine-alert-modal');
    if (!modal) {
        modal = document.createElement('div');
        modal.id = 'live-medicine-alert-modal';
        modal.className = 'fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4 animate-fade-in';
        modal.innerHTML = `
            <div class="bg-white max-w-md w-full rounded-3xl shadow-2xl p-6 sm:p-7 border-2 border-sky-400 text-center space-y-4 relative overflow-hidden">
                <div class="w-16 h-16 rounded-2xl bg-sky-100 text-sky-600 flex items-center justify-center text-3xl mx-auto shadow-inner animate-bounce">
                    💊
                </div>
                <div>
                    <span class="text-[10px] font-black uppercase tracking-widest text-sky-700 bg-sky-50 border border-sky-200 px-3 py-1 rounded-full">
                        ⏰ EXACT TIME MEDICINE ALERT
                    </span>
                    <h3 id="alert-modal-med" class="text-xl sm:text-2xl font-black text-slate-900 mt-2">Metformin 500mg</h3>
                    <p id="alert-modal-food" class="text-xs text-amber-700 font-bold bg-amber-50 border border-amber-200 rounded-xl p-2.5 mt-2">
                        🍽️ Take 15-30 minutes after your meal with water
                    </p>
                </div>
                <div class="flex items-center gap-2 pt-2">
                    <button onclick="takeMedicine(); closeLiveAlertModal();" class="flex-1 py-3 bg-emerald-600 hover:bg-emerald-700 text-white font-black rounded-xl text-xs shadow-lg transition flex items-center justify-center gap-1.5 cursor-pointer">
                        <i class="fa-solid fa-check"></i> I Took It Now
                    </button>
                    <button onclick="snoozeMedicine(null, 10); closeLiveAlertModal();" class="flex-1 py-3 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold rounded-xl text-xs transition flex items-center justify-center gap-1.5 cursor-pointer">
                        <i class="fa-solid fa-bell"></i> Snooze 10 Mins
                    </button>
                </div>
                <button onclick="closeLiveAlertModal()" class="text-xs text-slate-400 hover:text-slate-600">Dismiss</button>
            </div>
        `;
        document.body.appendChild(modal);
    }

    if (medName) {
        const medEl = document.getElementById('alert-modal-med');
        if (medEl) medEl.innerText = `${medName} ${dosage || ''}`;
    }
    if (foodAdvice) {
        const foodEl = document.getElementById('alert-modal-food');
        if (foodEl) foodEl.innerText = `🍽️ ${foodAdvice}`;
    }
    modal.classList.remove('hidden');
}

function closeLiveAlertModal() {
    const modal = document.getElementById('live-medicine-alert-modal');
    if (modal) modal.classList.add('hidden');
}

function showDoseDueAlert(medName, dosage) {
    if (window.Notification && Notification.permission === "granted") {
        new Notification(`⏰ Medicine Due: ${medName || 'Metformin'} ${dosage || '500mg'}`, {
            body: "Exact time to take your scheduled dose. Tap to confirm taken or snooze.",
            icon: "https://cdn-icons-png.flaticon.com/512/883/883407.png"
        });
    }
}

// Action: Mark Taken
async function takeMedicine(scheduleId) {
    const id = scheduleId || currentScheduleId;
    if (!id) return;

    try {
        const resp = await fetch(`/api/medicines/take/${id}`, { method: 'POST' });
        const data = await resp.json();
        
        if (data.status === 'SUCCESS') {
            showToast("✓ Dose recorded as TAKEN. Adherence updated to " + data.adherencePercentage + "%!", "success");
            playAlertChime();
            setTimeout(() => window.location.reload(), 1200);
        }
    } catch (e) {
        console.error(e);
        showToast("Error updating dose", "error");
    }
}

// Action: Snooze
async function snoozeMedicine(scheduleId, minutes = 10) {
    const id = scheduleId || currentScheduleId;
    if (!id) return;

    try {
        const resp = await fetch(`/api/medicines/snooze/${id}?minutes=${minutes}`, { method: 'POST' });
        const data = await resp.json();

        if (data.status === 'SUCCESS') {
            showToast(`⏰ Snoozed for ${minutes} minutes. Next alert set.`, "info");
            initMedicineCountdown(minutes * 60, id);
        }
    } catch (e) {
        console.error(e);
        showToast("Error snoozing dose", "error");
    }
}

// Action: Skip with Safe Guidance
function openSkipModal(scheduleId) {
    const modal = document.getElementById('skip-dose-modal');
    if (modal) {
        modal.classList.remove('hidden');
        document.getElementById('skip-schedule-id').value = scheduleId || currentScheduleId;
    }
}

function closeSkipModal() {
    const modal = document.getElementById('skip-dose-modal');
    if (modal) modal.classList.add('hidden');
}

async function confirmSkipDose() {
    const scheduleId = document.getElementById('skip-schedule-id').value;
    const reason = document.getElementById('skip-reason-select').value;
    closeSkipModal();

    try {
        const resp = await fetch(`/api/medicines/skip/${scheduleId}?reason=${encodeURIComponent(reason)}`, { method: 'POST' });
        const data = await resp.json();

        // Display clinical safety directive prominently
        showSafetyAlertModal(data.safetyDirective || "Do not take an extra dose at your next scheduled time.");
    } catch (e) {
        console.error(e);
        showToast("Error recording skipped dose", "error");
    }
}

function showSafetyAlertModal(directiveText) {
    const modal = document.getElementById('safety-directive-modal');
    if (modal) {
        document.getElementById('safety-directive-text').innerText = directiveText;
        modal.classList.remove('hidden');
    } else {
        alert(directiveText);
        window.location.reload();
    }
}

function closeSafetyAlertModal() {
    const modal = document.getElementById('safety-directive-modal');
    if (modal) modal.classList.add('hidden');
    window.location.reload();
}

// Universal toast helper
function showToast(msg, type = "info") {
    let toast = document.getElementById('platform-toast');
    if (!toast) {
        toast = document.createElement('div');
        toast.id = 'platform-toast';
        toast.className = 'fixed bottom-6 right-6 z-50 px-5 py-3 rounded-xl shadow-2xl text-white font-medium flex items-center gap-3 transition-all duration-300';
        document.body.appendChild(toast);
    }
    
    if (type === "success") toast.style.backgroundColor = "#059669";
    else if (type === "error") toast.style.backgroundColor = "#dc2626";
    else toast.style.backgroundColor = "#0284c7";

    toast.innerText = msg;
    toast.style.opacity = '1';
    toast.style.transform = 'translateY(0)';

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(20px)';
    }, 4000);
}

// Request browser notification permissions on first interaction
document.addEventListener('DOMContentLoaded', () => {
    if (window.Notification && Notification.permission === "default") {
        Notification.requestPermission();
    }
});
