package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChapterAIViewModel
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityOnPrimaryContainer
import com.example.ui.theme.HighDensityOnSurfaceVariant
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensityProgressTrack
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.HighDensitySurfaceVariant
import com.example.ui.theme.SuccessGreen

@Composable
fun ImprovementDashboardScreen(
    viewModel: ChapterAIViewModel,
    onOpenNote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.allNotes.collectAsState()

    val totalNotes = notes.size
    val totalQuizzesAttempted = notes.count { it.quizTotalQuestions > 0 }
    val totalCorrectAnswers = notes.map { it.quizCorrectCount }.sum()
    val totalQuestionsAnswered = notes.map { it.quizTotalQuestions }.sum()
    val averageMastery = if (totalQuizzesAttempted > 0) {
        notes.filter { it.quizTotalQuestions > 0 }.map { it.masteryScore }.average().toInt()
    } else {
        0
    }

    val accuracyText = if (totalQuestionsAnswered > 0) {
        val pct = ((totalCorrectAnswers.toDouble() / totalQuestionsAnswered.toDouble()) * 100.0).toInt()
        "$pct%"
    } else {
        "—"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High Density Mastery Overview Hero Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HighDensityPrimaryContainer),
            border = BorderStroke(1.dp, HighDensityOnPrimaryContainer.copy(alpha = 0.15f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mastery_summary_banner")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STUDENT MASTERY OVERVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = HighDensityOnPrimaryContainer.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HighDensityOnPrimaryContainer
                    ) {
                        Text(
                            text = "ACTIVE SESSION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (totalQuizzesAttempted > 0) "$averageMastery% Chapter Retention" else "Ready for Assessment",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = HighDensityOnPrimaryContainer,
                    fontSize = 21.sp,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "$totalNotes Chapters Synthesized • $totalQuizzesAttempted Quizzes Completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = HighDensityOnPrimaryContainer.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Accuracy / Readiness meter card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, HighDensityOnPrimaryContainer.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Overall Quiz Mastery",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityOnPrimaryContainer,
                                fontSize = 11.sp
                            )
                            Text(
                                text = accuracyText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = HighDensityOnPrimaryContainer,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { if (totalQuestionsAnswered > 0) (totalCorrectAnswers.toFloat() / totalQuestionsAnswered.toFloat()) else 0.75f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = HighDensityPrimary,
                            trackColor = HighDensityProgressTrack
                        )
                    }
                }
            }
        }

        // 3 Key Stats Counters in High Density Surface
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                title = "Total Chapters",
                value = "$totalNotes",
                subtitle = "Synthesized",
                icon = "📚",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Quiz Accuracy",
                value = accuracyText,
                subtitle = "$totalCorrectAnswers / $totalQuestionsAnswered hits",
                icon = "🎯",
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Recall System",
                value = "Active",
                subtitle = "Spaced Repetition",
                icon = "🧠",
                modifier = Modifier.weight(1f)
            )
        }

        // AI Spaced Repetition Schedule
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = HighDensitySurfaceVariant),
            border = BorderStroke(1.dp, HighDensityBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Timeline,
                        contentDescription = null,
                        tint = HighDensityPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "AI Spaced Repetition Schedule",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                StudyTimelineStep(
                    day = "Day 1",
                    title = "Initial Synthesis & Voice Lesson",
                    desc = "Read synthesized chapter notes and listen to AI Tutor explain core concepts."
                )
                StudyTimelineStep(
                    day = "Day 3",
                    title = "Active Recall & Flashcard Drill",
                    desc = "Test mental models using the interactive 3D flashcards without flipping until prompted."
                )
                StudyTimelineStep(
                    day = "Day 7",
                    title = "Self-Assessment & Weak-Spot Patching",
                    desc = "Complete the chapter quiz to calculate mastery score and address flagged traps."
                )
            }
        }

        // Chapter Mastery Breakdown List
        if (notes.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Chapter Performance & Weak Spots",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )

                notes.forEach { note ->
                    val improvement = viewModel.parseImprovementGuide(note.improvementGuideJson)
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = HighDensitySurfaceVariant),
                        border = BorderStroke(1.dp, HighDensityBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenNote(note.id) }
                            .testTag("dashboard_note_item_${note.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = note.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (note.masteryScore >= 75) HighDensitySecondaryContainer else HighDensityPrimaryContainer
                                ) {
                                    Text(
                                        text = if (note.masteryScore > 0) "${note.masteryScore}%" else "Unassessed",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (note.masteryScore >= 75) HighDensityPrimary else HighDensityOnPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (improvement.commonWeakSpots.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("⚠️", fontSize = 11.sp)
                                    Text(
                                        text = "Trap: ${improvement.commonWeakSpots.first()}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = HighDensityOnSurfaceVariant,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HighDensitySurfaceVariant),
        border = BorderStroke(1.dp, HighDensityBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(icon, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = HighDensityPrimary,
                fontSize = 10.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = HighDensityOnSurfaceVariant,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StudyTimelineStep(
    day: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = HighDensitySecondaryContainer,
            modifier = Modifier.width(52.dp)
        ) {
            Box(modifier = Modifier.padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = HighDensityOnSurfaceVariant,
                lineHeight = 15.sp,
                fontSize = 11.sp
            )
        }
    }
}
