package com.surgatrader.feature.riskradar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.CyanAccent
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber

@Composable
fun CorrelationHeatmap(
    modifier: Modifier = Modifier
) {
    val pairs = listOf("XAU", "EUR", "GBP", "JPY", "DXY")
    // Matrix korelasi terhadap XAUUSD dan USD pairs
    val matrix = listOf(
        listOf(1.00, 0.78, 0.65, -0.42, -0.85),  // XAU
        listOf(0.78, 1.00, 0.88, -0.55, -0.92),  // EUR (EURUSD)
        listOf(0.65, 0.88, 1.00, -0.48, -0.84),  // GBP (GBPUSD)
        listOf(-0.42, -0.55, -0.48, 1.00, 0.68), // JPY (USDJPY)
        listOf(-0.85, -0.92, -0.84, 0.68, 1.00)  // DXY (Dollar Index)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateCard, RoundedCornerShape(16.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "🔥 Heatmap Korelasi Pair & Emas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Text(
                text = "Korelasi > +0.70 melipatgandakan risiko keranjang. XAU & EUR berkorelasi positif tinggi (+0.78) terhadap pelemahan USD.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary,
                    fontSize = 11.sp
                ),
                modifier = Modifier.padding(bottom = 12.dp, top = 2.dp)
            )

            // Header baris
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1.2f)) // Corner kosong
                pairs.forEach { p ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Baris tabel
            pairs.forEachIndexed { rowIndex, rowName ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nama Baris
                    Box(
                        modifier = Modifier.weight(1.2f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = rowName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    // Sel Korelasi
                    matrix[rowIndex].forEach { value ->
                        val cellBg = when {
                            value == 1.0 -> SlateBorder.copy(alpha = 0.3f)
                            value >= 0.70 -> DangerRuby.copy(alpha = 0.28f)
                            value <= -0.70 -> CyanAccent.copy(alpha = 0.25f)
                            value in -0.30..0.30 -> SafeEmerald.copy(alpha = 0.20f)
                            else -> WarnAmber.copy(alpha = 0.22f)
                        }

                        val textColor = when {
                            value == 1.0 -> TextSecondary
                            value >= 0.70 -> DangerRuby
                            value <= -0.70 -> CyanAccent
                            value in -0.30..0.30 -> SafeEmerald
                            else -> WarnAmber
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(28.dp)
                                .padding(1.dp)
                                .background(cellBg, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format("%.2f", value),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = textColor
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
