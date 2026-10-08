package com.surgatrader.feature.journal.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.surgatrader.core.theme.CyanAccent
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.journal.data.TradeJournalEntity
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: JournalViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isAddDialogVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "JURNAL TRADING",
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
                                    .border(1.dp, AuraCyan, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ROOM SQLITE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "Audit Performa Institusional • XAUUSDc Exness Cent",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    // Tombol Tambah Transaksi
                    IconButton(onClick = { isAddDialogVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Catat Transaksi",
                            tint = AuraGoldPrimary
                        )
                    }
                    // Tombol Ekspor CSV
                    IconButton(onClick = {
                        val csv = viewModel.exportToCsv()
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Journal_CSV", csv))
                        Toast.makeText(context, "CSV Jurnal disalin ke Clipboard!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(
                            imageVector = Icons.Default.FileUpload,
                            contentDescription = "Ekspor CSV",
                            tint = AuraCyan
                        )
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))

                // Banner Notifikasi
                AnimatedVisibility(
                    visible = uiState.statusMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    uiState.statusMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x3300FF88), RoundedCornerShape(10.dp))
                                .border(1.dp, AuraGreenBull, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = msg,
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.dismissStatusMessage() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                // STATISTIK KUNCI AUDIT TRADING KUANTUM
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AuraGoldPrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = AuraGlassBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "METRIK KINERJA NYATA (AUDIT DEWAN)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AuraGoldLight,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Text(
                                text = "${uiState.winningTrades}W / ${uiState.losingTrades}L",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AuraCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatTile(
                                label = "Win Rate",
                                value = "${uiState.winRatePercent}%",
                                valueColor = if (uiState.winRatePercent >= 50.0) SafeEmerald else DangerRuby,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Profit Factor",
                                value = "${uiState.profitFactor}",
                                valueColor = AuraCyan,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Total P/L (USC)",
                                value = "${if (uiState.netPnlUsc >= 0) "+" else ""}${uiState.netPnlUsc}c",
                                valueColor = if (uiState.netPnlUsc >= 0) SafeEmerald else DangerRuby,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatTile(
                                label = "Total Net ($ USD)",
                                value = "${if (uiState.netPnlUsd >= 0) "+$" else "-$"}${String.format(Locale.US, "%.2f", kotlin.math.abs(uiState.netPnlUsd))}",
                                valueColor = if (uiState.netPnlUsd >= 0) SafeEmerald else DangerRuby,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Expectancy / Trade",
                                value = "${if (uiState.expectancyUsc >= 0) "+" else ""}${uiState.expectancyUsc}c",
                                valueColor = AuraGoldPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            StatTile(
                                label = "Max Drawdown",
                                value = "${uiState.maxDrawdownPercent}%",
                                valueColor = if (uiState.maxDrawdownPercent > 5.0) DangerRuby else WarnAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CATATAN TRANSAKSI TERAKHIR (${uiState.totalTrades})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Auto-save Room SQLite",
                        style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan),
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(uiState.trades, key = { it.id }) { trade ->
                TradeHistoryCard(
                    trade = trade,
                    onDelete = { viewModel.deleteTrade(trade.id) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        if (isAddDialogVisible) {
            AddTradeDialog(
                onDismiss = { isAddDialogVisible = false },
                onAddTrade = { type, open, close, lot, pnl, setup, emotion, notes ->
                    viewModel.addTrade(type, open, close, lot, pnl, setup, emotion, notes)
                    isAddDialogVisible = false
                },
                onImportFromAegis = { pnl, notes ->
                    viewModel.addTradeFromAegisSignal(pnl, notes)
                    isAddDialogVisible = false
                }
            )
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
            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
            fontFamily = FontFamily.Monospace
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
fun TradeHistoryCard(
    trade: TradeJournalEntity,
    onDelete: () -> Unit
) {
    val isProfit = trade.isProfit
    val pnlColor = if (isProfit) SafeEmerald else DangerRuby
    val sign = if (isProfit) "+" else ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isProfit) SafeEmerald.copy(alpha = 0.3f) else DangerRuby.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
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
                                color = if (trade.type == "BUY") SafeEmerald else DangerRuby,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${trade.symbol} • ${trade.volumeLot} Lot",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$sign${CurrencyFormatter.formatUsc(trade.pnlUsc)} ($sign$${String.format(Locale.US, "%.2f", trade.pnlUsd)})",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = pnlColor
                        )
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).padding(start = 4.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Harga Open -> Close
            Text(
                text = "${String.format(Locale.US, "%.3f", trade.openPrice)} ➔ ${String.format(Locale.US, "%.3f", trade.closePrice)} • #${trade.ticket}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF94A3B8)
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tag Setup, Skor Dewan, dan Emosi
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .background(AuraCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🎯 ${trade.setupTag}",
                        style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    )
                }

                if (trade.dewanConfluenceScore > 0) {
                    Box(
                        modifier = Modifier
                            .background(AuraGoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "⚡ Skor ${trade.dewanConfluenceScore}",
                            style = MaterialTheme.typography.labelSmall.copy(color = AuraGoldPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(SafeEmerald.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🧠 ${trade.emotionTag}",
                        style = MaterialTheme.typography.labelSmall.copy(color = SafeEmerald, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = trade.notes,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFCBD5E1))
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = trade.formattedDateWib,
                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            )
        }
    }
}

@Composable
fun AddTradeDialog(
    onDismiss: () -> Unit,
    onAddTrade: (String, Double, Double, Double, Double, String, String, String) -> Unit,
    onImportFromAegis: (Double, String) -> Unit
) {
    var type by remember { mutableStateOf("BUY") }
    var openPriceText by remember { mutableStateOf("4100.234") }
    var closePriceText by remember { mutableStateOf("4108.500") }
    var lotText by remember { mutableStateOf("0.02") }
    var pnlUscText by remember { mutableStateOf("165.32") }
    var setupText by remember { mutableStateOf("Breakout Sesi London") }
    var notesText by remember { mutableStateOf("Eksekusi disiplin sesuai rencana dewan.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Catat Transaksi Jurnal Baru",
                color = AuraGoldPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tombol Cepat Impor Aegis
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33FFD700), RoundedCornerShape(8.dp))
                        .border(1.dp, AuraGoldPrimary, RoundedCornerShape(8.dp))
                        .clickable {
                            val pnl = pnlUscText.toDoubleOrNull() ?: 200.0
                            onImportFromAegis(pnl, notesText)
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡ Impor Otomatis dari Sinyal Aegis Terkini",
                        color = AuraGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { type = "BUY" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "BUY") SafeEmerald else Color.DarkGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("BUY", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { type = "SELL" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (type == "SELL") DangerRuby else Color.DarkGray
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SELL", fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = openPriceText,
                    onValueChange = { openPriceText = it },
                    label = { Text("Harga Open (USC/3 des)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                OutlinedTextField(
                    value = closePriceText,
                    onValueChange = { closePriceText = it },
                    label = { Text("Harga Close") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = lotText,
                        onValueChange = { lotText = it },
                        label = { Text("Lot Cent") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = pnlUscText,
                        onValueChange = { pnlUscText = it },
                        label = { Text("P/L (USC)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                OutlinedTextField(
                    value = setupText,
                    onValueChange = { setupText = it },
                    label = { Text("Setup Trading") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Catatan Eksekusi") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val open = openPriceText.toDoubleOrNull() ?: 4100.0
                    val close = closePriceText.toDoubleOrNull() ?: 4105.0
                    val lot = lotText.toDoubleOrNull() ?: 0.02
                    val pnl = pnlUscText.toDoubleOrNull() ?: 100.0
                    onAddTrade(type, open, close, lot, pnl, setupText, "Disiplin", notesText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AuraGoldPrimary)
            ) {
                Text("Simpan ke SQLite", color = Color(0xFF02050E), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("Batal")
            }
        },
        containerColor = Color(0xFF040A1A)
    )
}
