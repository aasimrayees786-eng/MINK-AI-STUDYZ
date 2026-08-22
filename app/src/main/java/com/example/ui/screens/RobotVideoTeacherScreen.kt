package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LanguageVoiceOption
import com.example.data.model.RobotLesson
import com.example.data.model.RobotSpeechCue
import com.example.data.model.VoiceCatalog
import com.example.data.model.VoicePersona
import com.example.ui.ChapterAIViewModel
import com.example.ui.components.AnimatedRobotPresenter
import com.example.ui.components.HologramCanvasVisualizer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RobotVideoTeacherScreen(
    viewModel: ChapterAIViewModel,
    modifier: Modifier = Modifier
) {
    val lessonsList by viewModel.robotLessonsList.collectAsState()
    val activeLesson by viewModel.activeRobotLesson.collectAsState()
    val activeCueIndex by viewModel.activeRobotCueIndex.collectAsState()
    val isPlaying by viewModel.isPlayingRobotVideo.collectAsState()
    val videoSpeed by viewModel.robotVideoSpeed.collectAsState()
    val activeWord by viewModel.robotSpokenWordHighlight.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val amplitudes by viewModel.voiceManager.visualizerAmplitudes.collectAsState()
    val isGenerating by viewModel.isGeneratingRobotLesson.collectAsState()
    val generationStage by viewModel.robotLessonGenerationStage.collectAsState()
    val errorMessage by viewModel.robotErrorMessage.collectAsState()
    val currentPersona by viewModel.voiceManager.currentPersona.collectAsState()
    val currentLanguage by viewModel.voiceManager.currentLanguage.collectAsState()

    var showCreateLessonDialog by remember { mutableStateOf(false) }
    var showVoiceAccentDialog by remember { mutableStateOf(false) }

    val currentCue: RobotSpeechCue = remember(activeLesson, activeCueIndex) {
        if (activeLesson.cues.isNotEmpty() && activeCueIndex in activeLesson.cues.indices) {
            activeLesson.cues[activeCueIndex]
        } else {
            activeLesson.cues.firstOrNull() ?: RobotSpeechCue(
                id = "c0",
                text = "Welcome to AI Studio Video Classroom.",
                keyword = "Welcome",
                visualType = com.example.data.model.HologramVisualType.BONES_SKELETON,
                visualTitle = "Interactive 3D Stage",
                visualSubtitle = "Smart Visuals Synchronized with Speech"
            )
        }
    }

    val meanAmplitude = remember(amplitudes) {
        if (amplitudes.isEmpty()) 0.2f else amplitudes.average().toFloat().coerceIn(0.1f, 1.0f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
                        ) {
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = "Robot Teacher",
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "RoboTeacher AI Studio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Synchronized 3D Holograms & Interactive Voice",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showCreateLessonDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF00E5FF).copy(alpha = 0.18f),
                            contentColor = Color(0xFF00E5FF)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("create_custom_robot_video_button")
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "New Lesson",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF060913)
                )
            )
        },
        containerColor = Color(0xFF060913),
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Message Banner
            if (errorMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF7F1D1D).copy(alpha = 0.8f),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.robotErrorMessage.value = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = Color.White)
                            }
                        }
                    }
                }
            }

            // 1. MAIN VIRTUAL STAGE: 3D Hologram (Back) + Animated Robot Teacher (Front)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(340.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF090D1A),
                                    Color(0xFF030712)
                                )
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF00E5FF).copy(alpha = 0.6f),
                                    Color(0xFF6366F1).copy(alpha = 0.4f),
                                    Color(0xFF00E5FF).copy(alpha = 0.2f)
                                )
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .testTag("robot_video_stage_container")
                ) {
                    // Layer A: Background 3D Hologram Projection (Triggered dynamically on keywords)
                    HologramCanvasVisualizer(
                        activeCue = currentCue,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    )

                    // Layer B: Foreground Animated Robot Teacher (Hovering and gesturing at the hologram)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.55f)
                            .height(290.dp)
                            .align(Alignment.BottomStart)
                            .padding(start = 12.dp, bottom = 4.dp)
                    ) {
                        AnimatedRobotPresenter(
                            gesture = currentCue.robotGesture,
                            emotion = currentCue.robotEmotion,
                            isSpeaking = isSpeaking,
                            audioAmplitude = meanAmplitude,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Live Broadcast Watermark Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) Color(0xFFEF4444) else Color(0xFF94A3B8))
                            )
                            Text(
                                text = if (isPlaying) "AI PRESENTING" else "PAUSED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 2. TIMELINE & SYNCHRONIZED KARAOKE SUBTITLE PROMPTER
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Progress Segments (Cue Indicators)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            activeLesson.cues.forEachIndexed { idx, _ ->
                                val isActive = idx == activeCueIndex
                                val isPassed = idx < activeCueIndex
                                val barColor = when {
                                    isActive -> Color(0xFF00E5FF)
                                    isPassed -> Color(0xFF38BDF8).copy(alpha = 0.6f)
                                    else -> Color(0xFF334155)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(5.dp)
                                        .clip(CircleShape)
                                        .background(barColor)
                                        .clickable { viewModel.seekToRobotCue(idx) }
                                )
                            }
                        }

                        // Subtitle Karaoke Text Display
                        val annotatedSpeech = buildAnnotatedString {
                            val words = currentCue.text.split(" ")
                            words.forEach { w ->
                                val cleanW = w.replace(Regex("[^A-Za-z0-9]"), "")
                                val isWordActive = cleanW.equals(activeWord, ignoreCase = true)
                                val isKeyword = cleanW.contains(currentCue.keyword, ignoreCase = true)

                                when {
                                    isWordActive -> {
                                        withStyle(
                                            SpanStyle(
                                                color = Color(0xFF00E5FF),
                                                fontWeight = FontWeight.ExtraBold,
                                                background = Color(0xFF00E5FF).copy(alpha = 0.25f)
                                            )
                                        ) {
                                            append("$w ")
                                        }
                                    }
                                    isKeyword -> {
                                        withStyle(
                                            SpanStyle(
                                                color = Color(0xFFFFD54F),
                                                fontWeight = FontWeight.Bold,
                                                background = Color(0xFFFFD54F).copy(alpha = 0.15f)
                                            )
                                        ) {
                                            append("$w ")
                                        }
                                    }
                                    else -> {
                                        withStyle(
                                            SpanStyle(
                                                color = Color(0xFFE2E8F0),
                                                fontWeight = FontWeight.Normal
                                            )
                                        ) {
                                            append("$w ")
                                        }
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF060913))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = annotatedSpeech,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier.testTag("robot_karaoke_subtitle_text")
                            )
                        }

                        // Video Player Controls Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cue Counter
                            Text(
                                text = "Part ${activeCueIndex + 1} of ${activeLesson.cues.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )

                            // Main Playback Controls
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { viewModel.previousRobotCue() },
                                    enabled = activeCueIndex > 0,
                                    modifier = Modifier.testTag("robot_prev_cue_button")
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Previous Cue",
                                        tint = if (activeCueIndex > 0) Color.White else Color(0xFF475569)
                                    )
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = if (isPlaying) Color(0xFFFF5252) else Color(0xFF00E5FF),
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clickable { viewModel.togglePlayPauseRobotVideo() }
                                        .testTag("robot_play_pause_button"),
                                    shadowElevation = 6.dp
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = Color(0xFF060913),
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.nextRobotCue() },
                                    enabled = activeCueIndex < activeLesson.cues.size - 1,
                                    modifier = Modifier.testTag("robot_next_cue_button")
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next Cue",
                                        tint = if (activeCueIndex < activeLesson.cues.size - 1) Color.White else Color(0xFF475569)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.seekToRobotCue(0) },
                                    modifier = Modifier.testTag("robot_replay_button")
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Restart Lesson",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            // Speed Selector Menu
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                                    val isSelected = videoSpeed == speed
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF1E293B),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color(0xFF334155)),
                                        modifier = Modifier.clickable { viewModel.setRobotVideoSpeed(speed) }
                                    ) {
                                        Text(
                                            text = "${speed}x",
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF94A3B8),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. VOICE & ACCENT SETTINGS BAR
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF0F172A),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Robot Accent & Voice",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "${currentPersona.name} • ${currentLanguage.displayName}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showVoiceAccentDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("change_robot_voice_accent_button")
                        ) {
                            Text("Change Voice", fontSize = 11.sp, color = Color(0xFF38BDF8))
                        }
                    }
                }
            }

            // 4. INTERACTIVE LESSON CATALOG CAROUSEL
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured 3D Video Lessons",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${lessonsList.size} Lessons",
                            fontSize = 12.sp,
                            color = Color(0xFF00E5FF)
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lessonsList) { lesson ->
                            val isSelected = lesson.id == activeLesson.id
                            LessonCardItem(
                                lesson = lesson,
                                isSelected = isSelected,
                                onClick = { viewModel.selectRobotLesson(lesson) }
                            )
                        }
                    }
                }
            }
        }
    }

    // CREATE CUSTOM AI LESSON DIALOG
    if (showCreateLessonDialog) {
        var topicInput by remember { mutableStateOf("") }
        var selectedSub by remember { mutableStateOf("Human Biology") }

        AlertDialog(
            onDismissRequest = { showCreateLessonDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFF00E5FF)
                    )
                    Text(
                        "Create AI Robot Video Lesson",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter any subject topic. AI Studio will script the lesson and choreograph synchronized 3D holograms for the robot presenter!",
                        fontSize = 13.sp,
                        color = Color(0xFFCBD5E1)
                    )

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("Lesson Topic / Concept") },
                        placeholder = { Text("e.g. Human Bones, Neural Synapses, Solar System, Atoms") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("robot_custom_topic_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00E5FF),
                            unfocusedBorderColor = Color(0xFF475569)
                        )
                    )

                    // Quick topic chips
                    Text(
                        text = "Quick Inspiration:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Human Skeleton 206 Bones",
                            "Heart Blood Circulation",
                            "Brain & 86 Billion Neurons",
                            "Planets & Gravity",
                            "Atoms & Electrons",
                            "DNA Double Helix"
                        ).forEach { suggestion ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                modifier = Modifier.clickable { topicInput = suggestion }
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 11.sp,
                                    color = Color(0xFF00E5FF),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    if (isGenerating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF00E5FF),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = generationStage.ifBlank { "Directing AI presentation..." },
                                fontSize = 12.sp,
                                color = Color(0xFF00E5FF)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (topicInput.isNotBlank()) {
                            viewModel.generateCustomAiRobotLesson(topicInput, selectedSub)
                            showCreateLessonDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                    enabled = !isGenerating && topicInput.isNotBlank(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("generate_robot_video_confirm_button")
                ) {
                    Text("Generate Video", color = Color(0xFF060913), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateLessonDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }

    // VOICE ACCENT SELECTOR DIALOG
    if (showVoiceAccentDialog) {
        AlertDialog(
            onDismissRequest = { showVoiceAccentDialog = false },
            title = {
                Text("Select Robot Voice & Accent", fontWeight = FontWeight.Bold, color = Color.White)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Choose the spoken accent and tone for the AI Robot Teacher:",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )

                    VoiceCatalog.ALL_PERSONAS.forEach { persona ->
                        val isSelected = persona == currentPersona
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.15f) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF00E5FF) else Color(0xFF334155)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.voiceManager.setPersona(persona)
                                    showVoiceAccentDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = persona.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF00E5FF) else Color.White
                                    )
                                    Text(
                                        text = "${persona.title} — ${persona.styleDescription}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoiceAccentDialog = false }) {
                    Text("Close", color = Color(0xFF00E5FF))
                }
            },
            containerColor = Color(0xFF0F172A)
        )
    }
}

@Composable
private fun LessonCardItem(
    lesson: RobotLesson,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("robot_lesson_card_${lesson.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else Color(0xFF0F172A)
        ),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) Color(0xFF00E5FF) else Color(0xFF334155)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = lesson.thumbnailIcon,
                    fontSize = 24.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF00E5FF).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${lesson.cues.size} Cues",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = lesson.subject,
                fontSize = 11.sp,
                color = Color(0xFF38BDF8),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = lesson.description,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp
            )
        }
    }
}
