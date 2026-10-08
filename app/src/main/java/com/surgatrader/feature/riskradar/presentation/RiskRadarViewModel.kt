package com.surgatrader.feature.riskradar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.aura.domain.AuraMarketStateHolder
import com.surgatrader.feature.aura.domain.MarketSnapshot
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.riskradar.data.DefaultSymbols
import com.surgatrader.feature.riskradar.data.SymbolRepository
import com.surgatrader.feature.riskradar.domain.calculator.LotSizeCalculator
import com.surgatrader.feature.riskradar.domain.calculator.RiskScoreCalculator
import com.surgatrader.feature.riskradar.domain.model.LotCalculationParams
import com.surgatrader.feature.riskradar.domain.model.LotCalculationResult
import com.surgatrader.feature.riskradar.domain.model.OrderDirection
import com.surgatrader.feature.riskradar.domain.model.RiskAssessment
import com.surgatrader.feature.riskradar.domain.model.RiskRadarInput
import com.surgatrader.feature.riskradar.domain.model.SlInputMode
import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification
import com.surgatrader.feature.riskradar.domain.model.TpInputMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RiskRadarUiState(
    val symbolSpec: SymbolSpecification = DefaultSymbols.XAUUSD_EXNESS_CENT,
    val lotParams: LotCalculationParams = LotCalculationParams(),
    val lotResult: LotCalculationResult = LotSizeCalculator.calculate(LotCalculationParams(), DefaultSymbols.XAUUSD_EXNESS_CENT),
    val radarInput: RiskRadarInput = RiskRadarInput(),
    val assessment: RiskAssessment = RiskScoreCalculator.assessRisk(RiskRadarInput()),
    val activeTab: Int = 0, // 0 = Kalkulator Lot Cent, 1 = Radar Risiko 5D/6D
    val marketSnapshot: MarketSnapshot = MarketSnapshot(),
    val statusMessage: String? = null
)

@HiltViewModel
class RiskRadarViewModel @Inject constructor(
    private val symbolRepository: SymbolRepository,
    private val marketStateHolder: AuraMarketStateHolder
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiskRadarUiState())
    val uiState: StateFlow<RiskRadarUiState> = _uiState.asStateFlow()

    init {
        // Observasi spesifikasi simbol
        viewModelScope.launch {
            symbolRepository.getDefaultSymbol().collect { spec ->
                val currentLotParams = _uiState.value.lotParams
                val newLotResult = LotSizeCalculator.calculate(currentLotParams, spec)
                _uiState.value = _uiState.value.copy(
                    symbolSpec = spec,
                    lotResult = newLotResult
                )
            }
        }

        // Observasi data pasar terkini dari modul Aura
        viewModelScope.launch {
            marketStateHolder.snapshot.collect { snapshot ->
                _uiState.value = _uiState.value.copy(marketSnapshot = snapshot)
            }
        }
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    fun dismissStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    /**
     * Sinkronkan otomatis saldo, harga entry, dan volatilitas dari tick pasar live/demo saat ini
     */
    fun syncWithMarket() {
        val snapshot = marketStateHolder.snapshot.value
        val updatedParams = _uiState.value.lotParams.copy(
            balance = if (snapshot.balanceUsc > 0) snapshot.balanceUsc else _uiState.value.lotParams.balance,
            entryPrice = if (snapshot.bidPrice > 0) snapshot.bidPrice else _uiState.value.lotParams.entryPrice
        )
        val updatedRadar = _uiState.value.radarInput.copy(
            marginLevelPercent = if (snapshot.marginLevel > 0) snapshot.marginLevel else _uiState.value.radarInput.marginLevelPercent,
            currentAtr = if (snapshot.atr14 > 0) snapshot.atr14 * 20.0 else _uiState.value.radarInput.currentAtr
        )

        recalculateAll(updatedParams, updatedRadar)
        _uiState.value = _uiState.value.copy(
            statusMessage = "✅ Tersinkronisasi dengan Pasar: Saldo ${CurrencyFormatter.formatUsc(updatedParams.balance)} • Harga Entry ${updatedParams.entryPrice}"
        )
    }

    /**
     * Terapkan rekomendasi resmi dari entitas dewan Aegis (Sintesis Konsensus Dewan Kuantum)
     */
    fun importAegisRecommendation() {
        val consensus = marketStateHolder.snapshot.value.latestConsensus
        if (consensus == null) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "⚠️ Belum ada rekomendasi sinyal aktif dari Dewan Kuantum."
            )
            return
        }

        val dir = if (consensus.consensusBias == CouncilBias.BEARISH) OrderDirection.SELL else OrderDirection.BUY
        val entry = if (consensus.entryPriceMin > 0) consensus.entryPriceMin else _uiState.value.marketSnapshot.bidPrice
        val sl = consensus.stopLossPrice
        val tp = if (consensus.takeProfit1Price > 0) consensus.takeProfit1Price else entry

        val updatedParams = _uiState.value.lotParams.copy(
            direction = dir,
            entryPrice = entry,
            slMode = SlInputMode.PRICE,
            slInput = sl,
            tpMode = TpInputMode.PRICE,
            tpInput = tp
        )

        recalculateLot(updatedParams)
        _uiState.value = _uiState.value.copy(
            statusMessage = "⚡ Rekomendasi Aegis Diterapkan: ${consensus.setupType.label} • Skor ${consensus.confluenceScore}/100 • Lot Rekomendasi ${consensus.recommendedLotCent}"
        )
    }

    // --- Pembaruan Parameter Kalkulator Lot ---

    fun updateBalance(newBalance: Double) {
        val updated = _uiState.value.lotParams.copy(balance = newBalance)
        recalculateLot(updated)
    }

    fun updateRiskPercent(newRisk: Double) {
        val updated = _uiState.value.lotParams.copy(riskPercent = newRisk)
        // Sinkronkan juga ke input radar risiko
        val updatedRadar = _uiState.value.radarInput.copy(riskPerTradePercent = newRisk)
        recalculateAll(updated, updatedRadar)
    }

    fun updateDirection(newDirection: OrderDirection) {
        val updated = _uiState.value.lotParams.copy(direction = newDirection)
        recalculateLot(updated)
    }

    fun updateEntryPrice(newPrice: Double) {
        val updated = _uiState.value.lotParams.copy(entryPrice = newPrice)
        recalculateLot(updated)
    }

    fun updateSlMode(newMode: SlInputMode) {
        val updated = _uiState.value.lotParams.copy(slMode = newMode)
        recalculateLot(updated)
    }

    fun updateSlInput(newSl: Double) {
        val updated = _uiState.value.lotParams.copy(slInput = newSl)
        recalculateLot(updated)
    }

    fun updateTpMode(newMode: TpInputMode) {
        val updated = _uiState.value.lotParams.copy(tpMode = newMode)
        recalculateLot(updated)
    }

    fun updateTpInput(newTp: Double) {
        val updated = _uiState.value.lotParams.copy(tpInput = newTp)
        recalculateLot(updated)
    }

    fun toggleIncludeSpread(include: Boolean) {
        val updated = _uiState.value.lotParams.copy(includeSpread = include)
        recalculateLot(updated)
    }

    // --- Pembaruan Parameter Radar Risiko ---

    fun updateDrawdown(currentDd: Double, maxDdLimit: Double = _uiState.value.radarInput.maxDailyDrawdownLimit) {
        val updated = _uiState.value.radarInput.copy(
            currentDrawdownPercent = currentDd,
            maxDailyDrawdownLimit = maxDdLimit
        )
        val assessment = RiskScoreCalculator.assessRisk(updated)
        _uiState.value = _uiState.value.copy(radarInput = updated, assessment = assessment)
    }

    fun updatePositions(count: Int, totalLots: Double) {
        val updated = _uiState.value.radarInput.copy(
            openPositionsCount = count,
            totalFloatingLots = totalLots
        )
        val assessment = RiskScoreCalculator.assessRisk(updated)
        _uiState.value = _uiState.value.copy(radarInput = updated, assessment = assessment)
    }

    fun updateMarginStatus(marginLevel: Double, pointsToSo: Double) {
        val updated = _uiState.value.radarInput.copy(
            marginLevelPercent = marginLevel,
            pointsToStopOut = pointsToSo
        )
        val assessment = RiskScoreCalculator.assessRisk(updated)
        _uiState.value = _uiState.value.copy(radarInput = updated, assessment = assessment)
    }

    fun updateAtr(currentAtr: Double, normalAtr: Double = 25.0) {
        val updated = _uiState.value.radarInput.copy(
            currentAtr = currentAtr,
            normalAtr = normalAtr
        )
        val assessment = RiskScoreCalculator.assessRisk(updated)
        _uiState.value = _uiState.value.copy(radarInput = updated, assessment = assessment)
    }

    private fun recalculateLot(params: LotCalculationParams) {
        val result = LotSizeCalculator.calculate(params, _uiState.value.symbolSpec)
        _uiState.value = _uiState.value.copy(lotParams = params, lotResult = result)
    }

    private fun recalculateAll(params: LotCalculationParams, radar: RiskRadarInput) {
        val result = LotSizeCalculator.calculate(params, _uiState.value.symbolSpec)
        val assessment = RiskScoreCalculator.assessRisk(radar)
        _uiState.value = _uiState.value.copy(
            lotParams = params,
            lotResult = result,
            radarInput = radar,
            assessment = assessment
        )
    }
}
