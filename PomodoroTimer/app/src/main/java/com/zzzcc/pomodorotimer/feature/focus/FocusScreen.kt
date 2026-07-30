package com.zzzcc.pomodorotimer.feature.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.zzzcc.pomodorotimer.R
import com.zzzcc.pomodorotimer.ui.components.PomodoroCard
import com.zzzcc.pomodorotimer.ui.components.PomodoroPrimaryButton
import com.zzzcc.pomodorotimer.ui.theme.PomodoroTimerTheme
import kotlinx.coroutines.delay

private const val FocusDurationSeconds = 25 * 60

enum class FocusTimerState {
    Idle,
    Running,
    Paused,
    Finished
}

@Composable
fun FocusRoute() {
    var remainingSeconds by rememberSaveable {
        mutableIntStateOf(FocusDurationSeconds)
    }
    var timerState by rememberSaveable {
        mutableStateOf(FocusTimerState.Idle)
    }

    LaunchedEffect(timerState) {
        if (timerState == FocusTimerState.Running) {
            while (remainingSeconds > 0) {
                delay(1_000L)
                remainingSeconds -= 1
            }

            if (remainingSeconds == 0) {
                timerState = FocusTimerState.Finished
            }
        }
    }

    FocusScreen(
        remainingSeconds = remainingSeconds,
        timerState = timerState,
        onPrimaryAction = {
            when (timerState) {
                FocusTimerState.Idle -> timerState = FocusTimerState.Running
                FocusTimerState.Running -> timerState = FocusTimerState.Paused
                FocusTimerState.Paused -> timerState = FocusTimerState.Running
                FocusTimerState.Finished -> {
                    remainingSeconds = FocusDurationSeconds
                    timerState = FocusTimerState.Idle
                }
            }
        }
    )
}

@Composable
fun FocusScreen(
    remainingSeconds: Int,
    timerState: FocusTimerState,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (timerState == FocusTimerState.Finished) {
        stringResource(R.string.header_finished)
    } else {
        stringResource(R.string.header_focus)
    }

    val buttonLabel = when (timerState) {
        FocusTimerState.Idle -> stringResource(R.string.action_start)
        FocusTimerState.Running -> stringResource(R.string.action_pause)
        FocusTimerState.Paused -> stringResource(R.string.action_resume)
        FocusTimerState.Finished -> stringResource(R.string.action_restart)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(103.dp))

        TimerRing(
            timeText = formatTimerText(remainingSeconds),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        CurrentTaskCard()

        Spacer(modifier = Modifier.height(72.dp))

        PomodoroPrimaryButton(
            text = buttonLabel,
            onClick = onPrimaryAction,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun TimerRing(
    timeText: String,
    modifier: Modifier = Modifier
) {
    val ringColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = ringColor,
                style = Stroke(width = 12.dp.toPx())
            )
        }

        Text(
            text = timeText,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CurrentTaskCard(modifier: Modifier = Modifier) {
    PomodoroCard(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.current_task_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = stringResource(R.string.current_task_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

private fun formatTimerText(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

@Preview(widthDp = 412, heightDp = 917, showBackground = true)
@Composable
private fun FocusScreenPreview() {
    PomodoroTimerTheme {
        FocusScreen(
            remainingSeconds = FocusDurationSeconds,
            timerState = FocusTimerState.Idle,
            onPrimaryAction = {}
        )
    }
}
