package com.surgatrader.feature.aura.domain.model

import androidx.compose.ui.graphics.Color

data class AuraTerminalLog(
    val timestamp: String,
    val speakerName: String,
    val speakerColor: Color,
    val message: String
)
