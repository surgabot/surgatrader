package com.surgatrader.feature.riskradar.domain.calculator

import com.google.common.truth.Truth.assertThat
import com.surgatrader.feature.riskradar.data.DefaultSymbols
import com.surgatrader.feature.riskradar.domain.model.LotCalculationParams
import com.surgatrader.feature.riskradar.domain.model.OrderDirection
import com.surgatrader.feature.riskradar.domain.model.SlInputMode
import com.surgatrader.feature.riskradar.domain.model.TpInputMode
import org.junit.Test

class LotSizeCalculatorTest {

    private val exnessCentSpec = DefaultSymbols.XAUUSD_EXNESS_CENT

    @Test
    fun `calculate lot size correctly for Exness Cent XAUUSDc with 1 percent risk`() {
        // Saldo: 50,000 USC ($500 USD)
        // Risiko: 1% = 500 USC ($5 USD)
        // SL: 500 points. Include spread: false -> 500 points
        // Point value 1 lot = 10 USC
        // Raw lot = 500 USC / (500 pts * 10 USC) = 500 / 5000 = 0.10 lot
        val params = LotCalculationParams(
            balance = 50000.0,
            riskPercent = 1.0,
            direction = OrderDirection.BUY,
            entryPrice = 4125.500,
            slMode = SlInputMode.POINTS,
            slInput = 500.0,
            tpMode = TpInputMode.RATIO,
            tpInput = 2.0,
            includeSpread = false
        )

        val result = LotSizeCalculator.calculate(params, exnessCentSpec)

        assertThat(result.recommendedLot).isEqualTo(0.10)
        assertThat(result.riskAmountUsc).isEqualTo(500.0)
        assertThat(result.riskAmountUsd).isEqualTo(5.0)
        assertThat(result.effectiveSlPoints).isEqualTo(500.0)
        assertThat(result.slPrice).isEqualTo(4125.000) // 4125.500 - (500 * 0.001) = 4125.000
        assertThat(result.tpPrice).isEqualTo(4126.500) // 4125.500 + (1000 * 0.001) = 4126.500
        assertThat(result.potentialProfitUsc).isEqualTo(1000.0)
        assertThat(result.riskRewardRatio).isEqualTo(2.0)
    }

    @Test
    fun `calculate lot size with spread included`() {
        // Saldo: 50,000 USC
        // SL: 475 points + spread 25 points = 500 points
        val params = LotCalculationParams(
            balance = 50000.0,
            riskPercent = 1.0,
            direction = OrderDirection.BUY,
            entryPrice = 4125.500,
            slMode = SlInputMode.POINTS,
            slInput = 475.0,
            tpMode = TpInputMode.RATIO,
            tpInput = 2.0,
            includeSpread = true // spread 25 pts
        )

        val result = LotSizeCalculator.calculate(params, exnessCentSpec)

        assertThat(result.effectiveSlPoints).isEqualTo(500.0)
        assertThat(result.recommendedLot).isEqualTo(0.10)
    }

    @Test
    fun `calculate lot size with price SL mode`() {
        // Entry: 4125.500, SL price: 4120.500 -> diff 5.000 = 5000 points
        // includeSpread: false
        val params = LotCalculationParams(
            balance = 50000.0,
            riskPercent = 1.0,
            direction = OrderDirection.BUY,
            entryPrice = 4125.500,
            slMode = SlInputMode.PRICE,
            slInput = 4120.500,
            tpMode = TpInputMode.POINTS,
            tpInput = 10000.0,
            includeSpread = false
        )

        val result = LotSizeCalculator.calculate(params, exnessCentSpec)

        assertThat(result.effectiveSlPoints).isEqualTo(5000.0)
        // Raw lot = 500 USC / (5000 pts * 10 USC) = 0.01 lot
        assertThat(result.recommendedLot).isEqualTo(0.01)
        assertThat(result.slPrice).isEqualTo(4120.500)
    }

    @Test
    fun `clamping to min lot when calculated lot is below broker minimum`() {
        // Saldo kecil: 1,000 USC ($10 USD)
        // Risiko 1% = 10 USC
        // SL: 500 points. Raw lot = 10 / 5000 = 0.002 -> Clamped to minLot (0.01)
        val params = LotCalculationParams(
            balance = 1000.0,
            riskPercent = 1.0,
            direction = OrderDirection.BUY,
            entryPrice = 4125.500,
            slMode = SlInputMode.POINTS,
            slInput = 500.0,
            includeSpread = false
        )

        val result = LotSizeCalculator.calculate(params, exnessCentSpec)

        assertThat(result.recommendedLot).isEqualTo(0.01)
        assertThat(result.warningMessage).isNotNull()
        assertThat(result.warningMessage).contains("lebih kecil dari batas minimum")
    }

    @Test
    fun `margin requirement calculation is accurate for 1 to 2000 leverage`() {
        // Lot: 0.10, contractSize: 100, price: 4125.500, leverage: 2000
        // Margin USD = (0.10 * 100 * 4125.500) / 2000 = 41255 / 2000 = $20.6275 USD
        // Margin USC = 2062.75 USC
        val params = LotCalculationParams(
            balance = 50000.0,
            riskPercent = 1.0,
            direction = OrderDirection.BUY,
            entryPrice = 4125.500,
            slMode = SlInputMode.POINTS,
            slInput = 500.0,
            includeSpread = false
        )

        val result = LotSizeCalculator.calculate(params, exnessCentSpec)

        assertThat(result.requiredMarginUsd).isWithin(0.1).of(20.63)
        assertThat(result.requiredMarginUsc).isWithin(10.0).of(2062.75)
    }
}
