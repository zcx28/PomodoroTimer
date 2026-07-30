package com.zzzcc.pomodorotimer.feature.focus

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zzzcc.pomodorotimer.R
import com.zzzcc.pomodorotimer.core.model.DefaultFocusDurationSeconds
import com.zzzcc.pomodorotimer.core.model.FocusTimerState
import com.zzzcc.pomodorotimer.ui.components.PomodoroCard
import com.zzzcc.pomodorotimer.ui.components.PomodoroPrimaryButton
import com.zzzcc.pomodorotimer.ui.theme.PomodoroTimerTheme

@Composable
fun FocusRoute(
    contentPadding: PaddingValues,
    viewModel: FocusViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    FocusScreen(
        remainingSeconds = uiState.remainingSeconds,
        timerState = uiState.timerState,
        contentPadding = contentPadding,
        onPrimaryAction = {
            val startsTimer = uiState.timerState == FocusTimerState.Idle ||
                uiState.timerState == FocusTimerState.Paused
            val needsNotificationPermission =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED

            if (startsTimer && needsNotificationPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            viewModel.onPrimaryAction()
        }
    )
}

@Composable
fun FocusScreen(
    remainingSeconds: Int,
    timerState: FocusTimerState,
    contentPadding: PaddingValues,
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
            .padding(contentPadding)
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
            remainingSeconds = DefaultFocusDurationSeconds,
            timerState = FocusTimerState.Idle,
            contentPadding = PaddingValues(),
            onPrimaryAction = {}
        )
    }
}
