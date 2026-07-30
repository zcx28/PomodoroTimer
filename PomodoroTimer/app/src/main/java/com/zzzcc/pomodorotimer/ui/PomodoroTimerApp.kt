package com.zzzcc.pomodorotimer.ui

import androidx.compose.runtime.Composable
import com.zzzcc.pomodorotimer.ui.navigation.PomodoroAppNavigation
import com.zzzcc.pomodorotimer.ui.theme.PomodoroTimerTheme

@Composable
fun PomodoroTimerApp() {
    PomodoroTimerTheme {
        PomodoroAppNavigation()
    }
}
