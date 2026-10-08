package com.surgatrader.feature.aura.domain

import com.surgatrader.feature.aura.domain.council.CouncilConsensusResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Snapshot data pasar dan akun real-time / simulasi untuk disinkronkan antar modul
 * (Command Room -> Kalkulator Lot & Radar Risiko).
 */
data class MarketSnapshot(
    val currentPrice: Double = 4100.234,
    val bidPrice: Double = 4100.234,
    val askPrice: Double = 4100.354,
    val spreadPoints: Double = 120.0,
    val balanceUsc: Double = 50000.0,
    val equityUsc: Double = 50000.0,
    val freeMarginUsc: Double = 50000.0,
    val marginLevel: Double = 1250.0,
    val atr14: Double = 1.450,
    val latestConsensus: CouncilConsensusResult? = null,
    val isLiveConnected: Boolean = false,
    val accountServer: String = "Exness-Cent-Demo"
)

@Singleton
class AuraMarketStateHolder @Inject constructor() {

    private val _snapshot = MutableStateFlow(MarketSnapshot())
    val snapshot: StateFlow<MarketSnapshot> = _snapshot.asStateFlow()

    fun updateSnapshot(newSnapshot: MarketSnapshot) {
        _snapshot.value = newSnapshot
    }

    fun updatePriceAndAtr(bid: Double, ask: Double, spreadPts: Double, atr: Double) {
        _snapshot.value = _snapshot.value.copy(
            currentPrice = bid,
            bidPrice = bid,
            askPrice = ask,
            spreadPoints = spreadPts,
            atr14 = atr
        )
    }

    fun updateAccount(balance: Double, equity: Double, freeMargin: Double, marginLevel: Double, isLive: Boolean, server: String) {
        _snapshot.value = _snapshot.value.copy(
            balanceUsc = balance,
            equityUsc = equity,
            freeMarginUsc = freeMargin,
            marginLevel = marginLevel,
            isLiveConnected = isLive,
            accountServer = server
        )
    }

    fun updateConsensus(consensus: CouncilConsensusResult?) {
        _snapshot.value = _snapshot.value.copy(
            latestConsensus = consensus
        )
    }
}
