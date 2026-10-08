package com.surgatrader.feature.aura.domain.council

import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.aura.domain.model.CouncilBias
import java.util.Locale

object AegisExecutionSynthesizer {

    /**
     * Mensintesis 4 laporan anggota dewan sebelumnya untuk menentukan konsensus bulat,
     * mendeteksi bentrokan arah, menghitung skor konfluensi, zona entri, SL, TP, dan lot cent.
     */
    fun synthesize(
        currentPrice: Double,
        equityUsc: Double,
        atr: Double,
        oracleReport: CouncilMemberReport,
        chronosReport: CouncilMemberReport,
        gaiaReport: CouncilMemberReport,
        athenaReport: CouncilMemberReport
    ): Pair<CouncilMemberReport, CouncilConsensusResult> {
        val timestamp = System.currentTimeMillis()

        // 1. Cek Veto Zona Bahaya Berita Athena
        val isNewsDanger = athenaReport.importance == ImportanceLevel.CRITICAL
        // 2. Cek Veto Volatilitas / Spread Gaia
        val isSpreadSpike = gaiaReport.importance == ImportanceLevel.CRITICAL

        // 3. Cek Keselarasan Tren (Oracle) vs Momentum (Chronos)
        val isDirectionAligned = (oracleReport.bias == chronosReport.bias) &&
                (oracleReport.bias != CouncilBias.NEUTRAL)

        val isConsensusAgreed = !isNewsDanger && !isSpreadSpike && isDirectionAligned

        val consensusBias = if (isConsensusAgreed) oracleReport.bias else CouncilBias.NEUTRAL

        // Confluence Score calculation
        val rawScore = (oracleReport.confidenceScore * 0.35 +
                chronosReport.confidenceScore * 0.35 +
                gaiaReport.confidenceScore * 0.15 +
                athenaReport.confidenceScore * 0.15).toInt()

        val confluenceScore = if (isConsensusAgreed) rawScore.coerceIn(65, 98) else (rawScore / 2).coerceIn(20, 50)

        // Setup Type
        val setupType = when {
            !isConsensusAgreed -> SetupType.WAIT_NO_CONSENSUS
            chronosReport.headline.contains("Breakout", ignoreCase = true) -> SetupType.BREAKOUT
            chronosReport.headline.contains("Breakdown", ignoreCase = true) -> SetupType.BREAKOUT
            oracleReport.detailedReason.contains("Swing", ignoreCase = true) -> SetupType.PULLBACK
            else -> SetupType.PULLBACK
        }

        // SL & TP calculations berbasis 1.5x ATR dari Gaia
        val slDistanceUsc = (atr * 1.5).coerceAtLeast(1.0)
        val tp1DistanceUsc = slDistanceUsc * 1.5
        val tp2DistanceUsc = slDistanceUsc * 2.5

        val isBullish = consensusBias == CouncilBias.BULLISH

        val entryMin = currentPrice - (atr * 0.15)
        val entryMax = currentPrice + (atr * 0.15)

        val stopLossPrice = if (isBullish) currentPrice - slDistanceUsc else currentPrice + slDistanceUsc
        val takeProfit1Price = if (isBullish) currentPrice + tp1DistanceUsc else currentPrice - tp1DistanceUsc
        val takeProfit2Price = if (isBullish) currentPrice + tp2DistanceUsc else currentPrice - tp2DistanceUsc

        // Lot cent aman (misal 0.01 lot cent untuk setiap 50,000 USC saldo)
        val recommendedLotCent = if (isConsensusAgreed) {
            val calculatedLot = (equityUsc / 100000.0 * 0.02).coerceIn(0.01, 0.10)
            String.format(Locale.US, "%.2f", calculatedLot).toDouble()
        } else {
            0.00
        }

        val formattedPrice = CurrencyFormatter.formatPrice(currentPrice, 3)
        val formattedEntryMin = CurrencyFormatter.formatPrice(entryMin, 3)
        val formattedEntryMax = CurrencyFormatter.formatPrice(entryMax, 3)
        val formattedSl = CurrencyFormatter.formatPrice(stopLossPrice, 3)
        val formattedTp1 = CurrencyFormatter.formatPrice(takeProfit1Price, 3)
        val formattedTp2 = CurrencyFormatter.formatPrice(takeProfit2Price, 3)

        val verdictTitle = when {
            isNewsDanger -> "DEWAN TIDAK SEPAKAT • ZONA BAHAYA BERITA"
            isSpreadSpike -> "DEWAN TIDAK SEPAKAT • WASPADA SPREAD MELEBAR"
            !isDirectionAligned -> "DEWAN TIDAK SEPAKAT • SINYAL TREN & MOMENTUM BENTROK"
            isBullish -> "KONSENSUS BULAT: ${setupType.label} [BUY]"
            else -> "KONSENSUS BULAT: ${setupType.label} [SELL]"
        }

        val verdictSummary = buildString {
            if (isConsensusAgreed) {
                append("Dewan 5 Entitas sepakat bulat dengan bias $consensusBias (Konfluensi $confluenceScore%). ")
                append("Zona entri terukur: $formattedEntryMin - $formattedEntryMax USC. ")
                append("Stop Loss protektif di $formattedSl USC. ")
                append("Take Profit 1 di $formattedTp1 USC (R:R 1:1.5) dan Take Profit 2 di $formattedTp2 USC (R:R 1:2.5). ")
                append("Alokasi lot cent Exness: $recommendedLotCent Lot Cent.")
            } else {
                append("Dewan tidak mencapai mufakat. ")
                if (isNewsDanger) append("Alasan: Athena mendeteksi zona bahaya rilis berita berdampak tinggi. ")
                else if (isSpreadSpike) append("Alasan: Gaia mendeteksi pelebaran spread di atas batas toleransi. ")
                else append("Alasan: Terjadi divergensi sinyal antara tren makro Oracle (${oracleReport.bias}) dan momentum Chronos (${chronosReport.bias}). ")
                append("Perintah eksekusi: DITOLAK. Mode menunggu konfirmasi baru.")
            }
        }

        val spokenNarration = if (isConsensusAgreed) {
            val directionText = if (isBullish) "posisi beli" else "posisi jual"
            "Aegis-Execution mengumumkan putusan dewan kuantum. Seluruh entitas bersepakat bulat mengeksekusi $directionText emas XAUUSDc dengan tipe setup ${setupType.label}. Skor konfluensi mencapai $confluenceScore persen. Zona entri berada di $formattedPrice USC. Batas stop loss dikunci di $formattedSl USC. Target keuntungan pertama di $formattedTp1 USC dan target kedua di $formattedTp2 USC. Alokasi risiko ditetapkan sebesar $recommendedLotCent lot cent."
        } else {
            "Aegis-Execution menyampaikan putusan dewan kuantum. Eksekusi dibatalkan karena tidak tercapai konsensus bulat. $verdictTitle. Seluruh pesanan baru ditahan demi perlindungan modal."
        }

        val keyLevels = listOf(
            KeyLevel("Zona Entri", currentPrice),
            KeyLevel("Stop Loss", stopLossPrice),
            KeyLevel("Take Profit 1", takeProfit1Price),
            KeyLevel("Take Profit 2", takeProfit2Price),
            KeyLevel("Lot Cent", recommendedLotCent)
        )

        val aegisReport = CouncilMemberReport(
            entityId = "execution",
            entityName = "AEGIS-EXECUTION",
            role = "Aturan Eksekusi",
            bias = consensusBias,
            confidenceScore = confluenceScore,
            importance = if (isConsensusAgreed) ImportanceLevel.CRITICAL else ImportanceLevel.HIGH,
            keyLevels = keyLevels,
            headline = verdictTitle,
            detailedReason = verdictSummary,
            spokenNarration = spokenNarration
        )

        val memberMap = mapOf(
            "oracle" to oracleReport,
            "hft" to chronosReport,
            "risk" to gaiaReport,
            "sentiment" to athenaReport,
            "execution" to aegisReport
        )

        val consensusResult = CouncilConsensusResult(
            timestamp = timestamp,
            memberReports = memberMap,
            consensusBias = consensusBias,
            isConsensusAgreed = isConsensusAgreed,
            confluenceScore = confluenceScore,
            setupType = setupType,
            verdictTitle = verdictTitle,
            verdictSummary = verdictSummary,
            entryPriceMin = entryMin,
            entryPriceMax = entryMax,
            stopLossPrice = stopLossPrice,
            takeProfit1Price = takeProfit1Price,
            takeProfit2Price = takeProfit2Price,
            riskRewardRatio1 = 1.5,
            riskRewardRatio2 = 2.5,
            recommendedLotCent = recommendedLotCent
        )

        return aegisReport to consensusResult
    }
}
