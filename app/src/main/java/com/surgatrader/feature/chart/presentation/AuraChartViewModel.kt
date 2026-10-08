package com.surgatrader.feature.chart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.feature.aura.domain.AuraMarketStateHolder
import com.surgatrader.feature.aura.domain.MarketSnapshot
import com.surgatrader.feature.aura.domain.council.CouncilConsensusResult
import com.surgatrader.feature.aura.domain.model.AuraMarketMath
import com.surgatrader.feature.aura.domain.model.MarketCandle
import com.surgatrader.feature.chart.domain.model.ChartIndicatorOverlay
import com.surgatrader.feature.chart.domain.model.ChartTimeframe
import com.surgatrader.feature.chart.domain.model.PriceKeyLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class ChartUiState(
    val candles: List<MarketCandle> = emptyList(),
    val ema20Values: List<Double?> = emptyList(),
    val ema50Values: List<Double?> = emptyList(),
    val ema200Values: List<Double?> = emptyList(),
    val pivotLevels: List<PriceKeyLevel> = emptyList(),
    val aegisLevels: List<PriceKeyLevel> = emptyList(),
    val selectedTimeframe: ChartTimeframe = ChartTimeframe.M5,
    val overlays: ChartIndicatorOverlay = ChartIndicatorOverlay(),
    val marketSnapshot: MarketSnapshot = MarketSnapshot(),
    val selectedCandle: MarketCandle? = null,
    val crosshairPrice: Double? = null
)

@HiltViewModel
class AuraChartViewModel @Inject constructor(
    private val marketStateHolder: AuraMarketStateHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChartUiState())
    val uiState: StateFlow<ChartUiState> = _uiState.asStateFlow()

    init {
        // Generate dataset 40 candle awal berbasis harga awal
        val initialPrice = 4100.234
        val baseCandles = generateHistoricalCandles(initialPrice, 40, ChartTimeframe.M5)
        updateChartCalculations(baseCandles, null)

        // Observasi tick pasar real-time dari AuraMarketStateHolder
        viewModelScope.launch {
            marketStateHolder.snapshot.collect { snapshot ->
                onMarketTickReceived(snapshot)
            }
        }
    }

    fun selectTimeframe(tf: ChartTimeframe) {
        val currentPrice = _uiState.value.marketSnapshot.bidPrice
        val candles = generateHistoricalCandles(currentPrice, 40, tf)
        _uiState.update { it.copy(selectedTimeframe = tf) }
        updateChartCalculations(candles, _uiState.value.marketSnapshot.latestConsensus)
    }

    fun toggleEma20() {
        val updated = _uiState.value.overlays.copy(showEma20 = !_uiState.value.overlays.showEma20)
        _uiState.update { it.copy(overlays = updated) }
    }

    fun toggleEma50() {
        val updated = _uiState.value.overlays.copy(showEma50 = !_uiState.value.overlays.showEma50)
        _uiState.update { it.copy(overlays = updated) }
    }

    fun toggleEma200() {
        val updated = _uiState.value.overlays.copy(showEma200 = !_uiState.value.overlays.showEma200)
        _uiState.update { it.copy(overlays = updated) }
    }

    fun togglePivots() {
        val updated = _uiState.value.overlays.copy(showPivots = !_uiState.value.overlays.showPivots)
        _uiState.update { it.copy(overlays = updated) }
    }

    fun toggleAegisLevels() {
        val updated = _uiState.value.overlays.copy(showAegisLevels = !_uiState.value.overlays.showAegisLevels)
        _uiState.update { it.copy(overlays = updated) }
    }

    fun onCandleSelected(candle: MarketCandle?, price: Double?) {
        _uiState.update { it.copy(selectedCandle = candle, crosshairPrice = price) }
    }

    private fun onMarketTickReceived(snapshot: MarketSnapshot) {
        val currentCandles = _uiState.value.candles.toMutableList()
        if (currentCandles.isEmpty()) return

        val lastIndex = currentCandles.lastIndex
        val last = currentCandles[lastIndex]
        val newPrice = snapshot.bidPrice

        val updatedLast = last.copy(
            close = newPrice,
            high = max(last.high, newPrice),
            low = min(last.low, newPrice)
        )
        currentCandles[lastIndex] = updatedLast

        updateChartCalculations(currentCandles, snapshot.latestConsensus)
        _uiState.update { it.copy(marketSnapshot = snapshot) }
    }

    private fun updateChartCalculations(
        candles: List<MarketCandle>,
        consensus: CouncilConsensusResult?
    ) {
        val closes = candles.map { it.close }
        val ema20 = calculateEma(closes, 20)
        val ema50 = calculateEma(closes, 50)
        val ema200 = calculateEma(closes, 200)

        // Hitung Classic Floor Pivot Points dari H1/Daily
        val highMax = candles.maxOfOrNull { it.high } ?: 4110.0
        val lowMin = candles.minOfOrNull { it.low } ?: 4090.0
        val closeLast = closes.lastOrNull() ?: 4100.0

        val pivot = (highMax + lowMin + closeLast) / 3.0
        val r1 = 2 * pivot - lowMin
        val s1 = 2 * pivot - highMax
        val r2 = pivot + (highMax - lowMin)
        val s2 = pivot - (highMax - lowMin)

        val pivotList = listOf(
            PriceKeyLevel("R2", r2, 0xFFFF3366),
            PriceKeyLevel("R1", r1, 0xFFFFB92D),
            PriceKeyLevel("P", pivot, 0xFFFFD700),
            PriceKeyLevel("S1", s1, 0xFF00FF88),
            PriceKeyLevel("S2", s2, 0xFF00F2FE)
        )

        // Level Aegis Konsensus Dewan
        val aegisList = mutableListOf<PriceKeyLevel>()
        if (consensus != null) {
            if (consensus.entryPriceMin > 0) aegisList.add(PriceKeyLevel("ENTRY", consensus.entryPriceMin, 0xFF00F2FE))
            if (consensus.stopLossPrice > 0) aegisList.add(PriceKeyLevel("SL", consensus.stopLossPrice, 0xFFFF3366))
            if (consensus.takeProfit1Price > 0) aegisList.add(PriceKeyLevel("TP1", consensus.takeProfit1Price, 0xFF00FF88))
            if (consensus.takeProfit2Price > 0) aegisList.add(PriceKeyLevel("TP2", consensus.takeProfit2Price, 0xFF00FFAA))
        }

        _uiState.update {
            it.copy(
                candles = candles,
                ema20Values = ema20,
                ema50Values = ema50,
                ema200Values = ema200,
                pivotLevels = pivotList,
                aegisLevels = aegisList
            )
        }
    }

    private fun calculateEma(prices: List<Double>, period: Int): List<Double?> {
        if (prices.isEmpty()) return emptyList()
        val result = MutableList<Double?>(prices.size) { null }
        if (prices.size < period) {
            // Gunakan SMA sederhana jika data belum mencapai periode penuh
            val avg = prices.average()
            prices.indices.forEach { result[it] = avg }
            return result
        }

        val multiplier = 2.0 / (period + 1)
        var ema = prices.take(period).average()
        result[period - 1] = ema

        for (i in period until prices.size) {
            ema = (prices[i] * multiplier) + (ema * (1.0 - multiplier))
            result[i] = ema
        }
        // Isi nilai awal untuk estetika garis halus
        for (i in 0 until period - 1) {
            result[i] = result[period - 1]
        }
        return result
    }

    private fun generateHistoricalCandles(currentPrice: Double, count: Int, tf: ChartTimeframe): List<MarketCandle> {
        val list = mutableListOf<MarketCandle>()
        val intervalMs = tf.seconds * 1000L
        val now = System.currentTimeMillis()
        var price = currentPrice - (count * 0.25)

        for (i in 0 until count) {
            val delta = (Math.sin(i * 0.35) * 1.5) + (Math.cos(i * 0.15) * 0.8)
            val open = price
            val close = open + delta
            val high = max(open, close) + abs(Math.sin(i.toDouble())) * 1.2
            val low = min(open, close) - abs(Math.cos(i.toDouble())) * 1.0
            price = close

            list.add(
                MarketCandle(
                    time = now - (count - i) * intervalMs,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = (100L..900L).random()
                )
            )
        }
        return list
    }
}
