package com.surgatrader.core.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.FontFamilyOrbitron

/**
 * High-tech primary button with gold-to-orange gradient and haptic feedback.
 * Meets accessibility standards with minimum touch height of 48dp.
 */
@Composable
fun AuraGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    minHeight: Dp = 48.dp
) {
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(24.dp)
    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xFFFFD700),
            Color(0xFFFF8C00)
        )
    )

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = AuraGoldPrimary.copy(alpha = 0.5f),
                spotColor = AuraGoldPrimary.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(if (enabled) gradient else Brush.linearGradient(listOf(Color(0xFF4A4A4A), Color(0xFF2B2B2B))))
            .clickable(
                enabled = enabled,
                role = Role.Button
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                color = Color(0xFF02050E),
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamilyOrbitron,
                letterSpacing = 1.2.sp
            )
        }
    }
}

/**
 * Secondary cyber button with dark glass background and neon border.
 */
@Composable
fun AuraSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = AuraGoldPrimary,
    textColor: Color = Color.White,
    minHeight: Dp = 48.dp
) {
    val haptic = LocalHapticFeedback.current
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = minHeight)
            .clip(shape)
            .background(Color(0x33000000), shape)
            .border(1.dp, borderColor.copy(alpha = 0.6f), shape)
            .clickable(
                role = Role.Button
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamilyOrbitron,
            letterSpacing = 1.sp
        )
    }
}
