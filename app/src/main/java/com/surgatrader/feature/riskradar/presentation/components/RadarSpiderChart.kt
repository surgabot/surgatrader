package com.surgatrader.feature.riskradar.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.feature.riskradar.domain.model.RiskFactor
import kotlin.math.cos
import kotlin.math.sin

/**
 * Radar / Spider Chart Hologram Interaktif (5-6 Dimensi Risiko Portofolio & Emas)
 * Menggunakan Compose Canvas dengan garis jaring kuantum, poligon risiko berpendar,
 * dan titik simpul data bergradasi.
 */
@Composable
fun RadarSpiderChart(
    factors: List<RiskFactor>,
    modifier: Modifier = Modifier,
    polygonColor: Color = AuraCyan
) {
    if (factors.isEmpty()) return

    Box(
        modifier = modifier
            .height(310.dp)
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (minOf(size.width, size.height) / 2f) * 0.70f
            val count = factors.size
            val angleStep = (2 * Math.PI / count).toFloat()
            val startAngle = -Math.PI / 2 // Mulai dari jam 12

            // 0. Pendaran Hologram Pusat
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuraCyan.copy(alpha = 0.12f),
                        AuraGoldPrimary.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = maxRadius * 1.1f
                ),
                radius = maxRadius * 1.1f,
                center = center
            )

            // 1. Gambar Jaring Grid (Web) untuk level 20%, 40%, 60%, 80%, 100%
            val gridLevels = listOf(0.20f, 0.40f, 0.60f, 0.80f, 1.0f)
            gridLevels.forEach { level ->
                val radius = maxRadius * level
                val gridPath = Path()
                for (i in 0 until count) {
                    val angle = startAngle + i * angleStep
                    val x = center.x + (radius * cos(angle)).toFloat()
                    val y = center.y + (radius * sin(angle)).toFloat()
                    if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                }
                gridPath.close()

                val isOuter = level == 1.0f
                val gridColor = if (isOuter) {
                    AuraGoldPrimary.copy(alpha = 0.45f)
                } else {
                    SlateBorder.copy(alpha = 0.35f)
                }

                drawPath(
                    path = gridPath,
                    color = gridColor,
                    style = Stroke(
                        width = if (isOuter) 1.5f else 1f
                    )
                )
            }

            // 2. Gambar Garis Spoke / Sumbu dari pusat ke titik ujung
            for (i in 0 until count) {
                val angle = startAngle + i * angleStep
                val x = center.x + (maxRadius * cos(angle)).toFloat()
                val y = center.y + (maxRadius * sin(angle)).toFloat()
                drawLine(
                    color = SlateBorder.copy(alpha = 0.45f),
                    start = center,
                    end = Offset(x, y),
                    strokeWidth = 1f
                )
            }

            // 3. Gambar Poligon Nilai Risiko Aktual
            val dataPath = Path()
            val pointCoords = mutableListOf<Offset>()

            factors.forEachIndexed { index, factor ->
                val ratio = (factor.score / factor.maxScore).toFloat().coerceIn(0.05f, 1.0f)
                val currentRadius = maxRadius * ratio
                val angle = startAngle + index * angleStep
                val x = center.x + (currentRadius * cos(angle)).toFloat()
                val y = center.y + (currentRadius * sin(angle)).toFloat()
                val pt = Offset(x, y)
                pointCoords.add(pt)

                if (index == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            // Isian Poligon dengan Gradien Pendar
            drawPath(
                path = dataPath,
                color = polygonColor.copy(alpha = 0.30f),
                style = Fill
            )

            // Garis Tepi Poligon Berpendar Kuantum
            drawPath(
                path = dataPath,
                color = polygonColor,
                style = Stroke(width = 2.8f, cap = StrokeCap.Round)
            )

            // 4. Gambar Titik Vertex Data dengan Lingkaran Konsentris Kuantum
            pointCoords.forEachIndexed { index, pt ->
                val factor = factors[index]
                val pointColor = when {
                    factor.score <= 35 -> SafeEmerald
                    factor.score <= 70 -> WarnAmber
                    else -> DangerRuby
                }

                // Cincin Pendar Luar
                drawCircle(
                    color = pointColor.copy(alpha = 0.35f),
                    radius = 8f,
                    center = pt
                )
                // Titik Utama
                drawCircle(
                    color = pointColor,
                    radius = 5f,
                    center = pt
                )
                // Inti Putih Terang
                drawCircle(
                    color = Color.White,
                    radius = 2.2f,
                    center = pt
                )
            }

            // 5. Gambar Label Sumbu & Skor Persentase
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 255, 255, 255)
                textSize = 28f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
            }

            val scorePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(230, 0, 242, 254)
                textSize = 23f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.NORMAL)
            }

            factors.forEachIndexed { index, factor ->
                val angle = startAngle + index * angleStep
                val labelRadius = maxRadius + 38f
                val x = center.x + (labelRadius * cos(angle)).toFloat()
                val y = center.y + (labelRadius * sin(angle)).toFloat()

                drawContext.canvas.nativeCanvas.drawText(
                    factor.name,
                    x,
                    y,
                    textPaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    "${factor.score.toInt()}%",
                    x,
                    y + 26f,
                    scorePaint
                )
            }
        }
    }
}
