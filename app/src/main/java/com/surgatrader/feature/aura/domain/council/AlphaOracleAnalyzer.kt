package com.surgatrader.feature.aura.domain.council

import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.aura.domain.model.MarketCandle
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

object AlphaOracleAnalyzer {

    /**
     * Menganalisis struktur tren makro, EMA multi-periode, swing high/low,
     * dan pivot point harian dari data candle riil XAUUSDc.
     */
    fun analyze(
        currentPrice: Double,
        candles: List<MarketCandle>
    ): CouncilMemberReport {
        if (candles.isEmpty()) {
            return fallbackReport(currentPrice)
        }

        val closes = candles.map { it.close }
        val highs = candles.map { it.high }
        val lows = candles.map { it.low }

        val ema20 = calculateEma(closes, 20)
        val ema50 = calculateEma(closes, 50)
        val ema200 = calculateEma(closes, 200)

        val swingHigh = highs.maxOrNull() ?: (currentPrice + 5.0)
        val swingLow = lows.minOrNull() ?: (currentPrice - 5.0)

        // Hitung Classic Pivot Point Harian dari range candle
        val periodHigh = highs.maxOrNull() ?: currentPrice
        val periodLow = lows.minOrNull() ?: currentPrice
        val periodClose = closes.lastOrNull() ?: currentPrice

        val pivot = (periodHigh + periodLow + periodClose) / 3.0
        val r1 = (2.0 * pivot) - periodLow
        val s1 = (2.0 * pivot) - periodHigh
        val r2 = pivot + (periodHigh - periodLow)
        val s2 = pivot - (periodHigh - periodLow)

        // Tentukan bias makro berdasarkan relasi harga vs EMA
        val isAboveEma20 = currentPrice >= ema20
        val isAboveEma50 = currentPrice >= ema50
        val isEmaBullishStack = ema20 >= ema50

        val (bias, confidence) = when {
            isAboveEma20 && isAboveEma50 && isEmaBullishStack -> {
                val dist = (currentPrice - ema50) / ema50
                val conf = (70 + (dist * 1000).toInt()).coerceIn(70, 95)
                CouncilBias.BULLISH to conf
            }
            !isAboveEma20 && !isAboveEma50 && !isEmaBullishStack -> {
                val dist = (ema50 - currentPrice) / ema50
                val conf = (70 + (dist * 1000).toInt()).coerceIn(70, 95)
                CouncilBias.BEARISH to conf
            }
            else -> {
                CouncilBias.NEUTRAL to 55
            }
        }

        val importance = when {
            confidence >= 85 -> ImportanceLevel.HIGH
            confidence >= 65 -> ImportanceLevel.MEDIUM
            else -> ImportanceLevel.LOW
        }

        val formattedPrice = CurrencyFormatter.formatPrice(currentPrice, 3)
        val formattedEma20 = CurrencyFormatter.formatPrice(ema20, 3)
        val formattedEma50 = CurrencyFormatter.formatPrice(ema50, 3)
        val formattedPivot = CurrencyFormatter.formatPrice(pivot, 3)
        val formattedS1 = CurrencyFormatter.formatPrice(s1, 3)
        val formattedR1 = CurrencyFormatter.formatPrice(r1, 3)

        val keyLevels = listOf(
            KeyLevel("Pivot", pivot),
            KeyLevel("Support 1", s1),
            KeyLevel("Resistance 1", r1),
            KeyLevel("Support 2", s2),
            KeyLevel("Resistance 2", r2),
            KeyLevel("EMA 20", ema20),
            KeyLevel("EMA 50", ema50),
            KeyLevel("EMA 200", ema200),
            KeyLevel("Swing High", swingHigh),
            KeyLevel("Swing Low", swingLow)
        )

        val headline = when (bias) {
            CouncilBias.BULLISH -> "Struktur Tren Bullish: Harga Bertengger di Atas EMA 20 & 50"
            CouncilBias.BEARISH -> "Struktur Tren Bearish: Tekanan Jual di Bawah EMA 20 & 50"
            CouncilBias.NEUTRAL -> "Konsolidasi Tren: Harga Berada di Dalam Range Pivot"
        }

        val detailedReason = buildString {
            append("Harga saat ini berada di level $formattedPrice USC. ")
            append("EMA 20 di $formattedEma20 dan EMA 50 di $formattedEma50. ")
            append("Pivot harian terhitung di $formattedPivot USC dengan batas S1 di $formattedS1 USC dan R1 di $formattedR1 USC. ")
            append("Struktur swing terbentang antara low $formattedS1 dan high $formattedR1.")
        }

        val spokenNarration = when (bias) {
            CouncilBias.BULLISH -> {
                "Alpha-Oracle melaporkan struktur tren emas XAUUSDc. Harga saat ini berada di $formattedPrice USC, berada di atas EMA dua puluh dan EMA lima puluh. Pivot harian terhitung di $formattedPivot USC dengan batas support satu di $formattedS1 USC dan resisten satu di $formattedR1 USC. Struktur makro mengonfirmasi bias bullish dengan tingkat keyakinan $confidence persen."
            }
            CouncilBias.BEARISH -> {
                "Alpha-Oracle melaporkan struktur tren emas XAUUSDc. Harga saat ini berada di $formattedPrice USC, tertekan di bawah EMA dua puluh dan EMA lima puluh. Pivot harian berada di $formattedPivot USC dengan batas support satu di $formattedS1 USC dan resisten satu di $formattedR1 USC. Struktur makro mengonfirmasi bias bearish dengan tingkat keyakinan $confidence persen."
            }
            CouncilBias.NEUTRAL -> {
                "Alpha-Oracle melaporkan struktur tren emas XAUUSDc. Harga saat ini berada di $formattedPrice USC, bergerak mendatar di sekitar garis pivot harian $formattedPivot USC. Indikator EMA menunjukkan kompresi harga. Bias tren netral dan dewan menyarankan menunggu konfirmasi arah."
            }
        }

        return CouncilMemberReport(
            entityId = "oracle",
            entityName = "ALPHA-ORACLE",
            role = "Makro & Tren",
            bias = bias,
            confidenceScore = confidence,
            importance = importance,
            keyLevels = keyLevels,
            headline = headline,
            detailedReason = detailedReason,
            spokenNarration = spokenNarration
        )
    }

    private fun calculateEma(prices: List<Double>, period: Int): Double {
        if (prices.isEmpty()) return 4100.0
        val multiplier = 2.0 / (period + 1.0)
        var ema = prices.first()
        for (i in 1 until prices.size) {
            ema = (prices[i] * multiplier) + (ema * (1.0 - multiplier))
        }
        return ema
    }

    private fun fallbackReport(currentPrice: Double): CouncilMemberReport {
        val formattedPrice = CurrencyFormatter.formatPrice(currentPrice, 3)
        return CouncilMemberReport(
            entityId = "oracle",
            entityName = "ALPHA-ORACLE",
            role = "Makro & Tren",
            bias = CouncilBias.NEUTRAL,
            confidenceScore = 50,
            importance = ImportanceLevel.LOW,
            keyLevels = emptyList(),
            headline = "Inisialisasi Data Makro",
            detailedReason = "Menunggu ketersediaan data historis untuk kalkulasi EMA di sekitar $formattedPrice USC.",
            spokenNarration = "Alpha-Oracle sedang menginisialisasi parameter makro emas di $formattedPrice USC. Menunggu konfirmasi data harga."
        )
    }
}
