package com.surgatrader.feature.advanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Mt5BridgeUiState(
    val endpointUrl: String = "http://10.0.2.2:22346/mcp", // Default IP host komputer dari emulator Android
    val authToken: String = "41N+oWQuYq5Q/s69xmv282vALkGOMpWBf0+Ce/1sBD",
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val accountData: Mt5AccountData? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class Mt5BridgeViewModel @Inject constructor(
    private val bridgeClient: Mt5McpBridgeClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(Mt5BridgeUiState())
    val uiState: StateFlow<Mt5BridgeUiState> = _uiState.asStateFlow()

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(endpointUrl = url, errorMessage = null)
    }

    fun updateToken(token: String) {
        _uiState.value = _uiState.value.copy(authToken = token, errorMessage = null)
    }

    fun testAndSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConnecting = true, errorMessage = null, successMessage = null)
            val result = bridgeClient.getTradingAccountInfo(
                baseUrl = _uiState.value.endpointUrl.trim(),
                token = _uiState.value.authToken.trim()
            )

            result.fold(
                onSuccess = { acc ->
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        isConnected = true,
                        accountData = acc,
                        successMessage = "Berhasil tersambung ke ${acc.broker} (${acc.server}) - Login: ${acc.login}"
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        isConnected = false,
                        errorMessage = "Gagal tersambung: ${err.message}. Pastikan MT5 & server MCP aktif. Jika menggunakan HP, ganti IP ke IP Wi-Fi laptop."
                    )
                }
            )
        }
    }
}
