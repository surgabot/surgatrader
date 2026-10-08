package com.surgatrader.feature.aura.domain.model

import androidx.compose.ui.graphics.Color
import com.surgatrader.core.theme.CouncilAegis
import com.surgatrader.core.theme.CouncilAthena
import com.surgatrader.core.theme.CouncilChronos
import com.surgatrader.core.theme.CouncilGaia
import com.surgatrader.core.theme.CouncilOracle
import kotlin.math.PI

/**
 * AI Quantum Council Entity representing an expert intelligence in gold trading
 */
data class AuraEntity(
    val id: String,
    val name: String,
    val title: String,
    val roleShort: String,
    val color: Color,
    val subColor: Color,
    val voiceRate: Float,
    val angle: Float,
    val radiusRatio: Float = 0.32f,
    val nodeSize: Float = 30f
)

val DefaultAuraEntities = listOf(
    AuraEntity(
        id = "oracle",
        name = "ALPHA-ORACLE",
        title = "Arsitek Model Makro Kuantitatif & Likuiditas Global",
        roleShort = "Makro & Likuiditas",
        color = CouncilOracle,
        subColor = Color(0xFFFF8C00),
        voiceRate = 0.96f,
        angle = (-PI / 2).toFloat(),
        radiusRatio = 0.34f,
        nodeSize = 32f
    ),
    AuraEntity(
        id = "hft",
        name = "CHRONOS-HFT",
        title = "Eksekusi Frekuensi Tinggi & Arbitrase Mikrostruktur COMEX",
        roleShort = "HFT & Order Flow",
        color = CouncilChronos,
        subColor = Color(0xFF4FACFE),
        voiceRate = 1.05f,
        angle = (-PI / 2 + (PI * 2 / 5)).toFloat(),
        radiusRatio = 0.34f,
        nodeSize = 30f
    ),
    AuraEntity(
        id = "risk",
        name = "VOLATILITY-GAIA",
        title = "Manajemen Risiko Termodinamika & Dynamic Delta Hedging",
        roleShort = "Risiko & Hedging",
        color = CouncilGaia,
        subColor = Color(0xFF059669),
        voiceRate = 0.93f,
        angle = (-PI / 2 + (PI * 2 / 5) * 2).toFloat(),
        radiusRatio = 0.34f,
        nodeSize = 30f
    ),
    AuraEntity(
        id = "sentiment",
        name = "SENTIMENT-ATHENA",
        title = "Analisis Sentimen Geopolitik & Pemrosesan Berita Satelit",
        roleShort = "Geopolitik & Berita",
        color = CouncilAthena,
        subColor = Color(0xFFFEE140),
        voiceRate = 0.98f,
        angle = (-PI / 2 + (PI * 2 / 5) * 3).toFloat(),
        radiusRatio = 0.34f,
        nodeSize = 30f
    ),
    AuraEntity(
        id = "execution",
        name = "AEGIS-EXECUTION",
        title = "Infrastruktur Mesin Eksekusi Kuantum & Alokasi Modal Dark Pool",
        roleShort = "Dark Pool & Alokasi",
        color = CouncilAegis,
        subColor = Color(0xFF6A11CB),
        voiceRate = 1.06f,
        angle = (-PI / 2 + (PI * 2 / 5) * 4).toFloat(),
        radiusRatio = 0.34f,
        nodeSize = 30f
    )
)
