package com.zzzcc.pomodorotimer.data.timer

import android.content.Context
import androidx.core.content.edit
import com.zzzcc.pomodorotimer.core.model.DefaultFocusDurationSeconds
import com.zzzcc.pomodorotimer.core.model.FocusTimerState

data class StoredFocusTimer(
    val state: FocusTimerState,
    val remainingSeconds: Int,
    val endAtMillis: Long
)

class FocusTimerRepository(context: Context) {
    private val preferences = context.getSharedPreferences(
        PreferencesName,
        Context.MODE_PRIVATE
    )

    fun load(): StoredFocusTimer {
        val state = runCatching {
            FocusTimerState.valueOf(
                preferences.getString(KeyState, FocusTimerState.Idle.name)
                    ?: FocusTimerState.Idle.name
            )
        }.getOrDefault(FocusTimerState.Idle)

        return StoredFocusTimer(
            state = state,
            remainingSeconds = preferences.getInt(
                KeyRemainingSeconds,
                DefaultFocusDurationSeconds
            ),
            endAtMillis = preferences.getLong(KeyEndAtMillis, 0L)
        )
    }

    fun save(
        state: FocusTimerState,
        remainingSeconds: Int,
        endAtMillis: Long = 0L
    ) {
        preferences.edit {
            putString(KeyState, state.name)
            putInt(KeyRemainingSeconds, remainingSeconds)
            putLong(KeyEndAtMillis, endAtMillis)
        }
    }

    companion object {
        private const val PreferencesName = "focus_timer"
        private const val KeyState = "state"
        private const val KeyRemainingSeconds = "remaining_seconds"
        private const val KeyEndAtMillis = "end_at_millis"
    }
}
