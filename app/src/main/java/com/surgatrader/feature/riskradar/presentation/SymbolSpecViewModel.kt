package com.surgatrader.feature.riskradar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surgatrader.feature.riskradar.data.DefaultSymbols
import com.surgatrader.feature.riskradar.data.SymbolRepository
import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SymbolSpecUiState(
    val currentSpec: SymbolSpecification = DefaultSymbols.XAUUSD_EXNESS_CENT,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SymbolSpecViewModel @Inject constructor(
    private val repository: SymbolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymbolSpecUiState())
    val uiState: StateFlow<SymbolSpecUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getDefaultSymbol().collect { spec ->
                _uiState.value = _uiState.value.copy(currentSpec = spec)
            }
        }
    }

    fun updateSpec(updated: SymbolSpecification) {
        _uiState.value = _uiState.value.copy(currentSpec = updated, isSaved = false)
    }

    fun saveSpec() {
        viewModelScope.launch {
            try {
                repository.saveSymbol(_uiState.value.currentSpec)
                _uiState.value = _uiState.value.copy(isSaved = true, errorMessage = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Gagal menyimpan: ${e.message}")
            }
        }
    }

    fun resetToExnessDefault() {
        viewModelScope.launch {
            val defaultSpec = DefaultSymbols.XAUUSD_EXNESS_CENT
            repository.saveSymbol(defaultSpec)
            _uiState.value = _uiState.value.copy(currentSpec = defaultSpec, isSaved = true)
        }
    }
}
