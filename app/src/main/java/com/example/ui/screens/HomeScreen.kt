package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppNavTab
import com.example.ui.ChapterAIViewModel
import kotlin.math.cos
import kotlin.math.sin

/**
 * Data structures for continue learning & AI recommendations
 */
data class RecentStudyChapter(
    val id: String,
    val subject: String,
    val chapterName: String,
    val progressPercent: Int,
    val lastStudied: String,
    val accentColor: Color
)

data class AiStudyRecommendation(
    val id: String,
    val title: String,
    val subject: String,
    val reason: String,
    val diagnosticType: String, // "Weak Topic", "High Yield", "Mistake Recovery"
    val accentColor: Color
)

/**
 * AASIM-STUDIO • MINK STUDY Home Interface & AI Dashboard
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: ChapterAIViewModel,
    onNavigateToTab: (AppNavTab) -> Unit,
    onReplayIntro: () -> Unit,
    onOpenThemeSelector: () -> Unit = {},
    onOpenAiModelSelector: () -> Unit = {},
    onOpenVoiceAccent: () -> Unit = {},
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gamificationState by viewModel.gamificationState.collectAsState()
    val allOmrTests by viewModel.allOmrTests.collectAsState()
    val badges by viewModel.badgesList.collectAsState()
    val currentThemeMode by viewModel.selectedThemeMode.collectAsState()
    val currentAiModel by viewModel.selectedAiModel.collectAsState()
    val currentAccent by viewModel.selectedAccent.collectAsState()

    var showNotificationsDialog by remember { mutableStateOf(false) }
    var quickTutorQuery by remember { mutableStateOf("") }
    var hasUnlockedPremiumSuccess by remember { mutableStateOf(false) }

    // Smooth Entrance Animation
    val heroEnterProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        heroEnterProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
    }

    // Animated numerical counters
    val animatedXp by animateIntAsState(
        targetValue = gamificationState.xp.coerceAtLeast(2450),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "xp_anim"
    )
    val animatedPoints by animateIntAsState(
        targetValue = gamificationState.studyPoints.coerceAtLeast(1850),
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "points_anim"
    )

    val sampleRecentChapters = remember {
        listOf(
            RecentStudyChapter("1", "Physics", "Laws of Motion & Friction", 68, "Today", Color(0xFF00D2FF)),
            RecentStudyChapter("2", "Biology", "Cell Structure & Cell Division", 92, "Yesterday", Color(0xFF10B981)),
            RecentStudyChapter("3", "Chemistry", "Chemical Bonding & Molecular Shapes", 45, "2 days ago", Color(0xFF8B5CF6)),
            RecentStudyChapter("4", "Mathematics", "Quadratic Equations & Complex Roots", 80, "3 days ago", Color(0xFFF59E0B))
        )
    }

    val sampleRecommendations = remember {
        listOf(
            AiStudyRecommendation(
                id = "r1",
                title = "Revise Algebraic Equations & Matrices",
                subject = "Mathematics",
                reason = "Your accuracy dropped to 62% in this topic during last test.",
                diagnosticType = "Weak Topic",
                accentColor = Color(0xFFEF4444)
            ),
            AiStudyRecommendation(
                id = "r2",
                title = "Physics: Wave Optics & Interference",
                subject = "Physics",
                reason = "High exam frequency • 3 tricky formula concepts flagged.",
                diagnosticType = "High Yield",
                accentColor = Color(0xFF38BDF8)
            ),
            AiStudyRecommendation(
                id = "r3",
                title = "Biology: Photosynthesis Light Reactions",
                subject = "Biology",
                reason = "Recommended based on your recent OMR mistake review.",
                diagnosticType = "Mistake Recovery",
                accentColor = Color(0xFFA855F7)
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // -------------------------------------------------------------
        // 1. TOP BRANDING & GREETING BAR
        // -------------------------------------------------------------
        item {
            Spacer(modifier = Modifier.height(6.dp))
            TopGreetingHeader(
                userName = "Aasim",
                levelNumber = gamificationState.levelNumber.coerceAtLeast(8),
                levelTitle = "Scholar",
                currentTheme = currentThemeMode,
                currentModel = currentAiModel,
                currentAccent = currentAccent,
                onOpenThemeSelector = onOpenThemeSelector,
                onOpenAiModelSelector = onOpenAiModelSelector,
                onOpenVoiceAccent = onOpenVoiceAccent,
                onOpenNotifications = { showNotificationsDialog = true },
                onReplayIntro = onReplayIntro,
                onOpenSettings = onOpenSettings
            )
        }

        // -------------------------------------------------------------
        // 2. HERO WELCOME SECTION (Holographic Orb & CTAs)
        // -------------------------------------------------------------
        item {
            HeroWelcomeCard(
                enterScale = heroEnterProgress.value,
                onStartOmr = { onNavigateToTab(AppNavTab.OMR_CHALLENGE) },
                onAskAiTutor = { onNavigateToTab(AppNavTab.VOICE_TUTOR) }
            )
        }

        // -------------------------------------------------------------
        // 3. QUICK ACTION CARDS (Responsive Grid)
        // -------------------------------------------------------------
        item {
            Text(
                text = "⚡ Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            QuickActionCardsSection(
                onNavigate = onNavigateToTab
            )
        }

        // -------------------------------------------------------------
        // 4. DAILY OMR CHALLENGE CARD
        // -------------------------------------------------------------
        item {
            DailyChallengeCard(
                streakDays = gamificationState.streakDays.coerceAtLeast(7),
                onStartDailyChallenge = {
                    viewModel.startDailyOmrChallenge()
                    onNavigateToTab(AppNavTab.OMR_CHALLENGE)
                }
            )
        }

        // -------------------------------------------------------------
        // 5. PROGRESS OVERVIEW & STATS DASHBOARD
        // -------------------------------------------------------------
        item {
            ProgressOverviewDashboard(
                xp = animatedXp,
                studyPoints = animatedPoints,
                levelNumber = gamificationState.levelNumber.coerceAtLeast(8),
                levelTitle = "Scholar",
                accuracy = 88.5f,
                testsCompleted = (allOmrTests.size + 24),
                streakDays = gamificationState.streakDays.coerceAtLeast(7)
            )
        }

        // -------------------------------------------------------------
        // 6. CONTINUE LEARNING SECTION (Recent Chapters)
        // -------------------------------------------------------------
        item {
            Text(
                text = "📖 Continue Learning",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            ContinueLearningCarousel(
                chapters = sampleRecentChapters,
                onContinue = { ch ->
                    viewModel.chapterSubject.value = ch.subject
                    viewModel.omrSubjectInput.value = ch.subject
                    viewModel.omrChapterInput.value = ch.chapterName
                    onNavigateToTab(AppNavTab.OMR_CHALLENGE)
                }
            )
        }

        // -------------------------------------------------------------
        // 7. AI TUTOR INTERACTIVE PREVIEW PANEL
        // -------------------------------------------------------------
        item {
            AiTutorPreviewPanel(
                query = quickTutorQuery,
                onQueryChange = { quickTutorQuery = it },
                onSubmitQuery = { q ->
                    if (q.isNotBlank()) {
                        viewModel.sendTutorQuestion(q)
                        quickTutorQuery = ""
                    }
                    onNavigateToTab(AppNavTab.VOICE_TUTOR)
                }
            )
        }

        // -------------------------------------------------------------
        // 8. RECOMMENDED STUDY SECTION ("Recommended For You")
        // -------------------------------------------------------------
        item {
            Text(
                text = "🎯 Recommended For You",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sampleRecommendations.forEach { rec ->
                    RecommendationCard(
                        rec = rec,
                        onStudyNow = {
                            viewModel.omrSubjectInput.value = rec.subject
                            viewModel.omrChapterInput.value = rec.title
                            onNavigateToTab(AppNavTab.GENERATE)
                        },
                        onPractice = {
                            viewModel.omrSubjectInput.value = rec.subject
                            viewModel.omrChapterInput.value = rec.title
                            onNavigateToTab(AppNavTab.OMR_CHALLENGE)
                        },
                        onViewExplanation = {
                            viewModel.sendTutorQuestion("Explain key concepts and formulas for ${rec.title} in detail.")
                            onNavigateToTab(AppNavTab.VOICE_TUTOR)
                        }
                    )
                }
            }
        }

        // -------------------------------------------------------------
        // 9. UNLOCK AI PREMIUM BANNER
        // -------------------------------------------------------------
        item {
            PremiumAiBanner(
                isSubscribed = gamificationState.isPremiumSubscribed,
                studyPoints = gamificationState.studyPoints.coerceAtLeast(1850),
                onViewPremium = { onNavigateToTab(AppNavTab.PREMIUM) },
                onUnlockWithPoints = {
                    viewModel.unlockAllPremiumWithPoints()
                    hasUnlockedPremiumSuccess = true
                }
            )
        }

        // -------------------------------------------------------------
        // 10. ACHIEVEMENTS PREVIEW
        // -------------------------------------------------------------
        item {
            Text(
                text = "🏆 Your Achievements",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF8FAFC),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            AchievementsPreviewSection(badges = badges)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Interactive Notification Dialog
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            containerColor = Color(0xFF0F172A),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color(0xFF00D2FF)
                    )
                    Text("Study Notifications", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    NotificationItem(
                        icon = Icons.Default.LocalFireDepartment,
                        title = "🔥 7-Day Streak Active!",
                        desc = "Complete today's OMR Challenge to keep your multiplier alive.",
                        tint = Color(0xFFF59E0B)
                    )
                    NotificationItem(
                        icon = Icons.Default.Bolt,
                        title = "⚡ +100 Study Points Bonus",
                        desc = "Awarded for scoring 90%+ in Physics Laws of Motion.",
                        tint = Color(0xFF00D2FF)
                    )
                    NotificationItem(
                        icon = Icons.Default.Psychology,
                        title = "🧠 AI Recovery Guide Ready",
                        desc = "Targeted revision generated for Wave Optics questions.",
                        tint = Color(0xFF8B5CF6)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Close", color = Color(0xFF38BDF8))
                }
            }
        )
    }
}

// ---------------------------------------------------------------------
// SUB-COMPONENTS
// ---------------------------------------------------------------------

@Composable
private fun TopGreetingHeader(
    userName: String,
    levelNumber: Int,
    levelTitle: String,
    currentTheme: com.example.ui.theme.AppThemeMode,
    currentModel: com.example.data.model.AiModelOption,
    currentAccent: com.example.data.model.VoiceAccent,
    onOpenThemeSelector: () -> Unit,
    onOpenAiModelSelector: () -> Unit,
    onOpenVoiceAccent: () -> Unit,
    onOpenNotifications: () -> Unit,
    onReplayIntro: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand & User Greeting
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Glowing Studio Logo Badge
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AASIM-STUDIO Logo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "AASIM-STUDIO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.6.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Welcome back, $userName",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Action Icons & Profile Avatar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Replay Intro Button
                IconButton(
                    onClick = onReplayIntro,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("replay_intro_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "Replay Intro",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Notification Bell
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(7.dp)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Settings Gear
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Profile Avatar with Level Ring
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L$levelNumber",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        // Quick Active Chips: Dynamic Theme, AI Model, Voice Accent
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Theme Chip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenThemeSelector() },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = currentTheme.iconEmoji, fontSize = 12.sp)
                    Text(
                        text = currentTheme.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // AI Model Chip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenAiModelSelector() },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = currentModel.iconEmoji, fontSize = 12.sp)
                    Text(
                        text = currentModel.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Voice Accent Chip
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenVoiceAccent() },
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = currentAccent.flagEmoji, fontSize = 12.sp)
                    Text(
                        text = currentAccent.name.substringBefore(" "),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroWelcomeCard(
    enterScale: Float,
    onStartOmr: () -> Unit,
    onAskAiTutor: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_orb")
    val orbRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.5.dp, Color(0xFF00D2FF).copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .scale(0.95f + 0.05f * enterScale)
            .testTag("hero_welcome_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1B4B).copy(alpha = 0.6f),
                            Color(0xFF0F172A)
                        ),
                        center = Offset(200f, 150f),
                        radius = 800f
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF00D2FF).copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Color(0xFF00D2FF).copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "✨ AI STUDY PLATFORM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00D2FF),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Ready to level up your learning?",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 26.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Practice, ask AI, and master every chapter with precision.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 18.sp
                        )
                    }

                    // Abstract Animated Holographic AI Orb Canvas
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            val baseRadius = size.width * 0.32f

                            // Outer ambient aura
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00D2FF).copy(alpha = 0.4f * glowPulse),
                                        Color(0xFF8B5CF6).copy(alpha = 0.2f),
                                        Color.Transparent
                                    ),
                                    center = Offset(cx, cy),
                                    radius = size.width * 0.5f
                                ),
                                radius = size.width * 0.5f,
                                center = Offset(cx, cy)
                            )

                            // Inner holographic core
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color(0xFF00D2FF),
                                        Color(0xFF6366F1)
                                    ),
                                    center = Offset(cx - 4f, cy - 4f),
                                    radius = baseRadius
                                ),
                                radius = baseRadius,
                                center = Offset(cx, cy)
                            )

                            // Orbiting electron rings & nodes
                            for (i in 0..2) {
                                val currentAngle = orbRotation + (i * 2.094f)
                                val nodeX = cx + cos(currentAngle) * (baseRadius + 14f)
                                val nodeY = cy + sin(currentAngle) * (baseRadius + 14f)

                                drawCircle(
                                    color = Color(0xFF00D2FF).copy(alpha = 0.8f),
                                    radius = 3.5f,
                                    center = Offset(nodeX, nodeY)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action CTAs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onStartOmr,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("hero_start_omr_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00D2FF)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = Color(0xFF070B19),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Start OMR Challenge",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF070B19)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onAskAiTutor,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("hero_ask_tutor_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF8B5CF6)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color(0xFF8B5CF6),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Ask AI Tutor",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionCardsSection(
    onNavigate: (AppNavTab) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionItemCard(
                title = "OMR Challenge",
                desc = "Test your knowledge",
                btnLabel = "Start Test",
                icon = Icons.Default.School,
                gradient = listOf(Color(0xFF0284C7), Color(0xFF6366F1)),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppNavTab.OMR_CHALLENGE) }
            )
            QuickActionItemCard(
                title = "AI Tutor",
                desc = "Instant explanations",
                btnLabel = "Ask AI",
                icon = Icons.Default.Psychology,
                gradient = listOf(Color(0xFF8B5CF6), Color(0xFFD946EF)),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppNavTab.VOICE_TUTOR) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionItemCard(
                title = "Chapter Generator",
                desc = "Create quiz from notes",
                btnLabel = "Generate",
                icon = Icons.Default.Description,
                gradient = listOf(Color(0xFF0D9488), Color(0xFF10B981)),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppNavTab.GENERATE) }
            )
            QuickActionItemCard(
                title = "Voice Learning",
                desc = "Multilingual models",
                btnLabel = "Listen",
                icon = Icons.Default.SpatialAudio,
                gradient = listOf(Color(0xFFF59E0B), Color(0xFFF43F5E)),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppNavTab.VOICE_TUTOR) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickActionItemCard(
                title = "Robot Video",
                desc = "3D Hologram teacher",
                btnLabel = "Watch",
                icon = Icons.Default.SmartToy,
                gradient = listOf(Color(0xFF3B82F6), Color(0xFF4F46E5)),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppNavTab.ROBOT_VIDEO) }
            )
            QuickActionItemCard(
                title = "Snap & Solve",
                desc = "Camera problem solver",
                btnLabel = "Snap Photo",
                icon = Icons.Default.PhotoCamera,
                gradient = listOf(Color(0xFF7C3AED), Color(0xFF06B6D4)),
                modifier = Modifier.weight(1f),
                onClick = { onNavigate(AppNavTab.SNAP_SOLVE) }
            )
        }
    }
}

@Composable
private fun QuickActionItemCard(
    title: String,
    desc: String,
    btnLabel: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A).copy(alpha = 0.9f)
        ),
        border = BorderStroke(1.dp, gradient.first().copy(alpha = 0.35f)),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("quick_action_${title.replace(" ", "_").lowercase()}")
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
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = gradient.first().copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, gradient.first().copy(alpha = 0.5f)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = gradient.first(),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    text = btnLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = gradient.first()
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DailyChallengeCard(
    streakDays: Int,
    onStartDailyChallenge: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.5.dp, Color(0xFFF59E0B).copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_challenge_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF451A03).copy(alpha = 0.35f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Daily OMR Challenge",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Curated multi-subject master test",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Streak Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🔥 $streakDays Day Streak",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                        }
                    }
                }

                // Info Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoBadge(text = "10 Questions", color = Color(0xFF38BDF8))
                    InfoBadge(text = "5 Minutes", color = Color(0xFF10B981))
                    InfoBadge(text = "100 XP Bonus", color = Color(0xFFF59E0B))
                    InfoBadge(text = "Medium", color = Color(0xFFA855F7))
                }

                // CTA Button
                Button(
                    onClick = onStartDailyChallenge,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("start_daily_challenge_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF070B19),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Start Daily Challenge",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF070B19)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoBadge(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun ProgressOverviewDashboard(
    xp: Int,
    studyPoints: Int,
    levelNumber: Int,
    levelTitle: String,
    accuracy: Float,
    testsCompleted: Int,
    streakDays: Int
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("progress_overview_dashboard")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Level $levelNumber — $levelTitle",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "$xp / 3,000 XP to Level ${levelNumber + 1}",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Rank #12",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF818CF8),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // XP Progress Bar
            val xpProgress = (xp % 1000) / 1000f
            LinearProgressIndicator(
                progress = { xpProgress.coerceIn(0.1f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF00D2FF),
                trackColor = Color(0xFF1E293B)
            )

            // 6 Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(label = "Total XP", value = "$xp", color = Color(0xFF00D2FF), modifier = Modifier.weight(1f))
                StatCard(label = "Points", value = "$studyPoints", color = Color(0xFFF59E0B), modifier = Modifier.weight(1f))
                StatCard(label = "Accuracy", value = "${accuracy}%", color = Color(0xFF10B981), modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(label = "Tests Solved", value = "$testsCompleted", color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
                StatCard(label = "Streak", value = "$streakDays Days", color = Color(0xFFEC4899), modifier = Modifier.weight(1f))
                StatCard(label = "Status", value = "Prime Active", color = Color(0xFF38BDF8), modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1E293B).copy(alpha = 0.5f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
private fun ContinueLearningCarousel(
    chapters: List<RecentStudyChapter>,
    onContinue: (RecentStudyChapter) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(chapters) { ch ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F172A)
                ),
                border = BorderStroke(1.dp, ch.accentColor.copy(alpha = 0.35f)),
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("recent_chapter_${ch.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ch.accentColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = ch.subject,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ch.accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = ch.lastStudied,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Text(
                        text = ch.chapterName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 17.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${ch.progressPercent}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ch.accentColor
                            )
                        }
                        LinearProgressIndicator(
                            progress = { ch.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp)),
                            color = ch.accentColor,
                            trackColor = Color(0xFF1E293B)
                        )
                    }

                    Button(
                        onClick = { onContinue(ch) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ch.accentColor.copy(alpha = 0.2f)
                        ),
                        border = BorderStroke(1.dp, ch.accentColor.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ch.accentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    rec: AiStudyRecommendation,
    onStudyNow: () -> Unit,
    onPractice: () -> Unit,
    onViewExplanation: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.dp, rec.accentColor.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rec_card_${rec.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rec.accentColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, rec.accentColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = rec.diagnosticType,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = rec.accentColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = rec.subject,
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = rec.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = rec.reason,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onPractice,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rec.accentColor
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        text = "Practice",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF070B19)
                    )
                }

                OutlinedButton(
                    onClick = onViewExplanation,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF64748B)),
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                ) {
                    Text(
                        text = "Ask AI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AiTutorPreviewPanel(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmitQuery: (String) -> Unit
) {
    val suggestions = listOf(
        "Explain this chapter",
        "Create MCQs",
        "Summarize my notes",
        "Solve a problem",
        "Teach me with voice"
    )

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.5.dp, Color(0xFF8B5CF6).copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ai_tutor_preview_panel")
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "What would you like to learn today?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Input Field with Glowing Send Button
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Ask anything about your subjects…",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tutor_quick_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00D2FF),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.5f),
                    unfocusedContainerColor = Color(0xFF1E293B).copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                trailingIcon = {
                    IconButton(
                        onClick = { onSubmitQuery(query) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00D2FF))
                            .testTag("tutor_send_quick_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color(0xFF070B19),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSubmitQuery(query) }),
                singleLine = true
            )

            // Suggestion Chips
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                suggestions.forEach { chipText ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSubmitQuery(chipText) }
                    ) {
                        Text(
                            text = chipText,
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumAiBanner(
    isSubscribed: Boolean,
    studyPoints: Int,
    onViewPremium: () -> Unit,
    onUnlockWithPoints: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F172A)
        ),
        border = BorderStroke(1.5.dp, Color(0xFFA855F7).copy(alpha = 0.45f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("premium_ai_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF3B0764).copy(alpha = 0.4f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = Color(0xFFA855F7),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Unlock AI Premium",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFA855F7).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (isSubscribed) "PRO ACTIVE" else "5,000 PTS / PRO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC084FC),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = "Get faster responses, deeper explanations, advanced voice models, and smarter learning tools.",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 17.sp
                )

                // Benefits Grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PremiumBenefitRow(text = "⚡ Faster responses & Turbo AI engine")
                    PremiumBenefitRow(text = "🧠 Deeper multi-step problem solutions")
                    PremiumBenefitRow(text = "🎙️ Advanced multilingual AI voice models")
                    PremiumBenefitRow(text = "🔍 Deep mistake root-cause analysis")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onViewPremium,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA855F7)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("view_premium_btn")
                    ) {
                        Text("View Premium", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onUnlockWithPoints,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF00D2FF)),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("unlock_points_btn")
                    ) {
                        Text("Use Study Points", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00D2FF))
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumBenefitRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF10B981),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = Color(0xFFE2E8F0)
        )
    }
}

@Composable
private fun AchievementsPreviewSection(
    badges: List<com.example.data.model.AchievementBadge>
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(badges) { badge ->
            val isUnlocked = badge.isUnlocked
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = if (isUnlocked) Color(0xFF0F172A) else Color(0xFF0B1120).copy(alpha = 0.6f),
                border = BorderStroke(
                    1.dp,
                    if (isUnlocked) Color(0xFFF59E0B).copy(alpha = 0.5f) else Color(0xFF334155).copy(alpha = 0.4f)
                ),
                modifier = Modifier
                    .width(150.dp)
                    .testTag("badge_${badge.id}")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isUnlocked) Color(0xFFF59E0B).copy(alpha = 0.15f) else Color(0xFF1E293B)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlocked) {
                            Text(text = badge.iconEmoji, fontSize = 22.sp)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = badge.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color.White else Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = if (isUnlocked) badge.description else "Locked",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    icon: ImageVector,
    title: String,
    desc: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = desc, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}
