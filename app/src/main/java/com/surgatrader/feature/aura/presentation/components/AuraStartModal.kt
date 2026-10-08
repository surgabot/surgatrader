package com.surgatrader.feature.aura.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.feature.aura.domain.model.AuraState

@Composable
fun AuraStartModal(
    state: AuraState,
    onCommence: () -> Unit,
    onOpenConnection: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .background(Color(0xF5020614), RoundedCornerShape(20.dp))
            .border(1.dp, AuraGoldPrimary, RoundedCornerShape(20.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "AKSES SINDIKAT TRADING EMAS SUPER INTELIJEN",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )

            // Pemeriksaan Status Bridge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (state.isMt5Connected) Color(0x2200FF88) else Color(0x22FFB92D),
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (state.isMt5Connected) AuraGreenBull else Color(0xFFFFB92D),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onOpenConnection() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (state.isMt5Connected) AuraGreenBull else Color(0xFFFFB92D),
                                    CircleShape
                                )
                        )
                        Text(
                            text = if (state.isMt5Connected)
                                "BRIDGE MT5 AKTIF (${state.mt5Server})"
                            else
                                "MODE DEMO • BUKAN DATA ASLI",
                            color = if (state.isMt5Connected) AuraGreenBull else Color(0xFFFFD15C),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = if (state.isMt5Connected) "${state.latencyMs.toLong()} MS" else "⚙️ UBAH",
                        color = AuraCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Text(
                text = "Anda memasuki pusat komando Aura Quantum. Lima entitas kecerdasan buatan kuantitatif tertinggi bermusyawarah dalam Bahasa Indonesia untuk menganalisis dan mengeksekusi strategi likuiditas pasar emas dunia (XAU/USDc).\n\nSemua metrik, 16 candle M5, dan volatilitas ATR dihitung dari data sungguhan tanpa angka karangan.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )

            // Tombol Mulai Orkestrasi
            Box(
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFFFD700), Color(0xFFFF8C00))),
                        RoundedCornerShape(30.dp)
                    )
                    .clickable { onCommence() }
                    .padding(horizontal = 28.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MULAI ORKESTRASI KUANTUM",
                    color = Color(0xFF02050E),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
