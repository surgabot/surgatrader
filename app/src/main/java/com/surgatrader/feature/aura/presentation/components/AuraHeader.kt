package com.surgatrader.feature.aura.presentation.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
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
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import com.surgatrader.feature.aura.domain.model.AuraState
import java.util.Locale

@Composable
fun AuraHeader(
    state: AuraState,
    onOpenConnectionSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Box
            Box(
                modifier = Modifier
                    .background(AuraGlassBg, RoundedCornerShape(12.dp))
                    .border(1.dp, AuraGlassBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .scale(pulseScale)
                                .background(AuraGoldPrimary, CircleShape)
                        )
                        Text(
                            text = "AURA QUANTUM",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        text = "SINDIKAT SUPER INTELIJEN • XAU/USD QUANT",
                        color = AuraGoldLight.copy(alpha = 0.75f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Mode & Connection Badge (Clickable to open Connection screen)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.isDemoMode) {
                    Box(
                        modifier = Modifier
                            .background(Color(0x33FFB92D), RoundedCornerShape(20.dp))
                            .border(1.dp, Color(0xFFFFB92D), RoundedCornerShape(20.dp))
                            .clickable { onOpenConnectionSettings() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 10.sp)
                            Text(
                                text = "MODE DEMO • BUKAN DATA ASLI",
                                color = Color(0xFFFFD15C),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0x2200FF88), Color(0x2200F2FE))
                                ),
                                RoundedCornerShape(20.dp)
                            )
                            .border(1.dp, AuraGreenBull.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { onOpenConnectionSettings() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(AuraGreenBull, CircleShape)
                            )
                            Text(
                                text = "LIVE • ${state.mt5Server}",
                                color = AuraGreenBull,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Settings gear
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(AuraGlassBg, CircleShape)
                        .border(1.dp, AuraGlassBorder, CircleShape)
                        .clickable { onOpenConnectionSettings() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚙️", fontSize = 13.sp)
                }
            }
        }

        // Horizontal Market Ticker Bar
        val scrollState = rememberScrollState()
        val priceColor by animateColorAsState(
            targetValue = if (state.isTickPositive) AuraGreenBull else AuraRedBear,
            label = "priceColor"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuraGlassBg, RoundedCornerShape(24.dp))
                .border(1.dp, AuraGlassBorder.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. XAUUSDc Bid / Ask 3 Desimal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "🥇 XAUUSDc",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "BID ${com.surgatrader.core.util.CurrencyFormatter.formatPrice(state.bidPriceUsc, 3)} / ASK ${com.surgatrader.core.util.CurrencyFormatter.formatPrice(state.askPriceUsc, 3)}",
                        color = priceColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 2. SPREAD (Points, 1 pt = 0.001)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "SPREAD:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${state.spreadPoints.toInt()} PT",
                        color = AuraCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 3. PERUBAHAN HARIAN %
                val isChangePositive = state.dailyChangePercent >= 0
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "24H:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${if (isChangePositive) "+" else ""}${String.format(Locale.US, "%.2f", state.dailyChangePercent)}%",
                        color = if (isChangePositive) AuraGreenBull else AuraRedBear,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 4. FLOATING P/L
                val isPnlPositive = state.floatingProfitUsc >= 0
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "P/L:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${if (isPnlPositive) "+" else ""}${String.format(Locale.US, "%.2f", state.floatingProfitUsc)} USC",
                        color = if (isPnlPositive) AuraGreenBull else AuraRedBear,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 5. EKUITAS USC & USD
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "EKUITAS:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${String.format(Locale.US, "%,.2f", state.mt5EquityUsc)} USC (~$${String.format(Locale.US, "%,.2f", state.mt5EquityUsc / 100.0)})",
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 6. LATENCY BRIDGE TERUKUR
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "PING:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = if (state.latencyMs > 0) "${state.latencyMs.toLong()} MS" else "DEMO",
                        color = if (state.latencyMs in 1.0..100.0) AuraGreenBull else AuraCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 7. JAM WIB REAL-TIME
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "WAKTU:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = state.wibClock,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // 8. SESI PASAR AKTIF
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "SESI:", color = Color(0xFF94A3B8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = state.activeSession,
                        color = AuraGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
