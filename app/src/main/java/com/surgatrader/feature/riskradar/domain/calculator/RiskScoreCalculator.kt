package com.surgatrader.feature.riskradar.domain.calculator

import com.surgatrader.core.util.DangerLevel
import com.surgatrader.core.util.DateTimeUtils
import com.surgatrader.feature.riskradar.domain.model.RiskAssessment
import com.surgatrader.feature.riskradar.domain.model.RiskFactor
import com.surgatrader.feature.riskradar.domain.model.RiskLevel
import com.surgatrader.feature.riskradar.domain.model.RiskRadarInput
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max
import kotlin.math.min

object RiskScoreCalculator {

    fun assessRisk(
        input: RiskRadarInput,
        dangerousPeriod: com.surgatrader.core.util.DangerousPeriod? = DateTimeUtils.getActiveDangerousPeriod()
    ): RiskAssessment {

        // 1. Faktor Drawdown (Bobot 25%)
        val ddRatio = if (input.maxDailyDrawdownLimit > 0) {
            input.currentDrawdownPercent / input.maxDailyDrawdownLimit
        } else {
            0.0
        }
        val ddScore = min(100.0, ddRatio * 85.0).coerceIn(5.0, 100.0)

        // 2. Faktor Jarak Stop Out & Margin Level (Bobot 25%)
        val marginScore = calculateMarginRiskScore(input.pointsToStopOut, input.marginLevelPercent)

        // 3. Faktor Risiko Per Trade (Bobot 15%)
        val tradeRiskScore = when {
            input.riskPerTradePercent <= 1.0 -> 15.0
            input.riskPerTradePercent <= 2.0 -> 35.0
            input.riskPerTradePercent <= 3.0 -> 65.0
            input.riskPerTradePercent <= 5.0 -> 85.0
            else -> 100.0
        }

        // 4. Faktor Exposure & Posisi Terbuka (Bobot 15%)
        val exposureScore = when {
            input.openPositionsCount <= 1 && input.totalFloatingLots <= 0.02 -> 10.0
            input.openPositionsCount <= 3 && input.totalFloatingLots <= 0.05 -> 30.0
            input.openPositionsCount <= 6 && input.totalFloatingLots <= 0.15 -> 65.0
            else -> 90.0
        }

        // 5. Faktor Korelasi Portofolio (Bobot 10%)
        val correlationScore = input.correlationScore.coerceIn(0.0, 100.0)

        // 6. Faktor Volatilitas Emas (ATR) & Jam Rawan (Bobot 10%)
        var volScore = 20.0
        val atrRatio = if (input.normalAtr > 0) input.currentAtr / input.normalAtr else 1.0
        if (atrRatio > 1.25) volScore += 25.0
        if (atrRatio > 1.6) volScore += 25.0

        if (dangerousPeriod != null) {
            volScore += when (dangerousPeriod.level) {
                DangerLevel.MODERATE -> 15.0
                DangerLevel.HIGH -> 25.0
                DangerLevel.EXTREME -> 35.0
            }
        }
        volScore = volScore.coerceIn(5.0, 100.0)

        // Weighted Overall Score:
        val overall = (
            ddScore * 0.25 +
            marginScore * 0.25 +
            tradeRiskScore * 0.15 +
            exposureScore * 0.15 +
            correlationScore * 0.10 +
            volScore * 0.10
        )
        val roundedScore = BigDecimal(overall).setScale(1, RoundingMode.HALF_UP).toDouble()

        val isBreached = input.currentDrawdownPercent >= input.maxDailyDrawdownLimit

        val level = when {
            marginScore >= 90.0 || isBreached || roundedScore > 70.0 -> RiskLevel.CRITICAL
            roundedScore <= 35.0 -> RiskLevel.SAFE
            else -> RiskLevel.WARNING
        }

        val statusTitle = when (level) {
            RiskLevel.SAFE -> "Kondisi Akun Stabil & Aman"
            RiskLevel.WARNING -> "Waspada! Risiko Mulai Meningkat"
            RiskLevel.CRITICAL -> "🚨 BAHAYA KRITIS! Kurangi Posisi Segera"
        }

        val dailyLossWarning = if (isBreached) {
            "🚨 BATAS DAILY LOSS TERLAMPAUI! Drawdown saat ini (${input.currentDrawdownPercent}%) telah melewati limit Anda (${input.maxDailyDrawdownLimit}%). Disarankan STOP trading hari ini!"
        } else if (input.currentDrawdownPercent >= input.maxDailyDrawdownLimit * 0.75) {
            "⚠️ PERINGATAN: Drawdown (${input.currentDrawdownPercent}%) mendekati batas maksimal harian (${input.maxDailyDrawdownLimit}%)."
        } else {
            null
        }

        val factors = listOf(
            RiskFactor(
                key = "DD",
                name = "Drawdown",
                score = round(ddScore),
                valueLabel = "${input.currentDrawdownPercent}% / Max ${input.maxDailyDrawdownLimit}%",
                description = "Penurunan ekuitas terhadap saldo"
            ),
            RiskFactor(
                key = "SO",
                name = "Jarak Stop Out",
                score = round(marginScore),
                valueLabel = "${input.pointsToStopOut.toInt()} pts (ML: ${input.marginLevelPercent.toInt()}%)",
                description = "Ketahanan margin sebelum auto-cut broker"
            ),
            RiskFactor(
                key = "RPT",
                name = "Risiko/Trade",
                score = round(tradeRiskScore),
                valueLabel = "${input.riskPerTradePercent}%",
                description = "Besaran risiko toleransi per transaksi"
            ),
            RiskFactor(
                key = "EXP",
                name = "Exposure & Posisi",
                score = round(exposureScore),
                valueLabel = "${input.openPositionsCount} pos (${input.totalFloatingLots} lot)",
                description = "Akumulasi volume floating market"
            ),
            RiskFactor(
                key = "CORR",
                name = "Korelasi Pair",
                score = round(correlationScore),
                valueLabel = "${correlationScore.toInt()}/100",
                description = "Tingkat tumpang tindih risiko antar pair"
            ),
            RiskFactor(
                key = "VOL",
                name = "Volatilitas ATR",
                score = round(volScore),
                valueLabel = "ATR $${input.currentAtr} / $${input.normalAtr}",
                description = if (dangerousPeriod != null) "Jam Rawan: ${dangerousPeriod.title}" else "Fluktuasi harian XAUUSD normal"
            )
        )

        val recommendations = mutableListOf<String>()
        if (isBreached) {
            recommendations.add("Hentikan aktivitas order baru hari ini untuk melindungi sisa modal.")
        }
        if (marginScore >= 70.0) {
            recommendations.add("Pertimbangkan cut loss sebagian atau tutup posisi terbesar untuk menambah jarak ke Stop Out.")
        }
        if (tradeRiskScore >= 70.0) {
            recommendations.add("Turunkan risiko per trade ke angka ideal 1% - 2% dari saldo akun.")
        }
        if (dangerousPeriod != null) {
            recommendations.add("Waspada jam rawan (${dangerousPeriod.title}). Hindari pasang pending order terlalu dekat.")
        }
        if (recommendations.isEmpty()) {
            recommendations.add("Manajemen risiko sangat baik. Pertahankan disiplin trading plan Anda.")
        }

        return RiskAssessment(
            overallScore = roundedScore,
            level = level,
            statusTitle = statusTitle,
            factors = factors,
            activeDangerousPeriod = dangerousPeriod,
            isDailyLossLimitBreached = isBreached,
            dailyLossWarning = dailyLossWarning,
            recommendations = recommendations
        )
    }

    private fun calculateMarginRiskScore(pointsToStopOut: Double, marginLevel: Double): Double {
        return when {
            pointsToStopOut <= 200 || (marginLevel in 1.0..150.0) -> 95.0
            pointsToStopOut <= 400 || (marginLevel in 150.0..300.0) -> 75.0
            pointsToStopOut <= 700 || (marginLevel in 300.0..600.0) -> 45.0
            pointsToStopOut <= 1200 || (marginLevel in 600.0..1000.0) -> 20.0
            else -> 10.0
        }
    }

    private fun round(value: Double): Double {
        return BigDecimal(value).setScale(1, RoundingMode.HALF_UP).toDouble()
    }
}
