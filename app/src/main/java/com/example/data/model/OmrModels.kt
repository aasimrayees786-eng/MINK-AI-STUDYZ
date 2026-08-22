package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OmrQuestion(
    val id: String,
    val questionNumber: Int,
    val questionText: String,
    val options: List<String>, // Exactly 4 options: ["Option A text", "Option B text", "Option C text", "Option D text"]
    val correctOption: String, // "A", "B", "C", or "D"
    val explanation: String,
    val difficulty: String = "Medium", // "Easy", "Medium", "Hard"
    val topic: String = ""
)

@Entity(tableName = "omr_test_records")
@JsonClass(generateAdapter = true)
data class OmrTestRecord(
    @PrimaryKey
    val id: String,
    val title: String,
    val subject: String,
    val chapterName: String,
    val gradeLevel: String,
    val difficulty: String,
    val totalQuestions: Int,
    val questionsJson: String, // List<OmrQuestion>
    val userAnswersJson: String, // Map<Int, String> (questionNumber -> chosenOption A/B/C/D)
    val score: Int,
    val maxScore: Int,
    val accuracyPercentage: Float,
    val correctCount: Int,
    val incorrectCount: Int,
    val unansweredCount: Int,
    val pointsEarned: Int,
    val xpEarned: Int,
    val streakBonusPoints: Int,
    val timeSpentSeconds: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val isDailyChallenge: Boolean = false,
    val mistakeRevisionNotes: String = ""
)

@JsonClass(generateAdapter = true)
data class GamificationState(
    val xp: Int = 180,
    val studyPoints: Int = 650,
    val streakDays: Int = 3,
    val lastTestDate: Long = 0L,
    val levelNumber: Int = 2,
    val levelTitle: String = "Learner",
    val totalTestsCompleted: Int = 3,
    val totalQuestionsAttempted: Int = 45,
    val totalCorrectAnswers: Int = 38,
    val averageAccuracy: Float = 84.4f,
    val unlockedPerkIds: List<String> = emptyList(),
    val isPremiumSubscribed: Boolean = false,
    val unlockedBadgeIds: List<String> = listOf("FIRST_TEST", "STREAK_3")
)

@JsonClass(generateAdapter = true)
data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val pointsReward: Int,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f,
    val requirementText: String = ""
)

@JsonClass(generateAdapter = true)
data class LeaderboardUser(
    val rank: Int,
    val username: String,
    val avatarEmoji: String,
    val studyPoints: Int,
    val xp: Int,
    val streakDays: Int,
    val accuracy: Float,
    val isCurrentUser: Boolean = false,
    val levelTitle: String = "Scholar"
)

@JsonClass(generateAdapter = true)
data class PremiumPerkItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val requiredPoints: Int,
    val isUnlocked: Boolean = false,
    val tag: String = "Popular"
)

@JsonClass(generateAdapter = true)
data class DifficultyAdaptationAnalysis(
    val recommendedDifficulty: String, // "Easy", "Medium", "Hard", "Mixed"
    val recentAccuracyAverage: Float,
    val strongTopics: List<String>,
    val weakTopics: List<String>,
    val recommendationMessage: String
)
