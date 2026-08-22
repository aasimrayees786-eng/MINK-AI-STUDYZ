package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TutorMessage
import com.example.ui.ChapterAIViewModel
import com.example.ui.components.SoundWaveVisualizer
import com.example.ui.theme.AudioWaveActive
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HighDensityAccentDeep
import com.example.ui.theme.HighDensityAccentLight
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityDarkContainer
import com.example.ui.theme.HighDensityOnPrimaryContainer
import com.example.ui.theme.HighDensityOnSurfaceVariant
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.HighDensitySurfaceVariant

private val QUICK_STUDY_PROMPTS = listOf(
    "Explain like I'm 12 🧒",
    "Give me an everyday analogy 💡",
    "Step-by-step mathematical derivation 📐",
    "What are common exam trick questions? ⚠️",
    "How does this apply in real life? 🌍",
    "Test my understanding with a question 🎯"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceTutorScreen(
    viewModel: ChapterAIViewModel,
    onOpenVoiceSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.voiceManager.currentLanguage.collectAsState()
    val currentPersona by viewModel.voiceManager.currentPersona.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val amplitudes by viewModel.voiceManager.visualizerAmplitudes.collectAsState()
    val activeNote by viewModel.activeNote.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val queryInput by viewModel.tutorQueryInput.collectAsState()
    val isThinking by viewModel.isTutorThinking.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // High Density Voice Model & Audio Status Card
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = HighDensitySurfaceVariant),
            border = BorderStroke(1.dp, if (isSpeaking) HighDensityPrimary else HighDensityBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("voice_tutor_header_card")
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(HighDensityPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentPersona.avatarIcon, fontSize = 22.sp)
                        }

                        Column {
                            Text(
                                text = currentPersona.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${currentLang.flagEmoji} ${currentLang.displayName} (${currentLang.nativeName})",
                                style = MaterialTheme.typography.labelSmall,
                                color = HighDensityPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isSpeaking) {
                            IconButton(
                                onClick = { viewModel.stopSpeaking() },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("stop_voice_tutor_button")
                            ) {
                                Icon(
                                    Icons.Default.Stop,
                                    contentDescription = "Stop Speech",
                                    tint = ErrorRed
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HighDensitySecondaryContainer,
                            modifier = Modifier
                                .clickable { onOpenVoiceSettings() }
                                .testTag("change_voice_model_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Tune,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Models",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // SoundWave Visualizer
                Spacer(modifier = Modifier.height(10.dp))
                SoundWaveVisualizer(
                    isSpeaking = isSpeaking,
                    amplitudes = amplitudes,
                    maxHeight = 24.dp,
                    minHeight = 4.dp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Chapter context indicator if studying a specific note
        if (activeNote != null) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = HighDensityPrimaryContainer.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, HighDensityPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = HighDensityPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Studying Chapter: ${activeNote?.title}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = HighDensityOnPrimaryContainer,
                        maxLines = 1,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Quick Preset Prompts
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(QUICK_STUDY_PROMPTS) { prompt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HighDensitySurfaceVariant,
                    border = BorderStroke(1.dp, HighDensityBorder),
                    modifier = Modifier
                        .clickable { viewModel.sendTutorQuestion(prompt) }
                        .testTag("quick_prompt_${prompt.take(10)}")
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Message List or High Density Empty State
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (chatMessages.isEmpty()) {
                // High Density Empty State Hero
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = HighDensityDarkContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(HighDensityAccentLight.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(currentPersona.avatarIcon, fontSize = 32.sp)
                        }

                        Text(
                            text = "Ask ${currentPersona.name} Anything!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "Interactive spoken lessons in ${currentLang.displayName}. Tap a quick prompt above or type your question below.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            lineHeight = 16.sp,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = HighDensityAccentLight,
                            modifier = Modifier
                                .clickable {
                                    viewModel.sendTutorQuestion("Explain the main concepts of this chapter step by step.")
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = HighDensityAccentDeep,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Start Lesson Discussion",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = HighDensityAccentDeep,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(chatMessages, key = { it.id }) { msg ->
                        TutorChatMessageItem(
                            message = msg,
                            onSpeakMessage = { viewModel.speakText(msg.messageText) }
                        )
                    }
                }
            }
        }

        // Thinking indicator
        AnimatedVisibility(visible = isThinking) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = HighDensityPrimary,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "${currentPersona.name} is preparing response in ${currentLang.displayName}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = HighDensityPrimary,
                    fontSize = 11.sp
                )
            }
        }

        // Question Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = queryInput,
                onValueChange = { viewModel.tutorQueryInput.value = it },
                placeholder = {
                    Text(
                        "Ask a question or request derivation...",
                        color = HighDensityOnSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("tutor_query_input"),
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HighDensityPrimary,
                    unfocusedBorderColor = HighDensityBorder,
                    focusedContainerColor = HighDensitySurfaceVariant.copy(alpha = 0.6f),
                    unfocusedContainerColor = HighDensitySurfaceVariant.copy(alpha = 0.3f)
                )
            )

            IconButton(
                onClick = { viewModel.sendTutorQuestion(queryInput) },
                enabled = queryInput.isNotBlank() && !isThinking,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (queryInput.isNotBlank() && !isThinking) HighDensityPrimary else HighDensityBorder.copy(alpha = 0.4f)
                    )
                    .testTag("send_tutor_query_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (queryInput.isNotBlank() && !isThinking) Color.White else HighDensityOnSurfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun TutorChatMessageItem(
    message: TutorMessage,
    onSpeakMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUser = message.isUser

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(HighDensityPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(message.personaIcon, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) HighDensitySecondaryContainer else HighDensitySurfaceVariant,
            border = if (!isUser) BorderStroke(1.dp, HighDensityBorder) else null,
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.senderName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = HighDensityPrimary,
                            fontSize = 11.sp
                        )

                        IconButton(
                            onClick = onSpeakMessage,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Play voice audio",
                                tint = HighDensityPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.messageText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUser) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp,
                    fontSize = 12.sp
                )
            }
        }
    }
}
