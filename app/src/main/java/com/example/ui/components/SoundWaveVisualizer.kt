package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AudioWaveActive
import com.example.ui.theme.IndigoPrimaryDark
import com.example.ui.theme.TealSecondaryDark

@Composable
fun SoundWaveVisualizer(
    isSpeaking: Boolean,
    amplitudes: List<Float> = emptyList(),
    barCount: Int = 16,
    maxHeight: Dp = 36.dp,
    minHeight: Dp = 6.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "soundwave")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight + 8.dp)
            .padding(horizontal = 8.dp)
            .testTag("sound_wave_visualizer"),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val count = if (amplitudes.isNotEmpty()) amplitudes.size else barCount

        for (i in 0 until count) {
            val animFraction by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = if (isSpeaking) 1.0f else 0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (i * 45) % 400,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            val currentAmp = if (isSpeaking && i < amplitudes.size) {
                amplitudes[i]
            } else if (isSpeaking) {
                animFraction
            } else {
                0.15f
            }

            val barHeight = minHeight + (maxHeight - minHeight) * currentAmp

            val brush = if (isSpeaking) {
                Brush.verticalGradient(
                    colors = listOf(
                        IndigoPrimaryDark,
                        AudioWaveActive,
                        TealSecondaryDark
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(3.dp))
                    .background(brush)
            )
        }
    }
}
