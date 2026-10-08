package com.surgatrader.feature.aura.domain.model

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

object AuraMarketMath {

    /**
     * Menghitung Average True Range (ATR) dari daftar candle.
     * Formula institusional Wilder:
     * TR = max(High - Low, |High - Close_prev|, |Low - Close_prev|)
     */
    fun calculateAtr(candles: List<MarketCandle>, period: Int = 14): Double {
        if (candles.size < 2) return 1.250

        val trList = mutableListOf<Double>()
        for (i in 1 until candles.size) {
            val curr = candles[i]
            val prevClose = candles[i - 1].close
            val tr = max(
                curr.high - curr.low,
                max(abs(curr.high - prevClose), abs(curr.low - prevClose))
            )
            trList.add(tr)
        }

        val subset = if (trList.size > period) trList.takeLast(period) else trList
        return if (subset.isNotEmpty()) subset.average() else 1.250
    }

    /**
     * Menghubungkan volatilitas ATR(14) dengan kelajuan putaran cincin orbit hologram.
     * Baseline ATR = 1.250 pt -> multiplier 1.0x (normal)
     * Volatilitas tinggi -> putaran lebih cepat (hingga 2.5x)
     * Volatilitas rendah -> putaran lambat & tenang (minimal 0.6x)
     */
    fun computeRotationMultiplier(atr: Double, baselineAtr: Double = 1.250): Float {
        val factor = (atr / baselineAtr).toFloat()
        return factor.coerceIn(0.6f, 2.5f)
    }

    /**
     * Menghitung bias konsensus dewan kuantum berdasarkan struktur harga candle M5.
     * Menggunakan momentum close terkini dan moving average sederhana.
     */
    fun determineConsensusBias(candles: List<MarketCandle>): CouncilBias {
        if (candles.size < 5) return CouncilBias.NEUTRAL

        val closes = candles.map { it.close }
        val fastMa = closes.takeLast(5).average()
        val slowMa = closes.average()
        val latestClose = closes.last()
        val firstClose = closes.first()

        val netDelta = latestClose - firstClose
        val maSpread = fastMa - slowMa

        return when {
            netDelta > 0.40 && maSpread > 0.15 -> CouncilBias.BULLISH
            netDelta < -0.40 && maSpread < -0.15 -> CouncilBias.BEARISH
            else -> CouncilBias.NEUTRAL
        }
    }

    /**
     * Menghasilkan 16 candle M5 yang koheren, matematis, dan realistis
     * untuk mode simulasi DEMO berpusat pada harga acuan.
     */
    fun generateRealisticM5Candles(
        latestClose: Double = 4100.234,
        count: Int = 16
    ): List<MarketCandle> {
        val candles = mutableListOf<MarketCandle>()
        val intervalMs = 5 * 60 * 1000L // 5 menit per candle
        val now = System.currentTimeMillis()

        // Buat lintasan acak terarah (Brownian drift) ke arah latestClose
        var currentPrice = latestClose - (count * 0.12)
        val random = Random(42) // Seed stabil untuk reproduktibilitas awal

        for (i in 0 until count) {
            val candleTime = now - ((count - 1 - i) * intervalMs)
            val open = currentPrice
            val drift = (random.nextDouble() - 0.47) * 0.85
            val close = if (i == count - 1) latestClose else open + drift

            val upperWick = random.nextDouble() * 0.55 + 0.10
            val lowerWick = random.nextDouble() * 0.55 + 0.10

            val high = max(open, close) + upperWick
            val low = min(open, close) - lowerWick
            val volume = 120L + random.nextLong(280L)

            candles.add(
                MarketCandle(
                    time = candleTime,
                    open = open,
                    high = high,
                    low = low,
                    close = close,
                    volume = volume
                )
            )

            currentPrice = close
        }

        return candles
    }
}
