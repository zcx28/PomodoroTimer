package com.zzzcc.pomodorotimer.core.model

const val DefaultFocusDurationSeconds = 25 * 60

enum class FocusTimerState {
    Idle,
    Running,
    Paused,
    Finished
}
