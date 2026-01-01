package com.sohitechnology.clubmanagement.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightOnPrimary,
    onSurface = LightOnSurface,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkOnPrimary,
    onSurface = DarkOnSurface,
    error = ErrorRed
)

@Composable
fun ClubManagementTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}


