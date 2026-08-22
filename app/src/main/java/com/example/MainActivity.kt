package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.SpatialAudio
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppNavTab
import com.example.ui.ChapterAIViewModel
import com.example.ui.components.VoiceSelectorSheet
import com.example.ui.screens.AppSplashScreen
import com.example.ui.screens.GenerateNotesScreen
import com.example.ui.screens.ImprovementDashboardScreen
import com.example.ui.screens.NoteDetailScreen
import com.example.ui.screens.NotesLibraryScreen
import com.example.ui.screens.RobotVideoTeacherScreen
import com.example.ui.screens.SnapSolveScreen
import com.example.ui.screens.VoiceTutorScreen
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityOnSurfaceVariant
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.MyApplicationTheme
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkspacePremium
import com.example.ui.screens.CinematicIntroScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OmrChallengeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: ChapterAIViewModel = viewModel()
            val currentThemeMode by viewModel.selectedThemeMode.collectAsState()
            
            MyApplicationTheme(themeMode = currentThemeMode) {
                ChapterAIApp(viewModel)
            }
        }
    }
}

@Composable
fun ChapterAIApp(viewModel: ChapterAIViewModel = viewModel()) {
    var showSplashScreen by remember { mutableStateOf(true) }
    val currentTab by viewModel.currentTab.collectAsState()
    val activeNote by viewModel.activeNote.collectAsState()
    var showVoiceSettingsSheet by remember { mutableStateOf(false) }
    var showThemeSelectorSheet by remember { mutableStateOf(false) }
    var showAiModelSelectorSheet by remember { mutableStateOf(false) }
    var showVoiceAccentSheet by remember { mutableStateOf(false) }

    val currentThemeMode by viewModel.selectedThemeMode.collectAsState()
    val currentAiModel by viewModel.selectedAiModel.collectAsState()
    val currentAccent by viewModel.selectedAccent.collectAsState()

    val currentLanguage by viewModel.voiceManager.currentLanguage.collectAsState()
    val currentPersona by viewModel.voiceManager.currentPersona.collectAsState()
    val speechRate by viewModel.voiceManager.speechRate.collectAsState()
    val pitch by viewModel.voiceManager.pitch.collectAsState()

    if (showSplashScreen) {
        CinematicIntroScreen(onFinish = { showSplashScreen = false })
        return
    }

    // Handle system back button when inside a note detail
    BackHandler(enabled = activeNote != null) {
        viewModel.closeNoteDetail()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (activeNote == null) {
                HighDensityNavigationBar(
                    currentTab = currentTab,
                    currentThemeMode = currentThemeMode,
                    currentAiModel = currentAiModel,
                    onSelectTab = { tab -> viewModel.selectTab(tab) },
                    onOpenThemeSelector = { showThemeSelectorSheet = true },
                    onOpenAiModelSelector = { showAiModelSelectorSheet = true },
                    onOpenVoiceAccentStudio = { showVoiceAccentSheet = true },
                    onOpenSettings = { showVoiceSettingsSheet = true }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (activeNote != null) {
                NoteDetailScreen(
                    viewModel = viewModel,
                    note = activeNote!!,
                    onBack = { viewModel.closeNoteDetail() },
                    onOpenVoiceSettings = { showVoiceSettingsSheet = true }
                )
            } else {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_navigation"
                ) { tab ->
                    when (tab) {
                        AppNavTab.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToTab = { targetTab -> viewModel.selectTab(targetTab) },
                            onReplayIntro = { showSplashScreen = true },
                            onOpenThemeSelector = { showThemeSelectorSheet = true },
                            onOpenAiModelSelector = { showAiModelSelectorSheet = true },
                            onOpenVoiceAccent = { showVoiceAccentSheet = true },
                            onOpenSettings = { showVoiceSettingsSheet = true }
                        )
                        AppNavTab.OMR_CHALLENGE -> OmrChallengeScreen(
                            viewModel = viewModel
                        )
                        AppNavTab.VOICE_TUTOR -> VoiceTutorScreen(
                            viewModel = viewModel,
                            onOpenVoiceSettings = { showVoiceSettingsSheet = true }
                        )
                        AppNavTab.PROGRESS -> ImprovementDashboardScreen(
                            viewModel = viewModel,
                            onOpenNote = { noteId -> viewModel.openNoteDetail(noteId) }
                        )
                        AppNavTab.PREMIUM -> OmrChallengeScreen(
                            viewModel = viewModel
                        )
                        AppNavTab.ROBOT_VIDEO -> RobotVideoTeacherScreen(
                            viewModel = viewModel
                        )
                        AppNavTab.SNAP_SOLVE -> SnapSolveScreen(
                            viewModel = viewModel,
                            onOpenVoiceSettings = { showVoiceSettingsSheet = true }
                        )
                        AppNavTab.GENERATE -> GenerateNotesScreen(
                            viewModel = viewModel,
                            onOpenVoiceSettings = { showVoiceSettingsSheet = true }
                        )
                        AppNavTab.LIBRARY -> NotesLibraryScreen(
                            viewModel = viewModel,
                            onOpenNote = { noteId -> viewModel.openNoteDetail(noteId) }
                        )
                    }
                }
            }
        }

        // Voice Persona & Language Selector Bottom Sheet
        if (showVoiceSettingsSheet) {
            VoiceSelectorSheet(
                voiceManager = viewModel.voiceManager,
                currentLanguage = currentLanguage,
                currentPersona = currentPersona,
                speechRate = speechRate,
                pitch = pitch,
                onDismiss = { showVoiceSettingsSheet = false }
            )
        }

        // Theme Selector Modal Bottom Sheet (Space, Nature, Ghost, Cyberpunk, Solar)
        if (showThemeSelectorSheet) {
            com.example.ui.components.ThemeSelectorSheet(
                currentTheme = currentThemeMode,
                onThemeSelected = { mode ->
                    viewModel.setThemeMode(mode)
                    showThemeSelectorSheet = false
                },
                onDismiss = { showThemeSelectorSheet = false }
            )
        }

        // Multi AI Model Selector Modal Bottom Sheet
        if (showAiModelSelectorSheet) {
            com.example.ui.components.AiModelSelectorSheet(
                currentModel = currentAiModel,
                onModelSelected = { model ->
                    viewModel.setAiModel(model)
                    showAiModelSelectorSheet = false
                },
                onDismiss = { showAiModelSelectorSheet = false }
            )
        }

        // Voice Accent & Pronunciation Studio Sheet
        if (showVoiceAccentSheet) {
            com.example.ui.components.VoiceAccentSheet(
                currentAccent = currentAccent,
                onAccentSelected = { accent ->
                    viewModel.setVoiceAccent(accent)
                    showVoiceAccentSheet = false
                },
                onPreviewAccent = { accent ->
                    viewModel.voiceManager.speakAccentSample(accent)
                },
                onDismiss = { showVoiceAccentSheet = false }
            )
        }
    }
}

@Composable
fun HighDensityHeader(
    title: String,
    onOpenSettings: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                shadowElevation = 2.dp
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.mink_study_logo_1787302592819),
                    contentDescription = "Mink Study Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.2).sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp
                )
                Text(
                    text = "AI Study Helper",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("header_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Voice Settings",
                    tint = HighDensityOnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun HighDensityNavigationBar(
    currentTab: AppNavTab,
    currentThemeMode: com.example.ui.theme.AppThemeMode,
    currentAiModel: com.example.data.model.AiModelOption,
    onSelectTab: (AppNavTab) -> Unit,
    onOpenThemeSelector: () -> Unit,
    onOpenAiModelSelector: () -> Unit,
    onOpenVoiceAccentStudio: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMoreMenu by remember { mutableStateOf(false) }

    val primaryTabs = listOf(
        AppNavTab.HOME to Pair(Icons.Default.Home, "Home"),
        AppNavTab.OMR_CHALLENGE to Pair(Icons.Default.School, "OMR"),
        AppNavTab.VOICE_TUTOR to Pair(Icons.Default.SpatialAudio, "AI Tutor"),
        AppNavTab.PROGRESS to Pair(Icons.Default.TrendingUp, "Progress"),
        AppNavTab.PREMIUM to Pair(Icons.Default.WorkspacePremium, "Premium")
    )

    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .testTag("app_navigation_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            primaryTabs.forEach { (tab, iconInfo) ->
                val isSelected = currentTab == tab
                val (icon, label) = iconInfo

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 4.dp)
                        .testTag("nav_tab_${tab.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // Icon Container with active glow
                        Box(
                            modifier = (if (isSelected) {
                                Modifier.background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            } else {
                                Modifier
                            })
                                .clip(RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Label
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )

                        // Small active indicator line
                        Box(
                            modifier = Modifier
                                .width(if (isSelected) 16.dp else 0.dp)
                                .height(2.dp)
                                .clip(RoundedCornerShape(1.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                )
                        )
                    }
                }
            }

            // More tools tab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { showMoreMenu = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("nav_tab_MORE"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "More Tools",
                        tint = if (currentTab in listOf(AppNavTab.ROBOT_VIDEO, AppNavTab.SNAP_SOLVE, AppNavTab.GENERATE, AppNavTab.LIBRARY)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "More",
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Box(modifier = Modifier.height(2.dp))
                }

                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("🎨 Themes (${currentThemeMode.iconEmoji} ${currentThemeMode.displayName})") },
                        onClick = {
                            showMoreMenu = false
                            onOpenThemeSelector()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🧠 AI Engine (${currentAiModel.name})") },
                        onClick = {
                            showMoreMenu = false
                            onOpenAiModelSelector()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🎙️ Voice Accent Studio") },
                        onClick = {
                            showMoreMenu = false
                            onOpenVoiceAccentStudio()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("🤖 Robot Video Teacher") },
                        onClick = {
                            onSelectTab(AppNavTab.ROBOT_VIDEO)
                            showMoreMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("📸 Snap & Solve") },
                        onClick = {
                            onSelectTab(AppNavTab.SNAP_SOLVE)
                            showMoreMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("📝 Generate Notes") },
                        onClick = {
                            onSelectTab(AppNavTab.GENERATE)
                            showMoreMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("📚 My Notes Library") },
                        onClick = {
                            onSelectTab(AppNavTab.LIBRARY)
                            showMoreMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("⚙️ Voice & Language Settings") },
                        onClick = {
                            onOpenSettings()
                            showMoreMenu = false
                        }
                    )
                }
            }
        }
    }
}
