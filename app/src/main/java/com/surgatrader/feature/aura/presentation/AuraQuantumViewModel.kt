package com.surgatrader.feature.aura.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.core.security.DataMode
import com.surgatrader.core.security.SecurePreferencesManager
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.feature.advanced.Mt5McpBridgeClient
import com.surgatrader.feature.aura.audio.AndroidIndonesianVoiceEngine
import com.surgatrader.feature.aura.audio.CyberSynthPlayer
import com.surgatrader.feature.aura.domain.model.AuraEntity
import com.surgatrader.feature.aura.domain.model.AuraState
import com.surgatrader.feature.aura.domain.model.AuraTerminalLog
import com.surgatrader.feature.aura.domain.model.DefaultAuraEntities
import com.surgatrader.feature.aura.domain.model.DefaultAuraScript
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AuraQuantumViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mt5Client: Mt5McpBridgeClient,
    private val securePrefs: SecurePreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuraState(dataMode = securePrefs.getDataMode()))
    val state: StateFlow<AuraState> = _state.asStateFlow()

    private val synth = CyberSynthPlayer()
    private var voiceEngine: AndroidIndonesianVoiceEngine? = null
    private var autoAdvanceJob: Job? = null
    private var marketTickJob: Job? = null

    init {
        initVoiceEngine()
        initTerminalWelcomeLogs()
        refreshConnectionAndMode()
    }

    private fun initVoiceEngine() {
        voiceEngine = AndroidIndonesianVoiceEngine(
            context = context,
            onSpeechStarted = {
                _state.update { it.copy(isSpeaking = true) }
            },
            onSpeechCompleted = {
                _state.update { it.copy(isSpeaking = false) }
                if (_state.value.isAutoPlay) {
                    scheduleAutoAdvance()
                }
            },
            onSpeechError = {
                _state.update { it.copy(isSpeaking = false) }
            }
        )
    }

    private fun initTerminalWelcomeLogs() {
        val initialLogs = listOf(
            AuraTerminalLog(
                timestamp = currentTimestamp(),
                speakerName = "QUANTUM-KERNEL",
                speakerColor = AuraCyan,
                message = "Inisialisasi sistem sub-milidetik Aura Quantum Gold v2.0 aktif."
            ),
            AuraTerminalLog(
                timestamp = currentTimestamp(),
                speakerName = "SECURITY-GUARD",
                speakerColor = AuraGoldPrimary,
                message = "Modul keamanan Android Keystore aktif. Kredensial tersimpan lokal terenkripsi."
            )
        )
        _state.update { it.copy(terminalLogs = initialLogs) }
    }

    fun refreshConnectionAndMode() {
        val currentMode = securePrefs.getDataMode()
        val url = securePrefs.getBridgeUrl()
        val token = securePrefs.getBridgeToken()

        _state.update { it.copy(dataMode = currentMode) }

        if (currentMode == DataMode.LIVE && url.isNotBlank()) {
            syncWithLiveBridge(url, token)
        } else {
            initDemoMode()
        }
    }

    private fun initDemoMode() {
        _state.update {
            it.copy(
                dataMode = DataMode.DEMO,
                isMt5Connected = false,
                mt5Server = "SIMULASI DEMO",
                mt5AccountType = "Standar Cent (Simulasi)",
                mt5BalanceUsc = 2500.00,
                mt5EquityUsc = 2500.00,
                mt5FreeMarginUsc = 2500.00,
                latencyMs = 0.0
            )
        }
        addTerminalLog(
            speaker = "MODE-DISPATCHER",
            color = AuraCyan,
            message = "Aplikasi berjalan dalam MODE DEMO • BUKAN DATA ASLI. Konfigurasikan koneksi untuk data Live."
        )
        startMarketSimulation()
    }

    private fun syncWithLiveBridge(url: String, token: String) {
        viewModelScope.launch {
            val testResult = mt5Client.testConnection(url, token)
            if (testResult.isConnected && testResult.accountData != null) {
                val acc = testResult.accountData
                _state.update {
                    it.copy(
                        isMt5Connected = true,
                        dataMode = DataMode.LIVE,
                        mt5BalanceUsc = acc.balance,
                        mt5EquityUsc = acc.equity,
                        mt5FreeMarginUsc = acc.marginFree,
                        mt5Server = acc.server,
                        mt5AccountType = acc.type,
                        latencyMs = testResult.latencyMs.toDouble()
                    )
                }
                addTerminalLog(
                    speaker = "MT5-BRIDGE",
                    color = AuraGreenBull,
                    message = "Koneksi Live terverifikasi ke ${acc.broker} (${acc.server}). Latency nyata: ${testResult.latencyMs} ms."
                )
            } else {
                _state.update {
                    it.copy(
                        isMt5Connected = false,
                        latencyMs = testResult.latencyMs.toDouble()
                    )
                }
                addTerminalLog(
                    speaker = "MT5-BRIDGE",
                    color = AuraCyan,
                    message = "Gagal terhubung ke Bridge MT5. Kembali ke data simulasi offline."
                )
                startMarketSimulation()
            }
        }
    }

    private fun startMarketSimulation() {
        marketTickJob?.cancel()
        marketTickJob = viewModelScope.launch {
            while (true) {
                delay(2000)
                val delta = (Random.nextDouble() - 0.48) * 0.35
                val newPrice = _state.value.goldPriceUsd + delta
                val newCentPrice = _state.value.goldPriceUsc + (delta * 1.54)

                _state.update {
                    it.copy(
                        goldPriceUsd = newPrice,
                        goldPriceUsc = newCentPrice,
                        isTickPositive = delta >= 0
                    )
                }
            }
        }
    }

    fun dismissStartModal() {
        _state.update { it.copy(isStartModalVisible = false) }
        playStep(0)
    }

    fun playStep(index: Int) {
        autoAdvanceJob?.cancel()

        val validIndex = when {
            index >= DefaultAuraScript.size -> 0
            index < 0 -> DefaultAuraScript.size - 1
            else -> index
        }

        val step = DefaultAuraScript[validIndex]
        val speaker = DefaultAuraEntities.find { it.id == step.speakerId } ?: DefaultAuraEntities[0]

        _state.update {
            it.copy(
                currentStepIndex = validIndex,
                isSpeaking = true
            )
        }

        // Add to terminal log
        addTerminalLog(
            speaker = speaker.name,
            color = speaker.color,
            message = step.text
        )

        // CyberSynth chime sound
        synth.playTransmissionChime(speaker.id, _state.value.volume)

        // Speak in Indonesian
        if (!_state.value.isMuted) {
            val combinedRate = (speaker.voiceRate * _state.value.speechSpeed).coerceIn(0.5f, 2.0f)
            voiceEngine?.speak(
                text = step.text,
                rate = combinedRate,
                volume = _state.value.volume
            )
        } else {
            _state.update { it.copy(isSpeaking = false) }
        }
    }

    private fun scheduleAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = viewModelScope.launch {
            delay(2400)
            if (_state.value.isAutoPlay) {
                playStep(_state.value.currentStepIndex + 1)
            }
        }
    }

    fun nextStep() {
        playStep(_state.value.currentStepIndex + 1)
    }

    fun prevStep() {
        playStep(_state.value.currentStepIndex - 1)
    }

    fun replayStep() {
        playStep(_state.value.currentStepIndex)
    }

    fun selectEntity(entity: AuraEntity) {
        val targetIndex = DefaultAuraScript.indexOfFirst { it.speakerId == entity.id }
        if (targetIndex != -1) {
            playStep(targetIndex)
        } else {
            synth.playTransmissionChime(entity.id, _state.value.volume)
        }
    }

    fun toggleAutoPlay() {
        _state.update { it.copy(isAutoPlay = !it.isAutoPlay) }
        if (_state.value.isAutoPlay && !_state.value.isSpeaking) {
            playStep(_state.value.currentStepIndex + 1)
        } else if (!_state.value.isAutoPlay) {
            autoAdvanceJob?.cancel()
        }
    }

    fun stopAudio() {
        autoAdvanceJob?.cancel()
        voiceEngine?.stop()
        _state.update { it.copy(isSpeaking = false) }
    }

    fun toggleMute() {
        val newMuted = !_state.value.isMuted
        _state.update { it.copy(isMuted = newMuted) }
        if (newMuted) {
            stopAudio()
        } else {
            replayStep()
        }
    }

    fun setSpeed(speed: Float) {
        _state.update { it.copy(speechSpeed = speed) }
    }

    fun setVolume(volume: Float) {
        _state.update { it.copy(volume = volume) }
    }

    fun toggleLeftPanel() {
        _state.update { it.copy(isLeftPanelCollapsed = !it.isLeftPanelCollapsed) }
    }

    fun toggleRightPanel() {
        _state.update { it.copy(isRightPanelCollapsed = !it.isRightPanelCollapsed) }
    }

    fun toggleTerminal() {
        _state.update { it.copy(isTerminalOpen = !it.isTerminalOpen) }
    }

    fun executeQuantumOrder() {
        viewModelScope.launch {
            synth.playTransmissionChime("execution", _state.value.volume)
            val orderTime = currentTimestamp()
            val orderMsg = "[$orderTime] SINYAL DISIMULASIKAN: Analisis 0.01 Lot XAUUSDc @ ${String.format(Locale.US, "%.3f", _state.value.goldPriceUsc)} USC. Mode eksekusi langsung dinonaktifkan demi kepatuhan risiko."
            
            addTerminalLog(
                speaker = "AEGIS-EXECUTION",
                color = AuraGreenBull,
                message = orderMsg
            )
            
            _state.update {
                it.copy(
                    lastOrderExecutionMessage = orderMsg,
                    terminalLogs = it.terminalLogs
                )
            }
        }
    }

    private fun addTerminalLog(speaker: String, color: androidx.compose.ui.graphics.Color, message: String) {
        val newLog = AuraTerminalLog(
            timestamp = currentTimestamp(),
            speakerName = speaker,
            speakerColor = color,
            message = message
        )
        _state.update {
            it.copy(terminalLogs = (it.terminalLogs + newLog).takeLast(60))
        }
    }

    private fun currentTimestamp(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onCleared() {
        super.onCleared()
        autoAdvanceJob?.cancel()
        marketTickJob?.cancel()
        voiceEngine?.shutdown()
    }
}
