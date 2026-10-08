package com.surgatrader.feature.aura.presentation.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGlassBg
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import com.surgatrader.feature.aura.domain.model.AuraState
import java.util.Locale

@Composable
fun AuraRightPanel(
    state: AuraState,
    onToggleMinimize: () -> Unit,
    onToggleAutoPlay: () -> Unit,
    onStopAudio: () -> Unit,
    onToggleMute: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onExecuteQuantumOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isRightPanelCollapsed) {
        // Collapsed floating pill
        Box(
            modifier = modifier
                .background(AuraGlassBg, RoundedCornerShape(12.dp))
                .border(1.dp, AuraGoldPrimary, RoundedCornerShape(12.dp))
                .clickable { onToggleMinimize() }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⚙️", fontSize = 14.sp)
                Text(
                    text = "KONTROL SUARA (BUKA)",
                    color = AuraGoldPrimary,
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
                .border(1.dp, AuraGoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                    Text(
                        text = "KONTROL AUDIO & EKSEKUSI",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🇮🇩 ID",
                            color = AuraGoldPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
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
                }

                // Autoplay Toggle Mode
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "ALUR TRANSMISI:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            text = if (state.isAutoPlay) "PUTAR TERUS" else "BERHENTI TIAP TEKS",
                            color = if (state.isAutoPlay) AuraGoldPrimary else AuraCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (state.isAutoPlay)
                                    Brush.horizontalGradient(listOf(Color(0x33FFD700), Color(0x44FFA500)))
                                else
                                    Brush.horizontalGradient(listOf(Color(0x2200F2FE), Color(0x330077B6))),
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (state.isAutoPlay) AuraGoldPrimary else AuraCyan,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onToggleAutoPlay() }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = if (state.isAutoPlay) "▶️" else "⏸️", fontSize = 12.sp)
                            Text(
                                text = if (state.isAutoPlay) "PUTAR TERUS (OTOMATIS)" else "BERHENTI PER TEKS (JEDA)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Audio Quick Actions (Stop & Mute)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                            .clickable { onStopAudio() }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⏹️ Stop Suara", color = Color(0xFFE2E8F0), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (state.isMuted) Color(0x33FF3366) else Color(0x22FFFFFF),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                1.dp,
                                if (state.isMuted) AuraRedBear else Color(0x33FFFFFF),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onToggleMute() }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.isMuted) "🔇 Suara Bisu" else "🔊 Suara Aktif",
                            color = if (state.isMuted) Color(0xFFFCA5A5) else Color(0xFFE2E8F0),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Speed Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "KECEPATAN BICARA:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${state.speechSpeed}x", color = AuraGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.85f to "0.85x", 1.0f to "1.0x (Normal)", 1.2f to "1.2x (Cepat)").forEach { (speed, label) ->
                            val isSelected = state.speechSpeed == speed
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) AuraGoldPrimary else Color(0x1AFFFFFF),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) AuraGoldPrimary else Color(0x2AFFFFFF),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { onSpeedSelected(speed) }
                                    .padding(vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF02050E) else Color(0xFFCBD5E1),
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Volume Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "VOLUME SUARA:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${(state.volume * 100).toInt()}%", color = AuraCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Slider(
                        value = state.volume,
                        onValueChange = onVolumeChanged,
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(
                            thumbColor = AuraGoldPrimary,
                            activeTrackColor = AuraGoldPrimary,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.height(26.dp)
                    )
                }

                // Exness MT5 Cent Quick Execution Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x77000000), RoundedCornerShape(10.dp))
                        .border(1.dp, AuraGreenBull.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "EXNESS STANDAR CENT", color = AuraGreenBull, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text(text = "REAL-37", color = Color(0xFF94A3B8), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Saldo Akun:", color = Color(0xFF94A3B8), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "${String.format(Locale.US, "%.2f", state.mt5BalanceUsc)} USC (~$${String.format(Locale.US, "%.2f", state.mt5BalanceUsc / 100.0)})",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Instant Quantum Order Execution Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(listOf(Color(0xFF00FF88), Color(0xFF00F2FE))),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onExecuteQuantumOrder() }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡ EKSEKUSI SINYAL QUANTUM (0.01 LOT)",
                                color = Color(0xFF02050E),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
