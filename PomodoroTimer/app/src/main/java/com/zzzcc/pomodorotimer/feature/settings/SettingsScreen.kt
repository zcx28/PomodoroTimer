package com.zzzcc.pomodorotimer.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zzzcc.pomodorotimer.R
import com.zzzcc.pomodorotimer.ui.components.PomodoroCard
import com.zzzcc.pomodorotimer.ui.components.PomodoroPrimaryButton

@Composable
fun SettingsRoute(
    contentPadding: PaddingValues,
    viewModel: SettingsViewModel = viewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()

    SettingsScreen(
        contentPadding = contentPadding,
        connectionState = connectionState,
        onRetryConnection = viewModel::checkConnection
    )
}

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    connectionState: NetworkConnectionState,
    onRetryConnection: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        PomodoroCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_focus_duration),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.settings_focus_duration_value),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PomodoroCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.network_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = connectionStatusText(connectionState),
                    color = when (connectionState) {
                        is NetworkConnectionState.Connected ->
                            MaterialTheme.colorScheme.primary
                        NetworkConnectionState.Checking ->
                            MaterialTheme.colorScheme.onSurfaceVariant
                        NetworkConnectionState.Failed ->
                            MaterialTheme.colorScheme.error
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PomodoroPrimaryButton(
            text = stringResource(R.string.network_retry),
            onClick = onRetryConnection,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun connectionStatusText(state: NetworkConnectionState): String {
    return when (state) {
        NetworkConnectionState.Checking -> stringResource(R.string.network_checking)
        is NetworkConnectionState.Connected -> stringResource(
            R.string.network_connected,
            state.serverName
        )
        NetworkConnectionState.Failed -> stringResource(R.string.network_failed)
    }
}
