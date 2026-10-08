package com.surgatrader.feature.advanced

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.core.security.DataMode
import com.surgatrader.core.security.SecurePreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Mt5BridgeUiState(
    val endpointUrl: String = "",
    val authToken: String = "",
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val accountData: Mt5AccountData? = null,
    val measuredLatencyMs: Long = 0L,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val dataMode: DataMode = DataMode.DEMO
)

@HiltViewModel
class Mt5BridgeViewModel @Inject constructor(
    private val bridgeClient: Mt5McpBridgeClient,
    private val securePrefs: SecurePreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        Mt5BridgeUiState(
            endpointUrl = securePrefs.getBridgeUrl().ifBlank { "http://10.0.2.2:22346/mcp" },
            authToken = securePrefs.getBridgeToken(),
            dataMode = securePrefs.getDataMode()
        )
    )
    val uiState: StateFlow<Mt5BridgeUiState> = _uiState.asStateFlow()

    fun updateUrl(url: String) {
        securePrefs.setBridgeUrl(url)
        _uiState.value = _uiState.value.copy(endpointUrl = url, errorMessage = null)
    }

    fun updateToken(token: String) {
        securePrefs.setBridgeToken(token)
        _uiState.value = _uiState.value.copy(authToken = token, errorMessage = null)
    }

    fun setDataMode(mode: DataMode) {
        securePrefs.setDataMode(mode)
        _uiState.value = _uiState.value.copy(dataMode = mode)
    }

    fun testAndSync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConnecting = true, errorMessage = null, successMessage = null)
            val testResult = bridgeClient.testConnection(
                baseUrl = _uiState.value.endpointUrl.trim(),
                token = _uiState.value.authToken.trim()
            )

            if (testResult.isConnected && testResult.accountData != null) {
                val acc = testResult.accountData
                securePrefs.setDataMode(DataMode.LIVE)
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    isConnected = true,
                    accountData = acc,
                    measuredLatencyMs = testResult.latencyMs,
                    dataMode = DataMode.LIVE,
                    successMessage = "Koneksi Live Aktif! Latency: ${testResult.latencyMs} ms. Server: ${acc.server}"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    isConnected = false,
                    measuredLatencyMs = testResult.latencyMs,
                    errorMessage = "Gagal tersambung: ${testResult.errorMessage ?: "Koneksi ditolak"}. Latency: ${testResult.latencyMs} ms. Periksa URL dan token bridge."
                )
            }
        }
    }
}
