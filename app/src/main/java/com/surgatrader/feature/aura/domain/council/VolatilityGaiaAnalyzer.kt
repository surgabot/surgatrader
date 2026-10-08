package com.surgatrader.feature.aura.domain.council

import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.aura.domain.model.AuraMarketMath
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.aura.domain.model.MarketCandle
import java.util.Locale

object VolatilityGaiaAnalyzer {

    /**
     * Menganalisis volatilitas ATR(14), lebar spread aktual vs rata-rata,
     * peringatan pelebaran spread, dan rekomendasi jarak Stop Loss terukur.
     */
    fun analyze(
        currentPrice: Double,
        spreadPoints: Double,
        candles: List<MarketCandle>
    ): CouncilMemberReport {
        val atr = AuraMarketMath.calculateAtr(candles, 14)
        val normalAverageSpread = 100.0 // Rata-rata spread normal Exness Cent (100 pt / 0.10)
        val isSpreadElevated = spreadPoints > (normalAverageSpread * 1.5)

        // Rekomendasi jarak Stop Loss minimum berbasis 1.5x ATR
        val minSlDistanceUsc = atr * 1.5
        val minSlPoints = minSlDistanceUsc * 1000.0 // 1 point = 0.001 USC
        val conservativeSlPoints = (atr * 2.0) * 1000.0

        val (bias, confidence) = when {
            isSpreadElevated -> {
                // Volatilitas abnormal / spread tinggi -> proteksi modal, bias netral/waspada
                CouncilBias.NEUTRAL to 80
            }
            atr in 1.1..2.8 -> {
                // Volatilitas likuid dan ideal untuk trading
                CouncilBias.BULLISH to 85 // Mendukung keteraturan eksekusi
            }
            atr > 3.5 -> {
                // Volatilitas ekstrem
                CouncilBias.NEUTRAL to 65
            }
            else -> {
                CouncilBias.NEUTRAL to 60
            }
        }

        val importance = when {
            isSpreadElevated || atr > 3.5 -> ImportanceLevel.CRITICAL
            atr in 1.1..2.8 -> ImportanceLevel.MEDIUM
            else -> ImportanceLevel.LOW
        }

        val formattedAtr = String.format(Locale.US, "%.3f", atr)
        val formattedMinSlPts = String.format(Locale.US, "%.0f", minSlPoints)
        val formattedMinSlUsc = String.format(Locale.US, "%.3f", minSlDistanceUsc)
        val formattedConservativeSlPts = String.format(Locale.US, "%.0f", conservativeSlPoints)
        val formattedPrice = CurrencyFormatter.formatPrice(currentPrice, 3)

        val keyLevels = listOf(
            KeyLevel("ATR(14)", atr),
            KeyLevel("Jarak SL Minimal", minSlDistanceUsc),
            KeyLevel("SL Acuan Buy", currentPrice - minSlDistanceUsc),
            KeyLevel("SL Acuan Sell", currentPrice + minSlDistanceUsc),
            KeyLevel("Spread Aktual", spreadPoints)
        )

        val headline = when {
            isSpreadElevated -> "Peringatan Spread Melebar: ${spreadPoints.toInt()} PT (Di Atas Normal)"
            atr > 3.5 -> "Volatilitas Ekstrem Terdeteksi: ATR $formattedAtr USC"
            else -> "Kondisi Volatilitas Optimal: ATR $formattedAtr USC & Spread Normal"
        }

        val detailedReason = buildString {
            append("Harga saat ini berada di level $formattedPrice USC. ")
            append("Nilai ATR 14 saat ini adalah $formattedAtr USC. ")
            append("Spread pasar saat ini sebesar ${spreadPoints.toInt()} point. ")
            if (isSpreadElevated) {
                append("Waspada: Terjadi pelebaran spread melebihi rata-rata normal 100 point. ")
            } else {
                append("Likuiditas spread stabil di bawah toleransi maksimum. ")
            }
            append("Rekomendasi jarak Stop Loss aman adalah minimal $formattedMinSlPts point ($formattedMinSlUsc USC) ")
            append("dan batas konservatif di $formattedConservativeSlPts point guna menghindari false stop out.")
        }

        val spokenNarration = when {
            isSpreadElevated -> {
                "Volatility-Gaia memberikan peringatan risiko. Spread broker saat ini melebar menjadi ${spreadPoints.toInt()} point. Nilai ATR empat belas tercatat $formattedAtr USC. Disarankan membatasi eksekusi baru dan memasang jarak stop loss minimal $formattedMinSlPts point dari harga pasar."
            }
            atr > 3.5 -> {
                "Volatility-Gaia mengukur lonjakan volatilitas. Nilai ATR empat belas melonjak menjadi $formattedAtr USC. Pasar bergerak sangat agresif. Jarak stop loss minimal yang diwajibkan adalah $formattedMinSlPts point untuk menjaga ketahanan modal."
            }
            else -> {
                "Volatility-Gaia melaporkan metrik risiko modal. Volatilitas ATR empat belas berada di zona ideal $formattedAtr USC dengan spread terukur ${spreadPoints.toInt()} point. Rekomendasi jarak stop loss berbasis volatilitas adalah $formattedMinSlPts point. Profil risiko aman."
            }
        }

        return CouncilMemberReport(
            entityId = "risk",
            entityName = "VOLATILITY-GAIA",
            role = "Risiko & Volatilitas",
            bias = bias,
            confidenceScore = confidence,
            importance = importance,
            keyLevels = keyLevels,
            headline = headline,
            detailedReason = detailedReason,
            spokenNarration = spokenNarration
        )
    }
}
