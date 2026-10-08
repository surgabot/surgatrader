package com.surgatrader.feature.dashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.feature.riskradar.data.SymbolRepository
import com.surgatrader.feature.riskradar.domain.calculator.RiskScoreCalculator
import com.surgatrader.feature.riskradar.domain.model.RiskAssessment
import com.surgatrader.feature.riskradar.domain.model.RiskRadarInput
import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val balanceUsc: Double = 50000.0,
    val dailyPnlUsc: Double = 1250.0,
    val weeklyPnlUsc: Double = 3450.0,
    val openTradesCount: Int = 2,
    val riskAssessment: RiskAssessment = RiskScoreCalculator.assessRisk(RiskRadarInput()),
    val activeSymbol: SymbolSpecification? = null,
    val roadmapProgressPercent: Int = 42,
    val nextEconomicEvent: String = "US Non-Farm Payrolls (NFP) - 19:30 WIB"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val symbolRepository: SymbolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            symbolRepository.getDefaultSymbol().collect { spec ->
                _uiState.value = _uiState.value.copy(activeSymbol = spec)
            }
        }
    }
}
