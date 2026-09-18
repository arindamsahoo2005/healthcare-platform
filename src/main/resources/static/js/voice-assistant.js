/**
 * Voice Healthcare Engine
 * Supports Web Speech API for voice queries and audio responses across English and Indian Languages
 */

let speechRecognizer = null;
let isListening = false;

function initVoiceAssistant() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
        console.warn("Web Speech API not supported on this browser.");
        return;
    }

    speechRecognizer = new SpeechRecognition();
    speechRecognizer.continuous = false;
    speechRecognizer.interimResults = false;

    speechRecognizer.onstart = () => {
        isListening = true;
        const btn = document.getElementById('voice-assistant-btn');
        if (btn) btn.classList.add('bg-red-500', 'text-white', 'animate-pulse');
        const status = document.getElementById('voice-status-text');
        if (status) status.innerText = "Listening to your voice... Speak now.";
    };

    speechRecognizer.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        console.log("Voice Transcript:", transcript);
        const status = document.getElementById('voice-status-text');
        if (status) status.innerText = `Heard: "${transcript}"`;

        // Pass voice text directly to AI Chat or Search
        handleVoiceCommand(transcript);
    };

    speechRecognizer.onerror = (event) => {
        console.warn("Speech error:", event.error);
        stopVoiceListening();
    };

    speechRecognizer.onend = () => {
        stopVoiceListening();
    };
}

function toggleVoiceAssistant() {
    if (!speechRecognizer) initVoiceAssistant();
    if (!speechRecognizer) {
        alert("Voice speech recognition is not supported in this browser environment. You can use text chat.");
        return;
    }

    if (isListening) {
        speechRecognizer.stop();
        stopVoiceListening();
    } else {
        const lang = localStorage.getItem('site_lang') || 'en';
        // Map language code to BCP 47 tags
        const langMap = {
            'en': 'en-IN', 'hi': 'hi-IN', 'bn': 'bn-IN', 'or': 'or-IN',
            'mr': 'mr-IN', 'ta': 'ta-IN', 'te': 'te-IN', 'kn': 'kn-IN',
            'gu': 'gu-IN', 'pa': 'pa-IN', 'ur': 'ur-IN'
        };
        speechRecognizer.lang = langMap[lang] || 'en-IN';
        try {
            speechRecognizer.start();
        } catch (e) {
            console.error(e);
        }
    }
}

function stopVoiceListening() {
    isListening = false;
    const btn = document.getElementById('voice-assistant-btn');
    if (btn) btn.classList.remove('bg-red-500', 'text-white', 'animate-pulse');
    const status = document.getElementById('voice-status-text');
    if (status && !status.innerText.startsWith("Heard:")) {
        status.innerText = "Tap microphone to speak";
    }
}

async function handleVoiceCommand(spokenText) {
    const text = spokenText.toLowerCase();

    // Consequential action confirmation safety: Confirm before booking or dispatch
    if (text.includes("call ambulance") || text.includes("emergency") || text.includes("sos")) {
        if (confirm(`Emergency Voice Request detected: "${spokenText}". Dispatch ambulance to current location?`)) {
            window.location.href = '/emergency';
        }
        return;
    }

    if (text.includes("medicine") || text.includes("timer") || text.includes("pill") || text.includes("dose")) {
        window.location.href = '/medicine-timer';
        return;
    }

    if (text.includes("doctor") || text.includes("specialist") || text.includes("cardiologist")) {
        window.location.href = '/doctors';
        return;
    }

    if (text.includes("hospital") || text.includes("admission") || text.includes("bed")) {
        window.location.href = '/hospitals';
        return;
    }

    // Default: Route spoken query to AI Health Assistant
    if (window.location.pathname !== '/ai-assistant') {
        window.location.href = `/ai-assistant?query=${encodeURIComponent(spokenText)}`;
    } else {
        const chatInput = document.getElementById('ai-chat-input');
        if (chatInput) {
            chatInput.value = spokenText;
            sendAiMessage();
        }
    }
}

function speakText(textToSpeak, langCode = 'en') {
    if (!window.speechSynthesis) return;
    window.speechSynthesis.cancel(); // Stop any previous speech

    // Clean markdown formatting before speaking
    const cleanText = textToSpeak.replace(/[\*\_#`]/g, '');

    const utterance = new SpeechSynthesisUtterance(cleanText);
    const langMap = {
        'en': 'en-IN', 'hi': 'hi-IN', 'bn': 'bn-IN', 'or': 'or-IN',
        'mr': 'mr-IN', 'ta': 'ta-IN', 'te': 'te-IN', 'kn': 'kn-IN'
    };
    utterance.lang = langMap[langCode] || 'en-IN';
    utterance.rate = 0.95;
    window.speechSynthesis.speak(utterance);
}
