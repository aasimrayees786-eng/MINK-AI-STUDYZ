package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementBadge
import com.example.data.model.GamificationState
import com.example.data.model.LeaderboardUser
import com.example.data.model.OmrQuestion
import com.example.data.model.OmrTestRecord
import com.example.data.model.PremiumPerkItem
import com.example.ui.ChapterAIViewModel
import com.example.ui.components.OmrSheetView
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OmrArenaSubView {
    DASHBOARD,
    GENERATOR,
    LEADERBOARD,
    ACHIEVEMENTS,
    PREMIUM_STORE
}

@Composable
fun OmrChallengeScreen(
    viewModel: ChapterAIViewModel,
    modifier: Modifier = Modifier
) {
    val activeTest by viewModel.activeOmrTest.collectAsState()
    val activeQuestions by viewModel.activeQuestionsList.collectAsState()
    val activeAnswers by viewModel.activeUserAnswers.collectAsState()
    val activeQIndex by viewModel.activeQuestionIndex.collectAsState()
    val timerSeconds by viewModel.omrTimerSeconds.collectAsState()
    val activeResult by viewModel.activeTestResult.collectAsState()

    var subView by remember { mutableStateOf(OmrArenaSubView.DASHBOARD) }

    when {
        // Active Interactive OMR Test Session
        activeTest != null && activeQuestions.isNotEmpty() -> {
            OmrSheetView(
                testRecord = activeTest!!,
                questions = activeQuestions,
                userAnswers = activeAnswers,
                currentIndex = activeQIndex,
                timerSeconds = timerSeconds,
                onSelectBubble = { qNum, opt -> viewModel.selectOmrBubble(qNum, opt) },
                onJumpToQuestion = { idx -> viewModel.jumpToQuestion(idx) },
                onNextQuestion = { viewModel.nextQuestion() },
                onPrevQuestion = { viewModel.prevQuestion() },
                onSubmitTest = { viewModel.submitOmrTest() },
                onCancelTest = { viewModel.closeOmrTest() }
            )
        }

        // Test Results & Performance Dashboard View
        activeResult != null -> {
            OmrTestResultScreen(
                record = activeResult!!,
                viewModel = viewModel,
                onRetake = {
                    viewModel.startNewOmrChallenge(
                        subject = activeResult!!.subject,
                        chapterName = activeResult!!.chapterName,
                        gradeLevel = activeResult!!.gradeLevel,
                        questionCount = activeResult!!.totalQuestions,
                        difficulty = activeResult!!.difficulty
                    )
                },
                onDone = {
                    viewModel.closeTestResult()
                    subView = OmrArenaSubView.DASHBOARD
                }
            )
        }

        // Main Arena Hub
        else -> {
            OmrArenaHub(
                viewModel = viewModel,
                currentSubView = subView,
                onSelectSubView = { subView = it }
            )
        }
    }
}

@Composable
fun OmrArenaHub(
    viewModel: ChapterAIViewModel,
    currentSubView: OmrArenaSubView,
    onSelectSubView: (OmrArenaSubView) -> Unit,
    modifier: Modifier = Modifier
) {
    val gamification by viewModel.gamificationState.collectAsState()
    val recentTests by viewModel.allOmrTests.collectAsState()
    val adaptation by viewModel.difficultyAdaptation.collectAsState()
    val badges by viewModel.badgesList.collectAsState()
    val leaderboard by viewModel.leaderboardUsers.collectAsState()
    val premiumPerks by viewModel.premiumPerks.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B1120))
    ) {
        // Gamification Top Hero Banner
        Surface(
            color = Color(0xFF1E293B),
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7),
                            border = BorderStroke(2.dp, Color(0xFF38BDF8)),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🎯", fontSize = 22.sp)
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Level ${gamification.levelNumber}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = gamification.levelTitle.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${gamification.xp} XP Earned",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Study Points & Streak Badges
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Streak Flame
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEA580C).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🔥", fontSize = 14.sp)
                                Text(
                                    text = "${gamification.streakDays}d",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFB923C)
                                )
                            }
                        }

                        // Study Points Wallet
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEAB308).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🪙", fontSize = 14.sp)
                                Text(
                                    text = "${gamification.studyPoints}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE047)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // XP Progress Bar to next level
                val maxLevelXp = when (gamification.levelNumber) {
                    1 -> 200
                    2 -> 600
                    3 -> 1500
                    4 -> 3500
                    5 -> 6500
                    else -> 10000
                }
                val minLevelXp = when (gamification.levelNumber) {
                    1 -> 0
                    2 -> 200
                    3 -> 600
                    4 -> 1500
                    5 -> 3500
                    else -> 6500
                }
                val xpFraction = ((gamification.xp - minLevelXp).toFloat() / (maxLevelXp - minLevelXp)).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress to Level ${gamification.levelNumber + 1}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = "${gamification.xp} / $maxLevelXp XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { xpFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF334155)
                )
            }
        }

        // Sub-Navigation Tabs Strip
        ScrollableTabRow(
            selectedTabIndex = currentSubView.ordinal,
            containerColor = Color(0xFF0F172A),
            contentColor = Color(0xFF38BDF8),
            edgePadding = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                Pair(OmrArenaSubView.DASHBOARD, "🎯 Arena"),
                Pair(OmrArenaSubView.GENERATOR, "✨ New Test"),
                Pair(OmrArenaSubView.LEADERBOARD, "🏆 Leaderboard"),
                Pair(OmrArenaSubView.ACHIEVEMENTS, "🎖️ Badges"),
                Pair(OmrArenaSubView.PREMIUM_STORE, "👑 AI Premium")
            ).forEach { (view, title) ->
                Tab(
                    selected = currentSubView == view,
                    onClick = { onSelectSubView(view) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (currentSubView == view) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Body Content by SubView
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (currentSubView) {
                OmrArenaSubView.DASHBOARD -> OmrDashboardView(
                    viewModel = viewModel,
                    gamification = gamification,
                    adaptation = adaptation,
                    recentTests = recentTests,
                    onStartNew = { onSelectSubView(OmrArenaSubView.GENERATOR) },
                    onStartDaily = { viewModel.startDailyOmrChallenge() },
                    onOpenStore = { onSelectSubView(OmrArenaSubView.PREMIUM_STORE) },
                    onOpenRecord = { viewModel.openTestRecordResult(it) }
                )

                OmrArenaSubView.GENERATOR -> OmrGeneratorForm(
                    viewModel = viewModel,
                    onTestStarted = { /* handled by activeOmrTest */ }
                )

                OmrArenaSubView.LEADERBOARD -> OmrLeaderboardView(
                    users = leaderboard,
                    currentUserXp = gamification.xp,
                    currentUserPoints = gamification.studyPoints
                )

                OmrArenaSubView.ACHIEVEMENTS -> OmrAchievementsView(badges = badges)

                OmrArenaSubView.PREMIUM_STORE -> OmrPremiumStoreView(
                    viewModel = viewModel,
                    gamification = gamification,
                    perks = premiumPerks
                )
            }
        }
    }
}

@Composable
fun OmrDashboardView(
    viewModel: ChapterAIViewModel,
    gamification: GamificationState,
    adaptation: com.example.data.model.DifficultyAdaptationAnalysis,
    recentTests: List<OmrTestRecord>,
    onStartNew: () -> Unit,
    onStartDaily: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenRecord: (OmrTestRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Daily Challenge Featured Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.5.dp, Color(0xFFF97316)),
                modifier = Modifier.fillMaxWidth().testTag("omr_daily_challenge_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFEA580C).copy(alpha = 0.2f), Color(0xFF1E293B))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF97316).copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🔥", fontSize = 12.sp)
                                    Text(
                                        text = "DAILY OMR CHALLENGE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        color = Color(0xFFFB923C)
                                    )
                                }
                            }

                            Text(
                                text = "+100 Bonus XP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Curated Master Quiz: Multi-Topic Arena",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "10 Questions • 100 XP • 5 Minutes • Boosts Daily Streak",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onStartDaily,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            modifier = Modifier.fillMaxWidth().testTag("start_daily_omr_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Today's Challenge", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Action Buttons Row: Generate Custom OMR & AI Premium Store
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF0284C7).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onStartNew() }
                        .testTag("omr_create_custom_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("✨", fontSize = 24.sp)
                        Text(
                            text = "Generate OMR",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Any chapter, class & count",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFFEAB308).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenStore() }
                        .testTag("omr_open_premium_store_card")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("👑", fontSize = 24.sp)
                        Text(
                            text = "AI Premium",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Spend points to unlock",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // AI Difficulty Adaptation Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Adaptation",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "AI Performance & Difficulty Calibration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = adaptation.recommendationMessage,
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Suggested Tier", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = adaptation.recommendedDifficulty,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Avg Accuracy", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = "${adaptation.recentAccuracyAverage.toInt()}%",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent OMR Test History
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Test History",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "${recentTests.size} Completed",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        if (recentTests.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📝", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tests completed yet",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tap 'Start Today's Challenge' or generate a test paper to earn your first study points!",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentTests) { record ->
                val dateStr = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()).format(Date(record.timestamp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenRecord(record) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = record.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${record.subject} • ${record.gradeLevel} • $dateStr",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "${record.correctCount}/${record.totalQuestions} Correct",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                                Text(
                                    text = "+${record.pointsEarned} Points",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE047)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = when {
                                record.accuracyPercentage >= 90f -> Color(0xFF10B981).copy(alpha = 0.2f)
                                record.accuracyPercentage >= 70f -> Color(0xFF0284C7).copy(alpha = 0.2f)
                                else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            },
                            border = BorderStroke(
                                1.5.dp,
                                when {
                                    record.accuracyPercentage >= 90f -> Color(0xFF4ADE80)
                                    record.accuracyPercentage >= 70f -> Color(0xFF38BDF8)
                                    else -> Color(0xFFF87171)
                                }
                            ),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${record.accuracyPercentage.toInt()}%",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OmrGeneratorForm(
    viewModel: ChapterAIViewModel,
    onTestStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subject by viewModel.omrSubjectInput.collectAsState()
    val chapter by viewModel.omrChapterInput.collectAsState()
    val grade by viewModel.omrGradeInput.collectAsState()
    val count by viewModel.omrQuestionCount.collectAsState()
    val difficulty by viewModel.omrDifficulty.collectAsState()

    val isGenerating by viewModel.isGeneratingOmrTest.collectAsState()
    val generationStage by viewModel.omrGenerationStage.collectAsState()
    val genError by viewModel.omrGenerationError.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "✨ AI Chapter-to-OMR Generator",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Specify any subject and chapter to generate curriculum-aligned test papers with realistic OMR bubble sheets.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        // Subject Input & Quick Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Subject", fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                OutlinedTextField(
                    value = subject,
                    onValueChange = { viewModel.omrSubjectInput.value = it },
                    placeholder = { Text("e.g. Physics, Biology, Chemistry, Mathematics") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("omr_input_subject")
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Physics", "Chemistry", "Biology", "Mathematics", "World History", "Computer Science").forEach { sub ->
                        FilterChip(
                            selected = subject.equals(sub, ignoreCase = true),
                            onClick = { viewModel.omrSubjectInput.value = sub },
                            label = { Text(sub, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }
        }

        // Chapter Name & Popular Chapters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Chapter / Topic Name", fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                OutlinedTextField(
                    value = chapter,
                    onValueChange = { viewModel.omrChapterInput.value = it },
                    placeholder = { Text("e.g. Laws of Motion, Cell Division, Thermodynamics") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF1E293B),
                        unfocusedContainerColor = Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("omr_input_chapter")
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "Laws of Motion & Friction",
                        "Human Skeleton & 206 Bones",
                        "Cell Structure & Organelles",
                        "Chemical Bonding & Reactions",
                        "Quadratic Equations & Roots"
                    ).forEach { ch ->
                        FilterChip(
                            selected = chapter.equals(ch, ignoreCase = true),
                            onClick = { viewModel.omrChapterInput.value = ch },
                            label = { Text(ch, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }
        }

        // Class / Grade Level
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Class / Grade Level", fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Class 8", "Class 9", "Class 10", "Class 11", "Class 12", "College / Competitive").forEach { gr ->
                        FilterChip(
                            selected = grade == gr,
                            onClick = { viewModel.omrGradeInput.value = gr },
                            label = { Text(gr, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color(0xFF94A3B8)
                            )
                        )
                    }
                }
            }
        }

        // Question Count (10, 20, 30, 50)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Number of Questions", fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(10, 20, 30, 50).forEach { qCount ->
                        val isSel = count == qCount
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF334155)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.omrQuestionCount.value = qCount }
                                .testTag("omr_count_$qCount")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$qCount Qs",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSel) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Difficulty Selector (Easy, Medium, Hard, Mixed)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Difficulty Level", fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Easy", "Medium", "Hard", "Mixed").forEach { diff ->
                        val isSel = difficulty == diff
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSel) Color(0xFF0284C7) else Color(0xFF1E293B),
                            border = BorderStroke(1.dp, if (isSel) Color(0xFF38BDF8) else Color(0xFF334155)),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.omrDifficulty.value = diff }
                                .testTag("omr_diff_$diff")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = diff,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSel) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }
            }
        }

        if (genError != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = genError!!,
                        color = Color(0xFFF87171),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Generate Action Button
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    viewModel.startNewOmrChallenge(
                        subject = subject,
                        chapterName = chapter,
                        gradeLevel = grade,
                        questionCount = count,
                        difficulty = difficulty
                    )
                },
                enabled = !isGenerating && subject.isNotBlank() && chapter.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("omr_generate_button")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (generationStage.isNotBlank()) generationStage else "Generating Questions...",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Generate",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Generate $count-Question OMR Test",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OmrTestResultScreen(
    record: OmrTestRecord,
    viewModel: ChapterAIViewModel,
    onRetake: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGeneratingLesson by viewModel.isGeneratingMistakeLesson.collectAsState()
    val mistakeLessonText by viewModel.activeMistakeLessonText.collectAsState()

    val questionsType = Types.newParameterizedType(List::class.java, OmrQuestion::class.java)
    val questions = try {
        Moshi.Builder().build().adapter<List<OmrQuestion>>(questionsType).fromJson(record.questionsJson) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }

    val userAnswers = mutableMapOf<Int, String>()
    try {
        val json = JSONObject(record.userAnswersJson)
        val keys = json.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            userAnswers[k.toInt()] = json.getString(k)
        }
    } catch (e: Exception) {
        // ignore
    }

    val minutes = record.timeSpentSeconds / 60
    val seconds = record.timeSpentSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Celebratory Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.5.dp, Color(0xFF38BDF8)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You earned +${record.pointsEarned} Study Points & +${record.xpEarned} XP!",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = Color(0xFFFDE047),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = record.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Matrix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${record.accuracyPercentage.toInt()}%",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = if (record.accuracyPercentage >= 80f) Color(0xFF4ADE80) else Color(0xFF38BDF8)
                            )
                            Text("Accuracy", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${record.correctCount}/${record.totalQuestions}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4ADE80)
                            )
                            Text("Correct", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${record.incorrectCount}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = if (record.incorrectCount > 0) Color(0xFFF87171) else Color(0xFF94A3B8)
                            )
                            Text("Incorrect", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = formattedTime,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8),
                                fontFamily = FontFamily.Monospace
                            )
                            Text("Time Taken", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }
        }

        // Action: Learn My Mistakes (AI Personalized Revision)
        if (record.incorrectCount > 0 || record.unansweredCount > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    border = BorderStroke(1.dp, Color(0xFFEAB308)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = "Mistakes",
                                    tint = Color(0xFFFDE047),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Targeted Recovery Guide",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { viewModel.generateMistakeLessonForTest(record) },
                                enabled = !isGeneratingLesson,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("learn_my_mistakes_button")
                            ) {
                                if (isGeneratingLesson) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Analyzing...", fontSize = 12.sp)
                                } else {
                                    Text("💡 Learn My Mistakes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (mistakeLessonText != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = mistakeLessonText!!,
                                    fontSize = 13.sp,
                                    color = Color(0xFFE2E8F0),
                                    lineHeight = 19.sp,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Detailed Question-by-Question Breakdown
        item {
            Text(
                text = "Detailed Question Paper Review",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        itemsIndexed(questions) { idx, q ->
            val userChosen = userAnswers[q.questionNumber]
            val isCorrect = userChosen?.equals(q.correctOption, ignoreCase = true) == true
            val isUnanswered = userChosen == null

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(
                    1.dp,
                    when {
                        isCorrect -> Color(0xFF10B981)
                        isUnanswered -> Color(0xFF64748B)
                        else -> Color(0xFFEF4444)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${q.questionNumber}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                                isUnanswered -> Color(0xFF64748B).copy(alpha = 0.2f)
                                else -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = when {
                                    isCorrect -> "✓ CORRECT (+10)"
                                    isUnanswered -> "— SKIPPED (0)"
                                    else -> "✗ INCORRECT (0)"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isCorrect -> Color(0xFF4ADE80)
                                    isUnanswered -> Color(0xFF94A3B8)
                                    else -> Color(0xFFF87171)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = q.questionText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Options Review
                    q.options.forEachIndexed { optIdx, optText ->
                        val optLetter = when (optIdx) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            else -> "D"
                        }
                        val isUserChoice = userChosen == optLetter
                        val isCorrectChoice = q.correctOption.equals(optLetter, ignoreCase = true)

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isCorrectChoice -> Color(0xFF10B981).copy(alpha = 0.2f)
                                isUserChoice -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                else -> Color(0xFF0F172A)
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isCorrectChoice -> Color(0xFF10B981)
                                    isUserChoice -> Color(0xFFEF4444)
                                    else -> Color(0xFF334155)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "[$optLetter]",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = when {
                                        isCorrectChoice -> Color(0xFF4ADE80)
                                        isUserChoice -> Color(0xFFF87171)
                                        else -> Color(0xFF94A3B8)
                                    }
                                )
                                Text(
                                    text = optText,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isCorrectChoice) {
                                    Text("✓ Correct", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                                } else if (isUserChoice) {
                                    Text("✗ Marked", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF87171))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Explanation Box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "💡 Explanation:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                            Text(
                                text = q.explanation,
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8)),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retake", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Retake Test")
                }

                Button(
                    onClick = onDone,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("omr_results_done_button")
                ) {
                    Text("Back to Arena", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun OmrLeaderboardView(
    users: List<LeaderboardUser>,
    currentUserXp: Int,
    currentUserPoints: Int,
    modifier: Modifier = Modifier
) {
    var timeframe by remember { mutableStateOf("Weekly") } // "Weekly" or "Monthly"

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🏆 Student Leaderboard",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Compete on accuracy & consistent daily study",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        listOf("Weekly", "Monthly").forEach { tf ->
                            val isSel = timeframe == tf
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) Color(0xFF0284C7) else Color.Transparent)
                                    .clickable { timeframe = tf }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = tf,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top 3 Podium
        item {
            val top1 = users.getOrNull(0)
            val top2 = users.getOrNull(1)
            val top3 = users.getOrNull(2)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Rank 2 (Silver)
                if (top2 != null) {
                    PodiumUserCard(
                        user = top2,
                        podiumColor = Color(0xFF94A3B8),
                        rankBadge = "🥈 2nd",
                        podiumHeight = 110.dp,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Rank 1 (Gold - Center & Taller)
                if (top1 != null) {
                    PodiumUserCard(
                        user = top1,
                        podiumColor = Color(0xFFEAB308),
                        rankBadge = "🥇 1st",
                        podiumHeight = 135.dp,
                        modifier = Modifier.weight(1.1f)
                    )
                }

                // Rank 3 (Bronze)
                if (top3 != null) {
                    PodiumUserCard(
                        user = top3,
                        podiumColor = Color(0xFFD97706),
                        rankBadge = "🥉 3rd",
                        podiumHeight = 95.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Full Arena Rankings",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFCBD5E1)
            )
        }

        // Remaining users list
        items(users) { user ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (user.isCurrentUser) Color(0xFF0284C7).copy(alpha = 0.25f) else Color(0xFF1E293B),
                border = BorderStroke(
                    1.dp,
                    if (user.isCurrentUser) Color(0xFF38BDF8) else Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "#${user.rank}",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = when (user.rank) {
                                1 -> Color(0xFFFDE047)
                                2 -> Color(0xFFCBD5E1)
                                3 -> Color(0xFFFB923C)
                                else -> Color(0xFF94A3B8)
                            },
                            modifier = Modifier.width(32.dp)
                        )

                        Text(text = user.avatarEmoji, fontSize = 20.sp)

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = user.username,
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.isCurrentUser) Color(0xFF38BDF8) else Color.White,
                                    fontSize = 13.sp
                                )
                                if (user.isCurrentUser) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF38BDF8).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "YOU",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF38BDF8),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${user.levelTitle} • 🔥 ${user.streakDays}d streak",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${user.studyPoints} pts",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${user.accuracy.toInt()}% accuracy",
                            fontSize = 10.sp,
                            color = Color(0xFF4ADE80)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PodiumUserCard(
    user: LeaderboardUser,
    podiumColor: Color,
    rankBadge: String,
    podiumHeight: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = user.avatarEmoji, fontSize = 24.sp)
        Text(
            text = user.username.take(10),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 1
        )
        Text(
            text = "${user.studyPoints} pts",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFDE047)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
            color = podiumColor.copy(alpha = 0.25f),
            border = BorderStroke(1.dp, podiumColor.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = rankBadge,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = podiumColor
                )
            }
        }
    }
}

@Composable
fun OmrAchievementsView(
    badges: List<AchievementBadge>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "🎖️ Achievements & Study Badges",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Unlock prestigious milestones and claim bonus study points & XP.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )
        }

        items(badges) { badge ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) Color(0xFF1E293B) else Color(0xFF161E2E)
                ),
                border = BorderStroke(
                    1.dp,
                    if (badge.isUnlocked) Color(0xFF38BDF8) else Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (badge.isUnlocked) Color(0xFF0284C7).copy(alpha = 0.2f) else Color(0xFF334155),
                        border = BorderStroke(
                            1.5.dp,
                            if (badge.isUnlocked) Color(0xFF38BDF8) else Color(0xFF64748B)
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = badge.iconEmoji, fontSize = 24.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = badge.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            if (badge.isUnlocked) {
                                Text("✓ Unlocked", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                            }
                        }

                        Text(
                            text = badge.description,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { badge.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (badge.isUnlocked) Color(0xFF4ADE80) else Color(0xFF38BDF8),
                            trackColor = Color(0xFF334155)
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "+${badge.pointsReward} pts",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFDE047),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "+${badge.xpReward} XP",
                            fontSize = 10.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OmrPremiumStoreView(
    viewModel: ChapterAIViewModel,
    gamification: GamificationState,
    perks: List<PremiumPerkItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Store Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.5.dp, Color(0xFFEAB308)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFEAB308).copy(alpha = 0.2f), Color(0xFF1E293B))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "👑 AI Premium Hub",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFDE047)
                                )
                                Text(
                                    text = "Unlock power features using your earned Study Points!",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEAB308).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFFFBBF24))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("🪙", fontSize = 14.sp)
                                    Text(
                                        text = "${gamification.studyPoints} pts",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE047),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Suite Unlock Button (5000 pts)
                        Button(
                            onClick = { viewModel.unlockAllPremiumWithPoints() },
                            enabled = gamification.studyPoints >= 5000 && !gamification.isPremiumSubscribed,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                            modifier = Modifier.fillMaxWidth().testTag("unlock_all_premium_button")
                        ) {
                            Text(
                                text = if (gamification.isPremiumSubscribed) "👑 Full VIP Suite Active" else "Unlock All Features (5,000 Study Points)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Individual AI Feature Perks",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }

        // List of Perks
        items(perks) { perk ->
            val isUnlocked = perk.isUnlocked || gamification.unlockedPerkIds.contains(perk.id) || gamification.isPremiumSubscribed
            val canAfford = gamification.studyPoints >= perk.requiredPoints

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(
                    1.dp,
                    if (isUnlocked) Color(0xFF4ADE80) else Color(0xFF334155)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = perk.iconEmoji, fontSize = 22.sp)
                            Text(
                                text = perk.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF38BDF8).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = perk.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = perk.description,
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("🪙", fontSize = 12.sp)
                            Text(
                                text = "${perk.requiredPoints} Study Points",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047)
                            )
                        }

                        if (isUnlocked) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = "Unlocked",
                                        tint = Color(0xFF4ADE80),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text("Unlocked", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                                }
                            }
                        } else {
                            Button(
                                onClick = { viewModel.unlockPremiumPerk(perk.id) },
                                enabled = canAfford,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                modifier = Modifier.testTag("unlock_perk_${perk.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Unlock",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (canAfford) "Unlock" else "Need Points",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
