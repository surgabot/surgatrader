package com.surgatrader.feature.journal.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class JournalTradeItem(
    val ticket: Long,
    val symbol: String,
    val type: String, // BUY / SELL
    val openPrice: Double,
    val closePrice: Double,
    val volume: Double,
    val pnlUsc: Double,
    val setupTag: String,
    val emotionTag: String,
    val notes: String
)

data class JournalStatsUiState(
    val winRatePercent: Double = 63.6,
    val profitFactor: Double = 2.18,
    val expectancyUsc: Double = 320.0,
    val averageRiskReward: Double = 2.2,
    val maxDrawdownPercent: Double = 3.8,
    val totalTrades: Int = 22,
    val trades: List<JournalTradeItem> = listOf(
        JournalTradeItem(
            ticket = 1045231,
            symbol = "XAUUSDc",
            type = "BUY",
            openPrice = 4118.250,
            closePrice = 4128.500,
            volume = 0.02,
            pnlUsc = 2050.0,
            setupTag = "Breakout London Session",
            emotionTag = "Disiplin & Tenang",
            notes = "Rejection di support 4118 pas London open, TP kena rapi."
        ),
        JournalTradeItem(
            ticket = 1045180,
            symbol = "XAUUSDc",
            type = "SELL",
            openPrice = 4132.100,
            closePrice = 4136.200,
            volume = 0.01,
            pnlUsc = -410.0,
            setupTag = "Supply Zone Reversal",
            emotionTag = "Sesuai Plan",
            notes = "Kena SL 410 pts, disiplin tidak digeser."
        ),
        JournalTradeItem(
            ticket = 1044992,
            symbol = "XAUUSDc",
            type = "BUY",
            openPrice = 4105.000,
            closePrice = 4122.300,
            volume = 0.03,
            pnlUsc = 5190.0,
            setupTag = "Trend Continuation",
            emotionTag = "Disiplin",
            notes = "Riding trend US session, partial close 50% di 4115."
        )
    )
)

@HiltViewModel
class JournalViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(JournalStatsUiState())
    val uiState: StateFlow<JournalStatsUiState> = _uiState.asStateFlow()
}
