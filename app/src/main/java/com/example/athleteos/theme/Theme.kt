package com.example.athleteos.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AthleteColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = CardSurface,
    primaryContainer = ElectricBlue.copy(alpha = 0.12f),
    onPrimaryContainer = ElectricBlueDark,
    secondary = SuccessGreen,
    onSecondary = CardSurface,
    secondaryContainer = SuccessGreen.copy(alpha = 0.12f),
    onSecondaryContainer = SuccessGreen,
    tertiary = WarningAmber,
    onTertiary = CardSurface,
    tertiaryContainer = WarningAmber.copy(alpha = 0.12f),
    onTertiaryContainer = WarningAmber,
    error = FailureRed,
    onError = CardSurface,
    errorContainer = FailureRed.copy(alpha = 0.12f),
    onErrorContainer = FailureRed,
    background = Color(0xFFFAFAFA),
    onBackground = TextPrimary,
    surface = CardSurface,
    onSurface = TextPrimary,
    surfaceVariant = CardSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = DividerColor,
    outlineVariant = DividerColor
)

@Composable
fun AthleteOSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AthleteColorScheme,
        typography = Typography,
        content = content
    )
}
