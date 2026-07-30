package com.zzzcc.pomodorotimer.feature.focus

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zzzcc.pomodorotimer.core.model.DefaultFocusDurationSeconds
import com.zzzcc.pomodorotimer.core.model.FocusTimerState
import com.zzzcc.pomodorotimer.data.timer.FocusTimerAlarmScheduler
import com.zzzcc.pomodorotimer.data.timer.FocusTimerRepository
import com.zzzcc.pomodorotimer.notification.FocusNotificationManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FocusUiState(
    val remainingSeconds: Int = DefaultFocusDurationSeconds,
    val timerState: FocusTimerState = FocusTimerState.Idle
)

class FocusViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FocusTimerRepository(application)
    private val alarmScheduler = FocusTimerAlarmScheduler(application)
    private val notificationManager = FocusNotificationManager(application)

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState = _uiState.asStateFlow()

    private var endAtMillis = 0L
    private var tickerJob: Job? = null

    init {
        restoreTimer()
    }

    fun onPrimaryAction() {
        when (_uiState.value.timerState) {
            FocusTimerState.Idle -> startTimer()
            FocusTimerState.Running -> pauseTimer()
            FocusTimerState.Paused -> startTimer()
            FocusTimerState.Finished -> resetTimer()
        }
    }

    private fun restoreTimer() {
        val storedTimer = repository.load()
        endAtMillis = storedTimer.endAtMillis

        if (storedTimer.state == FocusTimerState.Running) {
            val remainingSeconds = calculateRemainingSeconds(endAtMillis)
            if (remainingSeconds <= 0) {
                finishTimer(showNotification = true)
            } else {
                _uiState.value = FocusUiState(
                    remainingSeconds = remainingSeconds,
                    timerState = FocusTimerState.Running
                )
                alarmScheduler.schedule(endAtMillis)
                startTicker()
            }
        } else {
            _uiState.value = FocusUiState(
                remainingSeconds = storedTimer.remainingSeconds,
                timerState = storedTimer.state
            )
        }
    }

    private fun startTimer() {
        val remainingSeconds = _uiState.value.remainingSeconds
        endAtMillis = System.currentTimeMillis() + remainingSeconds * 1_000L

        _uiState.value = FocusUiState(
            remainingSeconds = remainingSeconds,
            timerState = FocusTimerState.Running
        )
        repository.save(
            state = FocusTimerState.Running,
            remainingSeconds = remainingSeconds,
            endAtMillis = endAtMillis
        )
        alarmScheduler.schedule(endAtMillis)
        startTicker()
    }

    private fun pauseTimer() {
        val remainingSeconds = calculateRemainingSeconds(endAtMillis)
        tickerJob?.cancel()
        alarmScheduler.cancel()

        if (remainingSeconds <= 0) {
            finishTimer(showNotification = true)
            return
        }

        endAtMillis = 0L
        _uiState.value = FocusUiState(
            remainingSeconds = remainingSeconds,
            timerState = FocusTimerState.Paused
        )
        repository.save(
            state = FocusTimerState.Paused,
            remainingSeconds = remainingSeconds
        )
    }

    private fun resetTimer() {
        tickerJob?.cancel()
        alarmScheduler.cancel()
        endAtMillis = 0L
        _uiState.value = FocusUiState()
        repository.save(
            state = FocusTimerState.Idle,
            remainingSeconds = DefaultFocusDurationSeconds
        )
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = viewModelScope.launch {
            while (_uiState.value.timerState == FocusTimerState.Running) {
                val remainingSeconds = calculateRemainingSeconds(endAtMillis)
                if (remainingSeconds <= 0) {
                    finishTimer(showNotification = true)
                    break
                }

                _uiState.value = _uiState.value.copy(
                    remainingSeconds = remainingSeconds
                )
                delay(250L)
            }
        }
    }

    private fun finishTimer(showNotification: Boolean) {
        tickerJob?.cancel()
        alarmScheduler.cancel()
        endAtMillis = 0L
        _uiState.value = FocusUiState(
            remainingSeconds = 0,
            timerState = FocusTimerState.Finished
        )
        repository.save(
            state = FocusTimerState.Finished,
            remainingSeconds = 0
        )

        if (showNotification) {
            notificationManager.showFocusFinished()
        }
    }

    private fun calculateRemainingSeconds(endAtMillis: Long): Int {
        val remainingMillis = (endAtMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        return ((remainingMillis + 999L) / 1_000L).toInt()
    }
}
