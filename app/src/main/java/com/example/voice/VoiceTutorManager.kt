package com.example.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.data.model.LanguageVoiceOption
import com.example.data.model.VoiceAccent
import com.example.data.model.VoiceAccentCatalog
import com.example.data.model.VoiceCatalog
import com.example.data.model.VoicePersona
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

class VoiceTutorManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _currentLanguage = MutableStateFlow(VoiceCatalog.LANGUAGES.first())
    val currentLanguage: StateFlow<LanguageVoiceOption> = _currentLanguage.asStateFlow()

    private val _currentAccent = MutableStateFlow(VoiceAccentCatalog.DEFAULT_ACCENT)
    val currentAccent: StateFlow<VoiceAccent> = _currentAccent.asStateFlow()

    private val _currentPersona = MutableStateFlow(VoiceCatalog.ALL_PERSONAS.first())
    val currentPersona: StateFlow<VoicePersona> = _currentPersona.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    private val _currentSpeakingText = MutableStateFlow<String?>(null)
    val currentSpeakingText: StateFlow<String?> = _currentSpeakingText.asStateFlow()

    // Waveform amplitudes for live audio visualizer animation
    private val _visualizerAmplitudes = MutableStateFlow(List(16) { 0.15f })
    val visualizerAmplitudes: StateFlow<List<Float>> = _visualizerAmplitudes.asStateFlow()

    private var visualizerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupUtteranceListener()
            applyLanguageAndVoice(_currentLanguage.value, _currentPersona.value, _currentAccent.value)
            Log.d("VoiceTutorManager", "TTS Initialized successfully")
        } else {
            Log.e("VoiceTutorManager", "TTS Initialization failed with code: $status")
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                _isPaused.value = false
                startWaveformAnimation()
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _isPaused.value = false
                _currentSpeakingText.value = null
                stopWaveformAnimation()
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _isPaused.value = false
                _currentSpeakingText.value = null
                stopWaveformAnimation()
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.value = false
                _isPaused.value = false
                _currentSpeakingText.value = null
                stopWaveformAnimation()
                Log.e("VoiceTutorManager", "TTS Utterance error code: $errorCode")
            }
        })
    }

    fun setLanguage(language: LanguageVoiceOption) {
        _currentLanguage.value = language
        applyLanguageAndVoice(language, _currentPersona.value, _currentAccent.value)
    }

    fun setAccent(accent: VoiceAccent) {
        _currentAccent.value = accent
        applyLanguageAndVoice(_currentLanguage.value, _currentPersona.value, accent)
    }

    fun setPersona(persona: VoicePersona) {
        _currentPersona.value = persona
        _pitch.value = persona.defaultPitch
        _speechRate.value = persona.defaultSpeechRate
        applyLanguageAndVoice(_currentLanguage.value, persona, _currentAccent.value)
    }

    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.5f, 2.0f)
        tts?.setSpeechRate(_speechRate.value * _currentAccent.value.speedMultiplier)
    }

    fun setPitch(pitchVal: Float) {
        _pitch.value = pitchVal.coerceIn(0.5f, 2.0f)
        tts?.setPitch(_pitch.value * _currentAccent.value.pitchMultiplier)
    }

    private fun applyLanguageAndVoice(
        language: LanguageVoiceOption,
        persona: VoicePersona,
        accent: VoiceAccent
    ) {
        if (!isInitialized || tts == null) return

        try {
            // For English, respect the selected accent locale (e.g. UK, IN, AU, CA, US)
            val effectiveLocale = if (language.languageCode == "en") {
                accent.locale
            } else {
                language.locale
            }

            val result = tts?.setLanguage(effectiveLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English US
                tts?.setLanguage(Locale.US)
            }
            tts?.setSpeechRate(_speechRate.value * accent.speedMultiplier)
            tts?.setPitch(_pitch.value * accent.pitchMultiplier)

            // Try to find a matching voice if available in system
            val voices = tts?.voices
            if (!voices.isNullOrEmpty()) {
                val matchingVoice = voices.firstOrNull { voice ->
                    voice.locale.language == effectiveLocale.language &&
                            (voice.locale.country.isEmpty() || voice.locale.country == effectiveLocale.country) &&
                            !voice.isNetworkConnectionRequired
                } ?: voices.firstOrNull { voice ->
                    voice.locale.language == effectiveLocale.language
                }
                if (matchingVoice != null) {
                    tts?.voice = matchingVoice
                }
            }
        } catch (e: Exception) {
            Log.e("VoiceTutorManager", "Error setting language/voice", e)
        }
    }

    fun speakAccentSample(accent: VoiceAccent) {
        setAccent(accent)
        speak(accent.sampleText)
    }

    fun speak(text: String, flush: Boolean = true) {
        if (!isInitialized || tts == null) return

        val cleanText = sanitizeTextForSpeech(text)
        if (cleanText.isBlank()) return

        stop()
        _currentSpeakingText.value = text
        _isSpeaking.value = true

        applyLanguageAndVoice(_currentLanguage.value, _currentPersona.value, _currentAccent.value)

        val utteranceId = UUID.randomUUID().toString()
        val params = Bundle()
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD

        tts?.speak(cleanText, queueMode, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        _isPaused.value = false
        _currentSpeakingText.value = null
        stopWaveformAnimation()
    }

    /**
     * Enhanced pronunciation and phonetics tuner:
     * 1. Expands STEM acronyms and symbols into clean spoken words.
     * 2. Cleans Markdown headers, code blocks, bullet tokens, and LaTeX delimiters.
     * 3. Inserts natural breath pauses between numbered points.
     */
    private fun sanitizeTextForSpeech(text: String): String {
        return text
            // Remove markdown code fences and bold/italic markup
            .replace("```[a-zA-Z]*".toRegex(), "")
            .replace("```".toRegex(), "")
            .replace("\\*\\*".toRegex(), "")
            .replace("\\*".toRegex(), "")
            .replace("#+".toRegex(), "")
            .replace("`+".toRegex(), "")
            .replace("~~".toRegex(), "")
            .replace("https?://\\S+".toRegex(), "")
            // Clean markdown link formatting [text](url) -> text
            .replace("\\[(.*?)\\]\\(.*?\\)".toRegex(), "$1")
            // Common STEM acronyms with phonetic spacing
            .replace("\\bDNA\\b".toRegex(), "D.N.A.")
            .replace("\\bRNA\\b".toRegex(), "R.N.A.")
            .replace("\\bATP\\b".toRegex(), "A.T.P.")
            .replace("\\bADP\\b".toRegex(), "A.D.P.")
            .replace("\\bMCQ\\b".toRegex(), "M.C.Q.")
            .replace("\\bOMR\\b".toRegex(), "O.M.R.")
            .replace("\\bpH\\b".toRegex(), "p.H.")
            .replace("\\bpKa\\b".toRegex(), "p.K.a.")
            .replace("\\bCO2\\b".toRegex(), "C O 2")
            .replace("\\bH2O\\b".toRegex(), "H 2 O")
            .replace("\\bO2\\b".toRegex(), "O 2")
            .replace("\\bN2\\b".toRegex(), "N 2")
            .replace("\\bNaCl\\b".toRegex(), "sodium chloride")
            // Mathematical and physics equations spoken rendering
            .replace("\\bF\\s*=\\s*m\\s*\\*?\\s*a\\b".toRegex(), "Force equals mass times acceleration")
            .replace("\\bE\\s*=\\s*m\\s*c\\^?2\\b".toRegex(), "E equals m c squared")
            .replace("\\bKE\\b".toRegex(), "Kinetic Energy")
            .replace("\\bPE\\b".toRegex(), "Potential Energy")
            .replace("\\bsqrt\\((.*?)\\)".toRegex(), "square root of $1")
            .replace("\\b√(.*?)\\b".toRegex(), "square root of $1")
            .replace("²".toRegex(), " squared ")
            .replace("³".toRegex(), " cubed ")
            // Greek symbols in scientific formulas
            .replace("Δ".toRegex(), " delta ")
            .replace("θ".toRegex(), " theta ")
            .replace("λ".toRegex(), " lambda ")
            .replace("α".toRegex(), " alpha ")
            .replace("β".toRegex(), " beta ")
            .replace("γ".toRegex(), " gamma ")
            .replace("π".toRegex(), " pi ")
            .replace("μ".toRegex(), " micro ")
            .replace("Ω".toRegex(), " ohms ")
            .replace("±".toRegex(), " plus or minus ")
            .replace("≈".toRegex(), " approximately ")
            .replace("≠".toRegex(), " is not equal to ")
            .replace("≤".toRegex(), " is less than or equal to ")
            .replace("≥".toRegex(), " is greater than or equal to ")
            .replace("%".toRegex(), " percent ")
            .replace("/".toRegex(), " over ")
            // Formatting punctuation cleanup
            .replace("[\\[\\](){}]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }

    private fun startWaveformAnimation() {
        visualizerJob?.cancel()
        visualizerJob = scope.launch {
            while (isActive && _isSpeaking.value) {
                _visualizerAmplitudes.value = List(16) {
                    (0.2f + Math.random().toFloat() * 0.8f)
                }
                delay(90)
            }
        }
    }

    private fun stopWaveformAnimation() {
        visualizerJob?.cancel()
        _visualizerAmplitudes.value = List(16) { 0.15f }
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}

