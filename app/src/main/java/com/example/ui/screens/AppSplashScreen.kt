package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.SpatialAudio
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HighDensityAccentLight
import com.example.ui.theme.HighDensityBorder
import com.example.ui.theme.HighDensityDarkContainer
import com.example.ui.theme.HighDensityOnPrimaryContainer
import com.example.ui.theme.HighDensityOnSurfaceVariant
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensityProgressTrack
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.HighDensitySurfaceVariant
import kotlinx.coroutines.delay

@Composable
fun AppSplashScreen(
    onTimeout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var loadingProgress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val steps = 20
        for (i in 1..steps) {
            delay(90)
            loadingProgress = i / steps.toFloat()
        }
        delay(200)
        onTimeout()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_scale")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("app_loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Main Branding Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            // Animated App Logo
            Surface(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                border = BorderStroke(1.5.dp, HighDensityPrimary.copy(alpha = 0.3f))
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.mink_study_logo_1787302592819),
                    contentDescription = "Mink Study Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Title
            Text(
                text = "MINK STUDY",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = HighDensityPrimary,
                fontSize = 30.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "AI STUDY HELPER",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = HighDensityOnSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            // High Density Progress Bar
            LinearProgressIndicator(
                progress = { loadingProgress },
                modifier = Modifier
                    .width(220.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = HighDensityPrimary,
                trackColor = HighDensityProgressTrack
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Initializing AI Voice Models...",
                style = MaterialTheme.typography.labelSmall,
                color = HighDensityOnSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }

        // Bottom "developed by ASM_CREATIONS" Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = HighDensitySurfaceVariant,
            border = BorderStroke(1.dp, HighDensityBorder),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .testTag("developed_by_asm_creations_badge")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = HighDensityPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "developed by ASM_CREATIONS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = HighDensityPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.testTag("developed_by_asm_creations_text")
                )
            }
        }
    }
}
