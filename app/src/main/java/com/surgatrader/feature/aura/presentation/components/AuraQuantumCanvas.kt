package com.surgatrader.feature.aura.presentation.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import com.surgatrader.feature.aura.domain.model.AuraEntity
import com.surgatrader.feature.aura.domain.model.AuraState
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.aura.domain.model.DefaultAuraEntities
import com.surgatrader.feature.aura.domain.model.MarketCandle
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

private class StarParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    val radius: Float,
    val color: Color,
    var pulse: Float
)

private class Ripple(
    val x: Float,
    val y: Float,
    var radius: Float,
    val maxRadius: Float,
    val color: Color,
    var alpha: Float,
    val speed: Float
)

private class DataPacket(
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    var progress: Float,
    val speed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun AuraQuantumCanvas(
    state: AuraState,
    modifier: Modifier = Modifier,
    onEntityTapped: (AuraEntity) -> Unit
) {
    var globalTime by remember { mutableFloatStateOf(0f) }
    val particles = remember { mutableStateListOf<StarParticle>() }
    val ripples = remember { mutableStateListOf<Ripple>() }
    val packets = remember { mutableStateListOf<DataPacket>() }

    val activeSpeaker = state.currentSpeaker
    val isSpeaking = state.isSpeaking
    val rotationMultiplier = state.rotationSpeedMultiplier

    // Continuous 60fps game render loop with ATR-adjusted velocity
    LaunchedEffect(Unit) {
        var lastNano = 0L
        while (true) {
            withFrameNanos { frameNano ->
                if (lastNano != 0L) {
                    val dt = ((frameNano - lastNano) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                    globalTime += dt * 60f
                }
                lastNano = frameNano
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val width = size.width
                    val height = size.height
                    val centerX = width / 2f
                    val centerY = height / 2f
                    val orbitRadius = min(width, height) * 0.35f

                    for (entity in DefaultAuraEntities) {
                        val ex = centerX + cos(entity.angle) * orbitRadius
                        val ey = centerY + sin(entity.angle) * orbitRadius
                        val dist = hypot(offset.x - ex, offset.y - ey)
                        if (dist <= 50f * density) {
                            ripples.add(
                                Ripple(
                                    x = ex,
                                    y = ey,
                                    radius = 12f,
                                    maxRadius = 180f,
                                    color = entity.color,
                                    alpha = 0.9f,
                                    speed = 3.5f
                                )
                            )
                            onEntityTapped(entity)
                            break
                        }
                    }
                }
            }
    ) {
        val width = size.width
        val height = size.height
        val centerX = width / 2f
        val centerY = height / 2f
        val orbitRadius = min(width, height) * 0.35f

        // Initialize particles if empty
        if (particles.isEmpty()) {
            val colors = listOf(AuraGoldPrimary, AuraGoldLight, AuraCyan, Color.White, Color(0xFFFF8C00))
            for (i in 0 until 80) {
                particles.add(
                    StarParticle(
                        x = Random.nextFloat() * width,
                        y = Random.nextFloat() * height,
                        vx = (Random.nextFloat() - 0.5f) * 0.4f,
                        vy = (Random.nextFloat() - 0.5f) * 0.4f,
                        radius = Random.nextFloat() * 2.5f + 1f,
                        color = colors[Random.nextInt(colors.size)],
                        pulse = Random.nextFloat() * (2 * PI.toFloat())
                    )
                )
            }
        }

        // 1. Space Radial Gradient Background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF040D22),
                    Color(0xFF020614),
                    Color(0xFF01030A)
                ),
                center = Offset(centerX, centerY),
                radius = maxOf(width, height) * 0.75f
            )
        )

        // 2. Cyber Matrix Grid Lines
        val gridSpacing = 65f
        var gx = 0f
        while (gx < width) {
            drawLine(
                color = Color(0x09FFD700),
                start = Offset(gx, 0f),
                end = Offset(gx, height),
                strokeWidth = 1f
            )
            gx += gridSpacing
        }
        var gy = 0f
        while (gy < height) {
            drawLine(
                color = Color(0x09FFD700),
                start = Offset(0f, gy),
                end = Offset(width, gy),
                strokeWidth = 1f
            )
            gy += gridSpacing
        }

        // 3. Update & Draw Floating Particles
        for (p in particles) {
            p.x += p.vx
            p.y += p.vy
            p.pulse += 0.04f
            if (p.x < 0f) p.x = width
            if (p.x > width) p.x = 0f
            if (p.y < 0f) p.y = height
            if (p.y > height) p.y = 0f

            val alpha = (0.35f + sin(p.pulse) * 0.25f).coerceIn(0.1f, 0.9f)
            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius,
                center = Offset(p.x, p.y)
            )
        }

        // Entity Positions (Pentagon Formation)
        val entityPositions = DefaultAuraEntities.map { entity ->
            val ex = centerX + cos(entity.angle) * orbitRadius
            val ey = centerY + sin(entity.angle) * orbitRadius
            entity to Offset(ex, ey)
        }

        // 4. Draw Synapse Web (Background interconnect lines between entities)
        for (i in entityPositions.indices) {
            for (j in (i + 1) until entityPositions.size) {
                drawLine(
                    color = Color(0x15FFD700),
                    start = entityPositions[i].second,
                    end = entityPositions[j].second,
                    strokeWidth = 1f
                )
            }
            // Lines from entities to core
            drawLine(
                color = Color(0x22FFFFFF),
                start = entityPositions[i].second,
                end = Offset(centerX, centerY),
                strokeWidth = 1f
            )
        }

        // 5. Active Speaker High-Energy Synapse Beam & Data Packets
        val speakerPos = entityPositions.find { it.first.id == activeSpeaker.id }?.second
        if (speakerPos != null) {
            // High-glow beam to core
            drawLine(
                color = activeSpeaker.color.copy(alpha = 0.85f),
                start = speakerPos,
                end = Offset(centerX, centerY),
                strokeWidth = 4f
            )
            drawLine(
                color = Color.White,
                start = speakerPos,
                end = Offset(centerX, centerY),
                strokeWidth = 1.5f
            )

            // Spawn data packets towards core during active speech
            if (isSpeaking && Random.nextFloat() < 0.15f) {
                packets.add(
                    DataPacket(
                        startX = speakerPos.x,
                        startY = speakerPos.y,
                        targetX = centerX,
                        targetY = centerY,
                        progress = 0f,
                        speed = (0.035f + Random.nextFloat() * 0.02f) * rotationMultiplier,
                        color = activeSpeaker.color,
                        size = Random.nextFloat() * 4f + 3f
                    )
                )
            }

            if (isSpeaking && Random.nextFloat() < 0.06f) {
                ripples.add(
                    Ripple(
                        x = speakerPos.x,
                        y = speakerPos.y,
                        radius = 12f,
                        maxRadius = 140f,
                        color = activeSpeaker.color,
                        alpha = 0.85f,
                        speed = 2.8f
                    )
                )
            }
        }

        // 6. Draw Traveling Data Packets
        val packetIterator = packets.iterator()
        while (packetIterator.hasNext()) {
            val pkt = packetIterator.next()
            pkt.progress += pkt.speed
            if (pkt.progress >= 1f) {
                packetIterator.remove()
                continue
            }
            val px = pkt.startX + (pkt.targetX - pkt.startX) * pkt.progress
            val py = pkt.startY + (pkt.targetY - pkt.startY) * pkt.progress

            drawCircle(
                color = pkt.color.copy(alpha = 0.9f),
                radius = pkt.size,
                center = Offset(px, py)
            )
            drawCircle(
                color = Color.White,
                radius = pkt.size * 0.5f,
                center = Offset(px, py)
            )
        }

        // 7. Draw Ripples
        val rippleIterator = ripples.iterator()
        while (rippleIterator.hasNext()) {
            val rip = rippleIterator.next()
            rip.radius += rip.speed
            rip.alpha -= 0.02f
            if (rip.alpha <= 0f || rip.radius >= rip.maxRadius) {
                rippleIterator.remove()
                continue
            }
            drawCircle(
                color = rip.color.copy(alpha = rip.alpha),
                radius = rip.radius,
                center = Offset(rip.x, rip.y),
                style = Stroke(width = 2f)
            )
        }

        // 8. Draw Central Holographic Gold Core with Real M5 Candles & ATR-Driven Rotation
        drawGoldHoloCore(
            centerX = centerX,
            centerY = centerY,
            time = globalTime,
            candles = state.m5Candles,
            rotationMultiplier = rotationMultiplier,
            consensusBias = state.consensusBias,
            atr14 = state.atr14
        )

        // 9. Draw Council Entity Avatars
        for ((entity, pos) in entityPositions) {
            val isCurrentSpeaker = activeSpeaker.id == entity.id && isSpeaking
            drawEntityAvatar(
                entity = entity,
                pos = pos,
                time = globalTime,
                isCurrentSpeaker = isCurrentSpeaker
            )
        }
    }
}

private fun DrawScope.drawGoldHoloCore(
    centerX: Float,
    centerY: Float,
    time: Float,
    candles: List<MarketCandle>,
    rotationMultiplier: Float,
    consensusBias: CouncilBias,
    atr14: Double
) {
    // Determine dynamic core color based on Council Consensus Bias
    val coreColor = when (consensusBias) {
        CouncilBias.BULLISH -> AuraGreenBull
        CouncilBias.BEARISH -> AuraRedBear
        CouncilBias.NEUTRAL -> AuraGoldPrimary
    }
    val coreSubColor = when (consensusBias) {
        CouncilBias.BULLISH -> Color(0xFF00B050)
        CouncilBias.BEARISH -> Color(0xFFB00020)
        CouncilBias.NEUTRAL -> Color(0xFFB8860B)
    }

    // Outer radial glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                coreColor.copy(alpha = 0.35f),
                coreSubColor.copy(alpha = 0.15f),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = 130f
        ),
        radius = 130f,
        center = Offset(centerX, centerY)
    )

    // 4 Elliptical Orbit Rings with Rotating Satellites (Speed regulated by ATR)
    val orbitColors = listOf(
        coreColor.copy(alpha = 0.85f),
        Color(0xAA00F2FE),
        Color(0x99FF8C00),
        coreSubColor.copy(alpha = 0.75f)
    )
    val rotSpeeds = listOf(0.016f, -0.022f, 0.026f, -0.012f)

    for (r in 0 until 4) {
        val ringRadius = 50f + r * 16f
        val rotationAngle = (time * rotSpeeds[r] * rotationMultiplier + (r * PI.toFloat() / 4f)) * (180f / PI.toFloat())

        rotate(degrees = rotationAngle, pivot = Offset(centerX, centerY)) {
            drawOval(
                color = orbitColors[r],
                topLeft = Offset(centerX - ringRadius, centerY - ringRadius * 0.42f),
                size = Size(ringRadius * 2f, ringRadius * 0.84f),
                style = Stroke(width = 1.3f)
            )

            // Orbiting satellite dot
            val satAngle = time * (0.04f + r * 0.012f) * rotationMultiplier
            val satX = centerX + cos(satAngle) * ringRadius
            val satY = centerY + sin(satAngle) * (ringRadius * 0.42f)
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = Offset(satX, satY)
            )
        }
    }

    // 16 Real M5 Candlesticks revolving in 3D perspective around the core
    val numCandles = if (candles.isNotEmpty()) candles.size else 16
    for (c in 0 until numCandles) {
        val candle = candles.getOrNull(c)
        val cAngle = (c * (2 * PI.toFloat() / numCandles)) + time * 0.012f * rotationMultiplier
        val dist = 95f + sin(time * 0.05f + c) * 6f
        val cxCandle = centerX + cos(cAngle) * dist
        val cyCandle = centerY + sin(cAngle) * (dist * 0.48f)

        // 3D Depth effect: front candles brighter and larger
        val zDepth = sin(cAngle)
        val depthAlpha = (0.55f + (zDepth + 1f) * 0.22f).coerceIn(0.3f, 1.0f)
        val depthScale = (0.85f + (zDepth + 1f) * 0.15f).coerceIn(0.7f, 1.3f)

        val isBullish = candle?.isBullish ?: (c % 2 == 0)
        val candleColor = (if (isBullish) AuraGreenBull else AuraRedBear).copy(alpha = depthAlpha)

        // Mathematical scaling based on real candle OHLC values
        val totalRange = candle?.totalRange ?: 1.0
        val bodySpan = candle?.bodyHeight ?: 0.5
        val upperWickSpan = candle?.upperWick ?: 0.25
        val lowerWickSpan = candle?.lowerWick ?: 0.25

        val totalPix = 24f * depthScale
        val bodyPix = (bodySpan / totalRange).toFloat().coerceIn(0.15f, 0.9f) * totalPix
        val upperWickPix = (upperWickSpan / totalRange).toFloat().coerceIn(0.05f, 0.5f) * totalPix
        val lowerWickPix = (lowerWickSpan / totalRange).toFloat().coerceIn(0.05f, 0.5f) * totalPix

        // Wick
        drawLine(
            color = candleColor,
            start = Offset(cxCandle, cyCandle - (bodyPix / 2f) - upperWickPix),
            end = Offset(cxCandle, cyCandle + (bodyPix / 2f) + lowerWickPix),
            strokeWidth = 1.2f * depthScale
        )

        // Body
        val bodyWidth = 5.5f * depthScale
        drawRect(
            color = candleColor,
            topLeft = Offset(cxCandle - (bodyWidth / 2f), cyCandle - (bodyPix / 2f)),
            size = Size(bodyWidth, maxOf(2f, bodyPix))
        )
    }

    // Inner Pulsing Core
    val corePulse = sin(time * 0.05f * rotationMultiplier) * 4f
    val coreRadius = 28f + corePulse
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                coreColor,
                coreSubColor,
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = coreRadius
        ),
        radius = coreRadius,
        center = Offset(centerX, centerY)
    )

    // Rotating Hexagon
    val hexPath = Path()
    val hexRadius = 18f + corePulse * 0.4f
    for (h in 0 until 6) {
        val hexAngle = (h * PI.toFloat() / 3f) + time * 0.02f * rotationMultiplier
        val hx = centerX + cos(hexAngle) * hexRadius
        val hy = centerY + sin(hexAngle) * hexRadius
        if (h == 0) hexPath.moveTo(hx, hy) else hexPath.lineTo(hx, hy)
    }
    hexPath.close()
    drawPath(
        path = hexPath,
        color = Color.White,
        style = Stroke(width = 1.4f)
    )

    // Core Title Text & Real Quant Status
    drawIntoCanvas { canvas ->
        val textPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 22f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.nativeCanvas.drawText("XAU/USD CORE", centerX, centerY + 46f, textPaint)

        val subPaint = Paint().apply {
            color = coreColor.toArgb()
            textSize = 15f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val biasText = "BIAS: ${consensusBias.label} • ATR(14): ${String.format(Locale.US, "%.3f", atr14)}"
        canvas.nativeCanvas.drawText(biasText, centerX, centerY + 65f, subPaint)
    }
}

private fun DrawScope.drawEntityAvatar(
    entity: AuraEntity,
    pos: Offset,
    time: Float,
    isCurrentSpeaker: Boolean
) {
    val x = pos.x
    val y = pos.y

    // Active Speaker Aura
    if (isCurrentSpeaker) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    entity.color.copy(alpha = 0.55f),
                    entity.subColor.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = pos,
                radius = 65f
            ),
            radius = 65f,
            center = pos
        )
    }

    val podSize = entity.nodeSize + (if (isCurrentSpeaker) sin(time * 0.08f) * 3f else 0f)

    // Outer Hexagonal Pod
    val hexPath = Path()
    for (s in 0 until 6) {
        val segAngle = (s * PI.toFloat() / 3f) + (if (isCurrentSpeaker) time * 0.02f else 0f)
        val sx = x + cos(segAngle) * (podSize + 10f)
        val sy = y + sin(segAngle) * (podSize + 10f)
        if (s == 0) hexPath.moveTo(sx, sy) else hexPath.lineTo(sx, sy)
    }
    hexPath.close()

    drawPath(
        path = hexPath,
        color = if (isCurrentSpeaker) entity.color else Color(0x55FFD700),
        style = Stroke(width = if (isCurrentSpeaker) 2f else 1f)
    )

    // Dashed Orbit Ring around Node
    drawCircle(
        color = entity.color.copy(alpha = 0.7f),
        radius = podSize + 4f,
        center = pos,
        style = Stroke(
            width = 1.2f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), time * 0.5f)
        )
    )

    // Shaded Metallic/Neon Orb
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White,
                entity.color,
                entity.subColor,
                Color(0xFF050C1E)
            ),
            center = Offset(x - 4f, y - 5f),
            radius = podSize
        ),
        radius = podSize,
        center = pos
    )

    // Center Hotspot
    drawCircle(
        color = Color.White,
        radius = 3.5f,
        center = pos
    )

    // Node Labels
    drawIntoCanvas { canvas ->
        val namePaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 20f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.nativeCanvas.drawText(entity.name, x, y + podSize + 22f, namePaint)

        val rolePaint = Paint().apply {
            color = entity.color.toArgb()
            textSize = 15f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.nativeCanvas.drawText(entity.roleShort.uppercase(), x, y + podSize + 38f, rolePaint)
    }
}
