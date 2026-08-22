package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

enum class AppThemeMode(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val backgroundHex: Long
) {
    SPACE(
        id = "space",
        title = "Space Odyssey",
        subtitle = "Deep Cosmic Navy & Nebula Cyan",
        iconEmoji = "🚀",
        primaryColorHex = 0xFF00D2FF,
        secondaryColorHex = 0xFF8B5CF6,
        backgroundHex = 0xFF060A14
    ),
    NATURE(
        id = "nature",
        title = "Nature Haven",
        subtitle = "Emerald Forest & Mint Vitality",
        iconEmoji = "🌿",
        primaryColorHex = 0xFF10B981,
        secondaryColorHex = 0xFF34D399,
        backgroundHex = 0xFF05130D
    ),
    GHOST(
        id = "ghost",
        title = "Phantom Dark",
        subtitle = "Ethereal Shadow & Ghost Violet",
        iconEmoji = "👻",
        primaryColorHex = 0xFFA78BFA,
        secondaryColorHex = 0xFFC4B5FD,
        backgroundHex = 0xFF08060F
    ),
    CYBERPUNK(
        id = "cyberpunk",
        title = "Cyberpunk Neon",
        subtitle = "Electric Cyan & Laser Magenta",
        iconEmoji = "⚡",
        primaryColorHex = 0xFF06B6D4,
        secondaryColorHex = 0xFFEC4899,
        backgroundHex = 0xFF090410
    ),
    SOLAR(
        id = "solar",
        title = "Solar Flare",
        subtitle = "Warm Amber & Radiant Sun Gold",
        iconEmoji = "☀️",
        primaryColorHex = 0xFFF59E0B,
        secondaryColorHex = 0xFFFBBF24,
        backgroundHex = 0xFF0D0803
    );

    val displayName: String get() = title
}

val LocalAppThemeMode = compositionLocalOf { AppThemeMode.SPACE }

// Space Theme Color Palette
val SpacePrimary = Color(0xFF00D2FF)
val SpacePrimaryContainer = Color(0xFF0F3657)
val SpaceOnPrimaryContainer = Color(0xFFBAE6FD)
val SpaceSecondary = Color(0xFF8B5CF6)
val SpaceSecondaryContainer = Color(0xFF2E1C59)
val SpaceOnSecondaryContainer = Color(0xFFDDD6FE)
val SpaceTertiary = Color(0xFF38BDF8)
val SpaceBackground = Color(0xFF060A14)
val SpaceSurface = Color(0xFF0D1527)
val SpaceSurfaceVariant = Color(0xFF131F37)
val SpaceOnSurface = Color(0xFFF8FAFC)
val SpaceOnSurfaceVariant = Color(0xFF94A3B8)
val SpaceOutline = Color(0xFF1E293B)

val SpaceColorScheme = darkColorScheme(
    primary = SpacePrimary,
    onPrimary = Color(0xFF02131F),
    primaryContainer = SpacePrimaryContainer,
    onPrimaryContainer = SpaceOnPrimaryContainer,
    secondary = SpaceSecondary,
    onSecondary = Color.White,
    secondaryContainer = SpaceSecondaryContainer,
    onSecondaryContainer = SpaceOnSecondaryContainer,
    tertiary = SpaceTertiary,
    onTertiary = Color.Black,
    background = SpaceBackground,
    onBackground = SpaceOnSurface,
    surface = SpaceSurface,
    onSurface = SpaceOnSurface,
    surfaceVariant = SpaceSurfaceVariant,
    onSurfaceVariant = SpaceOnSurfaceVariant,
    outline = SpaceOutline
)

// Nature Theme Color Palette
val NaturePrimary = Color(0xFF10B981)
val NaturePrimaryContainer = Color(0xFF093924)
val NatureOnPrimaryContainer = Color(0xFFA7F3D0)
val NatureSecondary = Color(0xFF34D399)
val NatureSecondaryContainer = Color(0xFF0F4A32)
val NatureOnSecondaryContainer = Color(0xFFD1FAE5)
val NatureTertiary = Color(0xFFF59E0B)
val NatureBackground = Color(0xFF05130D)
val NatureSurface = Color(0xFF0B241A)
val NatureSurfaceVariant = Color(0xFF133627)
val NatureOnSurface = Color(0xFFF0FDF4)
val NatureOnSurfaceVariant = Color(0xFF86EFAC)
val NatureOutline = Color(0xFF1B4D39)

val NatureColorScheme = darkColorScheme(
    primary = NaturePrimary,
    onPrimary = Color(0xFF021E11),
    primaryContainer = NaturePrimaryContainer,
    onPrimaryContainer = NatureOnPrimaryContainer,
    secondary = NatureSecondary,
    onSecondary = Color(0xFF021E11),
    secondaryContainer = NatureSecondaryContainer,
    onSecondaryContainer = NatureOnSecondaryContainer,
    tertiary = NatureTertiary,
    onTertiary = Color.Black,
    background = NatureBackground,
    onBackground = NatureOnSurface,
    surface = NatureSurface,
    onSurface = NatureOnSurface,
    surfaceVariant = NatureSurfaceVariant,
    onSurfaceVariant = NatureOnSurfaceVariant,
    outline = NatureOutline
)

// Ghost / Phantom Dark Color Palette
val GhostPrimary = Color(0xFFA78BFA)
val GhostPrimaryContainer = Color(0xFF2C2052)
val GhostOnPrimaryContainer = Color(0xFFEDE9FE)
val GhostSecondary = Color(0xFFC4B5FD)
val GhostSecondaryContainer = Color(0xFF3B2E68)
val GhostOnSecondaryContainer = Color(0xFFF5F3FF)
val GhostTertiary = Color(0xFFE2E8F0)
val GhostBackground = Color(0xFF08060F)
val GhostSurface = Color(0xFF120E22)
val GhostSurfaceVariant = Color(0xFF1C1733)
val GhostOnSurface = Color(0xFFF5F3FF)
val GhostOnSurfaceVariant = Color(0xFFCBD5E1)
val GhostOutline = Color(0xFF2D254D)

val GhostColorScheme = darkColorScheme(
    primary = GhostPrimary,
    onPrimary = Color(0xFF160E2E),
    primaryContainer = GhostPrimaryContainer,
    onPrimaryContainer = GhostOnPrimaryContainer,
    secondary = GhostSecondary,
    onSecondary = Color(0xFF160E2E),
    secondaryContainer = GhostSecondaryContainer,
    onSecondaryContainer = GhostOnSecondaryContainer,
    tertiary = GhostTertiary,
    onTertiary = Color.Black,
    background = GhostBackground,
    onBackground = GhostOnSurface,
    surface = GhostSurface,
    onSurface = GhostOnSurface,
    surfaceVariant = GhostSurfaceVariant,
    onSurfaceVariant = GhostOnSurfaceVariant,
    outline = GhostOutline
)

// Cyberpunk Neon Color Palette
val CyberPrimary = Color(0xFF06B6D4)
val CyberPrimaryContainer = Color(0xFF113848)
val CyberOnPrimaryContainer = Color(0xFFCFFAFE)
val CyberSecondary = Color(0xFFEC4899)
val CyberSecondaryContainer = Color(0xFF4A1033)
val CyberOnSecondaryContainer = Color(0xFFFCE7F3)
val CyberTertiary = Color(0xFFFACC15)
val CyberBackground = Color(0xFF090410)
val CyberSurface = Color(0xFF170B2A)
val CyberSurfaceVariant = Color(0xFF251242)
val CyberOnSurface = Color(0xFFFDF4FF)
val CyberOnSurfaceVariant = Color(0xFFE879F9)
val CyberOutline = Color(0xFF3E1D68)

val CyberpunkColorScheme = darkColorScheme(
    primary = CyberPrimary,
    onPrimary = Color.Black,
    primaryContainer = CyberPrimaryContainer,
    onPrimaryContainer = CyberOnPrimaryContainer,
    secondary = CyberSecondary,
    onSecondary = Color.White,
    secondaryContainer = CyberSecondaryContainer,
    onSecondaryContainer = CyberOnSecondaryContainer,
    tertiary = CyberTertiary,
    onTertiary = Color.Black,
    background = CyberBackground,
    onBackground = CyberOnSurface,
    surface = CyberSurface,
    onSurface = CyberOnSurface,
    surfaceVariant = CyberSurfaceVariant,
    onSurfaceVariant = CyberOnSurfaceVariant,
    outline = CyberOutline
)

// Solar Flare Color Palette
val SolarPrimary = Color(0xFFF59E0B)
val SolarPrimaryContainer = Color(0xFF4D2E04)
val SolarOnPrimaryContainer = Color(0xFFFEF3C7)
val SolarSecondary = Color(0xFFFBBF24)
val SolarSecondaryContainer = Color(0xFF5E3A06)
val SolarOnSecondaryContainer = Color(0xFFFFFBEB)
val SolarTertiary = Color(0xFFEF4444)
val SolarBackground = Color(0xFF0D0803)
val SolarSurface = Color(0xFF1C1206)
val SolarSurfaceVariant = Color(0xFF2E1D0A)
val SolarOnSurface = Color(0xFFFFFBEB)
val SolarOnSurfaceVariant = Color(0xFFFCD34D)
val SolarOutline = Color(0xFF4A3112)

val SolarColorScheme = darkColorScheme(
    primary = SolarPrimary,
    onPrimary = Color(0xFF261601),
    primaryContainer = SolarPrimaryContainer,
    onPrimaryContainer = SolarOnPrimaryContainer,
    secondary = SolarSecondary,
    onSecondary = Color(0xFF261601),
    secondaryContainer = SolarSecondaryContainer,
    onSecondaryContainer = SolarOnSecondaryContainer,
    tertiary = SolarTertiary,
    onTertiary = Color.White,
    background = SolarBackground,
    onBackground = SolarOnSurface,
    surface = SolarSurface,
    onSurface = SolarOnSurface,
    surfaceVariant = SolarSurfaceVariant,
    onSurfaceVariant = SolarOnSurfaceVariant,
    outline = SolarOutline
)

fun getThemeColorScheme(themeMode: AppThemeMode): ColorScheme {
    return when (themeMode) {
        AppThemeMode.SPACE -> SpaceColorScheme
        AppThemeMode.NATURE -> NatureColorScheme
        AppThemeMode.GHOST -> GhostColorScheme
        AppThemeMode.CYBERPUNK -> CyberpunkColorScheme
        AppThemeMode.SOLAR -> SolarColorScheme
    }
}

fun getHeroBrush(themeMode: AppThemeMode): Brush {
    return when (themeMode) {
        AppThemeMode.SPACE -> Brush.linearGradient(
            colors = listOf(Color(0xFF0B192C), Color(0xFF1E3E62), Color(0xFF000000))
        )
        AppThemeMode.NATURE -> Brush.linearGradient(
            colors = listOf(Color(0xFF0A2E1F), Color(0xFF155E3F), Color(0xFF04130D))
        )
        AppThemeMode.GHOST -> Brush.linearGradient(
            colors = listOf(Color(0xFF1A1333), Color(0xFF332463), Color(0xFF08060F))
        )
        AppThemeMode.CYBERPUNK -> Brush.linearGradient(
            colors = listOf(Color(0xFF1F0C3B), Color(0xFF4A1054), Color(0xFF090410))
        )
        AppThemeMode.SOLAR -> Brush.linearGradient(
            colors = listOf(Color(0xFF2D1805), Color(0xFF573208), Color(0xFF0D0803))
        )
    }
}

fun getAccentGlowBrush(themeMode: AppThemeMode): Brush {
    return when (themeMode) {
        AppThemeMode.SPACE -> Brush.horizontalGradient(
            colors = listOf(SpacePrimary, SpaceSecondary)
        )
        AppThemeMode.NATURE -> Brush.horizontalGradient(
            colors = listOf(NaturePrimary, NatureSecondary)
        )
        AppThemeMode.GHOST -> Brush.horizontalGradient(
            colors = listOf(GhostPrimary, GhostSecondary)
        )
        AppThemeMode.CYBERPUNK -> Brush.horizontalGradient(
            colors = listOf(CyberPrimary, CyberSecondary)
        )
        AppThemeMode.SOLAR -> Brush.horizontalGradient(
            colors = listOf(SolarPrimary, SolarSecondary)
        )
    }
}
