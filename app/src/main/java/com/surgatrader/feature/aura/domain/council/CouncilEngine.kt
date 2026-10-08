package com.surgatrader.feature.aura.domain.council

import com.surgatrader.core.util.DateTimeUtils
import com.surgatrader.feature.aura.domain.model.AuraMarketMath
import com.surgatrader.feature.aura.domain.model.AuraScriptStep
import com.surgatrader.feature.aura.domain.model.MarketCandle
import java.time.ZonedDateTime

object CouncilEngine {

    /**
     * Menjalankan siklus analisis kuantum komprehensif 5 dewan:
     * Oracle -> Chronos -> Gaia -> Athena -> Aegis
     * Menghasilkan laporan konsensus dan 5 naskah narasi suara dinamis.
     */
    fun evaluate(
        currentPrice: Double,
        equityUsc: Double,
        spreadPoints: Double,
        candles: List<MarketCandle>,
        now: ZonedDateTime = DateTimeUtils.getCurrentWibTime()
    ): Pair<CouncilConsensusResult, List<AuraScriptStep>> {
        val atr = AuraMarketMath.calculateAtr(candles, 14)

        // 1. Alpha-Oracle (Makro & Tren)
        val oracleReport = AlphaOracleAnalyzer.analyze(currentPrice, candles)

        // 2. Chronos-HFT (Waktu & Momentum)
        val chronosReport = ChronosAnalyzer.analyze(currentPrice, candles)

        // 3. Volatility-Gaia (Risiko & Volatilitas)
        val gaiaReport = VolatilityGaiaAnalyzer.analyze(currentPrice, spreadPoints, candles)

        // 4. Sentiment-Athena (Berita & Kalender)
        val athenaReport = SentimentAthenaAnalyzer.analyze(now)

        // 5. Aegis-Execution (Sintesis & Eksekusi)
        val (aegisReport, consensusResult) = AegisExecutionSynthesizer.synthesize(
            currentPrice = currentPrice,
            equityUsc = equityUsc,
            atr = atr,
            oracleReport = oracleReport,
            chronosReport = chronosReport,
            gaiaReport = gaiaReport,
            athenaReport = athenaReport
        )

        // Bangkitkan 5 fase naskah narasi dinamis
        val dynamicScript = listOf(
            AuraScriptStep(
                stage = "FASE 1: STRUKTUR MAKRO & TREN EMA",
                topic = oracleReport.headline,
                progress = 20,
                speakerId = "oracle",
                text = oracleReport.spokenNarration
            ),
            AuraScriptStep(
                stage = "FASE 2: WAKTU & MOMENTUM MIKROSTRUKTUR",
                topic = chronosReport.headline,
                progress = 40,
                speakerId = "hft",
                text = chronosReport.spokenNarration
            ),
            AuraScriptStep(
                stage = "FASE 3: MANAJEMEN RISIKO & VOLATILITAS ATR",
                topic = gaiaReport.headline,
                progress = 60,
                speakerId = "risk",
                text = gaiaReport.spokenNarration
            ),
            AuraScriptStep(
                stage = "FASE 4: SENTIMEN GLOBAL & KALENDER EKONOMI",
                topic = athenaReport.headline,
                progress = 80,
                speakerId = "sentiment",
                text = athenaReport.spokenNarration
            ),
            AuraScriptStep(
                stage = "FASE 5: SINTESIS KONSENSUS & KEPUTUSAN EKSEKUSI",
                topic = consensusResult.verdictTitle,
                progress = 100,
                speakerId = "execution",
                text = aegisReport.spokenNarration
            )
        )

        return consensusResult to dynamicScript
    }
}
