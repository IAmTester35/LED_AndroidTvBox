package com.reecotech.androidtvbox.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = darkColorScheme(
    primary = OceanBlue,
    secondary = LightBlue,
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    error = StatusRed
)

@Composable
fun AndroidTVBoxTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}
