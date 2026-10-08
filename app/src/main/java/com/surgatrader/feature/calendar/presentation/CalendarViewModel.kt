package com.surgatrader.feature.calendar.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

enum class NewsImpact {
    HIGH,
    MEDIUM,
    LOW
}

data class EconomicEventItem(
    val id: String,
    val currency: String,
    val timeWib: String,
    val eventName: String,
    val impact: NewsImpact,
    val forecast: String,
    val previous: String,
    val actual: String? = null
)

data class CalendarUiState(
    val selectedImpactFilter: NewsImpact? = null,
    val events: List<EconomicEventItem> = listOf(
        EconomicEventItem(
            id = "e1",
            currency = "USD",
            timeWib = "19:30 WIB",
            eventName = "Non-Farm Payrolls (NFP)",
            impact = NewsImpact.HIGH,
            forecast = "175K",
            previous = "142K"
        ),
        EconomicEventItem(
            id = "e2",
            currency = "USD",
            timeWib = "19:30 WIB",
            eventName = "Unemployment Rate",
            impact = NewsImpact.HIGH,
            forecast = "4.2%",
            previous = "4.2%"
        ),
        EconomicEventItem(
            id = "e3",
            currency = "USD",
            timeWib = "21:00 WIB",
            eventName = "ISM Services PMI",
            impact = NewsImpact.HIGH,
            forecast = "51.5",
            previous = "50.8"
        ),
        EconomicEventItem(
            id = "e4",
            currency = "EUR",
            timeWib = "15:00 WIB",
            eventName = "ECB Monetary Policy Statement",
            impact = NewsImpact.HIGH,
            forecast = "3.25%",
            previous = "3.50%"
        ),
        EconomicEventItem(
            id = "e5",
            currency = "USD",
            timeWib = "01:00 WIB",
            eventName = "FOMC Meeting Minutes",
            impact = NewsImpact.HIGH,
            forecast = "-",
            previous = "-"
        )
    )
)

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    fun filterImpact(impact: NewsImpact?) {
        _uiState.value = _uiState.value.copy(selectedImpactFilter = impact)
    }
}
