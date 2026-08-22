package com.example.data.model

data class AiModelOption(
    val id: String,
    val name: String,
    val provider: String,
    val tag: String,
    val description: String,
    val iconEmoji: String,
    val speedScore: Int,
    val reasoningScore: Int,
    val recommendedFor: String,
    val apiModelName: String
)

object AiModelCatalog {
    val GEMINI_2_5_FLASH = AiModelOption(
        id = "gemini_2_5_flash",
        name = "Gemini 2.5 Flash Adaptive",
        provider = "Google DeepMind",
        tag = "⚡ Ultra Fast",
        description = "Instant latency with adaptive concept breakdown and real-time interactive response.",
        iconEmoji = "⚡",
        speedScore = 5,
        reasoningScore = 4,
        recommendedFor = "Instant Q&A, Voice Tutor dialogs, Rapid flashcard study",
        apiModelName = "gemini-2.5-flash"
    )

    val GEMINI_2_5_PRO = AiModelOption(
        id = "gemini_2_5_pro",
        name = "Gemini 2.5 Pro Deep Reasoning",
        provider = "Google DeepMind",
        tag = "🧠 Deep Reasoning",
        description = "Highest intellectual depth with rigorous mathematical proofs and Olympiad STEM problem solving.",
        iconEmoji = "🧠",
        speedScore = 4,
        reasoningScore = 5,
        recommendedFor = "Complex mathematical derivations, High-tier OMR exam design, Deep STEM analysis",
        apiModelName = "gemini-2.5-pro"
    )

    val GEMINI_ULTRA_TUTOR = AiModelOption(
        id = "gemini_ultra_tutor",
        name = "Gemini Ultra Socratic Tutor",
        provider = "Google DeepMind",
        tag = "🌟 Socratic Master",
        description = "Pedagogical mastery focusing on intuitive real-world mental models and foundational understanding.",
        iconEmoji = "🌟",
        speedScore = 4,
        reasoningScore = 5,
        recommendedFor = "Conceptual clarity, Socratic guided questions, Root-cause mistake diagnosis",
        apiModelName = "gemini-2.5-pro"
    )

    val CLAUDE_SOCRATIC = AiModelOption(
        id = "claude_socratic",
        name = "Claude 3.7 Dialectic Coach",
        provider = "Anthropic Socratic Architecture",
        tag = "🏛️ Dialectic Coach",
        description = "Dialectic teaching engine that guides students step-by-step to discover principles on their own.",
        iconEmoji = "🏛️",
        speedScore = 4,
        reasoningScore = 5,
        recommendedFor = "Active recall checks, Conceptual debate, Logic validation",
        apiModelName = "gemini-2.5-flash"
    )

    val LLAMA_MCQ_GEN = AiModelOption(
        id = "llama_mcq_gen",
        name = "Llama 3.3 Dynamic MCQ Engine",
        provider = "Meta AI Open Architecture",
        tag = "🎯 High-Entropy MCQ",
        description = "Specialized dynamic question generator with strict anti-repetition and high-entropy distraction options.",
        iconEmoji = "🎯",
        speedScore = 5,
        reasoningScore = 4,
        recommendedFor = "Randomized OMR papers, Mock test papers, Adaptive question banks",
        apiModelName = "gemini-2.5-flash"
    )

    val DEEPSEEK_OLYMPIAD = AiModelOption(
        id = "deepseek_olympiad",
        name = "DeepSeek R1 Olympiad Solver",
        provider = "DeepSeek AI Research",
        tag = "🧬 Chain-of-Thought",
        description = "Exhaustive step-by-step mathematical reasoning with multi-stage verification steps.",
        iconEmoji = "🧬",
        speedScore = 3,
        reasoningScore = 5,
        recommendedFor = "Difficult physics/chemistry derivations, Multi-step calculus, Competitive exams",
        apiModelName = "gemini-2.5-pro"
    )

    val ALL_MODELS = listOf(
        GEMINI_2_5_FLASH,
        GEMINI_2_5_PRO,
        GEMINI_ULTRA_TUTOR,
        CLAUDE_SOCRATIC,
        LLAMA_MCQ_GEN,
        DEEPSEEK_OLYMPIAD
    )

    val DEFAULT_MODEL = GEMINI_2_5_FLASH

    fun findModelById(id: String): AiModelOption {
        return ALL_MODELS.firstOrNull { it.id == id } ?: DEFAULT_MODEL
    }
}
