package com.surgatrader.feature.riskradar.domain.model

import androidx.compose.ui.graphics.Color
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.core.util.DangerousPeriod

enum class RiskLevel(val label: String, val color: Color) {
    SAFE("AMAN", SafeEmerald),
    WARNING("WASPADA", WarnAmber),
    CRITICAL("BAHAYA / KRITIS", DangerRuby)
}

data class RiskFactor(
    val key: String,
    val name: String,
    val score: Double, // 0 - 100
    val maxScore: Double = 100.0,
    val valueLabel: String,
    val description: String
)

data class RiskRadarInput(
    val currentDrawdownPercent: Double = 2.4, // DD saat ini (%)
    val maxDailyDrawdownLimit: Double = 5.0,  // Batas toleransi harian (%)
    val riskPerTradePercent: Double = 1.0,     // % risiko trade saat ini/terakhir
    val openPositionsCount: Int = 2,           // Jumlah posisi floating
    val totalFloatingLots: Double = 0.04,      // Total volume lot floating
    val marginLevelPercent: Double = 1250.0,   // Margin level (%)
    val pointsToStopOut: Double = 850.0,       // Jarak points ke Stop Out (MC)
    val correlationScore: Double = 20.0,       // Tingkat korelasi antar posisi (0-100)
    val currentAtr: Double = 32.5,             // ATR harian XAUUSD (misal $32.5)
    val normalAtr: Double = 25.0               // ATR rata-rata normal XAUUSD
)

data class RiskAssessment(
    val overallScore: Double,                  // Skor gabungan 0 - 100
    val level: RiskLevel,
    val statusTitle: String,
    val factors: List<RiskFactor>,             // 6 faktor untuk Spider Chart
    val activeDangerousPeriod: DangerousPeriod?,
    val isDailyLossLimitBreached: Boolean,
    val dailyLossWarning: String?,
    val recommendations: List<String>
)
