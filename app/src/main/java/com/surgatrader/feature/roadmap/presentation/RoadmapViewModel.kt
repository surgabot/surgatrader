package com.surgatrader.feature.roadmap.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class RoadmapChecklistItem(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

data class RoadmapModuleData(
    val id: String,
    val tier: String, // "Pemula", "Menengah", "Mahir"
    val title: String,
    val description: String,
    val items: List<RoadmapChecklistItem>
)

data class RoadmapUiState(
    val modules: List<RoadmapModuleData> = listOf(
        RoadmapModuleData(
            id = "m1",
            tier = "Pemula",
            title = "Dasar Mekanisme Pasar & MT5",
            description = "Memahami jenis akun Cent (USC), spread, slippage, margin, dan leverage.",
            items = listOf(
                RoadmapChecklistItem("c1", "Pahami perhitungan 100 USC = 1 USD & contract size", true),
                RoadmapChecklistItem("c2", "Membaca spesifikasi simbol di MT5 (Market Watch)", true),
                RoadmapChecklistItem("c3", "Memahami perbedaan order market, limit, dan stop", false)
            )
        ),
        RoadmapModuleData(
            id = "m2",
            tier = "Menengah",
            title = "Manajemen Risiko & Position Sizing",
            description = "Kalkulasi ukuran lot ketat, batas drawdown harian, dan rasio R:R minimal 1:2.",
            items = listOf(
                RoadmapChecklistItem("c4", "Atur batas risiko per trade maksimal 1% - 2%", true),
                RoadmapChecklistItem("c5", "Gunakan kalkulator lot otomatis sebelum membuka order", false),
                RoadmapChecklistItem("c6", "Patuhi daily drawdown limit dan pantau jam rawan WIB", false)
            )
        ),
        RoadmapModuleData(
            id = "m3",
            tier = "Menengah",
            title = "Analisis Teknikal & Price Action Emas",
            description = "Support/Resistance kunci, likuiditas sesi London & New York, dan fakeouts.",
            items = listOf(
                RoadmapChecklistItem("c7", "Identifikasi struktur tren Higher High & Lower Low", false),
                RoadmapChecklistItem("c8", "Waspadai pembukaan pasar London 14:00 WIB & US 19:30 WIB", false),
                RoadmapChecklistItem("c9", "Penerapan stop loss dinamis berbasis ATR harian", false)
            )
        ),
        RoadmapModuleData(
            id = "m4",
            tier = "Mahir",
            title = "Psikologi Trading & Jurnal Disiplin",
            description = "Mencatat emosi FOMO/Revenge trade dan evaluasi expectancy trading bulanan.",
            items = listOf(
                RoadmapChecklistItem("c10", "Tulis jurnal lengkap untuk setiap transaksi", false),
                RoadmapChecklistItem("c11", "Evaluasi mingguan kurva ekuitas & win rate", false)
            )
        ),
        RoadmapModuleData(
            id = "m5",
            tier = "Mahir",
            title = "Strategi Otomatis / Expert Advisor (EA)",
            description = "Penerapan EA RiskRadar HUD, trailing basket breakeven, dan hedging 1:1.",
            items = listOf(
                RoadmapChecklistItem("c12", "Setup RiskRadar HUD v2.60 di MetaTrader 5 VPS", false),
                RoadmapChecklistItem("c13", "Uji coba fast bulk close & stop out protection", false)
            )
        )
    ),
    val personalDrawdownTargetPercent: Double = 5.0,
    val personalWinRateTargetPercent: Double = 55.0
)

@HiltViewModel
class RoadmapViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(RoadmapUiState())
    val uiState: StateFlow<RoadmapUiState> = _uiState.asStateFlow()

    fun toggleChecklistItem(moduleId: String, itemId: String) {
        val currentModules = _uiState.value.modules.map { mod ->
            if (mod.id == moduleId) {
                val updatedItems = mod.items.map { item ->
                    if (item.id == itemId) item.copy(isCompleted = !item.isCompleted) else item
                }
                mod.copy(items = updatedItems)
            } else {
                mod
            }
        }
        _uiState.value = _uiState.value.copy(modules = currentModules)
    }
}
