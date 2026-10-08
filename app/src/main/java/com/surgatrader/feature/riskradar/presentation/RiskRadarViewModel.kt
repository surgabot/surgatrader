package com.surgatrader.feature.riskradar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val activeTab: Int = 0 // 0 = Kalkulator Lot, 1 = Radar Risiko & Korelasi
)

@HiltViewModel
class RiskRadarViewModel @Inject constructor(
    private val symbolRepository: SymbolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RiskRadarUiState())
    val uiState: StateFlow<RiskRadarUiState> = _uiState.asStateFlow()

    init {
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
    }

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(activeTab = tabIndex)
    }

    // --- Pembaruan Parameter Kalkulator Lot ---

    fun updateBalance(newBalance: Double) {
        val updated = _uiState.value.lotParams.copy(balance = newBalance)
        recalculateLot(updated)
    }

    fun updateRiskPercent(newRisk: Double) {
        val updated = _uiState.value.lotParams.copy(riskPercent = newRisk)
        // Sinkronkan juga ke skor risiko
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
