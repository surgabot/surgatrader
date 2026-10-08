package com.surgatrader.feature.journal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.feature.aura.domain.AuraMarketStateHolder
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.journal.data.TradeJournalEntity
import com.surgatrader.feature.journal.data.TradeJournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max

data class JournalStatsUiState(
    val totalTrades: Int = 0,
    val winningTrades: Int = 0,
    val losingTrades: Int = 0,
    val winRatePercent: Double = 0.0,
    val profitFactor: Double = 0.0,
    val netPnlUsc: Double = 0.0,
    val netPnlUsd: Double = 0.0,
    val expectancyUsc: Double = 0.0,
    val maxDrawdownPercent: Double = 0.0,
    val trades: List<TradeJournalEntity> = emptyList(),
    val statusMessage: String? = null
)

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: TradeJournalRepository,
    private val marketStateHolder: AuraMarketStateHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalStatsUiState())
    val uiState: StateFlow<JournalStatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedSampleTradesIfEmpty()
        }

        viewModelScope.launch {
            repository.getAllTrades().collect { tradeList ->
                calculateStats(tradeList)
            }
        }
    }

    private fun calculateStats(tradeList: List<TradeJournalEntity>) {
        if (tradeList.isEmpty()) {
            _uiState.value = JournalStatsUiState(trades = emptyList())
            return
        }

        val total = tradeList.size
        val wins = tradeList.count { it.isProfit }
        val losses = total - wins
        val winRate = if (total > 0) (wins.toDouble() / total) * 100.0 else 0.0

        val totalProfitUsc = tradeList.filter { it.isProfit }.sumOf { it.pnlUsc }
        val totalLossUsc = tradeList.filter { !it.isProfit }.sumOf { abs(it.pnlUsc) }
        val netUsc = tradeList.sumOf { it.pnlUsc }
        val netUsd = netUsc / 100.0

        val profitFactor = when {
            totalLossUsc > 0 -> totalProfitUsc / totalLossUsc
            totalProfitUsc > 0 -> 9.99
            else -> 0.0
        }

        val expectancy = if (total > 0) netUsc / total else 0.0

        // Hitung drawdown historis dari kurva ekuitas kumulatif
        var peak = 0.0
        var currentEquity = 0.0
        var maxDd = 0.0
        tradeList.reversed().forEach { t ->
            currentEquity += t.pnlUsc
            if (currentEquity > peak) {
                peak = currentEquity
            }
            val dd = if (peak > 0) ((peak - currentEquity) / peak) * 100.0 else 0.0
            if (dd > maxDd) {
                maxDd = dd
            }
        }

        _uiState.value = _uiState.value.copy(
            totalTrades = total,
            winningTrades = wins,
            losingTrades = losses,
            winRatePercent = BigDecimal(winRate).setScale(1, RoundingMode.HALF_UP).toDouble(),
            profitFactor = BigDecimal(profitFactor).setScale(2, RoundingMode.HALF_UP).toDouble(),
            netPnlUsc = BigDecimal(netUsc).setScale(1, RoundingMode.HALF_UP).toDouble(),
            netPnlUsd = BigDecimal(netUsd).setScale(2, RoundingMode.HALF_UP).toDouble(),
            expectancyUsc = BigDecimal(expectancy).setScale(1, RoundingMode.HALF_UP).toDouble(),
            maxDrawdownPercent = BigDecimal(maxDd).setScale(1, RoundingMode.HALF_UP).toDouble(),
            trades = tradeList
        )
    }

    fun addTrade(
        type: String,
        openPrice: Double,
        closePrice: Double,
        volumeLot: Double,
        pnlUsc: Double,
        setupTag: String,
        emotionTag: String,
        notes: String
    ) {
        viewModelScope.launch {
            val consensus = marketStateHolder.snapshot.value.latestConsensus
            val score = consensus?.confluenceScore ?: 80

            val trade = TradeJournalEntity(
                ticket = (1000000..9999999).random().toLong(),
                timestampMillis = System.currentTimeMillis(),
                symbol = "XAUUSDc",
                type = type,
                openPrice = openPrice,
                closePrice = closePrice,
                volumeLot = volumeLot,
                pnlUsc = pnlUsc,
                setupTag = setupTag.ifBlank { "Sinyal Dewan Kuantum" },
                dewanConfluenceScore = score,
                emotionTag = emotionTag.ifBlank { "Disiplin & Sesuai Plan" },
                notes = notes.ifBlank { "Tereksekusi otomatis dari terminal." }
            )
            repository.addTrade(trade)
            _uiState.value = _uiState.value.copy(
                statusMessage = "✅ Transaksi #${trade.ticket} berhasil dicatat ke Jurnal Room!"
            )
        }
    }

    fun addTradeFromAegisSignal(pnlUsc: Double, notes: String) {
        viewModelScope.launch {
            val snapshot = marketStateHolder.snapshot.value
            val consensus = snapshot.latestConsensus

            val type = if (consensus?.consensusBias == CouncilBias.BEARISH) "SELL" else "BUY"
            val openPrice = if ((consensus?.entryPriceMin ?: 0.0) > 0) consensus!!.entryPriceMin else snapshot.bidPrice
            val closePrice = snapshot.bidPrice
            val lot = if ((consensus?.recommendedLotCent ?: 0.0) > 0) consensus!!.recommendedLotCent else 0.02
            val setup = consensus?.setupType?.label ?: "Konsensus Dewan Kuantum"
            val score = consensus?.confluenceScore ?: 85

            val trade = TradeJournalEntity(
                ticket = (1000000..9999999).random().toLong(),
                timestampMillis = System.currentTimeMillis(),
                symbol = "XAUUSDc",
                type = type,
                openPrice = openPrice,
                closePrice = closePrice,
                volumeLot = lot,
                pnlUsc = pnlUsc,
                setupTag = setup,
                dewanConfluenceScore = score,
                emotionTag = "Disiplin & Sesuai Plan",
                notes = notes.ifBlank { "Rekomendasi resmi Dewan Kuantum Aegis." }
            )
            repository.addTrade(trade)
            _uiState.value = _uiState.value.copy(
                statusMessage = "⚡ Transaksi sinyal Aegis #${trade.ticket} berhasil dijurnal!"
            )
        }
    }

    fun deleteTrade(id: Long) {
        viewModelScope.launch {
            repository.deleteTradeById(id)
            _uiState.value = _uiState.value.copy(
                statusMessage = "🗑️ Catatan transaksi dihapus."
            )
        }
    }

    fun exportToCsv(): String {
        return repository.exportToCsv(_uiState.value.trades)
    }

    fun dismissStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }
}
