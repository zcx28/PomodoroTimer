package com.zzzcc.pomodorotimer.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zzzcc.pomodorotimer.core.model.FocusTimerState
import com.zzzcc.pomodorotimer.data.timer.FocusTimerAlarmScheduler
import com.zzzcc.pomodorotimer.data.timer.FocusTimerRepository

class FocusTimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        FocusTimerAlarmScheduler(context).cancel()
        FocusTimerRepository(context).save(
            state = FocusTimerState.Finished,
            remainingSeconds = 0
        )
        FocusNotificationManager(context).showFocusFinished()
    }
}
