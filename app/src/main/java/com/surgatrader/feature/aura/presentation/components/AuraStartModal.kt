package com.surgatrader.feature.aura.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.surgatrader.core.theme.AuraGoldPrimary

@Composable
fun AuraStartModal(
    onCommence: () -> Unit,
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

            Text(
                text = "Anda memasuki pusat komando Aura Quantum. Lima entitas kecerdasan buatan kuantitatif tertinggi bermusyawarah dalam Bahasa Indonesia untuk mengeksekusi strategi arbitrase likuiditas pasar emas dunia (XAU/USD).\n\nKolom sinyal dan kolom kontrol suara dapat di-minimize kapan saja agar tampilan cetak biru trading tetap luas dan leluasa.",
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )

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
                    text = "MULAI ORKESTRASI TRADING",
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
