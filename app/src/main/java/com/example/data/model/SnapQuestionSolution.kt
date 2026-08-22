package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "snap_solutions")
@JsonClass(generateAdapter = true)
data class SnapSolutionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionText: String,
    val subject: String,
    val topic: String,
    val finalAnswer: String,
    val confidenceScore: Int = 98,
    val stepsJson: String, // List<SolutionStep> as JSON
    val givensJson: String = "[]", // List<String>
    val keyFormulasJson: String = "[]", // List<String>
    val commonMistakesJson: String = "[]", // List<String>
    val quickTip: String = "",
    val spokenAudioExplanation: String = "",
    val imageBase64: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class SolutionStep(
    val stepNumber: Int,
    val stepTitle: String,
    val explanation: String,
    val formulaOrMath: String = "",
    val whyItMatters: String = ""
)

@JsonClass(generateAdapter = true)
data class SnapSolutionResult(
    val detectedQuestion: String,
    val subject: String,
    val topic: String,
    val givens: List<String> = emptyList(),
    val steps: List<SolutionStep> = emptyList(),
    val finalAnswer: String,
    val keyFormulas: List<String> = emptyList(),
    val commonMistakes: List<String> = emptyList(),
    val proTip: String = "",
    val spokenSummary: String = ""
)
