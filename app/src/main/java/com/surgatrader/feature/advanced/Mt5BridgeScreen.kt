package com.surgatrader.feature.advanced

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.surgatrader.core.theme.CyanAccent
import com.surgatrader.core.theme.DangerRuby
import com.surgatrader.core.theme.GoldAccent
import com.surgatrader.core.theme.ObsidianBg
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.SlateCardElevated
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Mt5BridgeScreen(
    onNavigateBack: () -> Unit,
    viewModel: Mt5BridgeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Sinkronisasi MT5 MCP Bridge",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextPrimary
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SlateCard, RoundedCornerShape(12.dp))
                    .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MetaTrader 5 Local Bridge (MCP)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Menghubungkan aplikasi langsung ke terminal MT5 di komputer Anda secara aman tanpa pihak ketiga.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // URL Endpoint Field
            Text(
                text = "URL ENDPOINT BRIDGE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Quick Preset Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.endpointUrl == "http://10.0.2.2:22346/mcp",
                    onClick = { viewModel.updateUrl("http://10.0.2.2:22346/mcp") },
                    label = { Text("Emulator (10.0.2.2)", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = CyanAccent.copy(alpha = 0.25f),
                        selectedLabelColor = CyanAccent
                    )
                )
                FilterChip(
                    selected = uiState.endpointUrl == "http://127.0.0.1:22346/mcp",
                    onClick = { viewModel.updateUrl("http://127.0.0.1:22346/mcp") },
                    label = { Text("Localhost", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = CyanAccent.copy(alpha = 0.25f),
                        selectedLabelColor = CyanAccent
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = uiState.endpointUrl,
                onValueChange = { viewModel.updateUrl(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Token Auth Field
            Text(
                text = "TOKEN OTENTIKASI / BEARER KEY",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = uiState.authToken,
                onValueChange = { viewModel.updateToken(it) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanAccent,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Tombol Sinkron
            Button(
                onClick = { viewModel.testAndSync() },
                enabled = !uiState.isConnecting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = ObsidianBg
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = ObsidianBg,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menghubungkan ke MT5...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tes Koneksi & Sinkronkan Sekarang", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Error / Success Message
            if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DangerRuby.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, DangerRuby, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = DangerRuby,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                        )
                    }
                }
            }

            if (uiState.successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SafeEmerald.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, SafeEmerald, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = SafeEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.successMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LIVE ACCOUNT DATA CARD (JIKA SUDAH SINKRON)
            uiState.accountData?.let { acc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, SafeEmerald.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DATA AKUN LIVE MT5",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .background(SafeEmerald.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🟢 ONLINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SafeEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${CurrencyFormatter.formatUsc(acc.balance)}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = TextPrimary
                            )
                        )

                        Text(
                            text = "≈ ${CurrencyFormatter.formatUsd(CurrencyFormatter.uscToUsd(acc.balance))} USD",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        BridgeDetailRow("Broker", acc.broker)
                        BridgeDetailRow("Server", acc.server)
                        BridgeDetailRow("Nomor Akun (Login)", acc.login)
                        BridgeDetailRow("Tipe Akun", "${acc.name} (${acc.type.uppercase()})")
                        BridgeDetailRow("Equity", CurrencyFormatter.formatUsc(acc.equity))
                        BridgeDetailRow("Free Margin", CurrencyFormatter.formatUsc(acc.marginFree))
                        BridgeDetailRow("Mata Uang Akun", acc.currency)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun BridgeDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}
