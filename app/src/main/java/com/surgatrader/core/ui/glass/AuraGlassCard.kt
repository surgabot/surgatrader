package com.surgatrader.core.ui.glass

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.surgatrader.core.theme.AuraGlassBg
import com.surgatrader.core.theme.AuraGlassBorder

/**
 * Reusable Glassmorphism card for the Aura Quantum Command Deck.
 * Employs hardware-accelerated blur on Android 12+ (API 31+) with graceful translucency fallback.
 */
@Composable
fun AuraGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = AuraGlassBorder,
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = AuraGlassBg,
    glowColor: Color = Color(0x33FFD700),
    glowElevation: Dp = 8.dp,
    blurRadius: Float = 20f,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && blurRadius > 0f) {
        Modifier.graphicsLayer {
            try {
                renderEffect = android.graphics.RenderEffect
                    .createBlurEffect(blurRadius, blurRadius, android.graphics.Shader.TileMode.CLAMP)
                    .asComposeRenderEffect()
            } catch (_: Exception) {}
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = glowElevation,
                shape = shape,
                ambientColor = glowColor,
                spotColor = glowColor
            )
            .then(blurModifier)
            .clip(shape)
            .background(backgroundColor, shape)
            .border(borderWidth, borderColor, shape)
            .padding(14.dp),
        content = content
    )
}
