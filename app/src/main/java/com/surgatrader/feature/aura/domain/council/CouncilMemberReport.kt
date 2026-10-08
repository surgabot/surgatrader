package com.surgatrader.feature.aura.domain.council

import com.surgatrader.feature.aura.domain.model.CouncilBias

enum class ImportanceLevel(val label: String) {
    LOW("RENDAH"),
    MEDIUM("MODERAT"),
    HIGH("TINGGI"),
    CRITICAL("KRITIS")
}

data class KeyLevel(
    val label: String,
    val price: Double
)

/**
 * Laporan hasil analisis domain nyata dari satu entitas dewan kuantum
 */
data class CouncilMemberReport(
    val entityId: String,
    val entityName: String,
    val role: String,
    val bias: CouncilBias,
    val confidenceScore: Int, // 0..100
    val importance: ImportanceLevel,
    val keyLevels: List<KeyLevel>,
    val headline: String,
    val detailedReason: String,
    val spokenNarration: String
)

enum class SetupType(val label: String) {
    BREAKOUT("BREAKOUT (PENEMBUSAN STRUKTUR)"),
    PULLBACK("PULLBACK (RETEST AREA LIKUIDITAS)"),
    REVERSAL("REVERSAL (PEMBALIKAN MOMENTUM)"),
    WAIT_NO_CONSENSUS("TUNGGU (DEWAN TIDAK SEPAKAT)")
}

/**
 * Hasil sintesis musyawarah ke-5 entitas dewan kuantum
 */
data class CouncilConsensusResult(
    val timestamp: Long,
    val memberReports: Map<String, CouncilMemberReport>,
    val consensusBias: CouncilBias,
    val isConsensusAgreed: Boolean,
    val confluenceScore: Int, // 0..100
    val setupType: SetupType,
    val verdictTitle: String,
    val verdictSummary: String,
    val entryPriceMin: Double,
    val entryPriceMax: Double,
    val stopLossPrice: Double,
    val takeProfit1Price: Double,
    val takeProfit2Price: Double,
    val riskRewardRatio1: Double,
    val riskRewardRatio2: Double,
    val recommendedLotCent: Double
)
