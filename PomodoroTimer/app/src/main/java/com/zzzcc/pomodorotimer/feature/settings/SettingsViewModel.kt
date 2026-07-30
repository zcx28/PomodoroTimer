package com.zzzcc.pomodorotimer.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zzzcc.pomodorotimer.data.network.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NetworkConnectionState {
    data object Checking : NetworkConnectionState
    data class Connected(val serverName: String) : NetworkConnectionState
    data object Failed : NetworkConnectionState
}

class SettingsViewModel(
    private val networkRepository: NetworkRepository = NetworkRepository()
) : ViewModel() {
    private val _connectionState = MutableStateFlow<NetworkConnectionState>(
        NetworkConnectionState.Checking
    )
    val connectionState = _connectionState.asStateFlow()

    init {
        checkConnection()
    }

    fun checkConnection() {
        _connectionState.value = NetworkConnectionState.Checking
        viewModelScope.launch {
            _connectionState.value = networkRepository.checkConnection().fold(
                onSuccess = { connection ->
                    NetworkConnectionState.Connected(connection.serverName)
                },
                onFailure = {
                    NetworkConnectionState.Failed
                }
            )
        }
    }
}
