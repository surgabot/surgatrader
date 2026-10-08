package com.surgatrader.feature.aura.domain.council

import com.google.common.truth.Truth.assertThat
import com.surgatrader.feature.aura.domain.model.AuraMarketMath
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.aura.domain.model.MarketCandle
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class CouncilEngineTest {

    private val sampleCandles = AuraMarketMath.generateRealisticM5Candles(latestClose = 4100.234, count = 16)

    @Test
    fun `AlphaOracleAnalyzer generates macro levels and Indonesian narration`() {
        val report = AlphaOracleAnalyzer.analyze(currentPrice = 4100.234, candles = sampleCandles)

        assertThat(report.entityId).isEqualTo("oracle")
        assertThat(report.entityName).isEqualTo("ALPHA-ORACLE")
        assertThat(report.keyLevels).isNotEmpty()
        assertThat(report.spokenNarration).contains("Alpha-Oracle")
        assertThat(report.spokenNarration).contains("4,100.234")
        assertThat(report.confidenceScore).isIn(0..100)
    }

    @Test
    fun `ChronosAnalyzer calculates RSI, MACD, and session momentum`() {
        val report = ChronosAnalyzer.analyze(currentPrice = 4100.234, candles = sampleCandles)

        assertThat(report.entityId).isEqualTo("hft")
        assertThat(report.entityName).isEqualTo("CHRONOS-HFT")
        assertThat(report.spokenNarration).contains("Chronos")
        assertThat(report.keyLevels.any { it.label == "RSI(14)" }).isTrue()
        assertThat(report.confidenceScore).isIn(0..100)
    }

    @Test
    fun `VolatilityGaiaAnalyzer calculates ATR-based SL recommendation and warns on elevated spread`() {
        // Test normal spread
        val normalReport = VolatilityGaiaAnalyzer.analyze(currentPrice = 4100.234, spreadPoints = 110.0, candles = sampleCandles)
        assertThat(normalReport.entityId).isEqualTo("risk")
        assertThat(normalReport.importance).isNotEqualTo(ImportanceLevel.CRITICAL)

        // Test abnormal spread spike
        val spikeReport = VolatilityGaiaAnalyzer.analyze(currentPrice = 4100.234, spreadPoints = 350.0, candles = sampleCandles)
        assertThat(spikeReport.importance).isEqualTo(ImportanceLevel.CRITICAL)
        assertThat(spikeReport.headline).contains("Peringatan Spread Melebar")
    }

    @Test
    fun `SentimentAthenaAnalyzer detects News Danger Zone during release window`() {
        val testNow = ZonedDateTime.of(2026, 10, 9, 19, 20, 0, 0, ZoneId.of("Asia/Jakarta"))
        val mockRepo = object : EconomicCalendarRepository {
            override fun getUpcomingEvents(now: ZonedDateTime): List<EconomicEvent> {
                return listOf(
                    EconomicEvent(
                        id = "test-nfp",
                        title = "US Non-Farm Payrolls (NFP)",
                        currency = "USD",
                        impact = EventImpact.HIGH,
                        scheduledTimeWib = testNow.plusMinutes(10) // 10 menit lagi -> masuk jendela bahaya
                    )
                )
            }
        }

        val report = SentimentAthenaAnalyzer.analyze(now = testNow, calendarRepo = mockRepo)
        assertThat(report.importance).isEqualTo(ImportanceLevel.CRITICAL)
        assertThat(report.headline).contains("ZONA BAHAYA BERITA AKTIF")
        assertThat(report.bias).isEqualTo(CouncilBias.NEUTRAL)
    }

    @Test
    fun `AegisExecutionSynthesizer rejects trade if news danger or clash exists`() {
        val oracleBull = AlphaOracleAnalyzer.analyze(4100.234, sampleCandles).copy(bias = CouncilBias.BULLISH)
        val chronosBear = ChronosAnalyzer.analyze(4100.234, sampleCandles).copy(bias = CouncilBias.BEARISH)
        val gaiaNormal = VolatilityGaiaAnalyzer.analyze(4100.234, 100.0, sampleCandles)
        val athenaNormal = SentimentAthenaAnalyzer.analyze()

        val (_, consensus) = AegisExecutionSynthesizer.synthesize(
            currentPrice = 4100.234,
            equityUsc = 250000.0,
            atr = 1.45,
            oracleReport = oracleBull,
            chronosReport = chronosBear, // Clashes with Oracle
            gaiaReport = gaiaNormal,
            athenaReport = athenaNormal
        )

        assertThat(consensus.isConsensusAgreed).isFalse()
        assertThat(consensus.setupType).isEqualTo(SetupType.WAIT_NO_CONSENSUS)
        assertThat(consensus.recommendedLotCent).isEqualTo(0.00)
        assertThat(consensus.verdictTitle).contains("TIDAK SEPAKAT")
    }

    @Test
    fun `AegisExecutionSynthesizer agrees on aligned signals and computes SL, TP, and lot cent`() {
        val oracleBull = AlphaOracleAnalyzer.analyze(4100.234, sampleCandles).copy(bias = CouncilBias.BULLISH, confidenceScore = 85)
        val chronosBull = ChronosAnalyzer.analyze(4100.234, sampleCandles).copy(bias = CouncilBias.BULLISH, confidenceScore = 80)
        val gaiaNormal = VolatilityGaiaAnalyzer.analyze(4100.234, 100.0, sampleCandles)
        val athenaNormal = SentimentAthenaAnalyzer.analyze().copy(importance = ImportanceLevel.LOW)

        val (_, consensus) = AegisExecutionSynthesizer.synthesize(
            currentPrice = 4100.234,
            equityUsc = 250000.0,
            atr = 1.45,
            oracleReport = oracleBull,
            chronosReport = chronosBull,
            gaiaReport = gaiaNormal,
            athenaReport = athenaNormal
        )

        assertThat(consensus.isConsensusAgreed).isTrue()
        assertThat(consensus.consensusBias).isEqualTo(CouncilBias.BULLISH)
        assertThat(consensus.confluenceScore).isAtLeast(70)
        assertThat(consensus.stopLossPrice).isLessThan(4100.234) // SL below entry for BUY
        assertThat(consensus.takeProfit1Price).isGreaterThan(4100.234) // TP1 above entry
        assertThat(consensus.takeProfit2Price).isGreaterThan(consensus.takeProfit1Price) // TP2 higher than TP1
        assertThat(consensus.recommendedLotCent).isGreaterThan(0.0)
    }

    @Test
    fun `CouncilEngine evaluate generates 5 complete dynamic script phases with all speakers`() {
        val (consensus, scriptSteps) = CouncilEngine.evaluate(
            currentPrice = 4100.234,
            equityUsc = 250000.0,
            spreadPoints = 120.0,
            candles = sampleCandles
        )

        assertThat(scriptSteps).hasSize(5)
        assertThat(scriptSteps.map { it.speakerId }).containsExactly("oracle", "hft", "risk", "sentiment", "execution").inOrder()

        scriptSteps.forEach { step ->
            assertThat(step.stage).isNotEmpty()
            assertThat(step.topic).isNotEmpty()
            assertThat(step.text).isNotEmpty()
            assertThat(step.progress).isGreaterThan(0)
        }

        assertThat(consensus.memberReports).hasSize(5)
    }
}
