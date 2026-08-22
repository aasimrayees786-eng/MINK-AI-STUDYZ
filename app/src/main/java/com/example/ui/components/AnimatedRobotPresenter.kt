package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.RobotEmotion
import com.example.data.model.RobotGesture
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Animated Teaching Robot Presenter ("RoboTeacher Mink")
 * Realistic, friendly humanoid teaching robot with natural facial expressions,
 * audio-synced speaking mouth visemes, and expressive educational gestures.
 */
@Composable
fun AnimatedRobotPresenter(
    gesture: RobotGesture,
    emotion: RobotEmotion,
    isSpeaking: Boolean,
    audioAmplitude: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_idle_dynamics")

    // Smooth hover & breathing bobbing animation
    val hoverOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover"
    )

    // Core pulse
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "core_pulse"
    )

    // Eye Blink Timer
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay((2800..5000).random().toLong())
            isBlinking = true
            delay(160)
            isBlinking = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("animated_robot_presenter")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scale = size.height * 0.00165f
            val baseCenter = Offset(size.width * 0.5f, size.height * 0.52f + (hoverOffset * scale))

            // 1. Draw Hover Shadow
            drawOval(
                color = Color(0xFF00E5FF).copy(alpha = 0.22f),
                topLeft = Offset(baseCenter.x - 70f * scale, size.height * 0.90f),
                size = Size(140f * scale, 24f * scale)
            )

            // 2. Draw Robotic Torso & Chest Armor
            drawRobotTorso(baseCenter, scale, isSpeaking, corePulse, audioAmplitude)

            // 3. Draw Robotic Arms according to active Gesture (Pointing, Welcoming, Thinking, etc.)
            drawRobotArms(baseCenter, scale, gesture, isSpeaking, audioAmplitude)

            // 4. Draw Robot Head & Visor
            drawRobotHead(baseCenter, scale, emotion, isBlinking, isSpeaking, audioAmplitude)

            // 5. Draw Antenna Beacon
            drawRobotAntenna(baseCenter, scale, isSpeaking)
        }
    }
}

private fun DrawScope.drawRobotTorso(
    center: Offset,
    scale: Float,
    isSpeaking: Boolean,
    corePulse: Float,
    audioAmplitude: Float
) {
    val bodyTop = center.y - 30f * scale
    val bodyWidth = 110f * scale
    val bodyHeight = 130f * scale

    // Neck Connector
    drawRoundRect(
        color = Color(0xFF334155),
        topLeft = Offset(center.x - 18f * scale, bodyTop - 16f * scale),
        size = Size(36f * scale, 20f * scale),
        cornerRadius = CornerRadius(6f * scale)
    )

    // Main Torso Chassis (Futuristic Sleek Ceramic White & Platinum)
    val torsoPath = Path().apply {
        moveTo(center.x - bodyWidth * 0.45f, bodyTop)
        lineTo(center.x + bodyWidth * 0.45f, bodyTop)
        lineTo(center.x + bodyWidth * 0.40f, bodyTop + bodyHeight)
        lineTo(center.x - bodyWidth * 0.40f, bodyTop + bodyHeight)
        close()
    }

    drawPath(
        torsoPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFF8FAFC), Color(0xFFE2E8F0), Color(0xFFCBD5E1)),
            startY = bodyTop,
            endY = bodyTop + bodyHeight
        )
    )
    drawPath(torsoPath, color = Color(0xFF94A3B8), style = Stroke(width = 2.5f * scale))

    // Shoulder Armor Pauldrons
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White, Color(0xFF64748B))),
        radius = 20f * scale,
        center = Offset(center.x - bodyWidth * 0.48f, bodyTop + 14f * scale)
    )
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White, Color(0xFF64748B))),
        radius = 20f * scale,
        center = Offset(center.x + bodyWidth * 0.48f, bodyTop + 14f * scale)
    )

    // Cyber Chest Accent Inset
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(center.x - 36f * scale, bodyTop + 18f * scale),
        size = Size(72f * scale, 58f * scale),
        cornerRadius = CornerRadius(14f * scale)
    )

    // Pulsing Quantum Core / Arc Reactor (Blue/Cyan energy with audio reactivity)
    val coreRadius = (16f * scale) * (if (isSpeaking) 1f + (audioAmplitude * 0.3f) else corePulse)
    val coreCenter = Offset(center.x, bodyTop + 47f * scale)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                Color(0xFF00E5FF),
                Color(0xFF3B82F6),
                Color.Transparent
            ),
            center = coreCenter,
            radius = coreRadius * 1.5f
        ),
        radius = coreRadius * 1.5f,
        center = coreCenter
    )
    drawCircle(color = Color(0xFF00E5FF), radius = coreRadius, center = coreCenter)
    drawCircle(color = Color.White, radius = coreRadius * 0.4f, center = coreCenter)

    // Lower Torso / Hover Thruster Emitter Base
    val thrusterTop = bodyTop + bodyHeight
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(center.x - 30f * scale, thrusterTop),
        size = Size(60f * scale, 24f * scale),
        cornerRadius = CornerRadius(10f * scale)
    )

    // Anti-gravity Ion Propulsion Glow Ring
    drawOval(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00E5FF), Color(0xFF38BDF8), Color.Transparent),
            center = Offset(center.x, thrusterTop + 24f * scale),
            radius = 35f * scale
        ),
        topLeft = Offset(center.x - 35f * scale, thrusterTop + 14f * scale),
        size = Size(70f * scale, 22f * scale)
    )
}

private fun DrawScope.drawRobotHead(
    center: Offset,
    scale: Float,
    emotion: RobotEmotion,
    isBlinking: Boolean,
    isSpeaking: Boolean,
    audioAmplitude: Float
) {
    val headCenter = Offset(center.x, center.y - 100f * scale)
    val headWidth = 100f * scale
    val headHeight = 84f * scale

    // Sleek White Robot Helmet Shell
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF1F5F9), Color(0xFFE2E8F0)),
            startY = headCenter.y - headHeight * 0.5f,
            endY = headCenter.y + headHeight * 0.5f
        ),
        topLeft = Offset(headCenter.x - headWidth * 0.5f, headCenter.y - headHeight * 0.5f),
        size = Size(headWidth, headHeight),
        cornerRadius = CornerRadius(28f * scale)
    )
    drawRoundRect(
        color = Color(0xFF94A3B8),
        topLeft = Offset(headCenter.x - headWidth * 0.5f, headCenter.y - headHeight * 0.5f),
        size = Size(headWidth, headHeight),
        cornerRadius = CornerRadius(28f * scale),
        style = Stroke(width = 2.5f * scale)
    )

    // Ear Pod Dials on Sides
    drawCircle(
        color = Color(0xFF38BDF8),
        radius = 11f * scale,
        center = Offset(headCenter.x - headWidth * 0.52f, headCenter.y)
    )
    drawCircle(
        color = Color(0xFF38BDF8),
        radius = 11f * scale,
        center = Offset(headCenter.x + headWidth * 0.52f, headCenter.y)
    )

    // Dark OLED Screen Visor Faceplate
    val visorWidth = 78f * scale
    val visorHeight = 52f * scale
    val visorTop = headCenter.y - visorHeight * 0.48f

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF0F172A), Color(0xFF020617))
        ),
        topLeft = Offset(headCenter.x - visorWidth * 0.5f, visorTop),
        size = Size(visorWidth, visorHeight),
        cornerRadius = CornerRadius(18f * scale)
    )
    drawRoundRect(
        color = Color(0xFF00E5FF).copy(alpha = 0.5f),
        topLeft = Offset(headCenter.x - visorWidth * 0.5f, visorTop),
        size = Size(visorWidth, visorHeight),
        cornerRadius = CornerRadius(18f * scale),
        style = Stroke(width = 1.5f * scale)
    )

    // Digital LED Expressive Eyes
    val eyeColor = when (emotion) {
        RobotEmotion.HAPPY -> Color(0xFF00E5FF)
        RobotEmotion.ENTHUSIASTIC -> Color(0xFF38BDF8)
        RobotEmotion.CURIOUS -> Color(0xFFFFD54F)
        RobotEmotion.SERIOUS_FOCUS -> Color(0xFF818CF8)
    }

    val eyeY = visorTop + 18f * scale
    val eyeSpacing = 19f * scale

    if (isBlinking) {
        // Closed Eye Slits
        drawLine(
            color = eyeColor,
            start = Offset(headCenter.x - eyeSpacing - 9f * scale, eyeY),
            end = Offset(headCenter.x - eyeSpacing + 9f * scale, eyeY),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Round
        )
        drawLine(
            color = eyeColor,
            start = Offset(headCenter.x + eyeSpacing - 9f * scale, eyeY),
            end = Offset(headCenter.x + eyeSpacing + 9f * scale, eyeY),
            strokeWidth = 3f * scale,
            cap = StrokeCap.Round
        )
    } else {
        // Open Expressive Eyes (Capsules / Arcs)
        when (emotion) {
            RobotEmotion.HAPPY, RobotEmotion.ENTHUSIASTIC -> {
                // Happy curved eyes (^ ^)
                drawArc(
                    color = eyeColor,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(headCenter.x - eyeSpacing - 10f * scale, eyeY - 8f * scale),
                    size = Size(20f * scale, 16f * scale),
                    style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round)
                )
                drawArc(
                    color = eyeColor,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(headCenter.x + eyeSpacing - 10f * scale, eyeY - 8f * scale),
                    size = Size(20f * scale, 16f * scale),
                    style = Stroke(width = 3.5f * scale, cap = StrokeCap.Round)
                )
            }
            else -> {
                // Oval LED Eyes
                drawRoundRect(
                    color = eyeColor,
                    topLeft = Offset(headCenter.x - eyeSpacing - 8f * scale, eyeY - 6f * scale),
                    size = Size(16f * scale, 14f * scale),
                    cornerRadius = CornerRadius(5f * scale)
                )
                drawRoundRect(
                    color = eyeColor,
                    topLeft = Offset(headCenter.x + eyeSpacing - 8f * scale, eyeY - 6f * scale),
                    size = Size(16f * scale, 14f * scale),
                    cornerRadius = CornerRadius(5f * scale)
                )
                // Highlights
                drawCircle(color = Color.White, radius = 2.5f * scale, center = Offset(headCenter.x - eyeSpacing - 2f * scale, eyeY - 2f * scale))
                drawCircle(color = Color.White, radius = 2.5f * scale, center = Offset(headCenter.x + eyeSpacing + 2f * scale, eyeY - 2f * scale))
            }
        }
    }

    // Audio-Reactive Talking Digital Mouth / Equalizer Visemes
    val mouthY = visorTop + 38f * scale
    val mouthColor = Color(0xFF00E5FF)

    if (isSpeaking) {
        // Multi-bar animated LED waveform equalizer mouth
        val numBars = 5
        val barWidth = 4f * scale
        val maxBarHeight = 16f * scale
        val startX = headCenter.x - (numBars * barWidth * 1.6f / 2f)

        for (i in 0 until numBars) {
            val barAmp = (0.3f + (sin((i * 1.5f + audioAmplitude * 10f).toDouble()).toFloat().coerceAtLeast(0f)) * 0.7f)
            val h = maxBarHeight * barAmp.coerceIn(0.25f, 1.0f)
            val bx = startX + (i * barWidth * 1.8f)

            drawRoundRect(
                color = mouthColor,
                topLeft = Offset(bx, mouthY - h / 2f),
                size = Size(barWidth, h),
                cornerRadius = CornerRadius(2f * scale)
            )
        }
    } else {
        // Idle gentle smile line
        drawArc(
            color = mouthColor,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(headCenter.x - 10f * scale, mouthY - 4f * scale),
            size = Size(20f * scale, 8f * scale),
            style = Stroke(width = 2.5f * scale, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawRobotAntenna(
    center: Offset,
    scale: Float,
    isSpeaking: Boolean
) {
    val headTop = center.y - 142f * scale

    // Antenna Stalk
    drawLine(
        color = Color(0xFF64748B),
        start = Offset(center.x, headTop),
        end = Offset(center.x, headTop - 20f * scale),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    // Beacon Light
    val beaconColor = if (isSpeaking) Color(0xFF00E5FF) else Color(0xFFFFB74D)
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White, beaconColor, Color.Transparent)),
        radius = 12f * scale,
        center = Offset(center.x, headTop - 20f * scale)
    )
    drawCircle(
        color = beaconColor,
        radius = 6f * scale,
        center = Offset(center.x, headTop - 20f * scale)
    )
}

private fun DrawScope.drawRobotArms(
    center: Offset,
    scale: Float,
    gesture: RobotGesture,
    isSpeaking: Boolean,
    audioAmplitude: Float
) {
    val bodyTop = center.y - 30f * scale
    val shoulderLeft = Offset(center.x - 55f * scale, bodyTop + 14f * scale)
    val shoulderRight = Offset(center.x + 55f * scale, bodyTop + 14f * scale)

    val armColor = Color(0xFFE2E8F0)
    val jointColor = Color(0xFF475569)
    val handColor = Color(0xFF00E5FF)

    when (gesture) {
        RobotGesture.POINT_RIGHT -> {
            // Right arm points dramatically up-right at the background hologram!
            val elbowR = Offset(center.x + 95f * scale, bodyTop - 10f * scale)
            val handR = Offset(center.x + 155f * scale, bodyTop - 45f * scale)

            drawLine(color = armColor, start = shoulderRight, end = elbowR, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowR)
            drawLine(color = armColor, start = elbowR, end = handR, strokeWidth = 8f * scale, cap = StrokeCap.Round)

            // Pointing finger tip with laser emission
            drawCircle(color = handColor, radius = 8f * scale, center = handR)
            drawCircle(color = Color.White, radius = 4f * scale, center = handR)

            // Left arm relaxed conversational pose
            val elbowL = Offset(center.x - 75f * scale, bodyTop + 50f * scale)
            val handL = Offset(center.x - 60f * scale, bodyTop + 85f * scale)
            drawLine(color = armColor, start = shoulderLeft, end = elbowL, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowL)
            drawLine(color = armColor, start = elbowL, end = handL, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 7f * scale, center = handL)
        }
        RobotGesture.POINT_LEFT -> {
            // Left arm points up-left at the background hologram!
            val elbowL = Offset(center.x - 95f * scale, bodyTop - 10f * scale)
            val handL = Offset(center.x - 155f * scale, bodyTop - 45f * scale)

            drawLine(color = armColor, start = shoulderLeft, end = elbowL, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowL)
            drawLine(color = armColor, start = elbowL, end = handL, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 8f * scale, center = handL)

            // Right arm conversational
            val elbowR = Offset(center.x + 75f * scale, bodyTop + 50f * scale)
            val handR = Offset(center.x + 60f * scale, bodyTop + 85f * scale)
            drawLine(color = armColor, start = shoulderRight, end = elbowR, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowR)
            drawLine(color = armColor, start = elbowR, end = handR, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 7f * scale, center = handR)
        }
        RobotGesture.WELCOME_OPEN, RobotGesture.EXCITED_BOTH -> {
            // Both arms wide open in warm welcoming or enthusiastic gesture
            val elbowL = Offset(center.x - 95f * scale, bodyTop + 20f * scale)
            val handL = Offset(center.x - 130f * scale, bodyTop - 10f * scale)

            val elbowR = Offset(center.x + 95f * scale, bodyTop + 20f * scale)
            val handR = Offset(center.x + 130f * scale, bodyTop - 10f * scale)

            drawLine(color = armColor, start = shoulderLeft, end = elbowL, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowL)
            drawLine(color = armColor, start = elbowL, end = handL, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 8f * scale, center = handL)

            drawLine(color = armColor, start = shoulderRight, end = elbowR, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowR)
            drawLine(color = armColor, start = elbowR, end = handR, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 8f * scale, center = handR)
        }
        RobotGesture.THINKING_CHIN -> {
            // Right hand tapping chin thoughtfully
            val elbowR = Offset(center.x + 65f * scale, bodyTop + 40f * scale)
            val handR = Offset(center.x + 25f * scale, center.y - 70f * scale)

            drawLine(color = armColor, start = shoulderRight, end = elbowR, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowR)
            drawLine(color = armColor, start = elbowR, end = handR, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 7f * scale, center = handR)

            // Left hand holding elbow
            val elbowL = Offset(center.x - 70f * scale, bodyTop + 50f * scale)
            val handL = Offset(center.x + 10f * scale, bodyTop + 55f * scale)
            drawLine(color = armColor, start = shoulderLeft, end = elbowL, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowL)
            drawLine(color = armColor, start = elbowL, end = handL, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 7f * scale, center = handL)
        }
        else -> { // EXPLAINING_HANDS
            // Hands gesturing forward in conversational cadence
            val handBob = if (isSpeaking) sin((audioAmplitude * 10f).toDouble()).toFloat() * 6f * scale else 0f

            val elbowL = Offset(center.x - 75f * scale, bodyTop + 40f * scale)
            val handL = Offset(center.x - 50f * scale, bodyTop + 45f * scale + handBob)

            val elbowR = Offset(center.x + 75f * scale, bodyTop + 40f * scale)
            val handR = Offset(center.x + 50f * scale, bodyTop + 45f * scale - handBob)

            drawLine(color = armColor, start = shoulderLeft, end = elbowL, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowL)
            drawLine(color = armColor, start = elbowL, end = handL, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 7f * scale, center = handL)

            drawLine(color = armColor, start = shoulderRight, end = elbowR, strokeWidth = 10f * scale, cap = StrokeCap.Round)
            drawCircle(color = jointColor, radius = 7f * scale, center = elbowR)
            drawLine(color = armColor, start = elbowR, end = handR, strokeWidth = 8f * scale, cap = StrokeCap.Round)
            drawCircle(color = handColor, radius = 8f * scale, center = handR)
        }
    }
}
