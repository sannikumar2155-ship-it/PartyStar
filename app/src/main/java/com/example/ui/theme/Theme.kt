package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PartyColorScheme = darkColorScheme(
    primary = NeonPink,
    onPrimary = TextPrimary,
    primaryContainer = NeonPurple,
    onPrimaryContainer = TextPrimary,
    secondary = NeonCyan,
    onSecondary = PartyBg,
    secondaryContainer = PartySurfaceVariant,
    onSecondaryContainer = TextSecondary,
    tertiary = NeonGold,
    onTertiary = PartyBg,
    background = PartyBg,
    onBackground = TextPrimary,
    surface = PartySurface,
    onSurface = TextPrimary,
    surfaceVariant = PartySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = DangerRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PartyColorScheme,
        typography = Typography,
        content = content
    )
}
