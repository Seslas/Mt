package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MtDarkColorScheme = darkColorScheme(
    primary = MtGold,
    onPrimary = Color(0xFF1B1200),
    primaryContainer = MtGoldContainer,
    onPrimaryContainer = MtGoldLight,
    secondary = MtCyan,
    onSecondary = Color(0xFF001B20),
    secondaryContainer = Color(0xFF00363D),
    onSecondaryContainer = Color(0xFF70F5FF),
    tertiary = MtGreen,
    onTertiary = Color(0xFF00210A),
    background = MtDarkBg,
    onBackground = MtTextPrimary,
    surface = MtDarkSurface,
    onSurface = MtTextPrimary,
    surfaceVariant = MtDarkSurfaceVariant,
    onSurfaceVariant = MtTextSecondary,
    outline = MtDivider,
    error = MtRed,
    onError = Color.White
)

private val MtLightColorScheme = lightColorScheme(
    primary = MtGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE082),
    onPrimaryContainer = Color(0xFF261900),
    secondary = Color(0xFF00838F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF002024),
    tertiary = Color(0xFF2E7D32),
    onTertiary = Color.White,
    background = Color(0xFFF4F6F9),
    onBackground = Color(0xFF1E232A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E232A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = MtRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to MT Manager dark theme
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) MtDarkColorScheme else MtLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

