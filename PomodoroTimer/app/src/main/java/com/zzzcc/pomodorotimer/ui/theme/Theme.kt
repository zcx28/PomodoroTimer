package com.zzzcc.pomodorotimer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PomodoroColorScheme = lightColorScheme(
    primary = PomodoroPrimary,
    onPrimary = PomodoroSurface,
    background = PomodoroBackground,
    onBackground = PomodoroText,
    surface = PomodoroSurface,
    onSurface = PomodoroText
)

@Composable
fun PomodoroTimerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PomodoroColorScheme,
        typography = Typography,
        content = content
    )
}
