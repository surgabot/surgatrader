package com.surgatrader.core.ui.glass

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Subtle CRT Scanlines & Radial Vignette overlay to achieve cinematic retro-futuristic visuals.
 * Can be completely bypassed in battery-saving mode.
 */
@Composable
fun AuraScanlineOverlay(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    scanlineSpacing: Float = 6f
) {
    if (!enabled) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. CRT Scanlines
        var y = 0f
        while (y < height) {
            drawLine(
                color = Color(0x10000000),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.5f
            )
            y += scanlineSpacing
        }

        // 2. Cinematic Vignette
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x9901030A)
                ),
                center = Offset(width / 2f, height / 2f),
                radius = maxOf(width, height) * 0.72f
            )
        )
    }
}
