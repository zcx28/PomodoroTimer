package com.zzzcc.pomodorotimer.ui

import androidx.compose.runtime.Composable
import com.zzzcc.pomodorotimer.feature.focus.FocusRoute
import com.zzzcc.pomodorotimer.ui.theme.PomodoroTimerTheme

@Composable
fun PomodoroTimerApp() {
    PomodoroTimerTheme {
        FocusRoute()
    }
}
