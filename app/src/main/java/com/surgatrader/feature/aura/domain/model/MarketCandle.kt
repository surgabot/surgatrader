package com.surgatrader.feature.aura.domain.model

/**
 * Representasi Candle M5 riil untuk hologram kuantum dan chart
 * Akun Cent Exness (XAUUSDc) dengan presisi 3 desimal
 */
data class MarketCandle(
    val time: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Long = 100L
) {
    val isBullish: Boolean get() = close >= open
    val bodyHeight: Double get() = Math.abs(close - open)
    val totalRange: Double get() = Math.max(0.001, high - low)
    val upperWick: Double get() = high - Math.max(open, close)
    val lowerWick: Double get() = Math.min(open, close) - low
}

enum class CouncilBias(val label: String, val colorHex: Long) {
    BULLISH("BULLISH", 0xFF00FF88),
    BEARISH("BEARISH", 0xFFFF3366),
    NEUTRAL("NETRAL", 0xFFFFD700)
}
