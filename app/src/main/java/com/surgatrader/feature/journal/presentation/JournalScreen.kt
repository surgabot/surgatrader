package com.surgatrader.feature.journal.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.surgatrader.core.theme.CyanAccent
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.ObsidianBg
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.core.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Jurnal Trading",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(onClick = { /* TODO: Impor file CSV laporan dari MT5 */ }) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Impor MT5 CSV",
                            tint = CyanAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBg,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = ObsidianBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // STATISTIK KUNCI (Win Rate, Profit Factor, Expectancy)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "METRIK KINERJA TRADING",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatTile(
                                label = "Win Rate",
                                value = "${uiState.winRatePercent}%",
                                valueColor = SafeEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Profit Factor",
                                value = "${uiState.profitFactor}",
                                valueColor = CyanAccent,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Rata R:R",
                                value = "1 : ${uiState.averageRiskReward}",
                                valueColor = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatTile(
                                label = "Expectancy",
                                value = "+$${String.format("%.2f", CurrencyFormatter.uscToUsd(uiState.expectancyUsc))}",
                                valueColor = SafeEmerald,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Max Drawdown",
                                value = "${uiState.maxDrawdownPercent}%",
                                valueColor = WarnAmber,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Total Trades",
                                value = "${uiState.totalTrades}",
                                valueColor = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "CATATAN TRANSAKSI TERAKHIR",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.trades) { trade ->
                TradeHistoryCard(trade = trade)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        )
    }
}

@Composable
fun TradeHistoryCard(trade: JournalTradeItem) {
    val isProfit = trade.pnlUsc >= 0
    val pnlColor = if (isProfit) SafeEmerald else DangerRuby
    val sign = if (isProfit) "+" else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (trade.type == "BUY") SafeEmerald.copy(alpha = 0.2f) else DangerRuby.copy(alpha = 0.2f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = trade.type,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (trade.type == "BUY") SafeEmerald else DangerRuby
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${trade.symbol} • ${trade.volume} Lot",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Text(
                    text = "$sign${CurrencyFormatter.formatUsc(trade.pnlUsc)} ($sign$${String.format("%.2f", CurrencyFormatter.uscToUsd(trade.pnlUsc))})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = pnlColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tag Setup & Emosi
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .background(CyanAccent.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎯 ${trade.setupTag}",
                        style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent, fontSize = 10.sp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SafeEmerald.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🧠 ${trade.emotionTag}",
                        style = MaterialTheme.typography.labelSmall.copy(color = SafeEmerald, fontSize = 10.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = trade.notes,
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }
    }
}
