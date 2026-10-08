package com.surgatrader.feature.aura.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGlassBg
import com.surgatrader.core.theme.AuraGlassBorder
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.feature.aura.domain.model.AuraState
import com.surgatrader.feature.aura.domain.model.DefaultAuraScript

@Composable
fun AuraLeftPanel(
    state: AuraState,
    onToggleMinimize: () -> Unit,
    onPrevStep: () -> Unit,
    onReplayStep: () -> Unit,
    onNextStep: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLeftPanelCollapsed) {
        // Collapsed floating pill
        Box(
            modifier = modifier
                .background(AuraGlassBg, RoundedCornerShape(12.dp))
                .border(1.dp, state.currentSpeaker.color, RoundedCornerShape(12.dp))
                .clickable { onToggleMinimize() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "📊", fontSize = 14.sp)
                Text(
                    text = "${state.currentSpeaker.name} (BUKA SINYAL)",
                    color = state.currentSpeaker.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(text = "⛶", color = Color.White, fontSize = 13.sp)
            }
        }
    } else {
        // Expanded Panel
        Box(
            modifier = modifier
                .background(AuraGlassBg, RoundedCornerShape(16.dp))
                .border(1.dp, state.currentSpeaker.color.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "● SINYAL ALGORITMIK AKTIF",
                            color = AuraGoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${state.currentStepIndex + 1} / ${state.totalStepsCount}",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Minimize Button
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0x44FFFFFF), RoundedCornerShape(6.dp))
                            .clickable { onToggleMinimize() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "−", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Speaker Meta Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(state.currentSpeaker.color, CircleShape)
                        )
                        Text(
                            text = state.currentSpeaker.name,
                            color = state.currentSpeaker.color,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }

                    // Animated Waveform Visualizer
                    WaveformVisualizer(
                        isSpeaking = state.isSpeaking,
                        barColor = state.currentSpeaker.color
                    )
                }

                // Speaker Role & Stage
                Text(
                    text = state.currentSpeaker.title,
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1
                )

                val memberReport = state.activeCouncilReports[state.currentSpeaker.id]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0x1A00F2FE), RoundedCornerShape(6.dp))
                            .border(1.dp, AuraCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = state.currentStep.stage,
                            color = AuraCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (memberReport != null) {
                        val biasColor = when (memberReport.bias) {
                            com.surgatrader.feature.aura.domain.model.CouncilBias.BULLISH -> com.surgatrader.core.theme.AuraGreenBull
                            com.surgatrader.feature.aura.domain.model.CouncilBias.BEARISH -> com.surgatrader.core.theme.AuraRedBear
                            com.surgatrader.feature.aura.domain.model.CouncilBias.NEUTRAL -> AuraGoldPrimary
                        }
                        Text(
                            text = "BIAS: ${memberReport.bias.label} (${memberReport.confidenceScore}%)",
                            color = biasColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Dialogue Content Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x88000000), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "\"${state.currentStep.text}\"",
                        color = Color(0xFFF8FAFC),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Voice Status Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x11FFD700), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = if (state.isSpeaking) "🔊" else "⏸️", fontSize = 11.sp)
                        Text(
                            text = if (state.isSpeaking) "Membaca sintesis analisis Bahasa Indonesia..." else "Selesai dibaca / Siap",
                            color = AuraGoldLight,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = "${state.currentStepIndex + 1}/6",
                        color = AuraGoldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Navigation Controls Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PanelActionButton(
                        text = "⏮️ Mundur",
                        onClick = onPrevStep,
                        modifier = Modifier.weight(1f)
                    )
                    PanelActionButton(
                        text = "🔄 Ulangi",
                        onClick = onReplayStep,
                        modifier = Modifier.weight(1f)
                    )
                    PanelActionButton(
                        text = "⏭️ Lanjut",
                        onClick = onNextStep,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WaveformVisualizer(
    isSpeaking: Boolean,
    barColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val delays = listOf(0, 150, 300, 450, 200, 350)
        delays.forEachIndexed { idx, delayMs ->
            val heightFraction by infiniteTransition.animateFloat(
                initialValue = 4f,
                targetValue = if (isSpeaking) 16f else 4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(400 + idx * 80, delayMillis = delayMs),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "barHeight$idx"
            )

            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height(heightFraction.dp)
                    .background(if (isSpeaking) barColor else Color(0x44FFFFFF), RoundedCornerShape(1.dp))
            )
        }
    }
}

@Composable
private fun PanelActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0x22FFFFFF), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
