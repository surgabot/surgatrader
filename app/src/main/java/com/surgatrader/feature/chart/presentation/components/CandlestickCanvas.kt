package com.surgatrader.feature.chart.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.feature.aura.domain.model.MarketCandle
import com.surgatrader.feature.chart.domain.model.ChartIndicatorOverlay
import com.surgatrader.feature.chart.domain.model.PriceKeyLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Komponen Canvas Candlestick Interaktif dengan Pan, Zoom, Crosshair, dan Indikator Multi-layer
 */
@Composable
fun CandlestickCanvas(
    candles: List<MarketCandle>,
    ema20Values: List<Double?>,
    ema50Values: List<Double?>,
    ema200Values: List<Double?>,
    pivotLevels: List<PriceKeyLevel>,
    aegisLevels: List<PriceKeyLevel>,
    overlays: ChartIndicatorOverlay,
    onCandleSelected: (MarketCandle?, Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (candles.isEmpty()) return

    var candleWidthPx by remember { mutableFloatStateOf(24f) }
    var scrollOffsetPx by remember { mutableFloatStateOf(0f) }
    var crosshairPos by remember { mutableStateOf<Offset?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    candleWidthPx = (candleWidthPx * zoom).coerceIn(10f, 60f)
                    scrollOffsetPx += pan.x
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        crosshairPos = offset
                        tryAwaitRelease()
                        crosshairPos = null
                        onCandleSelected(null, null)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> crosshairPos = offset },
                    onDrag = { change, _ ->
                        crosshairPos = change.position
                        change.consume()
                    },
                    onDragEnd = {
                        crosshairPos = null
                        onCandleSelected(null, null)
                    },
                    onDragCancel = {
                        crosshairPos = null
                        onCandleSelected(null, null)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val chartWidth = size.width - 140f // Margin kanan untuk skala harga
            val chartHeight = size.height - 50f // Margin bawah untuk waktu

            val spacing = candleWidthPx * 0.35f
            val totalCandleWidth = candleWidthPx + spacing

            // Batas scroll horizontal
            val maxScroll = 0f
            val minScroll = min(0f, chartWidth - (candles.size * totalCandleWidth) - 40f)
            scrollOffsetPx = scrollOffsetPx.coerceIn(minScroll, maxScroll)

            // Hitung rentang harga candle yang terlihat
            val visibleCandles = candles
            val minPrice = visibleCandles.minOfOrNull { it.low }?.minus(0.5) ?: 4095.0
            val maxPrice = visibleCandles.maxOfOrNull { it.high }?.plus(0.5) ?: 4115.0
            val priceRange = max(0.001, maxPrice - minPrice)

            fun priceToY(price: Double): Float {
                return (chartHeight - ((price - minPrice) / priceRange * chartHeight)).toFloat()
            }

            fun yToPrice(y: Float): Double {
                return maxPrice - ((y / chartHeight) * priceRange)
            }

            // 1. Gambar Grid Harga Horisontal & Skala Y
            val priceStepCount = 5
            val priceTextPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.argb(200, 148, 163, 184)
                textSize = 24f
                textAlign = android.graphics.Paint.Align.LEFT
                isAntiAlias = true
                typeface = android.graphics.Typeface.MONOSPACE
            }

            for (i in 0..priceStepCount) {
                val p = minPrice + (priceRange * (i.toDouble() / priceStepCount))
                val y = priceToY(p)
                drawLine(
                    color = SlateBorder.copy(alpha = 0.25f),
                    start = Offset(0f, y),
                    end = Offset(chartWidth, y),
                    strokeWidth = 1f
                )
                drawContext.canvas.nativeCanvas.drawText(
                    String.format(Locale.US, "%.3f", p),
                    chartWidth + 12f,
                    y + 8f,
                    priceTextPaint
                )
            }

            // 2. Gambar Candlestick Riil (OHLC)
            val candleXCoords = mutableListOf<Float>()
            candles.forEachIndexed { index, candle ->
                val x = chartWidth - ((candles.size - 1 - index) * totalCandleWidth) + scrollOffsetPx - candleWidthPx
                candleXCoords.add(x)

                if (x in -candleWidthPx..(chartWidth + candleWidthPx)) {
                    val openY = priceToY(candle.open)
                    val closeY = priceToY(candle.close)
                    val highY = priceToY(candle.high)
                    val lowY = priceToY(candle.low)

                    val isBull = candle.close >= candle.open
                    val candleColor = if (isBull) AuraGreenBull else AuraRedBear

                    // Sumbu Atas & Bawah
                    drawLine(
                        color = candleColor,
                        start = Offset(x + candleWidthPx / 2f, highY),
                        end = Offset(x + candleWidthPx / 2f, lowY),
                        strokeWidth = 1.8f
                    )

                    // Badan Candle
                    val bodyTop = min(openY, closeY)
                    val bodyHeight = max(2.5f, abs(openY - closeY))
                    drawRect(
                        color = candleColor,
                        topLeft = Offset(x, bodyTop),
                        size = Size(candleWidthPx, bodyHeight),
                        style = Fill
                    )
                }
            }

            // 3. Gambar Garis Indikator EMA
            fun drawIndicatorPath(values: List<Double?>, color: Color, width: Float) {
                val path = Path()
                var hasStarted = false
                values.forEachIndexed { index, value ->
                    if (value != null && index < candleXCoords.size) {
                        val x = candleXCoords[index] + candleWidthPx / 2f
                        val y = priceToY(value)
                        if (x in -50f..(chartWidth + 50f)) {
                            if (!hasStarted) {
                                path.moveTo(x, y)
                                hasStarted = true
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                    }
                }
                if (hasStarted) {
                    drawPath(path = path, color = color, style = Stroke(width = width, cap = StrokeCap.Round))
                }
            }

            if (overlays.showEma20) drawIndicatorPath(ema20Values, AuraCyan, 1.8f)
            if (overlays.showEma50) drawIndicatorPath(ema50Values, AuraGoldPrimary, 1.8f)
            if (overlays.showEma200) drawIndicatorPath(ema200Values, Color(0xFF818CF8), 2.0f)

            // 4. Gambar Level Pivot Point
            if (overlays.showPivots) {
                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                pivotLevels.forEach { level ->
                    val y = priceToY(level.price)
                    if (y in 0f..chartHeight) {
                        drawLine(
                            color = Color(level.colorHex).copy(alpha = 0.6f),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 1.2f,
                            pathEffect = dashedEffect
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            "${level.label}: ${String.format(Locale.US, "%.3f", level.price)}",
                            10f,
                            y - 6f,
                            priceTextPaint.apply { color = level.colorHex.toInt() }
                        )
                    }
                }
            }

            // 5. Gambar Level Aegis (Entry, SL, TP)
            if (overlays.showAegisLevels) {
                val solidEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 6f), 0f)
                aegisLevels.forEach { level ->
                    val y = priceToY(level.price)
                    if (y in 0f..chartHeight) {
                        drawLine(
                            color = Color(level.colorHex),
                            start = Offset(0f, y),
                            end = Offset(chartWidth, y),
                            strokeWidth = 2.0f,
                            pathEffect = solidEffect
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            "⚡ ${level.label}: ${String.format(Locale.US, "%.3f", level.price)}",
                            chartWidth - 210f,
                            y - 6f,
                            priceTextPaint.apply { color = level.colorHex.toInt() }
                        )
                    }
                }
            }

            // 6. Crosshair HUD Interaktif
            crosshairPos?.let { pos ->
                val cx = pos.x.coerceIn(0f, chartWidth)
                val cy = pos.y.coerceIn(0f, chartHeight)
                val crosshairColor = Color.White.copy(alpha = 0.75f)
                val crossEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)

                // Garis vertikal & horisontal
                drawLine(color = crosshairColor, start = Offset(cx, 0f), end = Offset(cx, chartHeight), strokeWidth = 1.2f, pathEffect = crossEffect)
                drawLine(color = crosshairColor, start = Offset(0f, cy), end = Offset(chartWidth, cy), strokeWidth = 1.2f, pathEffect = crossEffect)

                // Label harga di skala Y
                val hoverPrice = yToPrice(cy)
                val tagRect = Size(130f, 32f)
                drawRect(
                    color = AuraGoldPrimary,
                    topLeft = Offset(chartWidth, cy - 16f),
                    size = tagRect
                )
                val hoverTextPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.argb(255, 2, 5, 14)
                    textSize = 22f
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                }
                drawContext.canvas.nativeCanvas.drawText(
                    String.format(Locale.US, "%.3f", hoverPrice),
                    chartWidth + 8f,
                    cy + 7f,
                    hoverTextPaint
                )

                // Temukan candle terdekat di sumbu X
                var closestIndex = -1
                var minDistance = Float.MAX_VALUE
                candleXCoords.forEachIndexed { idx, x ->
                    val dist = abs(cx - (x + candleWidthPx / 2f))
                    if (dist < minDistance) {
                        minDistance = dist
                        closestIndex = idx
                    }
                }
                if (closestIndex in candles.indices) {
                    val candle = candles[closestIndex]
                    onCandleSelected(candle, hoverPrice)

                    val timeSdf = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID"))
                    timeSdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val timeStr = timeSdf.format(Date(candle.time))

                    drawRect(
                        color = Color(0xFF040A1A),
                        topLeft = Offset(cx - 50f, chartHeight),
                        size = Size(100f, 26f)
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        timeStr,
                        cx - 40f,
                        chartHeight + 20f,
                        priceTextPaint.apply { color = android.graphics.Color.WHITE }
                    )
                }
            }
        }
    }
}
