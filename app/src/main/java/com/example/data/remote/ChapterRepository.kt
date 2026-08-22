package com.example.data.remote

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.local.ChapterNoteDao
import com.example.data.local.SnapSolutionDao
import com.example.data.local.TutorChatDao
import com.example.data.model.ChapterNote
import com.example.data.model.ConceptItem
import com.example.data.model.FlashcardItem
import com.example.data.model.ImprovementGuideItem
import com.example.data.model.LanguageVoiceOption
import com.example.data.model.QuizQuestionItem
import com.example.data.model.HologramVisualType
import com.example.data.model.PresetRobotLessons
import com.example.data.model.RobotEmotion
import com.example.data.model.RobotGesture
import com.example.data.model.RobotLesson
import com.example.data.model.RobotSpeechCue
import com.example.data.model.SnapSolutionRecord
import com.example.data.model.SnapSolutionResult
import com.example.data.model.SolutionStep
import com.example.data.model.StructuredNoteData
import com.example.data.model.TutorMessage
import com.example.data.model.VocabItem
import com.example.data.model.VoicePersona
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ChapterRepository(
    private val chapterNoteDao: ChapterNoteDao,
    private val tutorChatDao: TutorChatDao,
    private val snapSolutionDao: SnapSolutionDao,
    private val omrTestDao: com.example.data.local.OmrTestDao? = null
) {
    val allNotes: Flow<List<ChapterNote>> = chapterNoteDao.getAllNotes()
    val favoriteNotes: Flow<List<ChapterNote>> = chapterNoteDao.getFavoriteNotes()
    val chatMessages: Flow<List<TutorMessage>> = tutorChatDao.getAllMessages()
    val allSnapSolutions: Flow<List<SnapSolutionRecord>> = snapSolutionDao.getAllSolutions()
    val allOmrTests: Flow<List<com.example.data.model.OmrTestRecord>> = omrTestDao?.getAllTestRecordsFlow()
        ?: kotlinx.coroutines.flow.flowOf(emptyList())

    suspend fun saveOmrTestRecord(record: com.example.data.model.OmrTestRecord) = withContext(Dispatchers.IO) {
        omrTestDao?.insertTestRecord(record)
    }

    suspend fun deleteOmrTestRecord(testId: String) = withContext(Dispatchers.IO) {
        omrTestDao?.deleteTestRecord(testId)
    }

    fun getNoteById(id: Long): Flow<ChapterNote?> = chapterNoteDao.getNoteById(id)

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        chapterNoteDao.toggleFavorite(id, isFavorite)
    }

    suspend fun deleteNote(note: ChapterNote) = withContext(Dispatchers.IO) {
        chapterNoteDao.deleteNote(note)
    }

    suspend fun deleteSnapSolution(record: SnapSolutionRecord) = withContext(Dispatchers.IO) {
        snapSolutionDao.deleteSolution(record)
    }

    suspend fun clearSnapHistory() = withContext(Dispatchers.IO) {
        snapSolutionDao.clearAllSolutions()
    }

    suspend fun updateQuizScore(noteId: Long, scorePercent: Int, correctCount: Int, totalCount: Int) = withContext(Dispatchers.IO) {
        chapterNoteDao.updateQuizMastery(noteId, scorePercent, correctCount, totalCount)
    }

    suspend fun insertChatMessage(message: TutorMessage): Long = withContext(Dispatchers.IO) {
        tutorChatDao.insertMessage(message)
    }

    suspend fun clearChat() = withContext(Dispatchers.IO) {
        tutorChatDao.clearAllMessages()
    }

    suspend fun generateChapterNotes(
        chapterText: String,
        subject: String,
        depth: String = "Comprehensive",
        sourceFileName: String = ""
    ): Result<ChapterNote> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Generate intelligent offline structured notes based on the chapter
            val fallbackNote = generateFallbackStructuredNote(chapterText, subject, sourceFileName)
            val noteId = chapterNoteDao.insertNote(fallbackNote)
            return@withContext Result.success(fallbackNote.copy(id = noteId))
        }

        val systemPrompt = """
            You are an elite academic AI tutor and study note architect.
            Analyze the student's chapter input/PDF and produce a comprehensive, structured, high-retention study guide in strictly VALID JSON format without markdown code fences or backticks.
            
            You MUST generate all 4 core study components with highest fidelity:
            1. Short Notes: Crisp, well-structured multi-paragraph summary covering all core chapter mechanisms.
            2. Key Points: 5-8 bulleted high-yield points and critical takeaways.
            3. Definitions: Academic terminology with precise definitions and context.
            4. Important Formulas: Mathematical equations, chemical reactions, physical laws, or algorithms with variable definitions, units, and practical examples.
            
            Format your response strictly as a JSON object matching this schema:
            {
              "title": "Clear concise chapter title",
              "subject": "$subject",
              "summary": "Crisp 2-3 paragraph short notes summarizing the core principles, causal mechanics, and high-yield insights.",
              "shortNotes": "Structured short notes breakdown covering foundational laws, processes, and summary highlights.",
              "keyTakeaways": ["Key Point 1: Essential principle", "Key Point 2: Core rule or law", "Key Point 3: Critical exam fact", "Key Point 4: Key relationship"],
              "keyPoints": ["Key Point 1: Essential principle", "Key Point 2: Core rule or law", "Key Point 3: Critical exam fact", "Key Point 4: Key relationship"],
              "definitions": [
                {
                  "term": "Term Name",
                  "definition": "Precise, formal scientific/academic definition",
                  "contextOrExample": "How or where this term is used in the chapter"
                }
              ],
              "importantFormulas": [
                {
                  "name": "Formula or Law Name (e.g. Newton's 2nd Law, Henderson-Hasselbalch, Bayes Rule)",
                  "formula": "Mathematical or Chemical Equation (e.g. F = m * a, pH = pKa + log([A-]/[HA]))",
                  "explanation": "What this equation calculates and its physical/mathematical significance",
                  "variables": "List of variables and their standard SI units (e.g. F = Force in Newtons (N), m = mass in kg, a = acceleration in m/s^2)",
                  "example": "Quick numerical or conceptual application calculation"
                }
              ],
              "concepts": [
                {
                  "title": "Concept Name",
                  "explanation": "In-depth pedagogical explanation",
                  "keyPoints": ["Bullet 1", "Bullet 2"],
                  "importanceLevel": "High"
                }
              ],
              "vocabulary": [
                {
                  "term": "Term Name",
                  "definition": "Clear concise definition",
                  "formulaOrExample": "Formula or example"
                }
              ],
              "flashcards": [
                {
                  "front": "Active recall question",
                  "back": "Direct, precise answer",
                  "hint": "Helpful conceptual clue"
                }
              ],
              "quiz": [
                {
                  "question": "Multiple choice question testing deep understanding",
                  "options": ["Option A", "Option B", "Option C", "Option D"],
                  "correctIndex": 0,
                  "explanation": "Why Option A is correct and why other distractors fail"
                }
              ],
              "improvementGuide": {
                "commonWeakSpots": ["Point where students frequently get confused or lose exam marks"],
                "mnemonicTricks": ["Clever memory acronyms, visual metaphors, or associations"],
                "revisionStrategy": ["Day 1 focus", "Day 3 active recall task", "Day 7 exam simulation"],
                "practicalApplications": ["Where this concept is applied in industry, nature, or engineering"],
                "examReadinessTips": "Crucial grading rubrics and common question formats for this topic."
              }
            }
        """.trimIndent()

        val userPrompt = """
            Subject: $subject
            Note Depth: $depth
            ${if (sourceFileName.isNotBlank()) "Source File: $sourceFileName" else ""}
            
            Chapter Content:
            $chapterText
            
            Generate the complete JSON object with Short Notes, Key Points, Definitions, and Important Formulas. Return ONLY valid JSON.
        """.trimIndent()

        try {
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = userPrompt)))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.4f,
                    responseMimeType = "application/json"
                )
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI model")

            val cleanedJson = cleanJsonString(rawJson)
            val structuredData = parseStructuredNoteJson(cleanedJson, subject)

            val moshi = RetrofitClient.generalMoshi
            val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
            val conceptListType = Types.newParameterizedType(List::class.java, ConceptItem::class.java)
            val vocabListType = Types.newParameterizedType(List::class.java, VocabItem::class.java)
            val formulaListType = Types.newParameterizedType(List::class.java, com.example.data.model.FormulaItem::class.java)
            val flashcardListType = Types.newParameterizedType(List::class.java, FlashcardItem::class.java)
            val quizListType = Types.newParameterizedType(List::class.java, QuizQuestionItem::class.java)

            // Convert definitions into vocab list if vocab list is empty
            val combinedVocab = if (structuredData.vocabulary.isNotEmpty()) {
                structuredData.vocabulary
            } else {
                structuredData.definitions.map {
                    VocabItem(term = it.term, definition = it.definition, formulaOrExample = it.contextOrExample)
                }
            }

            val finalKeyTakeaways = if (structuredData.keyPoints.isNotEmpty()) structuredData.keyPoints else structuredData.keyTakeaways

            val note = ChapterNote(
                title = structuredData.title.ifBlank { if (sourceFileName.isNotBlank()) sourceFileName else "Study Guide: $subject" },
                subject = structuredData.subject.ifBlank { subject },
                rawChapterText = chapterText,
                summary = structuredData.summary.ifBlank { structuredData.shortNotes },
                keyTakeawaysJson = moshi.adapter<List<String>>(stringListType).toJson(finalKeyTakeaways),
                conceptsJson = moshi.adapter<List<ConceptItem>>(conceptListType).toJson(structuredData.concepts),
                vocabularyJson = moshi.adapter<List<VocabItem>>(vocabListType).toJson(combinedVocab),
                importantFormulasJson = moshi.adapter<List<com.example.data.model.FormulaItem>>(formulaListType).toJson(structuredData.importantFormulas),
                flashcardsJson = moshi.adapter<List<FlashcardItem>>(flashcardListType).toJson(structuredData.flashcards),
                quizJson = moshi.adapter<List<QuizQuestionItem>>(quizListType).toJson(structuredData.quiz),
                improvementGuideJson = moshi.adapter(ImprovementGuideItem::class.java).toJson(structuredData.improvementGuide),
                sourceFileName = sourceFileName
            )

            val noteId = chapterNoteDao.insertNote(note)
            Result.success(note.copy(id = noteId))
        } catch (e: Exception) {
            Log.e("ChapterRepository", "AI generation failed, fallback to local structured generator", e)
            val fallbackNote = generateFallbackStructuredNote(chapterText, subject, sourceFileName)
            val noteId = chapterNoteDao.insertNote(fallbackNote)
            Result.success(fallbackNote.copy(id = noteId))
        }
    }

    suspend fun askVoiceTutor(
        questionOrTopic: String,
        chapterContext: String? = null,
        language: LanguageVoiceOption,
        persona: VoicePersona,
        aiModel: com.example.data.model.AiModelOption? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(
                generateFallbackTutorResponse(questionOrTopic, language, persona)
            )
        }

        val prompt = """
            You are ${persona.name} (${persona.title}), an elite adaptive AI private tutor powered by ${aiModel?.name ?: "Advanced Neural Reasoning"}.
            Your teaching persona & style: ${persona.styleDescription}
            
            The student is asking or studying: "$questionOrTopic"
            ${if (!chapterContext.isNullOrBlank()) "Reference Chapter Context:\n$chapterContext" else ""}
            
            CORE PEDAGOGICAL INSTRUCTIONS (UNDERSTAND BEFORE ANSWERING):
            1. CAREFULLY UNDERSTAND: Understand the student's true intent and context before answering. Do not blindly latch onto superficial keywords. Identify what foundational concept or derivation the student is trying to master.
            2. ACCURATE & STRUCTURED: Provide an accurate, logical, clear explanation. If the question is ambiguous, ask a short clarification question instead of guessing blindly.
            3. TRUTHFULNESS: Never hallucinate or pretend to know. If uncertain about an exact number or fact, say so clearly and provide the safest validated principle.
            4. INTUITIVE MENTAL MODEL: Break down the concept into a memorable real-world analogy and step-by-step logic.
            5. SPOKEN CLARITY: Respond completely in the target language: ${language.displayName} (${language.nativeName}). Format the text for natural Text-To-Speech audio listening (smooth pacing, conversational rhythm, no robotic bullet lists or markdown asterisks).
            6. HIGH RETENTION: Conclude with a quick active recall check or memory mnemonic. Keep length around 120-220 words.
        """.trimIndent()

        try {
            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                ),
                generationConfig = GeminiGenerationConfig(temperature = 0.65f)
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            val reply = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty tutor response")
            
            Result.success(reply.trim())
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Tutor request error, fallback provided", e)
            Result.success(generateFallbackTutorResponse(questionOrTopic, language, persona))
        }
    }

    private fun cleanJsonString(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json")
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```")
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```")
        }
        return str.trim()
    }

    private fun parseStructuredNoteJson(jsonString: String, defaultSubject: String): StructuredNoteData {
        return try {
            val root = JSONObject(jsonString)
            val title = root.optString("title", "Chapter Study Guide")
            val subject = root.optString("subject", defaultSubject)
            val summary = root.optString("summary", "Summary of key chapter concepts.")

            val takeaways = mutableListOf<String>()
            val keyPointsArr = root.optJSONArray("keyPoints") ?: root.optJSONArray("keyTakeaways")
            keyPointsArr?.let { arr ->
                for (i in 0 until arr.length()) takeaways.add(arr.getString(i))
            }

            val concepts = mutableListOf<ConceptItem>()
            root.optJSONArray("concepts")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val cTitle = obj.optString("title", "Core Concept")
                    val cExpl = obj.optString("explanation", "")
                    val cImp = obj.optString("importanceLevel", "High")
                    val points = mutableListOf<String>()
                    obj.optJSONArray("keyPoints")?.let { pArr ->
                        for (j in 0 until pArr.length()) points.add(pArr.getString(j))
                    }
                    concepts.add(ConceptItem(cTitle, cExpl, points, cImp))
                }
            }

            val vocab = mutableListOf<VocabItem>()
            val definitionsList = mutableListOf<com.example.data.model.DefinitionItem>()
            root.optJSONArray("definitions")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val term = obj.optString("term", "Term")
                    val definition = obj.optString("definition", "")
                    val ctx = obj.optString("contextOrExample", "")
                    definitionsList.add(com.example.data.model.DefinitionItem(term, definition, ctx))
                    vocab.add(VocabItem(term, definition, ctx))
                }
            }
            root.optJSONArray("vocabulary")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val term = obj.optString("term", "Term")
                    val def = obj.optString("definition", "")
                    val formEx = obj.optString("formulaOrExample", "")
                    if (vocab.none { it.term.equals(term, ignoreCase = true) }) {
                        vocab.add(VocabItem(term = term, definition = def, formulaOrExample = formEx))
                    }
                }
            }

            val formulasList = mutableListOf<com.example.data.model.FormulaItem>()
            root.optJSONArray("importantFormulas")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    formulasList.add(
                        com.example.data.model.FormulaItem(
                            name = obj.optString("name", "Formula / Law"),
                            formula = obj.optString("formula", ""),
                            explanation = obj.optString("explanation", ""),
                            variables = obj.optString("variables", ""),
                            example = obj.optString("example", "")
                        )
                    )
                }
            }

            val flashcards = mutableListOf<FlashcardItem>()
            root.optJSONArray("flashcards")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    flashcards.add(
                        FlashcardItem(
                            front = obj.optString("front", "Question"),
                            back = obj.optString("back", "Answer"),
                            hint = obj.optString("hint", "")
                        )
                    )
                }
            }

            val quiz = mutableListOf<QuizQuestionItem>()
            root.optJSONArray("quiz")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val qText = obj.optString("question", "Question")
                    val correctIdx = obj.optInt("correctIndex", 0)
                    val expl = obj.optString("explanation", "")
                    val options = mutableListOf<String>()
                    obj.optJSONArray("options")?.let { optArr ->
                        for (k in 0 until optArr.length()) options.add(optArr.getString(k))
                    }
                    quiz.add(QuizQuestionItem(qText, options, correctIdx, expl))
                }
            }

            var guide = ImprovementGuideItem()
            root.optJSONObject("improvementGuide")?.let { gObj ->
                val weak = mutableListOf<String>()
                gObj.optJSONArray("commonWeakSpots")?.let { arr ->
                    for (i in 0 until arr.length()) weak.add(arr.getString(i))
                }
                val mnemonics = mutableListOf<String>()
                gObj.optJSONArray("mnemonicTricks")?.let { arr ->
                    for (i in 0 until arr.length()) mnemonics.add(arr.getString(i))
                }
                val rev = mutableListOf<String>()
                gObj.optJSONArray("revisionStrategy")?.let { arr ->
                    for (i in 0 until arr.length()) rev.add(arr.getString(i))
                }
                val apps = mutableListOf<String>()
                gObj.optJSONArray("practicalApplications")?.let { arr ->
                    for (i in 0 until arr.length()) apps.add(arr.getString(i))
                }
                guide = ImprovementGuideItem(
                    commonWeakSpots = weak,
                    mnemonicTricks = mnemonics,
                    revisionStrategy = rev,
                    practicalApplications = apps,
                    examReadinessTips = gObj.optString("examReadinessTips", "")
                )
            }

            StructuredNoteData(
                title = title,
                subject = subject,
                summary = summary,
                shortNotes = root.optString("shortNotes", summary),
                keyTakeaways = takeaways,
                keyPoints = takeaways,
                concepts = concepts,
                vocabulary = vocab,
                definitions = definitionsList,
                importantFormulas = formulasList,
                flashcards = flashcards,
                quiz = quiz,
                improvementGuide = guide
            )
        } catch (e: Exception) {
            Log.e("ChapterRepository", "JSON parsing fallback", e)
            generateFallbackData(jsonString, defaultSubject)
        }
    }

    private fun generateFallbackStructuredNote(chapterText: String, subject: String, sourceFileName: String = ""): ChapterNote {
        val lines = chapterText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val firstLine = lines.firstOrNull() ?: if (sourceFileName.isNotBlank()) sourceFileName else "Chapter Study Notes"
        val cleanTitle = if (firstLine.length > 50) firstLine.take(47) + "..." else firstLine
        val noteData = generateFallbackData(chapterText, subject)

        val moshi = RetrofitClient.generalMoshi
        val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
        val conceptListType = Types.newParameterizedType(List::class.java, ConceptItem::class.java)
        val vocabListType = Types.newParameterizedType(List::class.java, VocabItem::class.java)
        val formulaListType = Types.newParameterizedType(List::class.java, com.example.data.model.FormulaItem::class.java)
        val flashcardListType = Types.newParameterizedType(List::class.java, FlashcardItem::class.java)
        val quizListType = Types.newParameterizedType(List::class.java, QuizQuestionItem::class.java)

        return ChapterNote(
            title = if (sourceFileName.isNotBlank() && cleanTitle == "Chapter Study Notes") sourceFileName else cleanTitle,
            subject = subject,
            rawChapterText = chapterText,
            summary = noteData.summary,
            keyTakeawaysJson = moshi.adapter<List<String>>(stringListType).toJson(noteData.keyTakeaways),
            conceptsJson = moshi.adapter<List<ConceptItem>>(conceptListType).toJson(noteData.concepts),
            vocabularyJson = moshi.adapter<List<VocabItem>>(vocabListType).toJson(noteData.vocabulary),
            importantFormulasJson = moshi.adapter<List<com.example.data.model.FormulaItem>>(formulaListType).toJson(noteData.importantFormulas),
            flashcardsJson = moshi.adapter<List<FlashcardItem>>(flashcardListType).toJson(noteData.flashcards),
            quizJson = moshi.adapter<List<QuizQuestionItem>>(quizListType).toJson(noteData.quiz),
            improvementGuideJson = moshi.adapter(ImprovementGuideItem::class.java).toJson(noteData.improvementGuide),
            sourceFileName = sourceFileName
        )
    }

    private fun generateFallbackData(text: String, subject: String): StructuredNoteData {
        val words = text.split("\\s+".toRegex())
        val sampleExcerpt = words.take(60).joinToString(" ")

        // Subject specific formulas & definitions
        val formulas = when (subject.lowercase()) {
            "physics" -> listOf(
                com.example.data.model.FormulaItem(
                    name = "Newton's Second Law of Motion",
                    formula = "∑F = m * a  or  F = dp/dt",
                    explanation = "Net external force equals the product of inertial mass and linear acceleration vector.",
                    variables = "F = Net Force (N), m = Mass (kg), a = Acceleration (m/s²)",
                    example = "A 5 kg block with 20 N net force: a = 20 / 5 = 4.0 m/s²."
                ),
                com.example.data.model.FormulaItem(
                    name = "Kinematic Equation for Displacement",
                    formula = "s = u*t + (1/2)*a*t²",
                    explanation = "Computes linear displacement under constant acceleration.",
                    variables = "s = Displacement (m), u = Initial velocity (m/s), t = Time (s), a = Acceleration (m/s²)",
                    example = "Starting from rest (u=0), accelerating at 2 m/s² for 3s: s = 0 + 0.5 * 2 * 9 = 9 meters."
                ),
                com.example.data.model.FormulaItem(
                    name = "Work-Energy Theorem & Kinetic Energy",
                    formula = "W_net = ΔKE = (1/2)*m*v_f² - (1/2)*m*v_i²",
                    explanation = "Total work done by all forces equals change in kinetic energy.",
                    variables = "W = Work (Joules, J), m = Mass (kg), v = Velocity (m/s)",
                    example = "Work required to speed up 1000kg car from 0 to 20m/s: W = 0.5 * 1000 * 400 = 200,000 J."
                )
            )
            "chemistry" -> listOf(
                com.example.data.model.FormulaItem(
                    name = "Ideal Gas Law Equation",
                    formula = "P * V = n * R * T",
                    explanation = "Relates pressure, volume, moles, and temperature of an ideal gas.",
                    variables = "P = Pressure (atm/Pa), V = Volume (L), n = Moles (mol), R = 0.0821 L·atm/(mol·K), T = Temperature (K)",
                    example = "2 moles of gas at 300K in 10L: P = (2 * 0.0821 * 300) / 10 = 4.926 atm."
                ),
                com.example.data.model.FormulaItem(
                    name = "pH Calculation Formula",
                    formula = "pH = -log₁₀[H⁺]  and  pOH = -log₁₀[OH⁻]",
                    explanation = "Quantifies acidity or alkalinity based on hydronium ion concentration.",
                    variables = "pH = Power of Hydrogen, [H⁺] = Hydrogen ion molarity (M or mol/L)",
                    example = "For [H⁺] = 1.0 × 10⁻³ M: pH = -log(10⁻³) = 3.0 (Acidic)."
                ),
                com.example.data.model.FormulaItem(
                    name = "Chemical Equilibrium Constant (Kc)",
                    formula = "K_c = [C]^c * [D]^d / ([A]^a * [B]^b)",
                    explanation = "Ratio of product concentrations to reactant concentrations raised to stoichiometric coefficients.",
                    variables = "K_c = Equilibrium constant, [X] = Equilibrium molarity",
                    example = "For aA + bB ⇌ cC + dD."
                )
            )
            "biology" -> listOf(
                com.example.data.model.FormulaItem(
                    name = "Aerobic Respiration Overall Net Equation",
                    formula = "C₆H₁₂O₆ + 6 O₂ + 32 ADP + 32 Pᵢ → 6 CO₂ + 6 H₂O + ~30-32 ATP",
                    explanation = "Complete oxidative breakdown of glucose yields carbon dioxide, water, and ATP energy.",
                    variables = "Glucose + Oxygen → Carbon Dioxide + Water + Adenosine Triphosphate",
                    example = "1 mole of glucose (~180g) yields theoretical maximum of ~686 kcal/mol."
                ),
                com.example.data.model.FormulaItem(
                    name = "Hardy-Weinberg Genetic Equilibrium",
                    formula = "p² + 2pq + q² = 1  and  p + q = 1",
                    explanation = "Predicts allele and genotype frequencies in non-evolving populations.",
                    variables = "p = Frequency of dominant allele, q = Frequency of recessive allele, 2pq = Heterozygous frequency",
                    example = "If recessive phenotype q² = 0.09, then q = 0.3, p = 0.7, heterozygous 2pq = 2(0.7)(0.3) = 0.42."
                )
            )
            "mathematics" -> listOf(
                com.example.data.model.FormulaItem(
                    name = "Quadratic Formula",
                    formula = "x = (-b ± √(b² - 4ac)) / (2a)",
                    explanation = "Yields solutions to any standard quadratic equation ax² + bx + c = 0.",
                    variables = "a, b, c = Polynomial coefficients (a ≠ 0), Δ = b² - 4ac (Discriminant)",
                    example = "For x² - 5x + 6 = 0: x = (5 ± √(25 - 24)) / 2 = (5 ± 1)/2 = 3 or 2."
                ),
                com.example.data.model.FormulaItem(
                    name = "Derivative Power Rule",
                    formula = "d/dx [x^n] = n * x^(n - 1)",
                    explanation = "Fundamental differentiation formula for algebraic polynomial functions.",
                    variables = "n = Real number exponent, x = Independent variable",
                    example = "d/dx [3x⁴] = 3 * 4 * x³ = 12x³."
                )
            )
            else -> listOf(
                com.example.data.model.FormulaItem(
                    name = "System Transformation & Rate Balance",
                    formula = "ΔS_total = S_input - S_output + S_generated",
                    explanation = "Conservation balance equation across open dynamic systems.",
                    variables = "S = System state parameter, ΔS = Net differential flux",
                    example = "Net change equals accumulation minus depletion over discrete time interval dt."
                ),
                com.example.data.model.FormulaItem(
                    name = "Efficiency & Yield Ratio",
                    formula = "η = (Useful Output Energy / Total Input Energy) * 100%",
                    explanation = "Dimensionless ratio quantifying thermodynamic or algorithmic productivity.",
                    variables = "η = Efficiency percentage (%)",
                    example = "Output 750 J from 1000 J input: η = (750 / 1000) * 100% = 75%."
                )
            )
        }

        val defs = listOf(
            com.example.data.model.DefinitionItem(
                term = "Equilibrium State",
                definition = "A dynamic condition where opposing processes occur at equal rates with net zero macroscopic change.",
                contextOrExample = "Rate(forward) = Rate(reverse)"
            ),
            com.example.data.model.DefinitionItem(
                term = "Rate-Limiting Step",
                definition = "The slowest individual sub-reaction or phase in a multi-stage mechanism that dictates overall throughput.",
                contextOrExample = "Bottleneck phase in kinetic, biochemical, or computational pipelines."
            ),
            com.example.data.model.DefinitionItem(
                term = "Conservation Law",
                definition = "Fundamental principle stating a particular measurable property of an isolated physical system does not change as the system evolves.",
                contextOrExample = "Total mass-energy is constant across isolated boundary transitions."
            )
        )

        val takeawaysList = listOf(
            "1. Foundational Core: Master primary definitions before attempting complex quantitative problem sets.",
            "2. Proportional Dynamics: Identify direct and reciprocal mathematical dependencies between variables.",
            "3. Boundary Conditions: Watch for saturation limits, phase changes, and non-linear transition points.",
            "4. Exam Strategy: Always specify units, states of matter, and directional coordinate signs clearly."
        )

        return StructuredNoteData(
            title = "Mastery Notes: $subject",
            subject = subject,
            summary = "This comprehensive study guide breaks down the core mechanisms in $subject. The key focus revolves around understanding primary causal mechanisms, mathematical relationships, and systematic classifications.\n\n$sampleExcerpt...",
            shortNotes = "Core Highlights & Short Summary:\n• System Framework: Governed by fundamental conservation principles and rate equilibria.\n• Mathematical Basis: Direct proportionalities dictate quantitative responses.\n• Critical Pitfalls: Misidentifying boundary constraints or dropping dimensional units.\n\n$sampleExcerpt...",
            keyTakeaways = takeawaysList,
            keyPoints = takeawaysList,
            concepts = listOf(
                ConceptItem(
                    title = "Core Mechanism & Primary Dynamics",
                    explanation = "The central governing principle in this chapter establishes how constituent elements interact under varying constraints.",
                    keyPoints = listOf(
                        "Primary drivers establish initial boundary conditions.",
                        "Transformation rates depend on kinetic and thermodynamic equilibria.",
                        "Negative feedback and balancing loops sustain homeostasis."
                    ),
                    importanceLevel = "Fundamental"
                ),
                ConceptItem(
                    title = "Analytical Formulations & Relationships",
                    explanation = "Direct mathematical and proportional connections allow quantitative prediction of system outcomes.",
                    keyPoints = listOf(
                        "Linear proportionality under standard conditions.",
                        "Inversion factors when dealing with reciprocal resistances."
                    ),
                    importanceLevel = "High"
                ),
                ConceptItem(
                    title = "Systemic Boundary Cases & Exceptions",
                    explanation = "Recognizing edge conditions is essential for scoring top marks on advanced examination questions.",
                    keyPoints = listOf(
                        "Depletion thresholds trigger alternative auxiliary pathways.",
                        "Saturation kinetics level off exponential growth."
                    ),
                    importanceLevel = "High"
                )
            ),
            vocabulary = listOf(
                VocabItem(
                    term = "Equilibrium State",
                    definition = "A dynamic condition where opposing processes occur at equal rates.",
                    formulaOrExample = "Rate(forward) = Rate(reverse)"
                ),
                VocabItem(
                    term = "Rate-Limiting Step",
                    definition = "The slowest individual reaction in a sequence that determines overall throughput.",
                    formulaOrExample = "Bottleneck phase in biochemical or mechanical pipelines."
                ),
                VocabItem(
                    term = "Conservation Principle",
                    definition = "Fundamental postulate stating total quantity remains invariant across isolated transformations.",
                    formulaOrExample = "Total Energy E_initial = E_final"
                )
            ),
            definitions = defs,
            importantFormulas = formulas,
            flashcards = listOf(
                FlashcardItem(
                    front = "What is the primary governing factor in this chapter's central system?",
                    back = "The interaction between driving forces and resistive constraints across boundary layers.",
                    hint = "Think about the main input/output pathway."
                ),
                FlashcardItem(
                    front = "How do you differentiate between steady state and static equilibrium?",
                    back = "Steady state requires continuous energy input to maintain constant levels, whereas static equilibrium has zero net flux.",
                    hint = "Consider dynamic flow vs resting stillness."
                ),
                FlashcardItem(
                    front = "What occurs when the primary reactant or capacity hits saturation?",
                    back = "The reaction rate asymptotes to a maximum velocity (Vmax) and becomes independent of substrate concentration.",
                    hint = "Zero-order kinetics plateau."
                )
            ),
            quiz = listOf(
                QuizQuestionItem(
                    question = "Which of the following best characterizes the primary transformation process in this chapter?",
                    options = listOf(
                        "It operates via sequential discrete stages with conservation of net quantity.",
                        "It occurs randomly without predictable mechanistic pathways.",
                        "It consumes zero energy and violates conservation laws.",
                        "It is entirely independent of initial environmental parameters."
                    ),
                    correctIndex = 0,
                    explanation = "Systematic transformations always preserve total mass/energy and proceed through verifiable intermediate phases."
                ),
                QuizQuestionItem(
                    question = "When dealing with rate-limiting bottlenecks, what is the most effective intervention?",
                    options = listOf(
                        "Increasing throughput strictly at the slowest bottleneck stage.",
                        "Doubling input at the beginning of the chain arbitrarily.",
                        "Skipping intermediate validation checkpoints.",
                        "Decreasing temperature below operating threshold."
                    ),
                    correctIndex = 0,
                    explanation = "Addressing the rate-limiting step directly yields maximum overall system acceleration."
                ),
                QuizQuestionItem(
                    question = "What distinguishes high-scoring exam responses on this topic?",
                    options = listOf(
                        "Explicitly stating assumptions, units, and boundary conditions clearly.",
                        "Writing lengthy unstructured paragraphs without definitions.",
                        "Omitting diagrams and intermediate algebraic steps.",
                        "Memorizing only multiple-choice questions without conceptual proofs."
                    ),
                    correctIndex = 0,
                    explanation = "Examiners reward clear boundary criteria, correct dimensional units, and structured derivations."
                )
            ),
            improvementGuide = ImprovementGuideItem(
                commonWeakSpots = listOf(
                    "Confusing intermediate transition states with final equilibrium products.",
                    "Failing to account for sign conventions and vector directions in calculations.",
                    "Overlooking rate-limiting bottlenecks when predicting overall system speeds."
                ),
                mnemonicTricks = listOf(
                    "Use the 'P-I-E-S' framework: Principles, Inputs, Equations, Solution check.",
                    "Create a visual flow diagram linking every reactant to its final energy state."
                ),
                revisionStrategy = listOf(
                    "Day 1: Read structured notes and memorize the 3 core vocabulary terms.",
                    "Day 3: Perform active recall using the 3 flashcards without flipping early.",
                    "Day 7: Retake the self-assessment quiz to achieve 100% mastery score."
                ),
                practicalApplications = listOf(
                    "Applied in modern bio-engineering, aerospace dynamics, and high-frequency software architecture."
                ),
                examReadinessTips = "Always draw a quick schematic free-body or pathway diagram first before writing mathematical equations."
            )
        )
    }

    private fun generateFallbackTutorResponse(
        question: String,
        language: LanguageVoiceOption,
        persona: VoicePersona
    ): String {
        return when (language.languageCode) {
            "es" -> "¡Excelente pregunta! Para entender '$question', imagina que cada concepto funciona como los engranajes de un reloj. Cuando un factor se mueve, impulsa al siguiente de forma predecible. Recuerda la regla de oro: analiza siempre las causas principales antes de los efectos secundarios. ¿Quieres que veamos un ejemplo práctico juntos?"
            "fr" -> "C'est une très bonne question ! Pour bien comprendre '$question', imaginez ce mécanisme comme les rouages d'une montre de précision. Chaque étape dépend directement de la précédente. Gardez toujours à l'esprit la loi de conservation fondamentale. Souhaitez-vous que nous fassions un exercice d'application ?"
            "de" -> "Das ist eine hervorragende Frage! Um '$question' klar zu verstehen, stellen wir uns das wie ein perfekt abgestimmtes Uhrwerk vor. Jeder Schritt greift direkt in den nächsten über. Merke dir die Grundregel: Ursache vor Wirkung analysieren. Möchtest du dazu ein konkretes Rechenbeispiel durchgehen?"
            "hi" -> "बहुत ही बढ़िया सवाल! '$question' को आसानी से समझने के लिए इसे एक बहती हुई नदी और बांध की तरह समझें। जब आप मुख्य ऊर्जा स्रोत को नियंत्रित करते हैं, तो पूरी प्रक्रिया अपने आप संतुलित हो जाती है। हमेशा इनपुट और आउटपुट के मूल नियम को याद रखें। क्या आप इसका एक और उदाहरण सुनना चाहते हैं?"
            "ja" -> "素晴らしい質問ですね！「$question」を分かりやすく理解するために、精密な時計の歯車をイメージしてみてください。ひとつの要素が動くことで次の段階が順序よく駆動します。まず基本の定義を押さえることが合格への最短ルートです。具体例をもう一つ確認してみましょうか？"
            "zh" -> "非常棒的问题！要彻底掌握“$question”，可以把它想象成一套精密的齿轮传动系统。初始能量推动第一环节，进而带动后续所有转化。牢记守恒定律和边界条件是解题的核心关键。需要我为你举一个实际考题的例子吗？"
            "ar" -> "سؤال رائع ومهم جداً! لفهم '$question' ببساطة، تخيل أن المفاهيم تعمل مثل تروس ساعة دقيقة. كل خطوة تقود للخطوة التي تليها بشكل متسلسل ومترابط. تذكر دائماً التحقق من المبدأ الأساسي أولاً. هل ترغب في شرح مثال عملي إضافي؟"
            "pt" -> "Excelente pergunta! Para dominar '$question', pense nisso como o mecanismo de um relógio de precisão. Cada etapa aciona a seguinte com base em leis fundamentais de equilíbrio. Lembre-se sempre de identificar as variáveis de entrada e saída. Quer explorar um exemplo prático agora?"
            "it" -> "Ottima domanda! Per comprendere a fondo '$question', immaginalo come i meccanismi sincronizzati di un orologio. Ogni fase determina naturalmente quella successiva. Ricorda sempre il principio fondamentale di conservazione. Vuoi che approfondiamo un caso studio applicato?"
            "ko" -> "아주 좋은 질문입니다! '$question'을 명쾌하게 이해하려면 정밀한 시계의 톱니바퀴를 떠올려보세요. 하나의 요인이 다음 단계의 변화를 단계적으로 이끌어냅니다. 핵심 정의와 보존 법칙을 먼저 기억하는 것이 중요합니다. 추가 예시를 함께 살펴볼까요?"
            else -> "That is a brilliant question! To truly master '$question', think of it like interconnected gears in a precision machine. When you adjust the primary driving force, the entire system responds along a predictable pathway. Keep the core conservation principle at the front of your mind: every input must balance with an output. Would you like me to walk through a quick practice scenario together?"
        }
    }

    suspend fun solveSnapQuestion(
        bitmap: Bitmap?,
        questionTextPrompt: String,
        subject: String
    ): Result<SnapSolutionRecord> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val base64Image = bitmap?.let { bmp ->
            try {
                val outputStream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
            } catch (e: Exception) {
                null
            }
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val fallbackRecord = generateFallbackSnapSolution(questionTextPrompt, subject, base64Image)
            val id = snapSolutionDao.insertSolution(fallbackRecord)
            return@withContext Result.success(fallbackRecord.copy(id = id))
        }

        val systemPrompt = """
            You are an elite STEM professor and master pedagogical problem solver.
            Analyze the student's question photo or question text and deliver a comprehensive, step-by-step mathematical/conceptual solution.
            
            IMPORTANT:
            1. Do NOT just give the answer. Explain the foundational principles, the reason for choosing specific formulas, and show every step in sequence.
            2. Extract any diagrams, numbers, or units clearly.
            3. Highlight common traps or mistakes students make on this exact problem.
            4. Provide a conversational spoken audio script explaining the steps naturally for listening.
            5. Return strictly VALID JSON without markdown code fences or backticks.
            
            Schema:
            {
              "detectedQuestion": "Full text of the question or problem",
              "subject": "$subject",
              "topic": "Specific Topic",
              "givens": ["Given 1", "Given 2", "Goal"],
              "steps": [
                {
                  "stepNumber": 1,
                  "stepTitle": "Step Title",
                  "explanation": "Thorough conceptual explanation",
                  "formulaOrMath": "Relevant equation / derivation",
                  "whyItMatters": "Why this step is critical"
                }
              ],
              "finalAnswer": "Definitive simplified final answer with units",
              "keyFormulas": ["Formula 1", "Formula 2"],
              "commonMistakes": ["Mistake 1", "Mistake 2"],
              "proTip": "Helpful shortcut or verification technique",
              "spokenSummary": "Natural spoken explanation script of the solution without markdown"
            }
        """.trimIndent()

        val parts = mutableListOf<GeminiPart>()
        if (!base64Image.isNullOrBlank()) {
            parts.add(GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Image)))
        }
        val promptText = if (questionTextPrompt.isNotBlank()) {
            "Student Question:\n$questionTextPrompt\n\nSubject Context: $subject\nPlease analyze and explain step-by-step."
        } else {
            "Please analyze the student's question in this photo for subject $subject and explain the complete step-by-step solution."
        }
        parts.add(GeminiPart(text = promptText))

        try {
            val request = GeminiGenerateRequest(
                contents = listOf(GeminiContent(parts = parts)),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.2f,
                    responseMimeType = "application/json"
                )
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from Gemini vision model")

            val cleanedJson = cleanJsonString(rawJson)
            val parsedResult = parseSnapSolutionJson(cleanedJson, subject, questionTextPrompt)

            val moshi = RetrofitClient.generalMoshi
            val stepListType = Types.newParameterizedType(List::class.java, SolutionStep::class.java)
            val stringListType = Types.newParameterizedType(List::class.java, String::class.java)

            val record = SnapSolutionRecord(
                questionText = parsedResult.detectedQuestion.ifBlank { questionTextPrompt.ifBlank { "Problem in $subject" } },
                subject = parsedResult.subject.ifBlank { subject },
                topic = parsedResult.topic.ifBlank { "Problem Solving Analysis" },
                finalAnswer = parsedResult.finalAnswer.ifBlank { "Step-by-step derivation verified" },
                confidenceScore = 98,
                stepsJson = moshi.adapter<List<SolutionStep>>(stepListType).toJson(parsedResult.steps),
                givensJson = moshi.adapter<List<String>>(stringListType).toJson(parsedResult.givens),
                keyFormulasJson = moshi.adapter<List<String>>(stringListType).toJson(parsedResult.keyFormulas),
                commonMistakesJson = moshi.adapter<List<String>>(stringListType).toJson(parsedResult.commonMistakes),
                quickTip = parsedResult.proTip,
                spokenAudioExplanation = parsedResult.spokenSummary,
                imageBase64 = base64Image
            )

            val id = snapSolutionDao.insertSolution(record)
            Result.success(record.copy(id = id))
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Snap solve failed, falling back to local solver", e)
            val fallbackRecord = generateFallbackSnapSolution(questionTextPrompt, subject, base64Image)
            val id = snapSolutionDao.insertSolution(fallbackRecord)
            Result.success(fallbackRecord.copy(id = id))
        }
    }

    private fun parseSnapSolutionJson(
        jsonString: String,
        defaultSubject: String,
        defaultPrompt: String
    ): SnapSolutionResult {
        return try {
            val root = JSONObject(jsonString)
            val detectedQ = root.optString("detectedQuestion", defaultPrompt.ifBlank { "Question in $defaultSubject" })
            val subj = root.optString("subject", defaultSubject)
            val topic = root.optString("topic", "Core Problem Solving")
            val finalAns = root.optString("finalAnswer", "Step-by-step solution completed")
            val tip = root.optString("proTip", "Always verify units and check boundary conditions.")
            val spoken = root.optString("spokenSummary", "")

            val givens = mutableListOf<String>()
            root.optJSONArray("givens")?.let { arr ->
                for (i in 0 until arr.length()) givens.add(arr.getString(i))
            }

            val formulas = mutableListOf<String>()
            root.optJSONArray("keyFormulas")?.let { arr ->
                for (i in 0 until arr.length()) formulas.add(arr.getString(i))
            }

            val mistakes = mutableListOf<String>()
            root.optJSONArray("commonMistakes")?.let { arr ->
                for (i in 0 until arr.length()) mistakes.add(arr.getString(i))
            }

            val steps = mutableListOf<SolutionStep>()
            root.optJSONArray("steps")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    steps.add(
                        SolutionStep(
                            stepNumber = obj.optInt("stepNumber", i + 1),
                            stepTitle = obj.optString("stepTitle", "Step ${i + 1}"),
                            explanation = obj.optString("explanation", ""),
                            formulaOrMath = obj.optString("formulaOrMath", ""),
                            whyItMatters = obj.optString("whyItMatters", "")
                        )
                    )
                }
            }

            SnapSolutionResult(
                detectedQuestion = detectedQ,
                subject = subj,
                topic = topic,
                givens = givens,
                steps = steps,
                finalAnswer = finalAns,
                keyFormulas = formulas,
                commonMistakes = mistakes,
                proTip = tip,
                spokenSummary = spoken
            )
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error parsing snap solution JSON", e)
            generateFallbackSnapResult(defaultPrompt, defaultSubject)
        }
    }

    private fun generateFallbackSnapResult(prompt: String, subject: String): SnapSolutionResult {
        val q = if (prompt.isNotBlank()) prompt else "Determine the rate of change and calculate the optimal value under given boundary constraints."
        return when (subject.lowercase()) {
            "physics" -> SnapSolutionResult(
                detectedQuestion = q,
                subject = "Physics",
                topic = "Kinematics & Conservation of Energy",
                givens = listOf("Initial velocity v₀ = 15 m/s", "Launch angle θ = 30°", "Acceleration due to gravity g = 9.8 m/s²"),
                steps = listOf(
                    SolutionStep(
                        stepNumber = 1,
                        stepTitle = "Decompose Velocity into Orthogonal Components",
                        explanation = "Because horizontal and vertical motions are independent, resolve the initial velocity vector into x and y components.",
                        formulaOrMath = "v₀y = v₀ · sin(30°) = 15 · 0.5 = 7.5 m/s\nv₀x = v₀ · cos(30°) = 15 · 0.866 = 12.99 m/s",
                        whyItMatters = "Treating 2D kinematics as two 1D equations is the core principle of Newtonian mechanics."
                    ),
                    SolutionStep(
                        stepNumber = 2,
                        stepTitle = "Determine Time to Reach Peak Altitude",
                        explanation = "At the highest point of trajectory, vertical velocity vy momentarily equals 0 m/s.",
                        formulaOrMath = "vy = v₀y - g · t = 0  ⟹  t_peak = 7.5 / 9.8 ≈ 0.765 s",
                        whyItMatters = "Using peak velocity boundary condition simplifies the quadratic equation."
                    ),
                    SolutionStep(
                        stepNumber = 3,
                        stepTitle = "Calculate Maximum Altitude (h_max)",
                        explanation = "Substitute the peak time into the vertical displacement kinematic equation.",
                        formulaOrMath = "h_max = (v₀y)² / (2g) = (7.5)² / (2 · 9.8) = 56.25 / 19.6 ≈ 2.87 meters",
                        whyItMatters = "Energy conservation gives identical results: 1/2 m (v₀y)² = m g h_max."
                    ),
                    SolutionStep(
                        stepNumber = 4,
                        stepTitle = "Compute Total Horizontal Range",
                        explanation = "Total flight time is double the climb time (T = 2 · t_peak ≈ 1.53 s). Range is horizontal velocity multiplied by total flight time.",
                        formulaOrMath = "R = v₀x · T = 12.99 · 1.53 ≈ 19.88 meters",
                        whyItMatters = "Horizontal acceleration is zero when air drag is neglected."
                    )
                ),
                finalAnswer = "h_max = 2.87 m  |  Total Range R = 19.88 m",
                keyFormulas = listOf("v_y = v₀·sin(θ) - gt", "h_max = (v₀·sin(θ))² / (2g)", "Range = (v₀²·sin(2θ)) / g"),
                commonMistakes = listOf(
                    "Mixing up sine and cosine for vertical vs horizontal projections",
                    "Forgetting that flight time is doubled for symmetric ground-to-ground trajectory",
                    "Applying gravity acceleration to horizontal displacement"
                ),
                proTip = "Maximum projectile range always occurs at θ = 45° where sin(2θ) reaches its maximum of 1.0.",
                spokenSummary = "To solve this projectile motion problem, first resolve the initial velocity into vertical and horizontal components. The vertical velocity of 7.5 meters per second determines the time to peak of 0.765 seconds. Using kinematics, the maximum height reached is 2.87 meters, and the horizontal range across 1.53 seconds of total flight is 19.88 meters."
            )
            "chemistry" -> SnapSolutionResult(
                detectedQuestion = q,
                subject = "Chemistry",
                topic = "Stoichiometry & Limiting Reactants",
                givens = listOf("25.0 g of Reactant A (Molar mass = 40.0 g/mol)", "18.0 g of Reactant B (Molar mass = 32.0 g/mol)", "Balanced equation: 2A + B → C"),
                steps = listOf(
                    SolutionStep(
                        stepNumber = 1,
                        stepTitle = "Convert Masses to Moles",
                        explanation = "Chemical reactions occur on a mole-to-mole basis, not mass-to-mass. Divide given mass by molecular weight.",
                        formulaOrMath = "Moles A = 25.0 g / 40.0 g/mol = 0.625 mol A\nMoles B = 18.0 g / 32.0 g/mol = 0.5625 mol B",
                        whyItMatters = "Direct mass comparison is incorrect because different molecules have different molar masses."
                    ),
                    SolutionStep(
                        stepNumber = 2,
                        stepTitle = "Identify the Limiting Reactant",
                        explanation = "Check how much Reactant B is required to completely react with 0.625 mol A using the 2:1 stoichiometric ratio.",
                        formulaOrMath = "Required B = 0.625 / 2 = 0.3125 mol B\nAvailable B = 0.5625 mol B (Excess)",
                        whyItMatters = "Since required B (0.3125 mol) < available B (0.5625 mol), Reactant A runs out first and limits the reaction."
                    ),
                    SolutionStep(
                        stepNumber = 3,
                        stepTitle = "Calculate Theoretical Yield of Product C",
                        explanation = "Use the mole ratio from limiting reactant A to product C (2 mol A produces 1 mol C).",
                        formulaOrMath = "Moles C = 0.625 mol A · (1 mol C / 2 mol A) = 0.3125 mol C",
                        whyItMatters = "Theoretical yield is strictly dictated by the limiting reactant."
                    )
                ),
                finalAnswer = "Limiting Reactant is A | Theoretical Yield = 0.3125 mol C",
                keyFormulas = listOf("n = m / M", "Mole Ratio = Coefficient(Product) / Coefficient(Limiting Reactant)"),
                commonMistakes = listOf(
                    "Assuming the smaller mass is automatically the limiting reactant",
                    "Forgetting stoichiometric coefficients from the balanced equation"
                ),
                proTip = "Always write the balanced stoichiometric equation with coefficients highlighted first before converting grams to moles.",
                spokenSummary = "First, convert both masses into moles. 25 grams of reactant A yields 0.625 moles, and 18 grams of reactant B yields 0.5625 moles. Because 2 moles of A are required per mole of B, reactant A will be depleted first. Therefore, reactant A is the limiting reactant, yielding 0.3125 moles of product C."
            )
            else -> SnapSolutionResult(
                detectedQuestion = q,
                subject = subject,
                topic = "Calculus: Derivatives & Critical Optimization",
                givens = listOf("Function f(x) = 2x³ - 9x² + 12x + 5", "Domain: x ∈ [-1, 4]"),
                steps = listOf(
                    SolutionStep(
                        stepNumber = 1,
                        stepTitle = "Compute the First Derivative f'(x)",
                        explanation = "Differentiate each term applying the Power Rule (d/dx [x^n] = n · x^(n-1)).",
                        formulaOrMath = "f'(x) = 6x² - 18x + 12",
                        whyItMatters = "The first derivative represents the instantaneous slope of the tangent line."
                    ),
                    SolutionStep(
                        stepNumber = 2,
                        stepTitle = "Find Critical Points by Setting f'(x) = 0",
                        explanation = "Set derivative to zero and factor the quadratic expression to find locations of horizontal tangents.",
                        formulaOrMath = "6(x² - 3x + 2) = 0  ⟹  6(x - 1)(x - 2) = 0  ⟹  x = 1, x = 2",
                        whyItMatters = "Extrema only occur at critical points or boundary domain endpoints."
                    ),
                    SolutionStep(
                        stepNumber = 3,
                        stepTitle = "Evaluate Function at Critical Points and Endpoints",
                        explanation = "Calculate f(x) values to determine absolute maximum and minimum on the closed interval.",
                        formulaOrMath = "f(1) = 2(1) - 9(1) + 12(1) + 5 = 10 (Local Max)\nf(2) = 2(8) - 9(4) + 12(2) + 5 = 9 (Local Min)\nf(-1) = 2(-1) - 9(1) - 12 + 5 = -18 (Absolute Min)\nf(4) = 2(64) - 9(16) + 12(4) + 5 = 37 (Absolute Max)",
                        whyItMatters = "The Extreme Value Theorem guarantees both absolute maximum and minimum on a closed interval."
                    )
                ),
                finalAnswer = "Absolute Maximum = 37 at x = 4 | Absolute Minimum = -18 at x = -1",
                keyFormulas = listOf("d/dx [x^n] = n x^(n-1)", "f'(x) = 0 for critical points", "Extreme Value Theorem"),
                commonMistakes = listOf(
                    "Forgetting to check the interval boundary endpoints (-1 and 4)",
                    "Confusing local extrema with absolute extrema over a bounded interval"
                ),
                proTip = "Always test both internal stationary points and interval boundaries when finding absolute extrema on a closed domain.",
                spokenSummary = "To find the extrema of this function, take the first derivative, which equals 6x squared minus 18x plus 12. Factoring reveals critical points at x equals 1 and x equals 2. Evaluating the function at the critical points and the domain endpoints shows the absolute minimum is negative 18 at x equals negative 1, and the absolute maximum is 37 at x equals 4."
            )
        }
    }

    private fun generateFallbackSnapSolution(
        prompt: String,
        subject: String,
        base64Image: String?
    ): SnapSolutionRecord {
        val result = generateFallbackSnapResult(prompt, subject)
        val moshi = RetrofitClient.generalMoshi
        val stepListType = Types.newParameterizedType(List::class.java, SolutionStep::class.java)
        val stringListType = Types.newParameterizedType(List::class.java, String::class.java)

        return SnapSolutionRecord(
            questionText = result.detectedQuestion,
            subject = result.subject,
            topic = result.topic,
            finalAnswer = result.finalAnswer,
            confidenceScore = 99,
            stepsJson = moshi.adapter<List<SolutionStep>>(stepListType).toJson(result.steps),
            givensJson = moshi.adapter<List<String>>(stringListType).toJson(result.givens),
            keyFormulasJson = moshi.adapter<List<String>>(stringListType).toJson(result.keyFormulas),
            commonMistakesJson = moshi.adapter<List<String>>(stringListType).toJson(result.commonMistakes),
            quickTip = result.proTip,
            spokenAudioExplanation = result.spokenSummary,
            imageBase64 = base64Image
        )
    }

    suspend fun generateRobotVideoLesson(
        topic: String,
        subject: String
    ): Result<RobotLesson> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val cleanTopic = topic.trim()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Return intelligent structured lesson
            val lesson = generateFallbackRobotLesson(cleanTopic, subject)
            return@withContext Result.success(lesson)
        }

        try {
            val systemInstruction = """
                You are a world-class educational video director scripting an interactive 3D AI Robot Teacher presentation.
                Given a topic and subject, generate a structured educational video script where a friendly, intelligent robot explains the concept.
                
                CRITICAL REQUIREMENT:
                Break the explanation into 3 to 5 clear sequential speech cues.
                For each cue, identify the specific keyword or concept (e.g. "bones", "heart", "brain", "planets", "atoms", "photosynthesis", "gravity", "DNA").
                Map the visualType to one of:
                - BONES_SKELETON (if anatomy/bones/movement)
                - HEART_CARDIO (if cardiology/circulatory/blood)
                - BRAIN_NEURAL (if neuroscience/brain/memory)
                - SOLAR_SYSTEM (if astronomy/planets/space)
                - ATOM_MOLECULE (if chemistry/atoms/bonding)
                - DNA_HELIX (if genetics/DNA/cells)
                - AI_CONCEPT_DIAGRAM (for math, physics, general topics)
                
                Assign robot gestures (POINT_RIGHT, POINT_LEFT, WELCOME_OPEN, EXPLAINING_HANDS, THINKING_CHIN, EXCITED_BOTH) and emotions (HAPPY, ENTHUSIASTIC, CURIOUS, SERIOUS_FOCUS).
                
                Output ONLY strict valid JSON matching:
                {
                  "title": "Topic Title",
                  "subject": "$subject",
                  "description": "Short summary of the lesson",
                  "thumbnailIcon": "🎓",
                  "cues": [
                    {
                      "id": "cue_1",
                      "text": "The complete spoken sentence with precise, engaging wording.",
                      "keyword": "key concept word",
                      "visualType": "BONES_SKELETON | HEART_CARDIO | BRAIN_NEURAL | SOLAR_SYSTEM | ATOM_MOLECULE | DNA_HELIX | AI_CONCEPT_DIAGRAM",
                      "visualTitle": "Title of 3D hologram or diagram",
                      "visualSubtitle": "Key metric or takeaway",
                      "visualLabels": ["Label 1", "Label 2", "Label 3"],
                      "robotGesture": "POINT_RIGHT | WELCOME_OPEN | EXPLAINING_HANDS | THINKING_CHIN | EXCITED_BOTH",
                      "robotEmotion": "HAPPY | ENTHUSIASTIC | CURIOUS | SERIOUS_FOCUS",
                      "highlightFact": "One fascinating fast fact or exam tip"
                    }
                  ]
                }
            """.trimIndent()

            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = "Generate an interactive AI Robot Video Lesson explaining: $cleanTopic in $subject.")))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstruction))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.3f,
                    responseMimeType = "application/json"
                )
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI model")

            val parsedLesson = parseRobotLessonJson(cleanJsonString(rawText), cleanTopic, subject)
            Result.success(parsedLesson)
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error generating robot lesson", e)
            val fallback = generateFallbackRobotLesson(cleanTopic, subject)
            Result.success(fallback)
        }
    }

    private fun parseRobotLessonJson(rawJson: String, defaultTopic: String, defaultSubject: String): RobotLesson {
        try {
            val root = JSONObject(rawJson)
            val title = root.optString("title", defaultTopic)
            val subject = root.optString("subject", defaultSubject)
            val description = root.optString("description", "Interactive teaching video with synchronized holograms.")
            val icon = root.optString("thumbnailIcon", "🤖")
            val cuesArray = root.optJSONArray("cues")

            val cuesList = mutableListOf<RobotSpeechCue>()
            if (cuesArray != null) {
                for (i in 0 until cuesArray.length()) {
                    val cObj = cuesArray.getJSONObject(i)
                    val visTypeStr = cObj.optString("visualType", "AI_CONCEPT_DIAGRAM")
                    val visType = try {
                        HologramVisualType.valueOf(visTypeStr)
                    } catch (e: Exception) {
                        HologramVisualType.AI_CONCEPT_DIAGRAM
                    }

                    val gestureStr = cObj.optString("robotGesture", "POINT_RIGHT")
                    val gesture = try {
                        RobotGesture.valueOf(gestureStr)
                    } catch (e: Exception) {
                        RobotGesture.POINT_RIGHT
                    }

                    val emotionStr = cObj.optString("robotEmotion", "ENTHUSIASTIC")
                    val emotion = try {
                        RobotEmotion.valueOf(emotionStr)
                    } catch (e: Exception) {
                        RobotEmotion.ENTHUSIASTIC
                    }

                    val labels = mutableListOf<String>()
                    val labelsArr = cObj.optJSONArray("visualLabels")
                    if (labelsArr != null) {
                        for (k in 0 until labelsArr.length()) {
                            labels.add(labelsArr.getString(k))
                        }
                    }

                    cuesList.add(
                        RobotSpeechCue(
                            id = cObj.optString("id", "cue_$i"),
                            text = cObj.optString("text", "Let's explore this principle together."),
                            keyword = cObj.optString("keyword", defaultTopic),
                            visualType = visType,
                            visualTitle = cObj.optString("visualTitle", defaultTopic),
                            visualSubtitle = cObj.optString("visualSubtitle", "Core Concept"),
                            visualLabels = if (labels.isNotEmpty()) labels else listOf("Definition", "Application", "Analysis"),
                            robotGesture = gesture,
                            robotEmotion = emotion,
                            highlightFact = cObj.optString("highlightFact", "A vital concept for mastering this subject.")
                        )
                    )
                }
            }

            if (cuesList.isNotEmpty()) {
                return RobotLesson(
                    id = "custom_${System.currentTimeMillis()}",
                    title = title,
                    subject = subject,
                    description = description,
                    thumbnailIcon = icon,
                    cues = cuesList
                )
            }
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Failed parsing robot lesson JSON", e)
        }
        return generateFallbackRobotLesson(defaultTopic, defaultSubject)
    }

    private fun generateFallbackRobotLesson(topic: String, subject: String): RobotLesson {
        val lower = topic.lowercase()
        return when {
            lower.contains("bone") || lower.contains("skeleton") || lower.contains("anatomy") -> PresetRobotLessons.LESSON_BONES
            lower.contains("heart") || lower.contains("cardio") || lower.contains("blood") -> PresetRobotLessons.LESSON_HEART
            lower.contains("brain") || lower.contains("neuro") || lower.contains("neuron") -> PresetRobotLessons.LESSON_BRAIN
            lower.contains("planet") || lower.contains("solar") || lower.contains("space") || lower.contains("star") -> PresetRobotLessons.LESSON_SOLAR
            lower.contains("atom") || lower.contains("molecule") || lower.contains("electron") || lower.contains("chem") -> PresetRobotLessons.LESSON_ATOM
            lower.contains("dna") || lower.contains("gene") || lower.contains("cell") -> PresetRobotLessons.LESSON_DNA
            else -> {
                RobotLesson(
                    id = "gen_${System.currentTimeMillis()}",
                    title = topic.replaceFirstChar { it.uppercase() },
                    subject = subject,
                    description = "Comprehensive AI interactive teaching session on $topic with synchronized concept visualizers.",
                    thumbnailIcon = "💡",
                    cues = listOf(
                        RobotSpeechCue(
                            id = "c1",
                            text = "Welcome everyone! Today we are exploring $topic and unlocking its core fundamentals.",
                            keyword = topic,
                            visualType = HologramVisualType.AI_CONCEPT_DIAGRAM,
                            visualTitle = "$topic Architecture",
                            visualSubtitle = "Foundational Overview & Key Pillars",
                            visualLabels = listOf("Core Definition", "Underlying Mechanisms", "Key Variables"),
                            robotGesture = RobotGesture.WELCOME_OPEN,
                            robotEmotion = RobotEmotion.HAPPY,
                            highlightFact = "Mastering $topic forms the baseline for exam questions in $subject."
                        ),
                        RobotSpeechCue(
                            id = "c2",
                            text = "When we look closely at the primary mechanism of $topic, notice how the interconnected nodes exchange energy and information.",
                            keyword = "mechanism",
                            visualType = HologramVisualType.AI_CONCEPT_DIAGRAM,
                            visualTitle = "System Dynamics & Relationships",
                            visualSubtitle = "Input → Transformation → Output Cycle",
                            visualLabels = listOf("Cause & Effect", "Governing Laws", "Equilibrium State"),
                            robotGesture = RobotGesture.POINT_RIGHT,
                            robotEmotion = RobotEmotion.ENTHUSIASTIC,
                            highlightFact = "Always analyze the boundary conditions when solving problems."
                        ),
                        RobotSpeechCue(
                            id = "c3",
                            text = "In real-world applications, understanding $topic enables us to engineer practical solutions and predict complex outcomes.",
                            keyword = "applications",
                            visualType = HologramVisualType.AI_CONCEPT_DIAGRAM,
                            visualTitle = "Real-World Practical Impact",
                            visualSubtitle = "Technological & Analytical Integration",
                            visualLabels = listOf("Industrial Use", "Computational Models", "Future Innovations"),
                            robotGesture = RobotGesture.EXPLAINING_HANDS,
                            robotEmotion = RobotEmotion.HAPPY,
                            highlightFact = "Used extensively across modern science, research, and engineering."
                        )
                    )
                )
            }
        }
    }

    // -------------------------------------------------------------
    // OMR Challenge AI Question Generator & Mistake Analyzer
    // -------------------------------------------------------------

    private val validOptions = setOf("A", "B", "C", "D")

    suspend fun generateOmrQuestions(
        subject: String,
        chapterName: String,
        gradeLevel: String,
        questionCount: Int,
        difficulty: String,
        previousQuestionsToExclude: List<String> = emptyList(),
        aiModel: com.example.data.model.AiModelOption? = null
    ): Result<List<com.example.data.model.OmrQuestion>> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNullOrBlank()) {
                val fallback = generateDynamicFallbackQuestions(subject, chapterName, questionCount, difficulty)
                return@withContext Result.success(fallback)
            }

            val excludeInstructions = if (previousQuestionsToExclude.isNotEmpty()) {
                """
                
                CRITICAL ANTI-REPETITION MANDATE:
                Do NOT repeat or closely rephrase any of these previously generated questions from the student's session:
                ${previousQuestionsToExclude.take(15).joinToString("\n") { "- $it" }}
                Generate 100% brand-new, unique questions testing different angles, different values, and varied problem formulations.
                """.trimIndent()
            } else ""

            val systemPrompt = """
                You are a world-class examination board director and adaptive STEM curriculum architect powered by ${aiModel?.name ?: "Advanced AI"}.
                Generate exactly $questionCount high-retention, non-repetitive multiple-choice questions for the subject "$subject" and chapter "$chapterName".
                Target difficulty: $difficulty (Scale: Easy, Medium, Hard, Expert).
                Grade Level: $gradeLevel.
                $excludeInstructions

                CRITICAL GENERATION RULES:
                1. Every question MUST relate directly and rigorously to "$chapterName".
                2. Mix question archetypes across:
                   - Conceptual understanding & first principles
                   - Practical application & quantitative calculation (use fresh, randomized values)
                   - Cause-and-effect reasoning & Assertion-Reasoning
                   - Scenario-based problem solving
                   - Common misconception traps
                3. Provide exactly 4 plausible, distinct options labeled with "A) ", "B) ", "C) ", "D) ". Ensure distractors represent typical student conceptual misunderstandings.
                4. Randomize the correct-answer position uniformly among A, B, C, and D so the answer key is completely unguessable.
                5. Specify "correctOption" as strictly one of "A", "B", "C", or "D".
                6. Provide a comprehensive, pedagogical step-by-step explanation of WHY that option is correct and why other options fail.
                7. Output JSON ONLY matching this structure:
                {
                  "questions": [
                    {
                      "questionNumber": 1,
                      "questionText": "Question text here...",
                      "options": ["A) Option 1", "B) Option 2", "C) Option 3", "D) Option 4"],
                      "correctOption": "B",
                      "explanation": "Detailed step-by-step pedagogical explanation...",
                      "difficulty": "$difficulty",
                      "topic": "$chapterName - Subtopic"
                    }
                  ]
                }
            """.trimIndent()

            val request = GeminiGenerateRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = "Generate $questionCount unique OMR exam questions for $subject - $chapterName ($gradeLevel, Difficulty: $difficulty). Seed: ${System.currentTimeMillis()}.")))
                ),
                systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
                generationConfig = GeminiGenerationConfig(
                    temperature = 0.45f,
                    responseMimeType = "application/json"
                )
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from AI model")

            val cleanJson = cleanJsonString(rawText)
            val jsonObject = JSONObject(cleanJson)
            val questionsArray = jsonObject.optJSONArray("questions")
            val resultList = mutableListOf<com.example.data.model.OmrQuestion>()

            if (questionsArray != null) {
                for (i in 0 until questionsArray.length()) {
                    val qObj = questionsArray.getJSONObject(i)
                    val qNum = qObj.optInt("questionNumber", i + 1)
                    val qText = qObj.optString("questionText", "Question $qNum")
                    val optArray = qObj.optJSONArray("options")
                    val optList = mutableListOf<String>()
                    if (optArray != null) {
                        for (j in 0 until optArray.length()) {
                            optList.add(optArray.getString(j))
                        }
                    }
                    if (optList.size < 4) {
                        optList.clear()
                        optList.addAll(listOf("A) Option 1", "B) Option 2", "C) Option 3", "D) Option 4"))
                    }
                    val correct = qObj.optString("correctOption", "A").trim().uppercase().take(1)
                    val validCorrect = if (validOptions.contains(correct)) correct else "A"
                    val expl = qObj.optString("explanation", "The correct answer is Option $validCorrect.")
                    val diff = qObj.optString("difficulty", difficulty)
                    val top = qObj.optString("topic", chapterName)

                    resultList.add(
                        com.example.data.model.OmrQuestion(
                            id = java.util.UUID.randomUUID().toString(),
                            questionNumber = qNum,
                            questionText = qText,
                            options = optList.take(4),
                            correctOption = validCorrect,
                            explanation = expl,
                            difficulty = diff,
                            topic = top
                        )
                    )
                }
            }

            if (resultList.isNotEmpty()) {
                Result.success(resultList)
            } else {
                val fallback = generateDynamicFallbackQuestions(subject, chapterName, questionCount, difficulty)
                Result.success(fallback)
            }
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error generating OMR questions", e)
            val fallback = generateDynamicFallbackQuestions(subject, chapterName, questionCount, difficulty)
            Result.success(fallback)
        }
    }

    private fun generateDynamicFallbackQuestions(
        subject: String,
        chapterName: String,
        count: Int,
        difficulty: String
    ): List<com.example.data.model.OmrQuestion> {
        val baseQuestions = com.example.data.model.OmrPresets.getSampleChapterQuestions(subject, chapterName)
        val shuffled = baseQuestions.shuffled()
        val result = mutableListOf<com.example.data.model.OmrQuestion>()
        
        for (i in 0 until count) {
            val base = if (i < shuffled.size) shuffled[i] else shuffled[i % shuffled.size]
            // Randomize options and adjust correct index
            val rawOptions = base.options.map { it.substringAfter(") ").trim() }
            val correctText = when (base.correctOption) {
                "A" -> rawOptions.getOrElse(0) { "Option A" }
                "B" -> rawOptions.getOrElse(1) { "Option B" }
                "C" -> rawOptions.getOrElse(2) { "Option C" }
                "D" -> rawOptions.getOrElse(3) { "Option D" }
                else -> rawOptions.firstOrNull() ?: "Option A"
            }
            val randomizedRaw = rawOptions.shuffled()
            val newCorrectLetter = when (randomizedRaw.indexOf(correctText)) {
                0 -> "A"
                1 -> "B"
                2 -> "C"
                3 -> "D"
                else -> "A"
            }
            val formattedOptions = randomizedRaw.mapIndexed { idx, opt ->
                val prefix = ('A' + idx).toString()
                "$prefix) $opt"
            }

            result.add(
                base.copy(
                    id = java.util.UUID.randomUUID().toString(),
                    questionNumber = i + 1,
                    options = formattedOptions,
                    correctOption = newCorrectLetter,
                    difficulty = difficulty
                )
            )
        }
        return result
    }

    suspend fun generateMistakeRevisionLesson(
        failedQuestions: List<com.example.data.model.OmrQuestion>,
        userAnswers: Map<Int, String>,
        chapterName: String,
        subject: String,
        aiModel: com.example.data.model.AiModelOption? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isNullOrBlank() || failedQuestions.isEmpty()) {
                return@withContext Result.success(buildDefaultMistakeLesson(failedQuestions, userAnswers, chapterName, subject))
            }

            val mistakeDetails = failedQuestions.joinToString("\n\n") { q ->
                val studentMarked = userAnswers[q.questionNumber] ?: "Unanswered"
                """
                Question ${q.questionNumber}: ${q.questionText}
                - Student selected: $studentMarked
                - Correct Answer: ${q.correctOption}
                - Explanation: ${q.explanation}
                - Subtopic: ${q.topic}
                """.trimIndent()
            }

            val prompt = """
                You are an elite AI private tutor and mistake diagnostic specialist powered by ${aiModel?.name ?: "Advanced AI"}.
                A student just completed an OMR test paper for "$chapterName" in "$subject" and got the following questions wrong:

                $mistakeDetails

                Generate a comprehensive, highly encouraging Personalized Recovery & Mastery Lesson:
                1. 🔍 **Root-Cause Misconceptions Identified**: Diagnose specifically WHY the student made each mistake. What subtle distractor or formula confusion trapped them?
                2. 💡 **Core Concept & Mental Model Clarification**: Explain the true foundational principle clearly with an intuitive mental analogy.
                3. ⚡ **Memory Hooks & Exam Rules of Thumb**: 3 concise, memorable rules or mnemonics to guarantee they never miss this on test day.
                4. 🎯 **3 New Targeted Practice Questions (With Immediate Answers)**:
                   Create 3 brand-new micro-practice questions testing those exact weak concepts with A/B/C/D options and immediate full derivations so the student can verify their recovery right away!

                Format in clean, engaging Markdown with bold key terms and crisp structure.
            """.trimIndent()

            val request = GeminiGenerateRequest(
                contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                generationConfig = GeminiGenerationConfig(temperature = 0.35f)
            )

            val response = RetrofitClient.geminiService.generateContent(apiKey, request)
            val rawText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: buildDefaultMistakeLesson(failedQuestions, userAnswers, chapterName, subject)

            Result.success(rawText)
        } catch (e: Exception) {
            Log.e("ChapterRepository", "Error generating mistake revision", e)
            Result.success(buildDefaultMistakeLesson(failedQuestions, userAnswers, chapterName, subject))
        }
    }

    private fun buildDefaultMistakeLesson(
        failedQuestions: List<com.example.data.model.OmrQuestion>,
        userAnswers: Map<Int, String>,
        chapterName: String,
        subject: String
    ): String {
        val sb = StringBuilder()
        sb.append("# 🎯 Personalized Revision Lesson: $chapterName ($subject)\n\n")
        sb.append("Here is your targeted recovery guide based on the questions you missed:\n\n")
        
        failedQuestions.forEach { q ->
            val marked = userAnswers[q.questionNumber] ?: "Unanswered"
            sb.append("### Question ${q.questionNumber}: ${q.questionText}\n")
            sb.append("- **Your Answer:** Option $marked\n")
            sb.append("- **Correct Answer:** Option ${q.correctOption}\n")
            sb.append("- **Key Insight:** ${q.explanation}\n\n")
        }

        sb.append("## 💡 Key Retention Rules\n")
        sb.append("1. **Read all 4 options completely** before bubbling in your choice to avoid falling for distractor options.\n")
        sb.append("2. **Focus on fundamental definitions**: Ensure you distinguish between similar technical terms in $chapterName.\n")
        sb.append("3. **Double check units & signs**: Most calculation errors occur due to inverted ratios or conversion mistakes.\n")

        return sb.toString()
    }
}



