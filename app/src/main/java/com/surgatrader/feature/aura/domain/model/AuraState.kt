package com.surgatrader.feature.aura.domain.model

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
    
    // Live Ticker Data (USC / USD)
    val goldPriceUsd: Double = 2658.45,
    val goldPriceUsc: Double = 4100.234,
    val isTickPositive: Boolean = true,
    val spreadPips: Double = 0.1,
    val latencyMs: Double = 0.038,
    val dailyAlphaUsd: Double = 4820350.0,
    val sharpeRatio: Double = 5.42,
    
    // MT5 Exness Cent Bridge State
    val isMt5Connected: Boolean = true,
    val mt5AccountLogin: Long = 263608312L,
    val mt5Server: String = "Exness-MT5Real37",
    val mt5AccountType: String = "Standar Cent",
    val mt5BalanceUsc: Double = 2604.60,
    val mt5EquityUsc: Double = 2604.60,
    val mt5FreeMarginUsc: Double = 2604.60,
    val lastOrderExecutionMessage: String? = null,
    
    // Audio waveform simulation values
    val waveformFractions: List<Float> = listOf(0.2f, 0.4f, 0.7f, 0.9f, 0.5f, 0.3f),
    
    // Logs
    val terminalLogs: List<AuraTerminalLog> = emptyList()
) {
    val currentStep: AuraScriptStep
        get() = DefaultAuraScript.getOrElse(currentStepIndex) { DefaultAuraScript[0] }

    val currentSpeaker: AuraEntity
        get() = DefaultAuraEntities.find { it.id == currentStep.speakerId } ?: DefaultAuraEntities[0]
}
