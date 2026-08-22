package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HologramVisualType
import com.example.data.model.RobotSpeechCue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-Tech Holographic Visual Projection Canvas
 * Renders rich, interactive, synchronized 3D-styled animated models and diagrams
 * behind or beside the robot presenter at the exact moment a concept is spoken.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HologramCanvasVisualizer(
    activeCue: RobotSpeechCue,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hologram_rotations")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heart_pulse"
    )

    val hologramFlicker by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F172A).copy(alpha = 0.85f),
                        Color(0xFF060913).copy(alpha = 0.95f),
                        Color.Black
                    )
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.7f),
                        Color(0xFF818CF8).copy(alpha = 0.5f),
                        Color(0xFF00E5FF).copy(alpha = 0.2f)
                    )
                ),
                RoundedCornerShape(24.dp)
            )
            .testTag("hologram_canvas_container")
    ) {
        // Futuristic Hologram Grid Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSpacing = 36.dp.toPx()
            val gridColor = Color(0xFF00E5FF).copy(alpha = 0.07f * hologramFlicker)

            var x = 0f
            while (x < size.width) {
                drawLine(
                    color = gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx()
                )
                x += gridSpacing
            }

            var y = 0f
            while (y < size.height) {
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
                y += gridSpacing
            }

            // Radial scanning concentric circles
            val center = Offset(size.width * 0.5f, size.height * 0.45f)
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.12f),
                radius = size.width * 0.38f,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF818CF8).copy(alpha = 0.08f),
                radius = size.width * 0.48f,
                center = center,
                style = Stroke(width = 1.dp.toPx())
            )
        }

        // Active Animated Holographic Model
        AnimatedContent(
            targetState = activeCue.visualType,
            transitionSpec = { fadeIn(tween(400)) togetherWith fadeOut(tween(400)) },
            label = "hologram_model_anim",
            modifier = Modifier.fillMaxSize()
        ) { visualType ->
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp)
            ) {
                val center = Offset(size.width * 0.5f, size.height * 0.44f)

                when (visualType) {
                    HologramVisualType.BONES_SKELETON -> {
                        drawSkeletalHologram(center, size, rotationAngle, pulseScale)
                    }
                    HologramVisualType.HEART_CARDIO -> {
                        drawCardiacHeartHologram(center, size, pulseScale)
                    }
                    HologramVisualType.BRAIN_NEURAL -> {
                        drawNeuralBrainHologram(center, size, rotationAngle, pulseScale)
                    }
                    HologramVisualType.SOLAR_SYSTEM -> {
                        drawSolarSystemHologram(center, size, rotationAngle)
                    }
                    HologramVisualType.ATOM_MOLECULE -> {
                        drawAtomicOrbitalHologram(center, size, rotationAngle)
                    }
                    HologramVisualType.DNA_HELIX -> {
                        drawDnaDoubleHelixHologram(center, size, rotationAngle)
                    }
                    else -> {
                        drawConceptDiagramHologram(center, size, rotationAngle)
                    }
                }
            }
        }

        // Holographic Header Tag
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E5FF))
                    )
                    Text(
                        text = "LIVE 3D HOLOGRAM • ${activeCue.visualType.label.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Keyword Trigger Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFB74D).copy(alpha = 0.18f),
                border = BorderStroke(1.dp, Color(0xFFFFB74D).copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Spoken: \"${activeCue.keyword}\"",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFB74D)
                    )
                }
            }
        }

        // Bottom Concept Card & Labels Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.98f)
                        )
                    )
                )
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = activeCue.visualTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                fontSize = 15.sp
            )

            Text(
                text = activeCue.visualSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF00E5FF),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (activeCue.visualLabels.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    activeCue.visualLabels.forEach { label ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "• $label",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            if (activeCue.highlightFact.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = activeCue.highlightFact,
                            fontSize = 10.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3D Canvas Visualizer Draw Implementations
// -------------------------------------------------------------

private fun DrawScope.drawSkeletalHologram(
    center: Offset,
    size: Size,
    rotation: Float,
    pulse: Float
) {
    val boneColor = Color(0xFFE0F2FE)
    val glowColor = Color(0xFF00E5FF)
    val jointColor = Color(0xFFFFB74D)

    val scale = (size.height * 0.0022f) * pulse

    // Cranium / Skull
    val skullY = center.y - (120f * scale)
    drawCircle(
        color = boneColor,
        radius = 28f * scale,
        center = Offset(center.x, skullY),
        style = Stroke(width = 3.5f * scale)
    )
    // Eye sockets
    drawCircle(color = glowColor, radius = 5f * scale, center = Offset(center.x - 10f * scale, skullY - 2f * scale))
    drawCircle(color = glowColor, radius = 5f * scale, center = Offset(center.x + 10f * scale, skullY - 2f * scale))

    // Jawbone
    drawLine(
        color = boneColor,
        start = Offset(center.x - 14f * scale, skullY + 18f * scale),
        end = Offset(center.x + 14f * scale, skullY + 18f * scale),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    // Cervical & Thoracic Spine
    val spineTop = skullY + 28f * scale
    val spineBottom = center.y + (50f * scale)
    drawLine(
        color = glowColor,
        start = Offset(center.x, spineTop),
        end = Offset(center.x, spineBottom),
        strokeWidth = 4f * scale,
        cap = StrokeCap.Round
    )

    // Vertebrae Beads
    for (i in 0..6) {
        val yPos = spineTop + (i * 18f * scale)
        drawCircle(
            color = boneColor,
            radius = 3.5f * scale,
            center = Offset(center.x, yPos)
        )
    }

    // Clavicle & Shoulders
    val shoulderY = spineTop + 14f * scale
    drawLine(
        color = boneColor,
        start = Offset(center.x - 55f * scale, shoulderY),
        end = Offset(center.x + 55f * scale, shoulderY),
        strokeWidth = 4f * scale,
        cap = StrokeCap.Round
    )
    drawCircle(color = jointColor, radius = 5f * scale, center = Offset(center.x - 55f * scale, shoulderY))
    drawCircle(color = jointColor, radius = 5f * scale, center = Offset(center.x + 55f * scale, shoulderY))

    // Ribcage Arcs
    for (i in 1..4) {
        val ry = shoulderY + (i * 15f * scale)
        val rx = (25f + i * 8f) * scale
        drawArc(
            color = boneColor.copy(alpha = 0.85f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - rx, ry - 10f * scale),
            size = Size(rx * 2f, 20f * scale),
            style = Stroke(width = 2.5f * scale, cap = StrokeCap.Round)
        )
    }

    // Pelvis Basin
    val pelvisY = spineBottom
    val pelvisPath = Path().apply {
        moveTo(center.x - 45f * scale, pelvisY)
        quadraticTo(center.x, pelvisY + 25f * scale, center.x + 45f * scale, pelvisY)
        lineTo(center.x + 35f * scale, pelvisY + 20f * scale)
        quadraticTo(center.x, pelvisY + 35f * scale, center.x - 35f * scale, pelvisY + 20f * scale)
        close()
    }
    drawPath(pelvisPath, color = boneColor, style = Stroke(width = 3f * scale))

    // Arms: Humerus, Radius/Ulna
    // Left Arm
    drawLine(
        color = boneColor,
        start = Offset(center.x - 55f * scale, shoulderY),
        end = Offset(center.x - 75f * scale, shoulderY + 50f * scale),
        strokeWidth = 3.5f * scale,
        cap = StrokeCap.Round
    )
    drawCircle(color = jointColor, radius = 4f * scale, center = Offset(center.x - 75f * scale, shoulderY + 50f * scale))
    drawLine(
        color = boneColor,
        start = Offset(center.x - 75f * scale, shoulderY + 50f * scale),
        end = Offset(center.x - 90f * scale, shoulderY + 100f * scale),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    // Right Arm
    drawLine(
        color = boneColor,
        start = Offset(center.x + 55f * scale, shoulderY),
        end = Offset(center.x + 75f * scale, shoulderY + 50f * scale),
        strokeWidth = 3.5f * scale,
        cap = StrokeCap.Round
    )
    drawCircle(color = jointColor, radius = 4f * scale, center = Offset(center.x + 75f * scale, shoulderY + 50f * scale))
    drawLine(
        color = boneColor,
        start = Offset(center.x + 75f * scale, shoulderY + 50f * scale),
        end = Offset(center.x + 90f * scale, shoulderY + 100f * scale),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    // Legs: Femur (Longest Bone Highlight), Tibia/Fibula
    val hipLeft = Offset(center.x - 30f * scale, pelvisY + 18f * scale)
    val hipRight = Offset(center.x + 30f * scale, pelvisY + 18f * scale)
    val kneeLeft = Offset(center.x - 35f * scale, pelvisY + 95f * scale)
    val kneeRight = Offset(center.x + 35f * scale, pelvisY + 95f * scale)
    val ankleLeft = Offset(center.x - 38f * scale, pelvisY + 160f * scale)
    val ankleRight = Offset(center.x + 38f * scale, pelvisY + 160f * scale)

    // Highlight Femur in Golden Amber
    drawLine(color = Color(0xFFFFD54F), start = hipLeft, end = kneeLeft, strokeWidth = 5f * scale, cap = StrokeCap.Round)
    drawLine(color = Color(0xFFFFD54F), start = hipRight, end = kneeRight, strokeWidth = 5f * scale, cap = StrokeCap.Round)
    drawCircle(color = jointColor, radius = 6f * scale, center = kneeLeft)
    drawCircle(color = jointColor, radius = 6f * scale, center = kneeRight)

    // Lower leg
    drawLine(color = boneColor, start = kneeLeft, end = ankleLeft, strokeWidth = 3.5f * scale, cap = StrokeCap.Round)
    drawLine(color = boneColor, start = kneeRight, end = ankleRight, strokeWidth = 3.5f * scale, cap = StrokeCap.Round)

    // 206 Bones Hologram Badge Floating beside skeleton
    drawRoundRect(
        color = Color(0xFF00E5FF).copy(alpha = 0.2f),
        topLeft = Offset(center.x + 85f * scale, center.y - 80f * scale),
        size = Size(80f * scale, 36f * scale),
        cornerRadius = CornerRadius(8f * scale),
        style = Fill
    )
    drawRoundRect(
        color = Color(0xFF00E5FF),
        topLeft = Offset(center.x + 85f * scale, center.y - 80f * scale),
        size = Size(80f * scale, 36f * scale),
        cornerRadius = CornerRadius(8f * scale),
        style = Stroke(width = 1.5f * scale)
    )
}

private fun DrawScope.drawCardiacHeartHologram(
    center: Offset,
    size: Size,
    pulse: Float
) {
    val scale = (size.height * 0.0028f) * pulse

    // Aorta & Pulmonary Artery Pipes at the Top
    // Aorta Arch (Red Oxygenated)
    val aortaPath = Path().apply {
        moveTo(center.x - 10f * scale, center.y - 40f * scale)
        cubicTo(
            center.x - 10f * scale, center.y - 110f * scale,
            center.x + 50f * scale, center.y - 110f * scale,
            center.x + 50f * scale, center.y - 50f * scale
        )
    }
    drawPath(aortaPath, color = Color(0xFFEF4444), style = Stroke(width = 18f * scale, cap = StrokeCap.Round))

    // Pulmonary Artery (Blue Deoxygenated)
    val pulmonaryPath = Path().apply {
        moveTo(center.x + 10f * scale, center.y - 30f * scale)
        cubicTo(
            center.x + 10f * scale, center.y - 90f * scale,
            center.x - 50f * scale, center.y - 80f * scale,
            center.x - 60f * scale, center.y - 40f * scale
        )
    }
    drawPath(pulmonaryPath, color = Color(0xFF38BDF8), style = Stroke(width = 14f * scale, cap = StrokeCap.Round))

    // 3D Anatomical Heart Body (4 Chambers)
    // Left Ventricle & Atrium (Oxygenated - Rich Crimson Red)
    val leftHeart = Path().apply {
        moveTo(center.x, center.y - 30f * scale)
        cubicTo(
            center.x + 75f * scale, center.y - 45f * scale,
            center.x + 85f * scale, center.y + 35f * scale,
            center.x, center.y + 90f * scale
        )
        close()
    }
    drawPath(
        leftHeart,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF5252), Color(0xFFB71C1C), Color(0xFF880E4F)),
            center = Offset(center.x + 30f * scale, center.y + 20f * scale),
            radius = 70f * scale
        )
    )
    drawPath(leftHeart, color = Color(0xFFFF8A80), style = Stroke(width = 2.5f * scale))

    // Right Ventricle & Atrium (Deoxygenated - Rich Blue / Cyan)
    val rightHeart = Path().apply {
        moveTo(center.x, center.y - 30f * scale)
        cubicTo(
            center.x - 75f * scale, center.y - 45f * scale,
            center.x - 85f * scale, center.y + 35f * scale,
            center.x, center.y + 90f * scale
        )
        close()
    }
    drawPath(
        rightHeart,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00E5FF), Color(0xFF0288D1), Color(0xFF0D47A1)),
            center = Offset(center.x - 30f * scale, center.y + 20f * scale),
            radius = 70f * scale
        )
    )
    drawPath(rightHeart, color = Color(0xFF80D8FF), style = Stroke(width = 2.5f * scale))

    // Interventricular Septum (Center dividing line)
    drawLine(
        color = Color(0xFFFDE047),
        start = Offset(center.x, center.y - 25f * scale),
        end = Offset(center.x, center.y + 85f * scale),
        strokeWidth = 3f * scale,
        cap = StrokeCap.Round
    )

    // Coronary Artery Branches
    val coronaryPath = Path().apply {
        moveTo(center.x, center.y)
        quadraticTo(center.x + 25f * scale, center.y + 20f * scale, center.x + 40f * scale, center.y + 50f * scale)
        moveTo(center.x, center.y + 20f * scale)
        quadraticTo(center.x - 20f * scale, center.y + 40f * scale, center.x - 35f * scale, center.y + 65f * scale)
    }
    drawPath(coronaryPath, color = Color(0xFFFDE047), style = Stroke(width = 2.5f * scale, cap = StrokeCap.Round))

    // Pulse Waveform & BPM Stat Overlay
    val ecgY = center.y + 115f * scale
    val ecgPath = Path().apply {
        moveTo(center.x - 100f * scale, ecgY)
        lineTo(center.x - 30f * scale, ecgY)
        lineTo(center.x - 20f * scale, ecgY - 8f * scale)
        lineTo(center.x - 10f * scale, ecgY + 8f * scale)
        lineTo(center.x, ecgY - 24f * scale) // QRS peak
        lineTo(center.x + 10f * scale, ecgY + 16f * scale)
        lineTo(center.x + 20f * scale, ecgY)
        lineTo(center.x + 35f * scale, ecgY - 6f * scale)
        lineTo(center.x + 50f * scale, ecgY)
        lineTo(center.x + 100f * scale, ecgY)
    }
    drawPath(ecgPath, color = Color(0xFF00E5FF), style = Stroke(width = 2.5f * scale, cap = StrokeCap.Round))
}

private fun DrawScope.drawNeuralBrainHologram(
    center: Offset,
    size: Size,
    rotation: Float,
    pulse: Float
) {
    val scale = (size.height * 0.0026f) * pulse
    val brainPink = Color(0xFFFF80AB)
    val brainNeon = Color(0xFF00E5FF)
    val synapseGold = Color(0xFFFFD54F)

    // Left & Right Cerebral Hemispheres
    // Left Hemisphere (Analytical, Blue glow)
    val leftHemi = Path().apply {
        moveTo(center.x - 4f * scale, center.y - 70f * scale)
        cubicTo(
            center.x - 85f * scale, center.y - 75f * scale,
            center.x - 95f * scale, center.y + 40f * scale,
            center.x - 45f * scale, center.y + 65f * scale
        )
        cubicTo(
            center.x - 25f * scale, center.y + 75f * scale,
            center.x - 10f * scale, center.y + 60f * scale,
            center.x - 4f * scale, center.y + 50f * scale
        )
        close()
    }
    drawPath(
        leftHemi,
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7), Color(0xFF0C4A6E)),
            center = Offset(center.x - 40f * scale, center.y),
            radius = 70f * scale
        )
    )
    drawPath(leftHemi, color = brainNeon, style = Stroke(width = 2.5f * scale))

    // Right Hemisphere (Creative, Pink/Purple glow)
    val rightHemi = Path().apply {
        moveTo(center.x + 4f * scale, center.y - 70f * scale)
        cubicTo(
            center.x + 85f * scale, center.y - 75f * scale,
            center.x + 95f * scale, center.y + 40f * scale,
            center.x + 45f * scale, center.y + 65f * scale
        )
        cubicTo(
            center.x + 25f * scale, center.y + 75f * scale,
            center.x + 10f * scale, center.y + 60f * scale,
            center.x + 4f * scale, center.y + 50f * scale
        )
        close()
    }
    drawPath(
        rightHemi,
        brush = Brush.radialGradient(
            colors = listOf(brainPink, Color(0xFFC026D3), Color(0xFF581C87)),
            center = Offset(center.x + 40f * scale, center.y),
            radius = 70f * scale
        )
    )
    drawPath(rightHemi, color = brainPink, style = Stroke(width = 2.5f * scale))

    // Cortical Sulci & Gyri Wrinkle Folds
    for (i in 0..4) {
        val yOffset = (i * 22f - 40f) * scale
        // Left gyri
        drawArc(
            color = brainNeon.copy(alpha = 0.8f),
            startAngle = 140f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(center.x - 70f * scale, center.y + yOffset - 15f * scale),
            size = Size(50f * scale, 30f * scale),
            style = Stroke(width = 2f * scale, cap = StrokeCap.Round)
        )
        // Right gyri
        drawArc(
            color = brainPink.copy(alpha = 0.8f),
            startAngle = -60f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(center.x + 20f * scale, center.y + yOffset - 15f * scale),
            size = Size(50f * scale, 30f * scale),
            style = Stroke(width = 2f * scale, cap = StrokeCap.Round)
        )
    }

    // Firing Synaptic Sparks (Neural network nodes)
    val nodes = listOf(
        Offset(center.x - 45f * scale, center.y - 30f * scale),
        Offset(center.x - 60f * scale, center.y + 15f * scale),
        Offset(center.x - 25f * scale, center.y + 40f * scale),
        Offset(center.x + 45f * scale, center.y - 30f * scale),
        Offset(center.x + 60f * scale, center.y + 15f * scale),
        Offset(center.x + 25f * scale, center.y + 40f * scale),
        Offset(center.x, center.y - 50f * scale)
    )

    // Connect synaptic sparks
    for (i in 0 until nodes.size - 1) {
        drawLine(
            color = synapseGold.copy(alpha = 0.75f),
            start = nodes[i],
            end = nodes[i + 1],
            strokeWidth = 1.5f * scale
        )
    }

    nodes.forEach { pt ->
        drawCircle(color = synapseGold, radius = 4.5f * scale, center = pt)
        drawCircle(color = Color.White, radius = 2f * scale, center = pt)
    }

    // Brainstem & Spinal cord base
    val brainstem = Path().apply {
        moveTo(center.x - 14f * scale, center.y + 50f * scale)
        lineTo(center.x - 10f * scale, center.y + 95f * scale)
        lineTo(center.x + 10f * scale, center.y + 95f * scale)
        lineTo(center.x + 14f * scale, center.y + 50f * scale)
        close()
    }
    drawPath(brainstem, color = Color(0xFFA855F7).copy(alpha = 0.8f), style = Stroke(width = 2.5f * scale))
}

private fun DrawScope.drawSolarSystemHologram(
    center: Offset,
    size: Size,
    rotation: Float
) {
    val scale = size.height * 0.0022f

    // Central Sun with Solar Flares
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, Color(0xFFFFD54F), Color(0xFFFF6D00), Color.Transparent),
            center = center,
            radius = 35f * scale
        ),
        radius = 35f * scale,
        center = center
    )

    // Orbital Ellipses and Orbiting Planets
    val planetOrbits = listOf(
        Triple(50f * scale, 3f * scale, Color(0xFFB0BEC5)), // Mercury
        Triple(75f * scale, 5.5f * scale, Color(0xFFFFCC80)), // Venus
        Triple(105f * scale, 6.5f * scale, Color(0xFF00E5FF)), // Earth
        Triple(135f * scale, 5f * scale, Color(0xFFFF5252)), // Mars
        Triple(175f * scale, 12f * scale, Color(0xFFFFB74D)), // Jupiter
        Triple(215f * scale, 10f * scale, Color(0xFFFFE082))  // Saturn
    )

    planetOrbits.forEachIndexed { idx, (radius, pSize, color) ->
        // Draw Orbit Path
        drawOval(
            color = Color.White.copy(alpha = 0.2f),
            topLeft = Offset(center.x - radius, center.y - radius * 0.45f),
            size = Size(radius * 2f, radius * 0.9f),
            style = Stroke(width = 1.2f * scale)
        )

        // Calculate planet position along ellipse with speed variation
        val speedMultiplier = 1f + (idx * 0.4f)
        val angleRad = ((rotation * speedMultiplier + (idx * 60f)) * PI / 180f).toFloat()
        val px = center.x + (radius * cos(angleRad))
        val py = center.y + (radius * 0.45f * sin(angleRad))

        // Draw Planet
        drawCircle(color = color, radius = pSize, center = Offset(px, py))

        // Saturn Rings
        if (idx == 5) {
            drawOval(
                color = Color(0xFFFFD54F).copy(alpha = 0.8f),
                topLeft = Offset(px - 18f * scale, py - 6f * scale),
                size = Size(36f * scale, 12f * scale),
                style = Stroke(width = 2f * scale)
            )
        }

        // Earth Moon
        if (idx == 2) {
            val moonRad = (rotation * 4f * PI / 180f).toFloat()
            val mx = px + (12f * scale * cos(moonRad))
            val my = py + (6f * scale * sin(moonRad))
            drawCircle(color = Color.White, radius = 2f * scale, center = Offset(mx, my))
        }
    }
}

private fun DrawScope.drawAtomicOrbitalHologram(
    center: Offset,
    size: Size,
    rotation: Float
) {
    val scale = size.height * 0.0024f

    // Nucleus with Protons (+) and Neutrons (0)
    val nucleusRadius = 22f * scale
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.White, Color(0xFFFF3D00), Color(0xFFD50000)),
            center = center,
            radius = nucleusRadius
        ),
        radius = nucleusRadius,
        center = center
    )

    // Nucleus beads
    val protonPositions = listOf(
        Offset(center.x - 6f * scale, center.y - 6f * scale),
        Offset(center.x + 6f * scale, center.y - 4f * scale),
        Offset(center.x - 4f * scale, center.y + 6f * scale),
        Offset(center.x + 7f * scale, center.y + 5f * scale)
    )
    protonPositions.forEachIndexed { i, pos ->
        val color = if (i % 2 == 0) Color(0xFFFFD54F) else Color(0xFF00E5FF)
        drawCircle(color = color, radius = 5f * scale, center = pos)
    }

    // 3 Tilted Orbitals (Bohr / Quantum Shells)
    val orbitalAngles = listOf(0f, 60f, 120f)
    val orbitalRadiusX = 130f * scale
    val orbitalRadiusY = 45f * scale

    orbitalAngles.forEachIndexed { idx, baseAngle ->
        rotate(baseAngle + (rotation * 0.2f), pivot = center) {
            // Orbital Ring
            drawOval(
                color = Color(0xFF00E5FF).copy(alpha = 0.5f),
                topLeft = Offset(center.x - orbitalRadiusX, center.y - orbitalRadiusY),
                size = Size(orbitalRadiusX * 2f, orbitalRadiusY * 2f),
                style = Stroke(width = 2f * scale)
            )

            // Orbiting Fast Electron
            val electronAngle = ((rotation * 2.5f + (idx * 90f)) * PI / 180f).toFloat()
            val ex = center.x + (orbitalRadiusX * cos(electronAngle))
            val ey = center.y + (orbitalRadiusY * sin(electronAngle))

            // Electron Glow Trail
            drawCircle(
                color = Color(0xFF00E5FF).copy(alpha = 0.35f),
                radius = 12f * scale,
                center = Offset(ex, ey)
            )
            drawCircle(
                color = Color.White,
                radius = 5.5f * scale,
                center = Offset(ex, ey)
            )
        }
    }
}

private fun DrawScope.drawDnaDoubleHelixHologram(
    center: Offset,
    size: Size,
    rotation: Float
) {
    val scale = size.height * 0.0025f
    val rungs = 16
    val heightStep = 14f * scale
    val startY = center.y - (rungs * heightStep / 2f)

    for (i in 0 until rungs) {
        val y = startY + (i * heightStep)
        val phase = ((i * 24f + rotation * 2f) * PI / 180f).toFloat()
        val xOffset = 55f * scale * cos(phase)

        val leftStrand = Offset(center.x - xOffset, y)
        val rightStrand = Offset(center.x + xOffset, y)

        // Color coding base pairs: Adenine (Red) - Thymine (Yellow), Guanine (Green) - Cytosine (Blue)
        val (color1, color2) = when (i % 4) {
            0 -> Pair(Color(0xFFEF4444), Color(0xFFFFD54F)) // A - T
            1 -> Pair(Color(0xFF10B981), Color(0xFF38BDF8)) // G - C
            2 -> Pair(Color(0xFFFFD54F), Color(0xFFEF4444)) // T - A
            else -> Pair(Color(0xFF38BDF8), Color(0xFF10B981)) // C - G
        }

        // Connecting Hydrogen Bond Ladder Rung
        val mid = Offset(center.x, y)
        drawLine(color = color1, start = leftStrand, end = mid, strokeWidth = 3f * scale, cap = StrokeCap.Round)
        drawLine(color = color2, start = mid, end = rightStrand, strokeWidth = 3f * scale, cap = StrokeCap.Round)
        drawCircle(color = Color.White, radius = 2.5f * scale, center = mid)

        // Outer Sugar-Phosphate Nodes
        drawCircle(color = Color(0xFF00E5FF), radius = 6f * scale, center = leftStrand)
        drawCircle(color = Color(0xFFA855F7), radius = 6f * scale, center = rightStrand)
    }
}

private fun DrawScope.drawConceptDiagramHologram(
    center: Offset,
    size: Size,
    rotation: Float
) {
    val scale = size.height * 0.0022f

    // Central Core Hub
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF00E5FF), Color(0xFF4F46E5), Color.Transparent),
            center = center,
            radius = 45f * scale
        ),
        radius = 45f * scale,
        center = center
    )
    drawCircle(color = Color.White, radius = 8f * scale, center = center)

    // 5 Radiating Concept Nodes
    val numNodes = 5
    for (i in 0 until numNodes) {
        val angle = ((i * (360f / numNodes) + rotation * 0.5f) * PI / 180f).toFloat()
        val dist = 110f * scale
        val nx = center.x + (dist * cos(angle))
        val ny = center.y + (dist * sin(angle))
        val nodePos = Offset(nx, ny)

        // Connector Vector
        drawLine(
            color = Color(0xFF00E5FF).copy(alpha = 0.6f),
            start = center,
            end = nodePos,
            strokeWidth = 2f * scale
        )

        // Outer Node Card
        drawCircle(color = Color(0xFF1E1B4B), radius = 22f * scale, center = nodePos)
        drawCircle(color = Color(0xFF00E5FF), radius = 22f * scale, center = nodePos, style = Stroke(width = 2f * scale))
        drawCircle(color = Color(0xFFFFD54F), radius = 6f * scale, center = nodePos)
    }
}
