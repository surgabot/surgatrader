package com.surgatrader.feature.chart.domain.model

enum class ChartTimeframe(val label: String, val seconds: Long) {
    M1("M1", 60L),
    M5("M5", 300L),
    M15("M15", 900L),
    H1("H1", 3600L),
    H4("H4", 14400L)
}

data class ChartIndicatorOverlay(
    val showEma20: Boolean = true,
    val showEma50: Boolean = true,
    val showEma200: Boolean = true,
    val showPivots: Boolean = true,
    val showAegisLevels: Boolean = true
)

data class PriceKeyLevel(
    val label: String,
    val price: Double,
    val colorHex: Long
)
