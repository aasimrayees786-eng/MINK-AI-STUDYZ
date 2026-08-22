package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "chapter_notes")
data class ChapterNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val rawChapterText: String,
    val summary: String, // Short notes / Comprehensive overview
    val keyTakeawaysJson: String, // JSON list of String (Key Points)
    val conceptsJson: String,      // JSON list of ConceptItem
    val vocabularyJson: String,    // JSON list of VocabItem (Definitions & Glossary)
    val importantFormulasJson: String = "[]", // JSON list of FormulaItem (Important formulas & equations)
    val flashcardsJson: String,    // JSON list of FlashcardItem
    val quizJson: String,          // JSON list of QuizQuestionItem
    val improvementGuideJson: String, // JSON of ImprovementGuideItem
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val masteryScore: Int = 0,     // 0 to 100
    val quizCorrectCount: Int = 0,
    val quizTotalQuestions: Int = 0,
    val sourceFileName: String = "" // Uploaded PDF/Chapter file name
)

@JsonClass(generateAdapter = true)
data class ConceptItem(
    val title: String,
    val explanation: String,
    val keyPoints: List<String> = emptyList(),
    val importanceLevel: String = "High" // "High", "Medium", "Fundamental"
)

@JsonClass(generateAdapter = true)
data class VocabItem(
    val term: String,
    val definition: String,
    val formulaOrExample: String = ""
)

@JsonClass(generateAdapter = true)
data class DefinitionItem(
    val term: String,
    val definition: String,
    val contextOrExample: String = ""
)

@JsonClass(generateAdapter = true)
data class FormulaItem(
    val name: String,
    val formula: String,
    val explanation: String,
    val variables: String = "",
    val example: String = ""
)

@JsonClass(generateAdapter = true)
data class FlashcardItem(
    val front: String,
    val back: String,
    val hint: String = ""
)

@JsonClass(generateAdapter = true)
data class QuizQuestionItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

@JsonClass(generateAdapter = true)
data class ImprovementGuideItem(
    val commonWeakSpots: List<String> = emptyList(),
    val mnemonicTricks: List<String> = emptyList(),
    val revisionStrategy: List<String> = emptyList(),
    val practicalApplications: List<String> = emptyList(),
    val examReadinessTips: String = ""
)

@JsonClass(generateAdapter = true)
data class StructuredNoteData(
    val title: String,
    val subject: String,
    val summary: String,
    val shortNotes: String = "",
    val keyTakeaways: List<String> = emptyList(),
    val keyPoints: List<String> = emptyList(),
    val concepts: List<ConceptItem> = emptyList(),
    val vocabulary: List<VocabItem> = emptyList(),
    val definitions: List<DefinitionItem> = emptyList(),
    val importantFormulas: List<FormulaItem> = emptyList(),
    val flashcards: List<FlashcardItem> = emptyList(),
    val quiz: List<QuizQuestionItem> = emptyList(),
    val improvementGuide: ImprovementGuideItem = ImprovementGuideItem()
)

