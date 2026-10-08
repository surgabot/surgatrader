package com.surgatrader.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            // Fallback to standard private mode if keystore is unavailable in tests/restricted environments
            context.getSharedPreferences(PREFS_FALLBACK_FILE_NAME, Context.MODE_PRIVATE)
        }
    }

    private val _dataModeFlow = MutableStateFlow(getDataMode())
    val dataModeFlow: StateFlow<DataMode> = _dataModeFlow.asStateFlow()

    fun getBridgeUrl(): String {
        return prefs.getString(KEY_BRIDGE_URL, "") ?: ""
    }

    fun setBridgeUrl(url: String) {
        prefs.edit().putString(KEY_BRIDGE_URL, url.trim()).apply()
    }

    fun getBridgeToken(): String {
        return prefs.getString(KEY_BRIDGE_TOKEN, "") ?: ""
    }

    fun setBridgeToken(token: String) {
        prefs.edit().putString(KEY_BRIDGE_TOKEN, token.trim()).apply()
    }

    fun getDataMode(): DataMode {
        val modeStr = prefs.getString(KEY_DATA_MODE, DataMode.DEMO.name) ?: DataMode.DEMO.name
        return try {
            DataMode.valueOf(modeStr)
        } catch (_: Exception) {
            DataMode.DEMO
        }
    }

    fun setDataMode(mode: DataMode) {
        prefs.edit().putString(KEY_DATA_MODE, mode.name).apply()
        _dataModeFlow.value = mode
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isAutoReconnectEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_RECONNECT, true)
    }

    fun setAutoReconnectEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_RECONNECT, enabled).apply()
    }

    fun clearCredentials() {
        prefs.edit()
            .remove(KEY_BRIDGE_URL)
            .remove(KEY_BRIDGE_TOKEN)
            .apply()
        setDataMode(DataMode.DEMO)
    }

    companion object {
        private const val PREFS_FILE_NAME = "aura_quantum_secure_prefs"
        private const val PREFS_FALLBACK_FILE_NAME = "aura_quantum_private_prefs"

        private const val KEY_BRIDGE_URL = "secure_bridge_url"
        private const val KEY_BRIDGE_TOKEN = "secure_bridge_token"
        private const val KEY_DATA_MODE = "secure_data_mode"
        private const val KEY_BIOMETRIC_ENABLED = "secure_biometric_enabled"
        private const val KEY_AUTO_RECONNECT = "secure_auto_reconnect"
    }
}
