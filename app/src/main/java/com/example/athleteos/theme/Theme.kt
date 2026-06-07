package com.example.athleteos.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AthleteColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = NearBlack,
    primaryContainer = ElectricBlueDark,
    onPrimaryContainer = TextPrimary,
    secondary = SuccessGreen,
    onSecondary = NearBlack,
    secondaryContainer = SuccessGreen.copy(alpha = 0.2f),
    onSecondaryContainer = SuccessGreen,
    tertiary = WarningAmber,
    onTertiary = NearBlack,
    tertiaryContainer = WarningAmber.copy(alpha = 0.2f),
    onTertiaryContainer = WarningAmber,
    error = FailureRed,
    onError = TextPrimary,
    errorContainer = FailureRed.copy(alpha = 0.2f),
    onErrorContainer = FailureRed,
    background = NearBlack,
    onBackground = TextPrimary,
    surface = DarkGray,
    onSurface = TextPrimary,
    surfaceVariant = CardSurface,
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
