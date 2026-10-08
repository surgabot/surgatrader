package com.surgatrader.feature.aura.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.AuraGlassBg
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import com.surgatrader.feature.aura.domain.council.CouncilMemberReport
import com.surgatrader.feature.aura.domain.model.AuraEntity
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.aura.domain.model.DefaultAuraEntities

@Composable
fun AuraBottomDock(
    currentSpeaker: AuraEntity,
    isSpeaking: Boolean,
    onEntitySelected: (AuraEntity) -> Unit,
    onToggleTerminal: () -> Unit,
    reports: Map<String, CouncilMemberReport> = emptyMap(),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dockPulse")
    val chipScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chipPulse"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Council selection chips scroll
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .weight(1f)
                .background(AuraGlassBg, RoundedCornerShape(14.dp))
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DefaultAuraEntities.forEach { entity ->
                val isSelected = currentSpeaker.id == entity.id
                val scale = if (isSelected && isSpeaking) chipScale else 1.0f
                val entityReport = reports[entity.id]
                val biasColor = when (entityReport?.bias) {
                    CouncilBias.BULLISH -> AuraGreenBull
                    CouncilBias.BEARISH -> AuraRedBear
                    CouncilBias.NEUTRAL -> AuraGoldPrimary
                    null -> entity.color
                }

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .background(
                            if (isSelected) entity.color.copy(alpha = 0.2f) else Color(0x11FFFFFF),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) entity.color else Color.Transparent,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onEntitySelected(entity) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(biasColor, CircleShape)
                        )
                        Text(
                            text = entity.name.split("-")[0],
                            color = if (isSelected) entity.color else Color(0xFFCBD5E1),
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        if (entityReport != null) {
                            Text(
                                text = "${entityReport.confidenceScore}%",
                                color = biasColor.copy(alpha = 0.8f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Terminal Toggle Button
        Box(
            modifier = Modifier
                .padding(start = 10.dp)
                .background(AuraGlassBg, RoundedCornerShape(10.dp))
                .border(1.dp, AuraGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .clickable { onToggleTerminal() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = "📟", fontSize = 12.sp)
                Text(
                    text = "AUDIT HFT",
                    color = AuraGoldLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
