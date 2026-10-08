package com.surgatrader.feature.connection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.core.security.DataMode
import com.surgatrader.core.security.SecurePreferencesManager
import com.surgatrader.feature.advanced.ConnectionTestResult
import com.surgatrader.feature.advanced.Mt5AccountData
import com.surgatrader.feature.advanced.Mt5McpBridgeClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConnectionUiState(
    val bridgeUrl: String = "",
    val bridgeToken: String = "",
    val isTokenVisible: Boolean = false,
    val dataMode: DataMode = DataMode.DEMO,
    val isBiometricEnabled: Boolean = false,
    val isAutoReconnectEnabled: Boolean = true,
    val isTesting: Boolean = false,
    val isConnected: Boolean = false,
    val latencyMs: Long = 0L,
    val accountData: Mt5AccountData? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ConnectionViewModel @Inject constructor(
    private val securePrefs: SecurePreferencesManager,
    private val bridgeClient: Mt5McpBridgeClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ConnectionUiState(
            bridgeUrl = securePrefs.getBridgeUrl().ifBlank { "http://10.0.2.2:22346/mcp" },
            bridgeToken = securePrefs.getBridgeToken(),
            dataMode = securePrefs.getDataMode(),
            isBiometricEnabled = securePrefs.isBiometricEnabled(),
            isAutoReconnectEnabled = securePrefs.isAutoReconnectEnabled()
        )
    )
    val uiState: StateFlow<ConnectionUiState> = _uiState.asStateFlow()

    fun onUrlChanged(url: String) {
        _uiState.update { it.copy(bridgeUrl = url, errorMessage = null, successMessage = null) }
        securePrefs.setBridgeUrl(url)
    }

    fun onTokenChanged(token: String) {
        _uiState.update { it.copy(bridgeToken = token, errorMessage = null, successMessage = null) }
        securePrefs.setBridgeToken(token)
    }

    fun toggleTokenVisibility() {
        _uiState.update { it.copy(isTokenVisible = !it.isTokenVisible) }
    }

    fun setDataMode(mode: DataMode) {
        securePrefs.setDataMode(mode)
        _uiState.update { it.copy(dataMode = mode, errorMessage = null, successMessage = null) }
    }

    fun toggleBiometric(enabled: Boolean) {
        securePrefs.setBiometricEnabled(enabled)
        _uiState.update { it.copy(isBiometricEnabled = enabled) }
    }

    fun toggleAutoReconnect(enabled: Boolean) {
        securePrefs.setAutoReconnectEnabled(enabled)
        _uiState.update { it.copy(isAutoReconnectEnabled = enabled) }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTesting = true, errorMessage = null, successMessage = null) }
            val url = _uiState.value.bridgeUrl.trim()
            val token = _uiState.value.bridgeToken.trim()

            val testResult: ConnectionTestResult = bridgeClient.testConnection(url, token)

            if (testResult.isConnected && testResult.accountData != null) {
                val acc = testResult.accountData
                securePrefs.setDataMode(DataMode.LIVE)
                _uiState.update {
                    it.copy(
                        isTesting = false,
                        isConnected = true,
                        latencyMs = testResult.latencyMs,
                        accountData = acc,
                        dataMode = DataMode.LIVE,
                        successMessage = "Koneksi berhasil! Latency nyata: ${testResult.latencyMs} ms. Server: ${acc.server}"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isTesting = false,
                        isConnected = false,
                        latencyMs = testResult.latencyMs,
                        errorMessage = "Gagal terhubung (${testResult.latencyMs} ms): ${testResult.errorMessage ?: "Koneksi ditolak"}"
                    )
                }
            }
        }
    }

    fun clearCredentials() {
        securePrefs.clearCredentials()
        _uiState.update {
            it.copy(
                bridgeUrl = "http://10.0.2.2:22346/mcp",
                bridgeToken = "",
                dataMode = DataMode.DEMO,
                isConnected = false,
                accountData = null,
                successMessage = "Kredensial dibersihkan dari penyimpanan lokal."
            )
        }
    }
}
