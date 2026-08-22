package com.example.data.model

import java.util.Locale

data class LanguageVoiceOption(
    val languageCode: String,      // e.g. "en", "es", "fr", "de", "hi", "ja", "zh", "ar", "pt", "it", "ko"
    val locale: Locale,
    val displayName: String,       // e.g. "English (US)"
    val nativeName: String,        // e.g. "English"
    val flagEmoji: String,         // e.g. "🇺🇸"
    val sampleGreeting: String,    // Spoken sample
    val supportedPersonas: List<VoicePersona>
)

data class VoicePersona(
    val id: String,
    val name: String,
    val title: String,
    val styleDescription: String,
    val defaultPitch: Float = 1.0f,
    val defaultSpeechRate: Float = 1.0f,
    val avatarIcon: String = "👨‍🏫"
)

data class VoiceAccent(
    val id: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val locale: Locale,
    val pitchMultiplier: Float = 1.0f,
    val speedMultiplier: Float = 1.0f,
    val description: String,
    val sampleText: String
)

object VoiceAccentCatalog {
    val US_NATURAL = VoiceAccent(
        id = "us_natural",
        name = "US Natural Conversational",
        country = "United States",
        flagEmoji = "🇺🇸",
        locale = Locale.US,
        pitchMultiplier = 1.0f,
        speedMultiplier = 1.0f,
        description = "Smooth American conversational rhythm with crisp consonants.",
        sampleText = "Hello! Let's break down this concept using first-principles thinking."
    )

    val UK_OXFORD = VoiceAccent(
        id = "uk_oxford",
        name = "UK Oxford RP Academic",
        country = "United Kingdom",
        flagEmoji = "🇬🇧",
        locale = Locale.UK,
        pitchMultiplier = 0.98f,
        speedMultiplier = 0.95f,
        description = "Articulate British academic cadence with clear scholarly cadence.",
        sampleText = "Good day. Let us examine the fundamental mechanics of this equation."
    )

    val IN_ENGLISH = VoiceAccent(
        id = "in_english",
        name = "Indian English Academic",
        country = "India",
        flagEmoji = "🇮🇳",
        locale = Locale("en", "IN"),
        pitchMultiplier = 1.05f,
        speedMultiplier = 1.0f,
        description = "Clear syllable-timed pacing ideal for structured STEM problem-solving.",
        sampleText = "Welcome! Let us solve this step-by-step to build complete clarity."
    )

    val AU_SCHOLASTIC = VoiceAccent(
        id = "au_scholastic",
        name = "Australian Scholastic",
        country = "Australia",
        flagEmoji = "🇦🇺",
        locale = Locale("en", "AU"),
        pitchMultiplier = 1.02f,
        speedMultiplier = 0.98f,
        description = "Warm energetic inflection with clear vowel pronunciation.",
        sampleText = "G'day! Ready to master today's lesson and ace your exams?"
    )

    val CA_STUDIO = VoiceAccent(
        id = "ca_studio",
        name = "Canadian Clear Studio",
        country = "Canada",
        flagEmoji = "🇨🇦",
        locale = Locale.CANADA,
        pitchMultiplier = 1.0f,
        speedMultiplier = 0.96f,
        description = "Neutral studio-grade acoustic resonance for high retention.",
        sampleText = "Welcome to your study session. Let's explore the core insights."
    )

    val CYBER_SYNTH = VoiceAccent(
        id = "cyber_synth",
        name = "Cybernetic Neural Synth",
        country = "AI Core",
        flagEmoji = "🤖",
        locale = Locale.US,
        pitchMultiplier = 0.88f,
        speedMultiplier = 1.05f,
        description = "Futuristic AI synthesizer voice for sci-fi immersive study.",
        sampleText = "System online. Initializing quantum neural pedagogical matrix."
    )

    val ALL_ACCENTS = listOf(
        US_NATURAL,
        UK_OXFORD,
        IN_ENGLISH,
        AU_SCHOLASTIC,
        CA_STUDIO,
        CYBER_SYNTH
    )

    val DEFAULT_ACCENT = US_NATURAL

    fun findAccentById(id: String): VoiceAccent {
        return ALL_ACCENTS.firstOrNull { it.id == id } ?: DEFAULT_ACCENT
    }
}

object VoiceCatalog {
    val PERSONA_SOPHIA = VoicePersona(
        id = "sophia",
        name = "Dr. Sophia Vance",
        title = "Structured & Analytical",
        styleDescription = "Clear step-by-step breakdown with intuitive analogies.",
        defaultPitch = 1.05f,
        defaultSpeechRate = 0.95f,
        avatarIcon = "👩‍🏫"
    )

    val PERSONA_MARCUS = VoicePersona(
        id = "marcus",
        name = "Prof. Marcus Reid",
        title = "Deep-Dive Academic",
        styleDescription = "Focuses on first-principles, definitions, and exam rigor.",
        defaultPitch = 0.9f,
        defaultSpeechRate = 0.95f,
        avatarIcon = "👨‍🏫"
    )

    val PERSONA_MAYA = VoicePersona(
        id = "maya",
        name = "Maya - High-Energy Mentor",
        title = "Fast & Motivational",
        styleDescription = "Simplifies heavy formulas into easy mnemonic memory aids.",
        defaultPitch = 1.15f,
        defaultSpeechRate = 1.1f,
        avatarIcon = "🚀"
    )

    val PERSONA_ALEX = VoicePersona(
        id = "alex",
        name = "Alex - Socratic Coach",
        title = "Conversational & Friendly",
        styleDescription = "Explains concepts like a study buddy with active check-ins.",
        defaultPitch = 1.0f,
        defaultSpeechRate = 1.0f,
        avatarIcon = "💡"
    )

    val ALL_PERSONAS = listOf(PERSONA_SOPHIA, PERSONA_MARCUS, PERSONA_MAYA, PERSONA_ALEX)

    val LANGUAGES: List<LanguageVoiceOption> = listOf(
        LanguageVoiceOption(
            languageCode = "en",
            locale = Locale.US,
            displayName = "English",
            nativeName = "English (US)",
            flagEmoji = "🇺🇸",
            sampleGreeting = "Hello! I am your AI study tutor. What concept would you like to master today?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "es",
            locale = Locale("es", "ES"),
            displayName = "Spanish",
            nativeName = "Español",
            flagEmoji = "🇪🇸",
            sampleGreeting = "¡Hola! Soy tu tutor de IA. ¿Qué concepto de este capítulo quieres que te explique?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "fr",
            locale = Locale.FRENCH,
            displayName = "French",
            nativeName = "Français",
            flagEmoji = "🇫🇷",
            sampleGreeting = "Bonjour ! Je suis votre tuteur IA. Quel concept souhaitez-vous approfondir aujourd'hui ?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "de",
            locale = Locale.GERMAN,
            displayName = "German",
            nativeName = "Deutsch",
            flagEmoji = "🇩🇪",
            sampleGreeting = "Hallo! Ich bin dein KI-Tutor. Welches Thema möchtest du heute gemeinsam durchgehen?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "hi",
            locale = Locale("hi", "IN"),
            displayName = "Hindi",
            nativeName = "हिन्दी",
            flagEmoji = "🇮🇳",
            sampleGreeting = "नमस्ते! मैं आपका AI ट्यूटर हूँ। आज आप कौन सा टॉपिक या कॉन्सेप्ट समझना चाहते हैं?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "ja",
            locale = Locale.JAPANESE,
            displayName = "Japanese",
            nativeName = "日本語",
            flagEmoji = "🇯🇵",
            sampleGreeting = "こんにちは！AI家庭教師です。どの分野の概念をわかりやすく解説しましょうか？",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "zh",
            locale = Locale.SIMPLIFIED_CHINESE,
            displayName = "Chinese (Mandarin)",
            nativeName = "中文",
            flagEmoji = "🇨🇳",
            sampleGreeting = "你好！我是你的AI导师。今天想让我为你讲解哪个章节的核心概念？",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "ar",
            locale = Locale("ar"),
            displayName = "Arabic",
            nativeName = "العربية",
            flagEmoji = "🇸🇦",
            sampleGreeting = "مرحباً! أنا معلمك الذكي. ما هو المفهوم الذي تود أن أشرحه لك اليوم؟",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "pt",
            locale = Locale("pt", "BR"),
            displayName = "Portuguese",
            nativeName = "Português",
            flagEmoji = "🇧🇷",
            sampleGreeting = "Olá! Sou seu tutor de IA. Qual conceito deste capítulo você gostaria de aprender?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "it",
            locale = Locale.ITALIAN,
            displayName = "Italian",
            nativeName = "Italiano",
            flagEmoji = "🇮🇹",
            sampleGreeting = "Ciao! Sono il tuo tutor IA. Quale argomento o concetto vorresti approfondire oggi?",
            supportedPersonas = ALL_PERSONAS
        ),
        LanguageVoiceOption(
            languageCode = "ko",
            locale = Locale.KOREAN,
            displayName = "Korean",
            nativeName = "한국어",
            flagEmoji = "🇰🇷",
            sampleGreeting = "안녕하세요! AI 학습 튜터입니다. 오늘 어떤 개념을 함께 공부할까요?",
            supportedPersonas = ALL_PERSONAS
        )
    )
}
