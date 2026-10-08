package com.surgatrader.feature.riskradar.domain.calculator

import com.google.common.truth.Truth.assertThat
import com.surgatrader.core.util.DangerLevel
import com.surgatrader.core.util.DangerousPeriod
import com.surgatrader.feature.riskradar.domain.model.RiskLevel
import com.surgatrader.feature.riskradar.domain.model.RiskRadarInput
import org.junit.Test

class RiskScoreCalculatorTest {

    @Test
    fun `healthy account state produces SAFE risk level below 35`() {
        val input = RiskRadarInput(
            currentDrawdownPercent = 0.8,
            maxDailyDrawdownLimit = 5.0,
            riskPerTradePercent = 1.0,
            openPositionsCount = 1,
            totalFloatingLots = 0.01,
            marginLevelPercent = 2500.0,
            pointsToStopOut = 1500.0,
            correlationScore = 10.0,
            currentAtr = 22.0,
            normalAtr = 25.0
        )

        val assessment = RiskScoreCalculator.assessRisk(input, dangerousPeriod = null)

        assertThat(assessment.level).isEqualTo(RiskLevel.SAFE)
        assertThat(assessment.overallScore).isAtMost(35.0)
        assertThat(assessment.isDailyLossLimitBreached).isFalse()
        assertThat(assessment.dailyLossWarning).isNull()
    }

    @Test
    fun `critical danger detected when points to stop out is less than 200 pts`() {
        val input = RiskRadarInput(
            currentDrawdownPercent = 4.8,
            maxDailyDrawdownLimit = 5.0,
            riskPerTradePercent = 3.5, // Risiko trade tinggi
            openPositionsCount = 7,    // Stacking banyak posisi
            totalFloatingLots = 0.25,
            marginLevelPercent = 135.0,
            pointsToStopOut = 180.0,   // Sangat dekat Stop Out!
            correlationScore = 85.0,
            currentAtr = 42.0,
            normalAtr = 25.0
        )

        val assessment = RiskScoreCalculator.assessRisk(input)

        assertThat(assessment.level).isEqualTo(RiskLevel.CRITICAL)
        assertThat(assessment.overallScore).isGreaterThan(70.0)
        assertThat(assessment.recommendations.any { it.contains("Stop Out") || it.contains("posisi") }).isTrue()
    }

    @Test
    fun `daily loss limit breach triggers alert warning`() {
        val input = RiskRadarInput(
            currentDrawdownPercent = 5.5, // Melebihi limit 5.0%
            maxDailyDrawdownLimit = 5.0
        )

        val assessment = RiskScoreCalculator.assessRisk(input)

        assertThat(assessment.isDailyLossLimitBreached).isTrue()
        assertThat(assessment.dailyLossWarning).isNotNull()
        assertThat(assessment.dailyLossWarning).contains("BATAS DAILY LOSS TERLAMPAUI")
        assertThat(assessment.recommendations.any { it.contains("Hentikan aktivitas") }).isTrue()
    }

    @Test
    fun `active dangerous period elevates volatility risk factor`() {
        val input = RiskRadarInput(
            currentAtr = 35.0,
            normalAtr = 25.0
        )

        val dummyDangerousPeriod = DangerousPeriod(
            title = "Rilis Data US NFP",
            description = "Non-Farm Payrolls",
            startHourWib = 19,
            startMinuteWib = 30,
            endHourWib = 21,
            endMinuteWib = 0,
            level = DangerLevel.EXTREME
        )

        val assessmentWithNews = RiskScoreCalculator.assessRisk(input, dangerousPeriod = dummyDangerousPeriod)
        val assessmentWithoutNews = RiskScoreCalculator.assessRisk(input, dangerousPeriod = null)

        val volScoreWithNews = assessmentWithNews.factors.first { it.key == "VOL" }.score
        val volScoreWithoutNews = assessmentWithoutNews.factors.first { it.key == "VOL" }.score

        assertThat(volScoreWithNews).isGreaterThan(volScoreWithoutNews)
        assertThat(assessmentWithNews.activeDangerousPeriod).isNotNull()
    }
}
