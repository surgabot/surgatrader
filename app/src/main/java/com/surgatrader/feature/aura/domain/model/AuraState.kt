package com.surgatrader.feature.aura.domain.model

import com.surgatrader.core.security.DataMode
import com.surgatrader.feature.aura.domain.council.CouncilConsensusResult
import com.surgatrader.feature.aura.domain.council.CouncilMemberReport

data class AuraState(
    val currentStepIndex: Int = 0,
    val isAutoPlay: Boolean = true,
    val isSpeaking: Boolean = false,
    val isMuted: Boolean = false,
    val speechSpeed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isLeftPanelCollapsed: Boolean = false,
    val isRightPanelCollapsed: Boolean = false,
    val isTerminalOpen: Boolean = false,
    val isStartModalVisible: Boolean = true,
    val dataMode: DataMode = DataMode.DEMO,
    
    // Live / Demo Ticker Data (USC 3 desimal & USD)
    val goldPriceUsd: Double = 2658.45,
    val goldPriceUsc: Double = 4100.234,
    val bidPriceUsc: Double = 4100.234,
    val askPriceUsc: Double = 4100.354,
    val spreadPoints: Double = 120.0,
    val spreadPips: Double = 0.12,
    val dailyChangePercent: Double = 0.42,
    val floatingProfitUsc: Double = 0.0,
    val isTickPositive: Boolean = true,
    val latencyMs: Double = 0.0,
    val dailyAlphaUsd: Double = 0.0,
    val sharpeRatio: Double = 0.0,
    val activeSession: String = "SESI ASIA",
    val wibClock: String = "00:00:00 WIB",
    
    // Hologram Data: 16 Real M5 Candles & ATR Volatility
    val m5Candles: List<MarketCandle> = emptyList(),
    val atr14: Double = 1.450,
    val rotationSpeedMultiplier: Float = 1.0f,
    val consensusBias: CouncilBias = CouncilBias.BULLISH,

    // Dynamic 5 Council Reports & Master Consensus
    val activeScript: List<AuraScriptStep> = emptyList(),
    val latestConsensus: CouncilConsensusResult? = null,
    val activeCouncilReports: Map<String, CouncilMemberReport> = emptyMap(),
    
    // MT5 Cent Bridge State (No hardcoded credentials)
    val isMt5Connected: Boolean = false,
    val mt5AccountLogin: Long = 0L,
    val mt5Server: String = "Offline",
    val mt5AccountType: String = "Demo / Standar Cent",
    val mt5BalanceUsc: Double = 0.0,
    val mt5EquityUsc: Double = 0.0,
    val mt5FreeMarginUsc: Double = 0.0,
    val mt5MarginLevel: Double = 0.0,
    val lastOrderExecutionMessage: String? = null,
    
    // Audio waveform simulation values
    val waveformFractions: List<Float> = listOf(0.2f, 0.4f, 0.7f, 0.9f, 0.5f, 0.3f),
    
    // Logs
    val terminalLogs: List<AuraTerminalLog> = emptyList()
) {
    val isDemoMode: Boolean
        get() = dataMode == DataMode.DEMO

    val currentScriptList: List<AuraScriptStep>
        get() = if (activeScript.isNotEmpty()) activeScript else DefaultAuraScript

    val currentStep: AuraScriptStep
        get() = currentScriptList.getOrElse(currentStepIndex) { currentScriptList[0] }

    val totalStepsCount: Int
        get() = currentScriptList.size

    val currentSpeaker: AuraEntity
        get() = DefaultAuraEntities.find { it.id == currentStep.speakerId } ?: DefaultAuraEntities[0]
}
