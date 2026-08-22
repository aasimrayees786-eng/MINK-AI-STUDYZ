package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChapterNote
import com.example.data.model.ConceptItem
import com.example.data.model.FlashcardItem
import com.example.data.model.HologramVisualType
import com.example.data.model.ImprovementGuideItem
import com.example.data.model.LanguageVoiceOption
import com.example.data.model.PresetRobotLessons
import com.example.data.model.QuizQuestionItem
import com.example.data.model.RobotEmotion
import com.example.data.model.RobotGesture
import com.example.data.model.RobotLesson
import com.example.data.model.RobotSpeechCue
import com.example.data.model.SampleChapterPreset
import com.example.data.model.SampleChaptersData
import com.example.data.model.SnapPresetQuestion
import com.example.data.model.SnapSolutionRecord
import com.example.data.model.SolutionStep
import com.example.data.model.TutorMessage
import com.example.data.model.VocabItem
import com.example.data.model.VoiceCatalog
import com.example.data.model.VoicePersona
import com.example.data.remote.ChapterRepository
import com.example.data.remote.RetrofitClient
import com.example.voice.VoiceTutorManager
import com.squareup.moshi.Types
import org.json.JSONObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String, val icon: String) {
    HOME("Home", "🏠"),
    OMR_CHALLENGE("OMR Arena", "🎯"),
    VOICE_TUTOR("AI Tutor", "🎙️"),
    PROGRESS("Progress", "📈"),
    PREMIUM("AI Premium", "👑"),
    ROBOT_VIDEO("Robot Video", "🤖"),
    SNAP_SOLVE("Snap & Solve", "📸"),
    GENERATE("Make Notes", "📝"),
    LIBRARY("My Notes", "📚")
}

enum class NoteDetailTab(val label: String) {
    SHORT_NOTES("Short Notes"),
    KEY_POINTS("Key Points"),
    DEFINITIONS("Definitions"),
    FORMULAS("Important Formulas"),
    FLASHCARDS("Flashcards"),
    QUIZ("Active Quiz"),
    IMPROVEMENT("Improvement Guide")
}

class ChapterAIViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repository = ChapterRepository(
        db.chapterNoteDao(),
        db.tutorChatDao(),
        db.snapSolutionDao(),
        db.omrTestDao()
    )
    val voiceManager = VoiceTutorManager(application)
    private val userPrefs = com.example.data.local.UserPreferencesDataStore(application)

    // Dynamic Theme Selector State (Space, Nature, Ghost, Cyberpunk, Solar)
    private val _selectedThemeMode = MutableStateFlow(com.example.ui.theme.AppThemeMode.SPACE)
    val selectedThemeMode: StateFlow<com.example.ui.theme.AppThemeMode> = _selectedThemeMode.asStateFlow()

    // Multi-AI Model Selection State
    private val _selectedAiModel = MutableStateFlow(com.example.data.model.AiModelCatalog.DEFAULT_MODEL)
    val selectedAiModel: StateFlow<com.example.data.model.AiModelOption> = _selectedAiModel.asStateFlow()

    // Voice Accent State
    private val _selectedAccent = MutableStateFlow(com.example.data.model.VoiceAccentCatalog.DEFAULT_ACCENT)
    val selectedAccent: StateFlow<com.example.data.model.VoiceAccent> = _selectedAccent.asStateFlow()

    // Anti-Repetition Question History Tracker
    val sessionGeneratedQuestionsHistory = MutableStateFlow<List<String>>(emptyList())

    init {
        // Collect persisted user preferences
        viewModelScope.launch {
            userPrefs.themeModeFlow.collect { savedTheme ->
                _selectedThemeMode.value = savedTheme
            }
        }
        viewModelScope.launch {
            userPrefs.aiModelFlow.collect { savedModel ->
                _selectedAiModel.value = savedModel
            }
        }
        viewModelScope.launch {
            userPrefs.voiceAccentFlow.collect { savedAccent ->
                _selectedAccent.value = savedAccent
                voiceManager.setAccent(savedAccent)
            }
        }
    }

    fun setThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        _selectedThemeMode.value = mode
        viewModelScope.launch {
            userPrefs.saveThemeMode(mode)
        }
    }

    fun setAiModel(model: com.example.data.model.AiModelOption) {
        _selectedAiModel.value = model
        viewModelScope.launch {
            userPrefs.saveAiModel(model)
        }
    }

    fun setVoiceAccent(accent: com.example.data.model.VoiceAccent) {
        _selectedAccent.value = accent
        voiceManager.setAccent(accent)
        viewModelScope.launch {
            userPrefs.saveVoiceAccent(accent)
        }
    }

    // Robot Video Teacher Presenter State
    val robotLessonsList = MutableStateFlow<List<RobotLesson>>(PresetRobotLessons.ALL_LESSONS)
    val activeRobotLesson = MutableStateFlow<RobotLesson>(PresetRobotLessons.LESSON_BONES)
    val activeRobotCueIndex = MutableStateFlow(0)
    val isPlayingRobotVideo = MutableStateFlow(false)
    val robotVideoSpeed = MutableStateFlow(1.0f)
    val isGeneratingRobotLesson = MutableStateFlow(false)
    val robotLessonGenerationStage = MutableStateFlow("")
    val robotPromptInput = MutableStateFlow("")
    val robotSubjectInput = MutableStateFlow("Biology")
    val robotSpokenWordHighlight = MutableStateFlow("")
    val robotErrorMessage = MutableStateFlow<String?>(null)
    private var robotPlaybackJob: kotlinx.coroutines.Job? = null

    // Navigation State
    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _selectedNoteId = MutableStateFlow<Long?>(null)
    val selectedNoteId: StateFlow<Long?> = _selectedNoteId.asStateFlow()

    private val _selectedDetailTab = MutableStateFlow(NoteDetailTab.SHORT_NOTES)
    val selectedDetailTab: StateFlow<NoteDetailTab> = _selectedDetailTab.asStateFlow()

    // Snap & Solve State
    val capturedPhoto = MutableStateFlow<Bitmap?>(null)
    val snapQuestionInput = MutableStateFlow("")
    val snapSubject = MutableStateFlow("Mathematics")
    val isSolvingSnap = MutableStateFlow(false)
    val snapSolvingStage = MutableStateFlow("")
    val snapErrorMessage = MutableStateFlow<String?>(null)
    val activeSnapSolution = MutableStateFlow<SnapSolutionRecord?>(null)

    val allSnapSolutions: StateFlow<List<SnapSolutionRecord>> = repository.allSnapSolutions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Generator Input State
    val chapterInputText = MutableStateFlow("")
    val chapterSubject = MutableStateFlow("Biology")
    val noteDepth = MutableStateFlow("Comprehensive") // "Quick Summary", "Comprehensive", "Exam Deep Dive"
    
    // Upload Document / PDF State
    val uploadedDocumentName = MutableStateFlow<String?>(null)
    val uploadedDocumentSize = MutableStateFlow<String?>(null)
    val uploadedDocumentType = MutableStateFlow<String?>(null)

    val isGeneratingNote = MutableStateFlow(false)
    val generationProgressMessage = MutableStateFlow("")
    val errorMessage = MutableStateFlow<String?>(null)

    // Notes Library Filters
    val searchQuery = MutableStateFlow("")
    val selectedSubjectFilter = MutableStateFlow("All")
    val showOnlyFavorites = MutableStateFlow(false)

    // All Notes from DB
    val allNotes: StateFlow<List<ChapterNote>> = repository.allNotes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered Notes
    val filteredNotes: StateFlow<List<ChapterNote>> = combine(
        allNotes,
        searchQuery,
        selectedSubjectFilter,
        showOnlyFavorites
    ) { notes, query, subject, favOnly ->
        notes.filter { note ->
            val matchesQuery = query.isBlank() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.summary.contains(query, ignoreCase = true) ||
                    note.subject.contains(query, ignoreCase = true)
            val matchesSubject = subject == "All" || note.subject.equals(subject, ignoreCase = true)
            val matchesFav = !favOnly || note.isFavorite
            matchesQuery && matchesSubject && matchesFav
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active Selected Note
    val activeNote: StateFlow<ChapterNote?> = combine(
        allNotes,
        _selectedNoteId
    ) { notes, noteId ->
        if (noteId == null) null else notes.firstOrNull { it.id == noteId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Quiz State for Active Note
    val currentQuizAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap()) // QuestionIndex -> SelectedOptionIndex
    val isQuizSubmitted = MutableStateFlow(false)
    val quizScoreResult = MutableStateFlow<Pair<Int, Int>?>(null) // Correct, Total

    // Flashcard State
    val currentFlashcardIndex = MutableStateFlow(0)
    val isFlashcardFlipped = MutableStateFlow(false)

    // Voice Tutor Chat State
    val chatMessages: StateFlow<List<TutorMessage>> = repository.chatMessages.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val tutorQueryInput = MutableStateFlow("")
    val isTutorThinking = MutableStateFlow(false)

    init {
        // Populate initial sample notes if database is empty on first launch
        viewModelScope.launch {
            delay(400)
            if (allNotes.value.isEmpty()) {
                val preset = SampleChaptersData.PRESETS.first()
                repository.generateChapterNotes(preset.excerpt, preset.subject)
            }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun openNoteDetail(noteId: Long) {
        _selectedNoteId.value = noteId
        _selectedDetailTab.value = NoteDetailTab.SHORT_NOTES
        resetQuizState()
        currentFlashcardIndex.value = 0
        isFlashcardFlipped.value = false
    }

    fun closeNoteDetail() {
        _selectedNoteId.value = null
        voiceManager.stop()
    }

    fun selectDetailTab(tab: NoteDetailTab) {
        _selectedDetailTab.value = tab
    }

    fun loadSamplePreset(preset: SampleChapterPreset) {
        chapterInputText.value = preset.excerpt
        chapterSubject.value = preset.subject
        uploadedDocumentName.value = null
        uploadedDocumentSize.value = null
        uploadedDocumentType.value = null
    }

    fun attachDocument(fileName: String, sizeText: String, mimeType: String, extractedText: String) {
        uploadedDocumentName.value = fileName
        uploadedDocumentSize.value = sizeText
        uploadedDocumentType.value = mimeType
        if (extractedText.isNotBlank()) {
            chapterInputText.value = extractedText
        } else if (chapterInputText.value.isBlank()) {
            chapterInputText.value = "Document: $fileName\n\nAnalyze this document and extract Short Notes, Key Points, Academic Definitions, and all Mathematical/Scientific Important Formulas."
        }
        errorMessage.value = null
    }

    fun clearUploadedDocument() {
        uploadedDocumentName.value = null
        uploadedDocumentSize.value = null
        uploadedDocumentType.value = null
    }

    fun generateNotes() {
        val text = chapterInputText.value.trim()
        if (text.isBlank()) {
            errorMessage.value = "Please upload a chapter/PDF or enter chapter content to generate notes."
            return
        }

        viewModelScope.launch {
            isGeneratingNote.value = true
            errorMessage.value = null

            val docName = uploadedDocumentName.value
            generationProgressMessage.value = if (docName != null) {
                "Reading & parsing $docName..."
            } else {
                "Reading & analyzing chapter structure..."
            }
            delay(500)
            generationProgressMessage.value = "Extracting Short Notes & Key Points..."
            delay(500)
            generationProgressMessage.value = "Synthesizing Definitions & Important Formulas..."
            delay(600)
            generationProgressMessage.value = "Generating active recall flashcards & quiz..."
            delay(500)
            generationProgressMessage.value = "Building student improvement guide..."

            val result = repository.generateChapterNotes(
                chapterText = text,
                subject = chapterSubject.value,
                depth = noteDepth.value,
                sourceFileName = docName ?: ""
            )

            isGeneratingNote.value = false

            result.onSuccess { note ->
                chapterInputText.value = ""
                uploadedDocumentName.value = null
                uploadedDocumentSize.value = null
                uploadedDocumentType.value = null
                openNoteDetail(note.id)
                _currentTab.value = AppNavTab.LIBRARY
            }.onFailure { err ->
                errorMessage.value = "Note generation failed: ${err.localizedMessage}"
            }
        }
    }

    fun toggleFavorite(note: ChapterNote) {
        viewModelScope.launch {
            repository.toggleFavorite(note.id, !note.isFavorite)
        }
    }

    fun deleteNote(note: ChapterNote) {
        viewModelScope.launch {
            if (_selectedNoteId.value == note.id) {
                closeNoteDetail()
            }
            repository.deleteNote(note)
        }
    }

    // Flashcards controls
    fun nextFlashcard(total: Int) {
        if (total > 0) {
            isFlashcardFlipped.value = false
            currentFlashcardIndex.value = (currentFlashcardIndex.value + 1) % total
        }
    }

    fun prevFlashcard(total: Int) {
        if (total > 0) {
            isFlashcardFlipped.value = false
            currentFlashcardIndex.value = (currentFlashcardIndex.value - 1 + total) % total
        }
    }

    fun flipFlashcard() {
        isFlashcardFlipped.value = !isFlashcardFlipped.value
    }

    // Quiz controls
    fun selectQuizOption(questionIndex: Int, optionIndex: Int) {
        if (!isQuizSubmitted.value) {
            currentQuizAnswers.value = currentQuizAnswers.value.toMutableMap().apply {
                put(questionIndex, optionIndex)
            }
        }
    }

    fun submitQuiz(quizQuestions: List<QuizQuestionItem>, noteId: Long) {
        if (quizQuestions.isEmpty()) return
        var correctCount = 0
        quizQuestions.forEachIndexed { index, question ->
            val selected = currentQuizAnswers.value[index]
            if (selected == question.correctIndex) {
                correctCount++
            }
        }
        val scorePercent = ((correctCount.toFloat() / quizQuestions.size) * 100).toInt()
        isQuizSubmitted.value = true
        quizScoreResult.value = Pair(correctCount, quizQuestions.size)

        viewModelScope.launch {
            repository.updateQuizScore(noteId, scorePercent, correctCount, quizQuestions.size)
        }
    }

    fun resetQuizState() {
        currentQuizAnswers.value = emptyMap()
        isQuizSubmitted.value = false
        quizScoreResult.value = null
    }

    // Voice Tutor Interaction
    fun sendTutorQuestion(presetQuestion: String? = null) {
        val question = presetQuestion ?: tutorQueryInput.value.trim()
        if (question.isBlank()) return

        val activeNoteContext = activeNote.value?.summary
        val language = voiceManager.currentLanguage.value
        val persona = voiceManager.currentPersona.value

        viewModelScope.launch {
            if (presetQuestion == null) {
                tutorQueryInput.value = ""
            }
            isTutorThinking.value = true

            // Insert student message
            val userMsg = TutorMessage(
                chapterNoteId = _selectedNoteId.value,
                chapterTitle = activeNote.value?.title ?: "",
                sender = "USER",
                messageText = question,
                languageCode = language.languageCode,
                personaId = persona.id
            )
            repository.insertChatMessage(userMsg)

            val result = repository.askVoiceTutor(
                questionOrTopic = question,
                chapterContext = activeNoteContext,
                language = language,
                persona = persona,
                aiModel = _selectedAiModel.value
            )

            isTutorThinking.value = false

            result.onSuccess { explanation ->
                val aiMsg = TutorMessage(
                    chapterNoteId = _selectedNoteId.value,
                    chapterTitle = activeNote.value?.title ?: "",
                    sender = "AI",
                    messageText = explanation,
                    spokenAudioScript = explanation,
                    languageCode = language.languageCode,
                    personaId = persona.id
                )
                repository.insertChatMessage(aiMsg)

                // Spoken output in the selected language and persona
                voiceManager.speak(explanation)
            }.onFailure { err ->
                val fallbackExplanation = "I am ready to help you understand $question in ${language.displayName}. Let's break it down into core principles."
                val aiMsg = TutorMessage(
                    chapterNoteId = _selectedNoteId.value,
                    chapterTitle = activeNote.value?.title ?: "",
                    sender = "AI",
                    messageText = fallbackExplanation,
                    spokenAudioScript = fallbackExplanation,
                    languageCode = language.languageCode,
                    personaId = persona.id
                )
                repository.insertChatMessage(aiMsg)
                voiceManager.speak(fallbackExplanation)
            }
        }
    }

    fun speakText(text: String) {
        voiceManager.speak(text)
    }

    fun stopSpeaking() {
        voiceManager.stop()
    }

    fun clearTutorChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // Snap & Solve Actions
    fun setCapturedBitmap(bitmap: Bitmap?) {
        capturedPhoto.value = bitmap
        snapErrorMessage.value = null
    }

    fun selectPresetSnapQuestion(preset: SnapPresetQuestion) {
        snapQuestionInput.value = preset.questionPrompt
        snapSubject.value = preset.subject
        snapErrorMessage.value = null
    }

    fun clearActiveSnap() {
        capturedPhoto.value = null
        snapQuestionInput.value = ""
        activeSnapSolution.value = null
        snapErrorMessage.value = null
        voiceManager.stop()
    }

    fun viewSavedSnapSolution(record: SnapSolutionRecord) {
        activeSnapSolution.value = record
        _currentTab.value = AppNavTab.SNAP_SOLVE
    }

    fun deleteSavedSnapSolution(record: SnapSolutionRecord) {
        viewModelScope.launch {
            repository.deleteSnapSolution(record)
            if (activeSnapSolution.value?.id == record.id) {
                activeSnapSolution.value = null
            }
        }
    }

    fun solveSnapQuestion() {
        val photo = capturedPhoto.value
        val textPrompt = snapQuestionInput.value.trim()
        val subject = snapSubject.value

        if (photo == null && textPrompt.isBlank()) {
            snapErrorMessage.value = "Please snap/upload a question photo or select a problem below to solve."
            return
        }

        viewModelScope.launch {
            isSolvingSnap.value = true
            snapSolvingStage.value = "Analyzing question photo & extracting mathematical principles..."
            snapErrorMessage.value = null

            delay(300)
            snapSolvingStage.value = "Formulating step-by-step pedagogical breakdown & derivations..."

            val result = repository.solveSnapQuestion(
                bitmap = photo,
                questionTextPrompt = textPrompt,
                subject = subject
            )

            result.onSuccess { solutionRecord ->
                activeSnapSolution.value = solutionRecord
                isSolvingSnap.value = false
                snapSolvingStage.value = ""
            }.onFailure { err ->
                snapErrorMessage.value = "Solution analysis encountered an error: ${err.localizedMessage ?: "Unknown error"}"
                isSolvingSnap.value = false
                snapSolvingStage.value = ""
            }
        }
    }

    fun convertSnapSolutionToNote(record: SnapSolutionRecord) {
        viewModelScope.launch {
            val contentToGenerate = """
                Chapter Problem Deep-Dive: ${record.topic}
                Subject: ${record.subject}
                
                Problem Statement:
                ${record.questionText}
                
                Final Verified Answer:
                ${record.finalAnswer}
                
                Key Formulas:
                ${record.keyFormulasJson}
            """.trimIndent()

            chapterInputText.value = contentToGenerate
            chapterSubject.value = record.subject
            _currentTab.value = AppNavTab.GENERATE
            generateNotes()
        }
    }

    fun parseSolutionSteps(json: String): List<SolutionStep> {
        return try {
            val type = Types.newParameterizedType(List::class.java, SolutionStep::class.java)
            RetrofitClient.generalMoshi.adapter<List<SolutionStep>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseGivens(json: String): List<String> {
        return try {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            RetrofitClient.generalMoshi.adapter<List<String>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseKeyFormulas(json: String): List<String> {
        return try {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            RetrofitClient.generalMoshi.adapter<List<String>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseCommonMistakes(json: String): List<String> {
        return try {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            RetrofitClient.generalMoshi.adapter<List<String>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // JSON parsing helper functions for Composables
    fun parseConcepts(json: String): List<ConceptItem> {
        return try {
            val type = Types.newParameterizedType(List::class.java, ConceptItem::class.java)
            RetrofitClient.generalMoshi.adapter<List<ConceptItem>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseTakeaways(json: String): List<String> {
        return try {
            val type = Types.newParameterizedType(List::class.java, String::class.java)
            RetrofitClient.generalMoshi.adapter<List<String>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseVocabulary(json: String): List<VocabItem> {
        return try {
            val type = Types.newParameterizedType(List::class.java, VocabItem::class.java)
            RetrofitClient.generalMoshi.adapter<List<VocabItem>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseFlashcards(json: String): List<FlashcardItem> {
        return try {
            val type = Types.newParameterizedType(List::class.java, FlashcardItem::class.java)
            RetrofitClient.generalMoshi.adapter<List<FlashcardItem>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseFormulas(json: String): List<com.example.data.model.FormulaItem> {
        return try {
            val type = Types.newParameterizedType(List::class.java, com.example.data.model.FormulaItem::class.java)
            RetrofitClient.generalMoshi.adapter<List<com.example.data.model.FormulaItem>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseDefinitions(json: String): List<com.example.data.model.DefinitionItem> {
        return try {
            val type = Types.newParameterizedType(List::class.java, com.example.data.model.DefinitionItem::class.java)
            RetrofitClient.generalMoshi.adapter<List<com.example.data.model.DefinitionItem>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseQuiz(json: String): List<QuizQuestionItem> {
        return try {
            val type = Types.newParameterizedType(List::class.java, QuizQuestionItem::class.java)
            RetrofitClient.generalMoshi.adapter<List<QuizQuestionItem>>(type).fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseImprovementGuide(json: String): ImprovementGuideItem {
        return try {
            RetrofitClient.generalMoshi.adapter(ImprovementGuideItem::class.java).fromJson(json)
                ?: ImprovementGuideItem()
        } catch (e: Exception) {
            ImprovementGuideItem()
        }
    }

    // -------------------------------------------------------------
    // Robot Video Teacher Playback Engine & AI Generator Methods
    // -------------------------------------------------------------

    fun selectRobotLesson(lesson: RobotLesson) {
        stopRobotVideo()
        activeRobotLesson.value = lesson
        activeRobotCueIndex.value = 0
        robotSpokenWordHighlight.value = ""
    }

    fun playRobotVideo() {
        if (isPlayingRobotVideo.value) return
        val lesson = activeRobotLesson.value
        if (lesson.cues.isEmpty()) return

        isPlayingRobotVideo.value = true
        robotPlaybackJob?.cancel()
        robotPlaybackJob = viewModelScope.launch {
            val startIndex = activeRobotCueIndex.value
            for (idx in startIndex until lesson.cues.size) {
                if (!isPlayingRobotVideo.value) break
                activeRobotCueIndex.value = idx
                val cue = lesson.cues[idx]

                // Speak cue text with high-clarity voice
                voiceManager.speak(cue.text)

                // Word karaoke highlighter simulation synchronized with words
                val words = cue.text.split(" ")
                val totalWords = words.size.coerceAtLeast(1)
                val durationPerWordMs = ((cue.durationSecondsEstimate * 1000f) / totalWords / robotVideoSpeed.value).toLong().coerceIn(180, 480)

                for (w in words) {
                    if (!isPlayingRobotVideo.value) break
                    robotSpokenWordHighlight.value = w.replace(Regex("[^A-Za-z0-9]"), "")
                    delay(durationPerWordMs)
                }

                // Wait for spoken audio to finish or short buffer
                while (voiceManager.isSpeaking.value && isPlayingRobotVideo.value) {
                    delay(100)
                }
                delay((600 / robotVideoSpeed.value).toLong())
            }

            // Finished playback
            isPlayingRobotVideo.value = false
            robotSpokenWordHighlight.value = ""
        }
    }

    fun pauseRobotVideo() {
        isPlayingRobotVideo.value = false
        robotPlaybackJob?.cancel()
        voiceManager.stop()
    }

    fun stopRobotVideo() {
        isPlayingRobotVideo.value = false
        robotPlaybackJob?.cancel()
        voiceManager.stop()
        activeRobotCueIndex.value = 0
        robotSpokenWordHighlight.value = ""
    }

    fun togglePlayPauseRobotVideo() {
        if (isPlayingRobotVideo.value) {
            pauseRobotVideo()
        } else {
            playRobotVideo()
        }
    }

    fun nextRobotCue() {
        val lesson = activeRobotLesson.value
        val nextIdx = (activeRobotCueIndex.value + 1).coerceAtMost(lesson.cues.size - 1)
        seekToRobotCue(nextIdx)
    }

    fun previousRobotCue() {
        val prevIdx = (activeRobotCueIndex.value - 1).coerceAtLeast(0)
        seekToRobotCue(prevIdx)
    }

    fun seekToRobotCue(index: Int) {
        val wasPlaying = isPlayingRobotVideo.value
        pauseRobotVideo()
        activeRobotCueIndex.value = index
        robotSpokenWordHighlight.value = ""
        if (wasPlaying) {
            playRobotVideo()
        }
    }

    fun setRobotVideoSpeed(speed: Float) {
        robotVideoSpeed.value = speed
        voiceManager.setSpeechRate(speed)
    }

    fun generateCustomAiRobotLesson(topic: String, subject: String = "Science") {
        if (topic.isBlank()) {
            robotErrorMessage.value = "Please enter a topic to create an AI Robot video lesson."
            return
        }

        viewModelScope.launch {
            isGeneratingRobotLesson.value = true
            robotLessonGenerationStage.value = "Directing AI robot script & timing visual hologram cues..."
            robotErrorMessage.value = null

            val result = repository.generateRobotVideoLesson(topic, subject)
            result.onSuccess { lesson ->
                val currentList = robotLessonsList.value.toMutableList()
                currentList.add(0, lesson)
                robotLessonsList.value = currentList
                selectRobotLesson(lesson)
                isGeneratingRobotLesson.value = false
                robotLessonGenerationStage.value = ""
                robotPromptInput.value = ""
                // Auto play new lesson
                playRobotVideo()
            }.onFailure { err ->
                robotErrorMessage.value = "Failed to generate lesson: ${err.localizedMessage ?: "Unknown error"}"
                isGeneratingRobotLesson.value = false
                robotLessonGenerationStage.value = ""
            }
        }
    }

    // =========================================================================
    // OMR CHALLENGE, GAMIFICATION & PREMIUM ENGINE
    // =========================================================================

    val allOmrTests: StateFlow<List<com.example.data.model.OmrTestRecord>> = repository.allOmrTests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Generator Inputs
    val omrSubjectInput = MutableStateFlow("Physics")
    val omrChapterInput = MutableStateFlow("Laws of Motion & Mechanics")
    val omrGradeInput = MutableStateFlow("Class 10")
    val omrQuestionCount = MutableStateFlow(10)
    val omrDifficulty = MutableStateFlow("Medium") // "Easy", "Medium", "Hard", "Mixed"

    val isGeneratingOmrTest = MutableStateFlow(false)
    val omrGenerationStage = MutableStateFlow("")
    val omrGenerationError = MutableStateFlow<String?>(null)

    // Active Test State
    val activeOmrTest = MutableStateFlow<com.example.data.model.OmrTestRecord?>(null)
    val activeQuestionsList = MutableStateFlow<List<com.example.data.model.OmrQuestion>>(emptyList())
    val activeUserAnswers = MutableStateFlow<Map<Int, String>>(emptyMap()) // Q# -> "A"|"B"|"C"|"D"
    val activeQuestionIndex = MutableStateFlow(0)
    val omrTimerSeconds = MutableStateFlow(0L)
    val isTimerRunning = MutableStateFlow(false)
    private var omrTimerJob: kotlinx.coroutines.Job? = null

    // Test Results State
    val activeTestResult = MutableStateFlow<com.example.data.model.OmrTestRecord?>(null)
    val isGeneratingMistakeLesson = MutableStateFlow(false)
    val activeMistakeLessonText = MutableStateFlow<String?>(null)

    // Gamification & Wallet
    val gamificationState = MutableStateFlow(loadInitialGamificationState())
    val badgesList = MutableStateFlow(com.example.data.model.OmrPresets.BADGES)
    val leaderboardUsers = MutableStateFlow(com.example.data.model.OmrPresets.LEADERBOARD_USERS)
    val premiumPerks = MutableStateFlow(com.example.data.model.OmrPresets.PREMIUM_PERKS)
    val difficultyAdaptation = MutableStateFlow(
        com.example.data.model.DifficultyAdaptationAnalysis(
            recommendedDifficulty = "Medium",
            recentAccuracyAverage = 84.4f,
            strongTopics = listOf("Mechanics", "Cell Biology", "Atomic Structure"),
            weakTopics = listOf("Photosynthesis Reactions", "Wave Optics"),
            recommendationMessage = "Great foundational accuracy (84%). AI recommends continuing on Medium with occasional Hard challenges to push mastery."
        )
    )

    private fun loadInitialGamificationState(): com.example.data.model.GamificationState {
        val prefs = getApplication<Application>().getSharedPreferences("omr_gamification_prefs", android.content.Context.MODE_PRIVATE)
        val xp = prefs.getInt("user_xp", 180)
        val points = prefs.getInt("study_points", 650)
        val streak = prefs.getInt("streak_days", 3)
        val levelNum = computeLevelNumber(xp)
        val levelTitle = computeLevelTitle(levelNum)
        val isSubscribed = prefs.getBoolean("is_premium_sub", false)
        val unlockedPerksStr = prefs.getString("unlocked_perks", "") ?: ""
        val perksList = if (unlockedPerksStr.isNotBlank()) unlockedPerksStr.split(",") else emptyList()

        return com.example.data.model.GamificationState(
            xp = xp,
            studyPoints = points,
            streakDays = streak,
            levelNumber = levelNum,
            levelTitle = levelTitle,
            isPremiumSubscribed = isSubscribed,
            unlockedPerkIds = perksList
        )
    }

    private fun saveGamificationState(state: com.example.data.model.GamificationState) {
        val prefs = getApplication<Application>().getSharedPreferences("omr_gamification_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putInt("user_xp", state.xp)
            .putInt("study_points", state.studyPoints)
            .putInt("streak_days", state.streakDays)
            .putBoolean("is_premium_sub", state.isPremiumSubscribed)
            .putString("unlocked_perks", state.unlockedPerkIds.joinToString(","))
            .apply()
        gamificationState.value = state
        updateLeaderboardWithCurrentUser(state)
        updateBadgesProgress(state)
    }

    private fun computeLevelNumber(xp: Int): Int {
        return when {
            xp < 200 -> 1
            xp < 600 -> 2
            xp < 1500 -> 3
            xp < 3500 -> 4
            xp < 6500 -> 5
            else -> 6
        }
    }

    private fun computeLevelTitle(level: Int): String {
        return when (level) {
            1 -> "Beginner"
            2 -> "Learner"
            3 -> "Smart Student"
            4 -> "Scholar"
            5 -> "Master"
            else -> "Grandmaster"
        }
    }

    fun startNewOmrChallenge(
        subject: String,
        chapterName: String,
        gradeLevel: String,
        questionCount: Int,
        difficulty: String
    ) {
        if (subject.isBlank() || chapterName.isBlank()) {
            omrGenerationError.value = "Please enter both Subject and Chapter name."
            return
        }

        viewModelScope.launch {
            isGeneratingOmrTest.value = true
            omrGenerationStage.value = "Crafting $questionCount curriculum-aligned OMR questions for $chapterName..."
            omrGenerationError.value = null

            val result = repository.generateOmrQuestions(
                subject = subject,
                chapterName = chapterName,
                gradeLevel = gradeLevel,
                questionCount = questionCount,
                difficulty = difficulty,
                previousQuestionsToExclude = sessionGeneratedQuestionsHistory.value,
                aiModel = _selectedAiModel.value
            )

            result.onSuccess { questions ->
                // Update anti-repetition session history with newly generated question texts
                val newHistory = (sessionGeneratedQuestionsHistory.value + questions.map { it.questionText }).takeLast(60)
                sessionGeneratedQuestionsHistory.value = newHistory

                val testId = java.util.UUID.randomUUID().toString()
                val qJson = com.squareup.moshi.Moshi.Builder().build()
                    .adapter<List<com.example.data.model.OmrQuestion>>(
                        Types.newParameterizedType(List::class.java, com.example.data.model.OmrQuestion::class.java)
                    ).toJson(questions)

                val newRecord = com.example.data.model.OmrTestRecord(
                    id = testId,
                    title = "$chapterName OMR Test",
                    subject = subject,
                    chapterName = chapterName,
                    gradeLevel = gradeLevel,
                    difficulty = difficulty,
                    totalQuestions = questions.size,
                    questionsJson = qJson,
                    userAnswersJson = "{}",
                    score = 0,
                    maxScore = questions.size * 10,
                    accuracyPercentage = 0f,
                    correctCount = 0,
                    incorrectCount = 0,
                    unansweredCount = questions.size,
                    pointsEarned = 0,
                    xpEarned = 0,
                    streakBonusPoints = 0,
                    timeSpentSeconds = 0L,
                    isDailyChallenge = false
                )

                activeQuestionsList.value = questions
                activeUserAnswers.value = emptyMap()
                activeQuestionIndex.value = 0
                activeOmrTest.value = newRecord
                activeTestResult.value = null
                activeMistakeLessonText.value = null

                isGeneratingOmrTest.value = false
                omrGenerationStage.value = ""

                startTimer()
            }.onFailure { err ->
                omrGenerationError.value = "Generation failed: ${err.localizedMessage ?: "Unknown error"}"
                isGeneratingOmrTest.value = false
                omrGenerationStage.value = ""
            }
        }
    }

    fun startDailyOmrChallenge() {
        val dailyQs = com.example.data.model.OmrPresets.getDailyChallengeQuestions()
        val testId = "daily_" + System.currentTimeMillis()
        val qJson = com.squareup.moshi.Moshi.Builder().build()
            .adapter<List<com.example.data.model.OmrQuestion>>(
                Types.newParameterizedType(List::class.java, com.example.data.model.OmrQuestion::class.java)
            ).toJson(dailyQs)

        val dailyRecord = com.example.data.model.OmrTestRecord(
            id = testId,
            title = "🔥 Daily OMR Challenge",
            subject = "Science & Multi-Disciplinary",
            chapterName = "Daily Curated Master Quiz",
            gradeLevel = "All Grades",
            difficulty = "Mixed",
            totalQuestions = dailyQs.size,
            questionsJson = qJson,
            userAnswersJson = "{}",
            score = 0,
            maxScore = dailyQs.size * 10,
            accuracyPercentage = 0f,
            correctCount = 0,
            incorrectCount = 0,
            unansweredCount = dailyQs.size,
            pointsEarned = 0,
            xpEarned = 0,
            streakBonusPoints = 0,
            timeSpentSeconds = 0L,
            isDailyChallenge = true
        )

        activeQuestionsList.value = dailyQs
        activeUserAnswers.value = emptyMap()
        activeQuestionIndex.value = 0
        activeOmrTest.value = dailyRecord
        activeTestResult.value = null
        activeMistakeLessonText.value = null

        startTimer()
    }

    private fun startTimer() {
        omrTimerJob?.cancel()
        omrTimerSeconds.value = 0L
        isTimerRunning.value = true
        omrTimerJob = viewModelScope.launch {
            while (isTimerRunning.value) {
                delay(1000)
                omrTimerSeconds.value += 1
            }
        }
    }

    private fun stopTimer() {
        isTimerRunning.value = false
        omrTimerJob?.cancel()
    }

    fun selectOmrBubble(questionNumber: Int, option: String) {
        val current = activeUserAnswers.value.toMutableMap()
        if (current[questionNumber] == option) {
            // Toggle off if re-tapped
            current.remove(questionNumber)
        } else {
            current[questionNumber] = option
        }
        activeUserAnswers.value = current
    }

    fun jumpToQuestion(index: Int) {
        if (index in 0 until activeQuestionsList.value.size) {
            activeQuestionIndex.value = index
        }
    }

    fun nextQuestion() {
        if (activeQuestionIndex.value < activeQuestionsList.value.size - 1) {
            activeQuestionIndex.value += 1
        }
    }

    fun prevQuestion() {
        if (activeQuestionIndex.value > 0) {
            activeQuestionIndex.value -= 1
        }
    }

    fun submitOmrTest() {
        val currentTest = activeOmrTest.value ?: return
        val questions = activeQuestionsList.value
        val answers = activeUserAnswers.value
        stopTimer()

        var correctCount = 0
        var incorrectCount = 0
        var unansweredCount = 0
        var currentStreak = 0
        var streakBonusPoints = 0

        questions.forEach { q ->
            val chosen = answers[q.questionNumber]
            if (chosen == null) {
                unansweredCount++
                currentStreak = 0
            } else if (chosen.equals(q.correctOption, ignoreCase = true)) {
                correctCount++
                currentStreak++
                if (currentStreak > 1) {
                    streakBonusPoints += (currentStreak * 2)
                }
            } else {
                incorrectCount++
                currentStreak = 0
            }
        }

        val totalQ = questions.size
        val accuracy = if (totalQ > 0) (correctCount.toFloat() / totalQ) * 100f else 0f
        val basePoints = correctCount * 10
        val accuracyBonusPoints = when {
            accuracy >= 100f -> 100
            accuracy >= 90f -> 50
            accuracy >= 80f -> 25
            else -> 0
        }
        val dailyBonusPoints = if (currentTest.isDailyChallenge) 50 else 0
        val totalPointsEarned = basePoints + streakBonusPoints + accuracyBonusPoints + dailyBonusPoints

        val baseXP = correctCount * 5
        val bonusXP = (if (accuracy >= 90f) 50 else 20) + (if (currentTest.isDailyChallenge) 100 else 0)
        val totalXpEarned = baseXP + bonusXP

        val answersJson = JSONObject(answers.mapKeys { it.key.toString() }).toString()

        val completedRecord = currentTest.copy(
            score = correctCount * 10,
            maxScore = totalQ * 10,
            accuracyPercentage = accuracy,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            unansweredCount = unansweredCount,
            pointsEarned = totalPointsEarned,
            xpEarned = totalXpEarned,
            streakBonusPoints = streakBonusPoints,
            timeSpentSeconds = omrTimerSeconds.value,
            userAnswersJson = answersJson
        )

        viewModelScope.launch {
            repository.saveOmrTestRecord(completedRecord)

            // Update user gamification wallet
            val currentGam = gamificationState.value
            val newXp = currentGam.xp + totalXpEarned
            val newPoints = currentGam.studyPoints + totalPointsEarned
            val newStreak = if (currentTest.isDailyChallenge) currentGam.streakDays + 1 else currentGam.streakDays
            val newLevel = computeLevelNumber(newXp)
            val newTitle = computeLevelTitle(newLevel)

            val updatedGam = currentGam.copy(
                xp = newXp,
                studyPoints = newPoints,
                streakDays = newStreak,
                levelNumber = newLevel,
                levelTitle = newTitle,
                totalTestsCompleted = currentGam.totalTestsCompleted + 1,
                totalQuestionsAttempted = currentGam.totalQuestionsAttempted + totalQ,
                totalCorrectAnswers = currentGam.totalCorrectAnswers + correctCount,
                averageAccuracy = ((currentGam.averageAccuracy * currentGam.totalTestsCompleted) + accuracy) / (currentGam.totalTestsCompleted + 1)
            )
            saveGamificationState(updatedGam)

            // Calculate difficulty adaptation
            calculateDifficultyAdaptation(updatedGam.averageAccuracy, completedRecord)

            activeTestResult.value = completedRecord
            activeOmrTest.value = null
        }
    }

    fun generateMistakeLessonForTest(testRecord: com.example.data.model.OmrTestRecord) {
        viewModelScope.launch {
            isGeneratingMistakeLesson.value = true
            activeMistakeLessonText.value = null

            val questionsType = Types.newParameterizedType(List::class.java, com.example.data.model.OmrQuestion::class.java)
            val qAdapter = com.squareup.moshi.Moshi.Builder().build().adapter<List<com.example.data.model.OmrQuestion>>(questionsType)
            val questions = try {
                qAdapter.fromJson(testRecord.questionsJson) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }

            val userAnswers = mutableMapOf<Int, String>()
            try {
                val json = JSONObject(testRecord.userAnswersJson)
                val keys = json.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    userAnswers[k.toInt()] = json.getString(k)
                }
            } catch (e: Exception) {
                // ignore
            }

            val failedQs = questions.filter { q ->
                val chosen = userAnswers[q.questionNumber]
                chosen == null || !chosen.equals(q.correctOption, ignoreCase = true)
            }

            val result = repository.generateMistakeRevisionLesson(
                failedQuestions = failedQs,
                userAnswers = userAnswers,
                chapterName = testRecord.chapterName,
                subject = testRecord.subject,
                aiModel = _selectedAiModel.value
            )

            result.onSuccess { lessonText ->
                activeMistakeLessonText.value = lessonText
                isGeneratingMistakeLesson.value = false
            }.onFailure {
                activeMistakeLessonText.value = "Failed to generate AI revision lesson: ${it.localizedMessage}"
                isGeneratingMistakeLesson.value = false
            }
        }
    }

    private fun calculateDifficultyAdaptation(
        avgAccuracy: Float,
        recentRecord: com.example.data.model.OmrTestRecord
    ) {
        val (recommended, message) = when {
            avgAccuracy >= 88f -> Pair(
                "Hard",
                "Exceptional Mastery ($avgAccuracy% Avg Accuracy)! The AI recommends challenging yourself with 'Hard' or 'Mixed' competitive question papers to build Olympiad & Board Exam readiness."
            )
            avgAccuracy >= 70f -> Pair(
                "Medium",
                "Solid Grasp ($avgAccuracy% Avg Accuracy). Continue reinforcing core concepts on 'Medium' difficulty, focusing on subtopic precision."
            )
            else -> Pair(
                "Easy",
                "Foundational Strengthening Needed ($avgAccuracy% Avg Accuracy). We suggest practicing 'Easy' to 'Medium' chapters with 'Learn My Mistakes' revision before attempting timed speed tests."
            )
        }

        difficultyAdaptation.value = com.example.data.model.DifficultyAdaptationAnalysis(
            recommendedDifficulty = recommended,
            recentAccuracyAverage = avgAccuracy,
            strongTopics = listOf(recentRecord.subject, "Conceptual Recall", "Direct Applications"),
            weakTopics = if (recentRecord.incorrectCount > 0) listOf("Analytical Edge Cases", "Distractor Dissection") else emptyList(),
            recommendationMessage = message
        )
    }

    private fun updateLeaderboardWithCurrentUser(state: com.example.data.model.GamificationState) {
        val currentList = leaderboardUsers.value.toMutableList()
        val userIdx = currentList.indexOfFirst { it.isCurrentUser }
        if (userIdx != -1) {
            currentList[userIdx] = currentList[userIdx].copy(
                studyPoints = state.studyPoints,
                xp = state.xp,
                streakDays = state.streakDays,
                accuracy = state.averageAccuracy,
                levelTitle = state.levelTitle
            )
            // Re-sort leaderboard by study points descending
            val sorted = currentList.sortedByDescending { it.studyPoints }.mapIndexed { idx, user ->
                user.copy(rank = idx + 1)
            }
            leaderboardUsers.value = sorted
        }
    }

    private fun updateBadgesProgress(state: com.example.data.model.GamificationState) {
        val updated = badgesList.value.map { badge ->
            when (badge.id) {
                "FIRST_TEST" -> badge.copy(
                    isUnlocked = state.totalTestsCompleted >= 1,
                    progress = if (state.totalTestsCompleted >= 1) 1.0f else 0f
                )
                "STREAK_7" -> badge.copy(
                    isUnlocked = state.streakDays >= 7,
                    progress = (state.streakDays / 7f).coerceIn(0f, 1f),
                    requirementText = "${state.streakDays} / 7 days"
                )
                "ACCURACY_90" -> badge.copy(
                    isUnlocked = state.averageAccuracy >= 90f,
                    progress = (state.averageAccuracy / 90f).coerceIn(0f, 1f)
                )
                "CHAPTER_MASTER" -> badge.copy(
                    isUnlocked = state.totalTestsCompleted >= 5,
                    progress = (state.totalTestsCompleted / 5f).coerceIn(0f, 1f),
                    requirementText = "${state.totalTestsCompleted} / 5 tests"
                )
                "OMR_LEGEND" -> badge.copy(
                    isUnlocked = state.studyPoints >= 5000,
                    progress = (state.studyPoints / 5000f).coerceIn(0f, 1f),
                    requirementText = "${state.studyPoints} / 5,000 pts"
                )
                else -> badge
            }
        }
        badgesList.value = updated
    }

    fun unlockPremiumPerk(perkId: String): Boolean {
        val currentGam = gamificationState.value
        val perk = premiumPerks.value.firstOrNull { it.id == perkId } ?: return false

        if (currentGam.unlockedPerkIds.contains(perkId) || currentGam.isPremiumSubscribed) {
            return true
        }

        if (currentGam.studyPoints >= perk.requiredPoints) {
            val remainingPoints = currentGam.studyPoints - perk.requiredPoints
            val newUnlocked = currentGam.unlockedPerkIds.toMutableList().apply { add(perkId) }
            val updated = currentGam.copy(
                studyPoints = remainingPoints,
                unlockedPerkIds = newUnlocked
            )
            saveGamificationState(updated)

            // Update perk items list
            premiumPerks.value = premiumPerks.value.map {
                if (it.id == perkId) it.copy(isUnlocked = true) else it
            }
            return true
        }
        return false
    }

    fun unlockAllPremiumWithPoints(): Boolean {
        val currentGam = gamificationState.value
        if (currentGam.studyPoints >= 5000) {
            val remainingPoints = currentGam.studyPoints - 5000
            val allIds = premiumPerks.value.map { it.id }
            val updated = currentGam.copy(
                studyPoints = remainingPoints,
                isPremiumSubscribed = true,
                unlockedPerkIds = allIds
            )
            saveGamificationState(updated)
            premiumPerks.value = premiumPerks.value.map { it.copy(isUnlocked = true) }
            return true
        }
        return false
    }

    fun subscribeToPremium() {
        val currentGam = gamificationState.value
        val allIds = premiumPerks.value.map { it.id }
        val updated = currentGam.copy(
            isPremiumSubscribed = true,
            unlockedPerkIds = allIds
        )
        saveGamificationState(updated)
        premiumPerks.value = premiumPerks.value.map { it.copy(isUnlocked = true) }
    }

    fun openTestRecordResult(record: com.example.data.model.OmrTestRecord) {
        activeTestResult.value = record
        activeOmrTest.value = null
        activeMistakeLessonText.value = null
    }

    fun closeTestResult() {
        activeTestResult.value = null
        activeMistakeLessonText.value = null
    }

    fun closeOmrTest() {
        stopTimer()
        activeOmrTest.value = null
        activeQuestionsList.value = emptyList()
        activeUserAnswers.value = emptyMap()
    }

    override fun onCleared() {
        super.onCleared()
        robotPlaybackJob?.cancel()
        omrTimerJob?.cancel()
        voiceManager.shutdown()
    }
}

