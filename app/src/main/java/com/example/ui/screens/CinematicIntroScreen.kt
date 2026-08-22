package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Data class for cosmic/ambient dust particles
 */
private data class CosmicParticle(
    val x: Float,
    val y: Float,
    val radius: Float,
    val speed: Float,
    val angle: Float,
    val color: Color,
    val maxAlpha: Float
)

/**
 * High-Budget Anime Studio Opening & Cinematic AI Learning Intro
 * AASIM-STUDIO • Learn Smarter. Create Faster. Go Further.
 */
@Composable
fun CinematicIntroScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation drivers
    val lineDrawProgress = remember { Animatable(0f) }
    val emblemGlowAlpha = remember { Animatable(0f) }
    val titleRevealAlpha = remember { Animatable(0f) }
    val subtitleRevealAlpha = remember { Animatable(0f) }
    val lightSweepProgress = remember { Animatable(-1f) }
    val exitTransitionAlpha = remember { Animatable(1f) }
    val emblemScale = remember { Animatable(0.7f) }

    var showSkipButton by remember { mutableStateOf(false) }
    var isSkipped by remember { mutableStateOf(false) }

    // Particles system state
    val particles = remember {
        val random = Random(42)
        List(45) {
            CosmicParticle(
                x = random.nextFloat(),
                y = random.nextFloat(),
                radius = random.nextFloat() * 2.5f + 1f,
                speed = random.nextFloat() * 0.08f + 0.02f,
                angle = random.nextFloat() * 6.28f,
                color = when (random.nextInt(4)) {
                    0 -> Color(0xFF00D2FF) // Cyan
                    1 -> Color(0xFF8B5CF6) // Violet
                    2 -> Color(0xFF38BDF8) // Electric Blue
                    else -> Color(0xFFE2E8F0) // Star white
                },
                maxAlpha = random.nextFloat() * 0.6f + 0.3f
            )
        }
    }

    // Continuous floating particle driver
    val infiniteTransition = rememberInfiniteTransition(label = "cosmic_drift")
    val cosmicPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cosmic_phase"
    )

    // Pulse for glowing emblem aura
    val emblemPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emblem_pulse"
    )

    // Master Timeline sequence (Approx 4.8 seconds total or skip)
    LaunchedEffect(Unit) {
        // Show skip button after 1 second
        launch {
            delay(1000)
            showSkipButton = true
        }

        // 0.0s - 0.4s: Initial dark cosmos fade-in
        delay(300)

        // 0.4s - 2.0s: Line drawing of the abstract knowledge & AI symbol
        launch {
            emblemScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(1800, easing = EaseInOutQuad)
            )
        }
        lineDrawProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1600, easing = EaseInOutQuad)
        )

        // 2.0s - 2.6s: Glow burst around symbol & reveal title "AASIM-STUDIO"
        launch {
            emblemGlowAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
        }
        titleRevealAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )

        // 2.6s - 3.2s: Light sweep across title & reveal subtitle
        launch {
            lightSweepProgress.animateTo(
                targetValue = 2f,
                animationSpec = tween(900, easing = LinearEasing)
            )
        }
        subtitleRevealAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )

        // 3.2s - 4.4s: Hold the glorious anime studio identity
        delay(1300)

        // 4.4s - 5.0s: Controlled smooth transition into Home Dashboard
        if (!isSkipped) {
            exitTransitionAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(500, easing = FastOutSlowInEasing)
            )
            onFinish()
        }
    }

    val handleSkip = {
        if (!isSkipped) {
            isSkipped = true
            onFinish()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(exitTransitionAlpha.value)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F172A), // Deep Navy Core
                        Color(0xFF070B19), // Midnight Blue
                        Color(0xFF030712)  // Cosmic Black
                    ),
                    center = Offset.Unspecified,
                    radius = 1200f
                )
            )
            .testTag("cinematic_intro_screen"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Cosmic Dust & Animated Light Rays Canvas
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val centerX = canvasWidth / 2f
            val centerY = canvasHeight / 2f

            // Soft blue-purple radial light rays in background
            val glowRadius = canvasWidth.coerceAtLeast(canvasHeight) * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6366F1).copy(alpha = 0.22f * emblemGlowAlpha.value),
                        Color(0xFF00D2FF).copy(alpha = 0.12f * emblemGlowAlpha.value),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = Offset(centerX, centerY)
            )

            // Animated light sweep beam
            if (lightSweepProgress.value in -0.5f..1.5f) {
                val sweepX = canvasWidth * lightSweepProgress.value
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00D2FF).copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.6f),
                            Color(0xFF8B5CF6).copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        startX = sweepX - 120f,
                        endX = sweepX + 120f
                    ),
                    start = Offset(sweepX - 80f, 0f),
                    end = Offset(sweepX + 80f, canvasHeight),
                    strokeWidth = 3f,
                    blendMode = BlendMode.Screen
                )
            }

            // Draw cosmic moving particles
            particles.forEachIndexed { i, p ->
                val dynamicOffset = (cosmicPhase + (i * 0.2f)) % 6.28318f
                val currentX = (p.x * canvasWidth + sin(dynamicOffset) * 24f * p.speed) % canvasWidth
                val currentY = (p.y * canvasHeight + cos(dynamicOffset) * 20f * p.speed) % canvasHeight
                val alpha = (sin(dynamicOffset + i) * 0.5f + 0.5f) * p.maxAlpha

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius,
                    center = Offset(currentX, currentY)
                )
            }
        }

        // 2. Centerpiece: Original Abstract AI-Knowledge Symbol & Title Sequence
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Abstract Emblem Container
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(emblemScale.value * emblemPulse),
                contentAlignment = Alignment.Center
            ) {
                // Vector Canvas drawing the abstract emblem
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f

                    // Draw outer subtle glowing diamond halo
                    if (emblemGlowAlpha.value > 0f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00D2FF).copy(alpha = 0.45f * emblemGlowAlpha.value),
                                    Color(0xFF8B5CF6).copy(alpha = 0.25f * emblemGlowAlpha.value),
                                    Color.Transparent
                                ),
                                center = Offset(cx, cy),
                                radius = w * 0.6f
                            ),
                            radius = w * 0.6f,
                            center = Offset(cx, cy)
                        )
                    }

                    // Vector Path: Futuristic Geometric Diamond-Constellation of Knowledge & AI
                    // Top Vertex: (cx, cy - 50)
                    // Right Vertex: (cx + 46, cy)
                    // Bottom Vertex: (cx, cy + 50)
                    // Left Vertex: (cx - 46, cy)
                    val emblemPath = Path().apply {
                        moveTo(cx, cy - 48f)
                        lineTo(cx + 44f, cy)
                        lineTo(cx, cy + 48f)
                        lineTo(cx - 44f, cy)
                        close()

                        // Inner Knowledge Prism Cross / Neural Links
                        moveTo(cx, cy - 48f)
                        lineTo(cx, cy + 48f)
                        moveTo(cx - 44f, cy)
                        lineTo(cx + 44f, cy)

                        // Inner diagonal faceted diamond lines (AI Synapses)
                        moveTo(cx - 24f, cy - 26f)
                        lineTo(cx + 24f, cy + 26f)
                        moveTo(cx + 24f, cy - 26f)
                        lineTo(cx - 24f, cy + 26f)
                    }

                    // Render path drawing with animated line progress
                    val pathMeasure = PathMeasure()
                    pathMeasure.setPath(emblemPath, false)
                    val totalLength = pathMeasure.length
                    val drawLength = totalLength * lineDrawProgress.value.coerceIn(0f, 1f)

                    val segmentPath = Path()
                    pathMeasure.getSegment(0f, drawLength, segmentPath, true)

                    // Draw the electric neon stroke
                    drawPath(
                        path = segmentPath,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00D2FF), // Cyan
                                Color(0xFF38BDF8), // Electric blue
                                Color(0xFF8B5CF6), // Violet
                                Color(0xFFA855F7)  // Electric purple
                            )
                        ),
                        style = Stroke(
                            width = 3.5f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw glowing neural nodes at key vertices
                    if (lineDrawProgress.value > 0.3f) {
                        val nodeAlpha = ((lineDrawProgress.value - 0.3f) / 0.7f).coerceIn(0f, 1f)
                        val vertices = listOf(
                            Offset(cx, cy - 48f),
                            Offset(cx + 44f, cy),
                            Offset(cx, cy + 48f),
                            Offset(cx - 44f, cy),
                            Offset(cx, cy) // Central neural node
                        )

                        vertices.forEachIndexed { index, pt ->
                            val ptColor = if (index == 4) Color.White else Color(0xFF00D2FF)
                            // Outer node glow
                            drawCircle(
                                color = ptColor.copy(alpha = 0.4f * nodeAlpha),
                                radius = if (index == 4) 8f else 5.5f,
                                center = pt
                            )
                            // Core node
                            drawCircle(
                                color = ptColor.copy(alpha = nodeAlpha),
                                radius = if (index == 4) 4.5f else 3f,
                                center = pt
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title: AASIM-STUDIO
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(titleRevealAlpha.value)
            ) {
                Text(
                    text = "AASIM-STUDIO",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    color = Color.White,
                    modifier = Modifier.testTag("cinematic_studio_title")
                )

                Spacer(modifier = Modifier.height(6.dp))

                // App Badge: MINK STUDY AI
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1B4B).copy(alpha = 0.75f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00D2FF).copy(alpha = 0.4f)),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF00D2FF),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "MINK STUDY AI PLATFORM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subtitle: "Learn Smarter. Create Faster. Go Further."
                Text(
                    text = "Learn Smarter. Create Faster. Go Further.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.8.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier
                        .alpha(subtitleRevealAlpha.value)
                        .testTag("cinematic_studio_subtitle")
                )
            }
        }

        // 3. Skip Intro Button (Appears bottom-right after 1 second)
        AnimatedVisibility(
            visible = showSkipButton,
            enter = fadeIn(animationSpec = tween(400)),
            exit = fadeOut(animationSpec = tween(200)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 36.dp, end = 24.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = handleSkip)
                    .testTag("skip_intro_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Skip Intro",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE2E8F0)
                    )
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Skip Intro",
                        tint = Color(0xFF00D2FF),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}
