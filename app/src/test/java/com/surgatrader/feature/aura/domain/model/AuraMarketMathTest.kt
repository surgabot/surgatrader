package com.surgatrader.feature.aura.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AuraMarketMathTest {

    @Test
    fun `generateRealisticM5Candles produces specified number of candles with valid OHLC constraints`() {
        val candles = AuraMarketMath.generateRealisticM5Candles(latestClose = 4100.234, count = 16)
        assertThat(candles).hasSize(16)

        candles.forEach { candle ->
            // High must be greater than or equal to both open and close
            assertThat(candle.high).isAtLeast(candle.open)
            assertThat(candle.high).isAtLeast(candle.close)

            // Low must be less than or equal to both open and close
            assertThat(candle.low).isAtMost(candle.open)
            assertThat(candle.low).isAtMost(candle.close)

            // Total range must be positive
            assertThat(candle.totalRange).isGreaterThan(0.0)
            assertThat(candle.volume).isGreaterThan(0L)
        }

        // Last candle close should match the requested latestClose
        assertThat(candles.last().close).isEqualTo(4100.234)
    }

    @Test
    fun `calculateAtr correctly computes Wilder True Range average`() {
        val candles = listOf(
            MarketCandle(time = 1000L, open = 4100.0, high = 4102.0, low = 4099.0, close = 4101.0),
            MarketCandle(time = 2000L, open = 4101.0, high = 4104.0, low = 4100.0, close = 4103.0), // TR: max(4, 3, 1) = 4.0
            MarketCandle(time = 3000L, open = 4103.0, high = 4105.0, low = 4101.0, close = 4102.0)  // TR: max(4, 2, 2) = 4.0
        )

        val atr = AuraMarketMath.calculateAtr(candles, period = 14)
        assertThat(atr).isEqualTo(4.0)
    }

    @Test
    fun `computeRotationMultiplier scales within safe rendering bounds`() {
        val normalMultiplier = AuraMarketMath.computeRotationMultiplier(atr = 1.250, baselineAtr = 1.250)
        assertThat(normalMultiplier).isEqualTo(1.0f)

        // Extremely high volatility should be clamped at 2.5f
        val extremeHighMultiplier = AuraMarketMath.computeRotationMultiplier(atr = 50.0, baselineAtr = 1.250)
        assertThat(extremeHighMultiplier).isEqualTo(2.5f)

        // Extremely low volatility should be clamped at 0.6f
        val extremeLowMultiplier = AuraMarketMath.computeRotationMultiplier(atr = 0.01, baselineAtr = 1.250)
        assertThat(extremeLowMultiplier).isEqualTo(0.6f)
    }

    @Test
    fun `determineConsensusBias identifies strong bullish and bearish trends`() {
        val bullishCandles = (1..10).map { i ->
            MarketCandle(
                time = i * 300_000L,
                open = 4100.0 + i,
                high = 4101.0 + i,
                low = 4099.5 + i,
                close = 4100.8 + i
            )
        }
        val bullishBias = AuraMarketMath.determineConsensusBias(bullishCandles)
        assertThat(bullishBias).isEqualTo(CouncilBias.BULLISH)

        val bearishCandles = (1..10).map { i ->
            MarketCandle(
                time = i * 300_000L,
                open = 4150.0 - i,
                high = 4151.0 - i,
                low = 4148.5 - i,
                close = 4149.0 - i
            )
        }
        val bearishBias = AuraMarketMath.determineConsensusBias(bearishCandles)
        assertThat(bearishBias).isEqualTo(CouncilBias.BEARISH)
    }
}
