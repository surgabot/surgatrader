package com.surgatrader.feature.aura.domain.council

import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.core.util.DateTimeUtils
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.aura.domain.model.MarketCandle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

object ChronosAnalyzer {

    /**
     * Menganalisis momentum waktu, RSI(14), MACD(12,26,9), sesi pasar,
     * dan deteksi breakout rentang sesi Asia.
     */
    fun analyze(
        currentPrice: Double,
        candles: List<MarketCandle>
    ): CouncilMemberReport {
        val activeSession = DateTimeUtils.getActiveTradingSession()
        val closes = candles.map { it.close }

        val rsi = calculateRsi(closes, 14)
        val (macdLine, signalLine, macdHist) = calculateMacd(closes)

        // Hitung estimasi range sesi Asia dari candle awal
        val asiaCandles = if (candles.size >= 8) candles.take(8) else candles
        val asiaHigh = asiaCandles.maxOfOrNull { it.high } ?: (currentPrice + 3.0)
        val asiaLow = asiaCandles.minOfOrNull { it.low } ?: (currentPrice - 3.0)

        val isAsiaBreakoutUp = currentPrice > asiaHigh
        val isAsiaBreakoutDown = currentPrice < asiaLow

        // Tentukan bias momentum
        val (bias, confidence) = when {
            rsi in 55.0..75.0 && macdHist > 0.05 -> {
                val conf = (70 + (macdHist * 50).toInt() + (if (isAsiaBreakoutUp) 10 else 0)).coerceIn(65, 95)
                CouncilBias.BULLISH to conf
            }
            rsi in 25.0..45.0 && macdHist < -0.05 -> {
                val conf = (70 + (abs(macdHist) * 50).toInt() + (if (isAsiaBreakoutDown) 10 else 0)).coerceIn(65, 95)
                CouncilBias.BEARISH to conf
            }
            rsi > 75.0 -> {
                CouncilBias.NEUTRAL to 60 // Overbought warning
            }
            rsi < 25.0 -> {
                CouncilBias.NEUTRAL to 60 // Oversold warning
            }
            else -> {
                CouncilBias.NEUTRAL to 50
            }
        }

        val importance = when {
            isAsiaBreakoutUp || isAsiaBreakoutDown -> ImportanceLevel.HIGH
            rsi > 75.0 || rsi < 25.0 -> ImportanceLevel.HIGH
            else -> ImportanceLevel.MEDIUM
        }

        val formattedPrice = CurrencyFormatter.formatPrice(currentPrice, 3)
        val formattedAsiaHigh = CurrencyFormatter.formatPrice(asiaHigh, 3)
        val formattedAsiaLow = CurrencyFormatter.formatPrice(asiaLow, 3)
        val formattedRsi = String.format(Locale.US, "%.1f", rsi)
        val formattedMacd = String.format(Locale.US, "%.2f", macdHist)

        val keyLevels = listOf(
            KeyLevel("Asia High", asiaHigh),
            KeyLevel("Asia Low", asiaLow),
            KeyLevel("RSI(14)", rsi),
            KeyLevel("MACD Line", macdLine),
            KeyLevel("Signal Line", signalLine),
            KeyLevel("MACD Hist", macdHist)
        )

        val headline = when {
            isAsiaBreakoutUp -> "Breakout Sesi Asia: Tekanan Beli Menembus $formattedAsiaHigh USC"
            isAsiaBreakoutDown -> "Breakdown Sesi Asia: Tekanan Jual Menembus $formattedAsiaLow USC"
            bias == CouncilBias.BULLISH -> "Momentum Bullish Terkonfirmasi: RSI $formattedRsi & MACD Positif"
            bias == CouncilBias.BEARISH -> "Momentum Bearish Terkonfirmasi: RSI $formattedRsi & MACD Negatif"
            else -> "Momentum Netral: RSI $formattedRsi Berada di Zona Tengah"
        }

        val detailedReason = buildString {
            append("Harga saat ini berada di level $formattedPrice USC. ")
            append("Sesi pasar aktif: $activeSession. ")
            append("RSI 14 tercatat pada angka $formattedRsi. ")
            append("Histogram MACD berada pada level $formattedMacd. ")
            append("Rentang high-low sesi Asia berada di $formattedAsiaLow hingga $formattedAsiaHigh USC. ")
            if (isAsiaBreakoutUp) append("Terjadi penembusan ke atas pada level high Asia. ")
            if (isAsiaBreakoutDown) append("Terjadi penembusan ke bawah pada level low Asia. ")
        }

        val spokenNarration = when {
            isAsiaBreakoutUp -> {
                "Chronos melaporkan momentum waktu pada $activeSession. Terdeteksi penembusan breakout ke atas batas sesi Asia di $formattedAsiaHigh USC. Indikator RSI empat belas di level $formattedRsi dan histogram MACD positif. Momentum beli sangat dominan."
            }
            isAsiaBreakoutDown -> {
                "Chronos melaporkan momentum waktu pada $activeSession. Terdeteksi penembusan breakdown ke bawah batas sesi Asia di $formattedAsiaLow USC. Indikator RSI empat belas di level $formattedRsi dan histogram MACD negatif. Momentum jual sangat dominan."
            }
            bias == CouncilBias.BULLISH -> {
                "Chronos melaporkan momentum waktu pada $activeSession. Indikator RSI empat belas berada di level $formattedRsi dengan momentum dorongan bullish. Histogram MACD berada di wilayah positif. Sinyal momentum searah dengan bias dewan."
            }
            bias == CouncilBias.BEARISH -> {
                "Chronos melaporkan momentum waktu pada $activeSession. Indikator RSI empat belas berada di level $formattedRsi dengan tekanan bearish aktif. Histogram MACD berada di wilayah negatif. Sinyal momentum mendukung penurunan harga."
            }
            else -> {
                "Chronos melaporkan momentum waktu pada $activeSession. Indikator RSI empat belas berada di angka normal $formattedRsi. Range sesi Asia masih terjaga rapat antara $formattedAsiaLow dan $formattedAsiaHigh USC. Momentum saat ini netral."
            }
        }

        return CouncilMemberReport(
            entityId = "hft",
            entityName = "CHRONOS-HFT",
            role = "Waktu & Momentum",
            bias = bias,
            confidenceScore = confidence,
            importance = importance,
            keyLevels = keyLevels,
            headline = headline,
            detailedReason = detailedReason,
            spokenNarration = spokenNarration
        )
    }

    private fun calculateRsi(prices: List<Double>, period: Int = 14): Double {
        if (prices.size < 2) return 50.0

        val relevantPrices = if (prices.size > period + 1) prices.takeLast(period + 1) else prices
        var gains = 0.0
        var losses = 0.0

        for (i in 1 until relevantPrices.size) {
            val delta = relevantPrices[i] - relevantPrices[i - 1]
            if (delta >= 0) gains += delta else losses += abs(delta)
        }

        val count = max(1, relevantPrices.size - 1)
        val avgGain = gains / count
        val avgLoss = losses / count

        if (avgLoss == 0.0) return 100.0
        val rs = avgGain / avgLoss
        return (100.0 - (100.0 / (1.0 + rs))).coerceIn(0.0, 100.0)
    }

    private fun calculateMacd(prices: List<Double>): Triple<Double, Double, Double> {
        if (prices.size < 2) return Triple(0.0, 0.0, 0.0)
        val fastEma = calculateSimpleEma(prices, 12)
        val slowEma = calculateSimpleEma(prices, 26)
        val macdLine = fastEma - slowEma
        val signalLine = macdLine * 0.8 // Estimasi sinyal 9 periode
        val histogram = macdLine - signalLine
        return Triple(macdLine, signalLine, histogram)
    }

    private fun calculateSimpleEma(prices: List<Double>, period: Int): Double {
        val multiplier = 2.0 / (period + 1.0)
        var ema = prices.first()
        for (i in 1 until prices.size) {
            ema = (prices[i] * multiplier) + (ema * (1.0 - multiplier))
        }
        return ema
    }
}
