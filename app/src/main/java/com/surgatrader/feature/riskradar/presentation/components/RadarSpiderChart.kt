package com.surgatrader.feature.riskradar.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.surgatrader.core.theme.CyanAccent
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.feature.riskradar.domain.model.RiskFactor
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarSpiderChart(
    factors: List<RiskFactor>,
    modifier: Modifier = Modifier,
    polygonColor: Color = CyanAccent
) {
    if (factors.isEmpty()) return

    Box(
        modifier = modifier
            .height(280.dp)
            .padding(16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (minOf(size.width, size.height) / 2f) * 0.72f
            val count = factors.size
            val angleStep = (2 * Math.PI / count).toFloat()
            val startAngle = -Math.PI / 2 // Mulai dari atas jam 12

            // 1. Gambar Jaring Grid (Web) untuk level 25%, 50%, 75%, 100%
            val gridLevels = listOf(0.25f, 0.5f, 0.75f, 1.0f)
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

                drawPath(
                    path = gridPath,
                    color = SlateBorder.copy(alpha = if (level == 1f) 0.8f else 0.4f),
                    style = Stroke(width = if (level == 1f) 1.5f else 1f)
                )
            }

            // 2. Gambar Garis Spoke / Sumbu dari pusat ke titik ujung
            for (i in 0 until count) {
                val angle = startAngle + i * angleStep
                val x = center.x + (maxRadius * cos(angle)).toFloat()
                val y = center.y + (maxRadius * sin(angle)).toFloat()
                drawLine(
                    color = SlateBorder.copy(alpha = 0.5f),
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

            // Isian Poligon dengan Transparansi Lembut
            drawPath(
                path = dataPath,
                color = polygonColor.copy(alpha = 0.28f),
                style = Fill
            )

            // Garis Tepi Poligon
            drawPath(
                path = dataPath,
                color = polygonColor,
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // 4. Gambar Titik Vertex Data dengan Warna Kategori
            pointCoords.forEachIndexed { index, pt ->
                val factor = factors[index]
                val pointColor = when {
                    factor.score <= 35 -> SafeEmerald
                    factor.score <= 70 -> WarnAmber
                    else -> DangerRuby
                }
                drawCircle(
                    color = pointColor,
                    radius = 5.5f,
                    center = pt
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5f,
                    center = pt
                )
            }

            // 5. Gambar Label Sumbu
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(255, 235, 240, 250)
                textSize = 28f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
            }

            val scorePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(200, 150, 158, 175)
                textSize = 22f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            factors.forEachIndexed { index, factor ->
                val angle = startAngle + index * angleStep
                val labelRadius = maxRadius + 36f
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
