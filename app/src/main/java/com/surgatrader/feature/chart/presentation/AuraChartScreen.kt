package com.surgatrader.feature.chart.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.surgatrader.core.theme.AuraBgDark
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGlassBg
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.chart.domain.model.ChartTimeframe
import com.surgatrader.feature.chart.presentation.components.CandlestickCanvas
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuraChartScreen(
    onNavigateToLotCalculator: () -> Unit = {},
    viewModel: AuraChartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snapshot = uiState.marketSnapshot
    val activeCandle = uiState.selectedCandle ?: uiState.candles.lastOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "XAUUSDc",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        letterSpacing = 1.sp
                                    ),
                                    color = AuraGoldPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .background(AuraCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.selectedTimeframe.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuraCyan,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                            Text(
                                text = "Bid: ${String.format(Locale.US, "%.3f", snapshot.bidPrice)} • Spread: ${snapshot.spreadPoints.toInt()} pts",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)),
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Tombol Cepat Kalkulator Lot
                        Box(
                            modifier = Modifier
                                .background(Color(0x33FFD700), RoundedCornerShape(8.dp))
                                .border(1.dp, AuraGoldPrimary, RoundedCornerShape(8.dp))
                                .clickable { onNavigateToLotCalculator() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "🧮 KALKULATOR",
                                color = AuraGoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuraBgDark,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = AuraBgDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. HUD OHLC Riil Terpilih / Terkini
            activeCandle?.let { candle ->
                val isBull = candle.close >= candle.open
                val chg = ((candle.close - candle.open) / candle.open) * 100.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF040A1A))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "O: ${String.format(Locale.US, "%.3f", candle.open)}", color = Color(0xFFCBD5E1), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "H: ${String.format(Locale.US, "%.3f", candle.high)}", color = AuraGreenBull, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "L: ${String.format(Locale.US, "%.3f", candle.low)}", color = AuraRedBear, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "C: ${String.format(Locale.US, "%.3f", candle.close)}", color = if (isBull) AuraGreenBull else AuraRedBear, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Text(
                        text = "${if (chg >= 0) "+" else ""}${String.format(Locale.US, "%.2f", chg)}%",
                        color = if (chg >= 0) AuraGreenBull else AuraRedBear,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // 2. Toolbar Timeframe Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "TF:", color = Color.Gray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                ChartTimeframe.values().forEach { tf ->
                    val isSelected = uiState.selectedTimeframe == tf
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) AuraGoldPrimary.copy(alpha = 0.25f) else Color(0x22FFFFFF),
                                RoundedCornerShape(6.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) AuraGoldPrimary else SlateBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { viewModel.selectTimeframe(tf) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tf.label,
                            color = if (isSelected) AuraGoldPrimary else Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // 3. Toolbar Overlays Indikator Teknis
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IndicatorChip(
                    label = "EMA 20",
                    color = AuraCyan,
                    isSelected = uiState.overlays.showEma20,
                    onClick = { viewModel.toggleEma20() }
                )
                IndicatorChip(
                    label = "EMA 50",
                    color = AuraGoldPrimary,
                    isSelected = uiState.overlays.showEma50,
                    onClick = { viewModel.toggleEma50() }
                )
                IndicatorChip(
                    label = "EMA 200",
                    color = Color(0xFF818CF8),
                    isSelected = uiState.overlays.showEma200,
                    onClick = { viewModel.toggleEma200() }
                )
                IndicatorChip(
                    label = "Pivot Points",
                    color = Color(0xFFFFB92D),
                    isSelected = uiState.overlays.showPivots,
                    onClick = { viewModel.togglePivots() }
                )
                IndicatorChip(
                    label = "Zona Aegis",
                    color = AuraGreenBull,
                    isSelected = uiState.overlays.showAegisLevels,
                    onClick = { viewModel.toggleAegisLevels() }
                )
            }

            // 4. Area Canvas Candlestick Interaktif
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF02050E))
            ) {
                CandlestickCanvas(
                    candles = uiState.candles,
                    ema20Values = uiState.ema20Values,
                    ema50Values = uiState.ema50Values,
                    ema200Values = uiState.ema200Values,
                    pivotLevels = uiState.pivotLevels,
                    aegisLevels = uiState.aegisLevels,
                    overlays = uiState.overlays,
                    onCandleSelected = { candle, price ->
                        viewModel.onCandleSelected(candle, price)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 5. Bilah Bawah Keterangan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF040A1A))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ketuk & geser untuk Crosshair • Cubit untuk Zoom",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "ATR(14): ${String.format(Locale.US, "%.3f", snapshot.atr14)}",
                    color = AuraCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun IndicatorChip(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else Color(0x15FFFFFF),
                RoundedCornerShape(6.dp)
            )
            .border(
                1.dp,
                if (isSelected) color else SlateBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(if (isSelected) color else Color.Gray, CircleShape)
            )
            Text(
                text = label,
                color = if (isSelected) color else Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
