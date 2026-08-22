package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestionItem
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.ErrorRedLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight

@Composable
fun QuizAssessmentCard(
    quizQuestions: List<QuizQuestionItem>,
    selectedAnswers: Map<Int, Int>,
    isSubmitted: Boolean,
    scoreResult: Pair<Int, Int>?,
    onSelectOption: (questionIndex: Int, optionIndex: Int) -> Unit,
    onSubmitQuiz: () -> Unit,
    onResetQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (quizQuestions.isEmpty()) {
        Card(shape = RoundedCornerShape(16.dp), modifier = modifier.fillMaxWidth()) {
            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No quiz questions generated for this note yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quiz_assessment_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Score Summary Banner if submitted
        AnimatedVisibility(visible = isSubmitted && scoreResult != null) {
            scoreResult?.let { (correct, total) ->
                val percent = if (total > 0) ((correct.toFloat() / total) * 100).toInt() else 0
                val (badgeColor, statusTitle, statusMsg) = when {
                    percent >= 80 -> Triple(SuccessGreen, "Outstanding Mastery! 🌟", "You have a solid command of this chapter's key principles.")
                    percent >= 50 -> Triple(MaterialTheme.colorScheme.tertiary, "Good Progress! 📚", "Review the flagged weak spots below to hit 100%.")
                    else -> Triple(ErrorRed, "Needs Focused Review 💡", "Check the AI Improvement Guide to solidify your fundamentals.")
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(2.dp, badgeColor),
                    modifier = Modifier.fillMaxWidth().testTag("quiz_score_summary_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(badgeColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = statusTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$correct / $total Correct ($percent% Mastery Score)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = badgeColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = statusMsg,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // List of Quiz Questions
        quizQuestions.forEachIndexed { qIndex, question ->
            val userSelectedOption = selectedAnswers[qIndex]
            val isQuestionCorrect = userSelectedOption == question.correctIndex

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(
                    1.dp,
                    if (isSubmitted) {
                        if (isQuestionCorrect) SuccessGreen.copy(alpha = 0.8f) else ErrorRed.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    }
                ),
                modifier = Modifier.fillMaxWidth().testTag("question_card_$qIndex")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Question Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "QUESTION ${qIndex + 1} OF ${quizQuestions.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        if (isSubmitted) {
                            if (isQuestionCorrect) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Correct", tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    Text("Correct", style = MaterialTheme.typography.labelSmall, color = SuccessGreen, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = ErrorRed, modifier = Modifier.size(16.dp))
                                    Text("Review", style = MaterialTheme.typography.labelSmall, color = ErrorRed, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Options
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        question.options.forEachIndexed { optIndex, optionText ->
                            val isSelected = userSelectedOption == optIndex
                            val isCorrectAnswer = optIndex == question.correctIndex

                            val (optionBg, optionBorder, optionTextColor) = when {
                                isSubmitted && isCorrectAnswer -> Triple(
                                    SuccessGreen.copy(alpha = 0.18f),
                                    BorderStroke(1.5.dp, SuccessGreen),
                                    SuccessGreen
                                )
                                isSubmitted && isSelected && !isCorrectAnswer -> Triple(
                                    ErrorRed.copy(alpha = 0.18f),
                                    BorderStroke(1.5.dp, ErrorRed),
                                    ErrorRed
                                )
                                !isSubmitted && isSelected -> Triple(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                                    MaterialTheme.colorScheme.primary
                                )
                                else -> Triple(
                                    MaterialTheme.colorScheme.surface,
                                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = optionBg,
                                border = optionBorder,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isSubmitted) {
                                        onSelectOption(qIndex, optIndex)
                                    }
                                    .testTag("q_${qIndex}_option_$optIndex")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected || (isSubmitted && isCorrectAnswer)) optionTextColor else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected || (isSubmitted && isCorrectAnswer)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = optionText,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // Explanation Box (shown after submit)
                    AnimatedVisibility(visible = isSubmitted && question.explanation.isNotBlank()) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "💡 Explanation & Exam Takeaway:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Submit / Retake Actions
        if (!isSubmitted) {
            val allAnswered = quizQuestions.indices.all { selectedAnswers.containsKey(it) }
            Button(
                onClick = onSubmitQuiz,
                enabled = allAnswered,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_quiz_button")
            ) {
                Text(
                    text = if (allAnswered) "Submit Quiz & Calculate Mastery Score" else "Answer All ${quizQuestions.size} Questions to Submit",
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            OutlinedButton(
                onClick = onResetQuiz,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("retake_quiz_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retake Quiz", fontWeight = FontWeight.Bold)
            }
        }
    }
}
