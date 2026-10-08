package com.surgatrader.feature.riskradar.domain.calculator

import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.riskradar.domain.model.LotCalculationParams
import com.surgatrader.feature.riskradar.domain.model.LotCalculationResult
import com.surgatrader.feature.riskradar.domain.model.OrderDirection
import com.surgatrader.feature.riskradar.domain.model.SlInputMode
import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification
import com.surgatrader.feature.riskradar.domain.model.TpInputMode
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

object LotSizeCalculator {

    fun calculate(
        params: LotCalculationParams,
        spec: SymbolSpecification
    ): LotCalculationResult {
        if (params.balance <= 0.0) {
            return emptyResult(spec)
        }

        // 1. Hitung Nominal Risiko (USC dan USD)
        val riskAmountUsc = when (params.slMode) {
            SlInputMode.MONEY -> params.slInput
            else -> params.balance * (params.riskPercent / 100.0)
        }

        // 2. Hitung Jarak Stop Loss dalam Points
        val baseSlPoints = when (params.slMode) {
            SlInputMode.POINTS -> max(1.0, params.slInput)
            SlInputMode.PRICE -> {
                val diff = abs(params.entryPrice - params.slInput)
                max(1.0, diff / spec.point)
            }
            SlInputMode.MONEY -> {
                // Jika input SL berupa uang, gunakan default 500 points atau jarak proporsional
                500.0
            }
        }

        val effectiveSlPoints = if (params.includeSpread) {
            baseSlPoints + spec.spreadPoints
        } else {
            baseSlPoints
        }

        // 3. Hitung Level Harga Stop Loss
        val slPrice = when (params.direction) {
            OrderDirection.BUY -> params.entryPrice - (effectiveSlPoints * spec.point)
            OrderDirection.SELL -> params.entryPrice + (effectiveSlPoints * spec.point)
        }

        // 4. Hitung Nilai Point per 1 Lot (dalam USC)
        val pointVal1Lot = if (spec.pointValuePerLot > 0) spec.pointValuePerLot else 10.0

        // 5. Hitung Raw Lot
        val rawLot = if (effectiveSlPoints > 0 && pointVal1Lot > 0) {
            riskAmountUsc / (effectiveSlPoints * pointVal1Lot)
        } else {
            spec.minLot
        }

        // 6. Normalisasi Lot berdasarkan lotStep, minLot, dan maxLot
        val step = if (spec.lotStep > 0) spec.lotStep else 0.01
        val steppedLot = floor(rawLot / step) * step
        val roundedLot = BigDecimal(steppedLot)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
        val recommendedLot = roundedLot.coerceIn(spec.minLot, spec.maxLot)

        // 7. Hitung Take Profit
        val (tpPoints, tpPrice) = calculateTakeProfit(params, spec, effectiveSlPoints)

        // 8. Hitung Potensi Profit & Rasio R:R
        val potentialProfitUsc = recommendedLot * tpPoints * pointVal1Lot
        val potentialProfitUsd = potentialProfitUsc / 100.0
        val actualRiskUsc = recommendedLot * effectiveSlPoints * pointVal1Lot
        val actualRiskUsd = actualRiskUsc / 100.0

        val rrRatio = if (effectiveSlPoints > 0) {
            BigDecimal(tpPoints / effectiveSlPoints)
                .setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        } else {
            0.0
        }

        // 9. Hitung Estimasi Margin yang Dibutuhkan
        // Margin = (Lot * Ukuran Kontrak * Harga) / Leverage
        val marginUsd = if (spec.leverage > 0) {
            (recommendedLot * spec.contractSize * params.entryPrice) / spec.leverage
        } else {
            0.0
        }
        val marginUsc = marginUsd * 100.0

        // 10. Hitung Ketahanan Jarak ke Stop Out (Points)
        // Rumus Stop Out Exness Cent (0% stop out level): Saldo / (Lot * Nilai Point per Lot)
        val stopOutBufferPoints = if (recommendedLot > 0 && pointVal1Lot > 0) {
            BigDecimal(params.balance / (recommendedLot * pointVal1Lot))
                .setScale(1, RoundingMode.HALF_UP)
                .toDouble()
        } else {
            0.0
        }

        // 11. Deteksi Peringatan Risiko
        val warningMessage = when {
            rawLot < spec.minLot -> {
                "Perhatian: Lot kalkulasi (${round(rawLot, 4)}) lebih kecil dari batas minimum broker (${spec.minLot}). Risiko aktual akan naik menjadi ${CurrencyFormatter.formatUsc(actualRiskUsc)}."
            }
            rawLot > spec.maxLot -> {
                "Perhatian: Lot kalkulasi (${round(rawLot, 2)}) melebihi batas lot maksimal broker (${spec.maxLot}). Dibatasi ke ${spec.maxLot}."
            }
            marginUsc > params.balance * 0.75 -> {
                "⚠️ PERINGATAN MARGIN: Kebutuhan margin (${CurrencyFormatter.formatUsc(marginUsc)}) memakan > 75% saldo Anda. Rentan terkena Stop Out!"
            }
            stopOutBufferPoints in 1.0..1200.0 -> {
                "⚠️ PERINGATAN KETAHANAN: Jarak ke Stop Out hanya ${stopOutBufferPoints.toInt()} points. Rentan terlikuidasi saat fluktuasi tajam!"
            }
            else -> null
        }

        return LotCalculationResult(
            recommendedLot = recommendedLot,
            rawLot = round(rawLot, 4),
            riskAmountUsc = actualRiskUsc,
            riskAmountUsd = actualRiskUsd,
            effectiveSlPoints = effectiveSlPoints,
            slPrice = round(slPrice, spec.digits),
            tpPrice = round(tpPrice, spec.digits),
            potentialProfitUsc = potentialProfitUsc,
            potentialProfitUsd = potentialProfitUsd,
            riskRewardRatio = rrRatio,
            requiredMarginUsc = marginUsc,
            requiredMarginUsd = marginUsd,
            pointValuePerLot = pointVal1Lot,
            stopOutBufferPoints = stopOutBufferPoints,
            warningMessage = warningMessage
        )
    }

    private fun calculateTakeProfit(
        params: LotCalculationParams,
        spec: SymbolSpecification,
        slPoints: Double
    ): Pair<Double, Double> {
        return when (params.tpMode) {
            TpInputMode.NONE -> Pair(0.0, 0.0)
            TpInputMode.RATIO -> {
                val ratio = max(0.1, params.tpInput)
                val tpPts = slPoints * ratio
                val price = when (params.direction) {
                    OrderDirection.BUY -> params.entryPrice + (tpPts * spec.point)
                    OrderDirection.SELL -> params.entryPrice - (tpPts * spec.point)
                }
                Pair(tpPts, price)
            }
            TpInputMode.POINTS -> {
                val tpPts = max(1.0, params.tpInput)
                val price = when (params.direction) {
                    OrderDirection.BUY -> params.entryPrice + (tpPts * spec.point)
                    OrderDirection.SELL -> params.entryPrice - (tpPts * spec.point)
                }
                Pair(tpPts, price)
            }
            TpInputMode.PRICE -> {
                val tpPrice = params.tpInput
                val diff = abs(tpPrice - params.entryPrice)
                val tpPts = diff / spec.point
                Pair(tpPts, tpPrice)
            }
        }
    }

    private fun round(value: Double, decimals: Int): Double {
        return if (value.isNaN() || value.isInfinite()) 0.0
        else BigDecimal(value).setScale(decimals, RoundingMode.HALF_UP).toDouble()
    }

    private fun emptyResult(spec: SymbolSpecification): LotCalculationResult {
        return LotCalculationResult(
            recommendedLot = spec.minLot,
            rawLot = 0.0,
            riskAmountUsc = 0.0,
            riskAmountUsd = 0.0,
            effectiveSlPoints = 0.0,
            slPrice = 0.0,
            tpPrice = 0.0,
            potentialProfitUsc = 0.0,
            potentialProfitUsd = 0.0,
            riskRewardRatio = 0.0,
            requiredMarginUsc = 0.0,
            requiredMarginUsd = 0.0,
            pointValuePerLot = spec.pointValuePerLot,
            warningMessage = "Saldo harus lebih dari 0"
        )
    }
}
