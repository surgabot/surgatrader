package com.surgatrader.feature.riskradar.presentation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.surgatrader.core.theme.GoldAccent
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.SlateCardElevated
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.riskradar.domain.model.OrderDirection
import com.surgatrader.feature.riskradar.domain.model.SlInputMode
import com.surgatrader.feature.riskradar.domain.model.TpInputMode
import com.surgatrader.feature.riskradar.presentation.components.CorrelationHeatmap
import com.surgatrader.feature.riskradar.presentation.components.DangerousHoursAlertCard
import com.surgatrader.feature.riskradar.presentation.components.RadarSpiderChart
import com.surgatrader.feature.riskradar.presentation.components.RiskScoreGauge
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskRadarScreen(
    onNavigateToSymbolSpec: () -> Unit,
    initialTab: Int = 0,
    viewModel: RiskRadarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spec = uiState.symbolSpec

    LaunchedEffect(initialTab) {
        viewModel.selectTab(initialTab)
    }

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
                                text = "AURA RISK & LOT",
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
                                    text = "EXNESS CENT",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AuraCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Text(
                            text = "${spec.profileName} • 3 Digits (1 pt = 0.001) • 100 USC = $1",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSymbolSpec) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Spesifikasi Simbol",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Segmented Tabs Cyberpunk
            SecondaryTabRow(
                selectedTabIndex = uiState.activeTab,
                containerColor = Color(0xFF040A1A),
                contentColor = AuraGoldPrimary,
                indicator = {
                    TabRowDefaults.SecondaryIndicator(
                        color = AuraGoldPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Text(
                            text = "🧮 Kalkulator Lot Cent",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == 0) AuraGoldPrimary else TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                )
                Tab(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Text(
                            text = "🛡️ Radar Risiko 5D/6D",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == 1) AuraCyan else TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                )
            }

            if (uiState.activeTab == 0) {
                LotCalculatorTab(
                    uiState = uiState,
                    viewModel = viewModel,
                    onNavigateToSymbolSpec = onNavigateToSymbolSpec
                )
            } else {
                RiskRadarAssessmentTab(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun LotCalculatorTab(
    uiState: RiskRadarUiState,
    viewModel: RiskRadarViewModel,
    onNavigateToSymbolSpec: () -> Unit
) {
    val spec = uiState.symbolSpec
    val params = uiState.lotParams
    val result = uiState.lotResult
    val snapshot = uiState.marketSnapshot
    val consensus = snapshot.latestConsensus

    var balanceText by remember(params.balance) { mutableStateOf(params.balance.toInt().toString()) }
    var entryPriceText by remember(params.entryPrice) { mutableStateOf(String.format(Locale.US, "%.3f", params.entryPrice)) }
    var slInputText by remember(params.slInput) {
        mutableStateOf(
            if (params.slMode == SlInputMode.PRICE) String.format(Locale.US, "%.3f", params.slInput)
            else params.slInput.toInt().toString()
        )
    }
    var tpInputText by remember(params.tpInput) {
        mutableStateOf(
            if (params.tpMode == TpInputMode.PRICE) String.format(Locale.US, "%.3f", params.tpInput)
            else params.tpInput.toString()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Status Notification Banner (jika aksi sync/import berhasil)
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

        // Action Bar Kuantum: Impor Rekomendasi Aegis & Sinkron Pasar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Tombol 1: Impor dari Sinyal Aegis
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .background(
                        if (consensus != null) Color(0x33FFD700) else Color(0x22FFFFFF),
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (consensus != null) AuraGoldPrimary else SlateBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.importAegisRecommendation() }
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "⚡", fontSize = 13.sp)
                    Column {
                        Text(
                            text = "IMPOR SINYAL AEGIS",
                            color = if (consensus != null) AuraGoldLight else Color.Gray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (consensus != null) "${consensus.consensusBias.label} • Skor ${consensus.confluenceScore}" else "Menunggu Dewan",
                            color = if (consensus != null) AuraCyan else Color.DarkGray,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Tombol 2: Sinkronkan dengan Pasar Terkini
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0x2200F2FE), RoundedCornerShape(10.dp))
                    .border(1.dp, AuraCyan.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .clickable { viewModel.syncWithMarket() }
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                    Column {
                        Text(
                            text = "SINKRON PASAR",
                            color = AuraCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${String.format(Locale.US, "%.3f", snapshot.bidPrice)} c",
                            color = Color(0xFFCBD5E1),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Badge Simbol Aktif & Parameter Broker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuraGlassBg, RoundedCornerShape(12.dp))
                .border(1.dp, AuraGoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                .clickable { onNavigateToSymbolSpec() }
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(SafeEmerald, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "${spec.symbolName} (${spec.profileName})",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "Leverage 1:${spec.leverage.toInt()} • Kontrak ${spec.contractSize.toInt()} oz",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
                Text(
                    text = "Spread: ${spec.spreadPoints} pts >",
                    style = MaterialTheme.typography.labelSmall.copy(color = AuraCyan, fontWeight = FontWeight.Bold),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Saldo Akun (Dual display USC & USD)
        Text(
            text = "SALDO AKUN (CENT USC)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AuraGoldLight,
                fontFamily = FontFamily.Monospace
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = balanceText,
            onValueChange = {
                balanceText = it
                it.toDoubleOrNull()?.let { b -> viewModel.updateBalance(b) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard
            ),
            shape = RoundedCornerShape(10.dp),
            supportingText = {
                Text(
                    text = "Konversi: ${CurrencyFormatter.formatDualBalance(params.balance)}",
                    style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent),
                    fontFamily = FontFamily.Monospace
                )
            }
        )

        // Preset Chips Saldo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10000.0 to "$100", 25000.0 to "$250", 50000.0 to "$500", 100000.0 to "$1,000").forEach { (usc, label) ->
                FilterChip(
                    selected = params.balance == usc,
                    onClick = {
                        balanceText = usc.toInt().toString()
                        viewModel.updateBalance(usc)
                    },
                    label = { Text("$label (${usc.toInt()}c)", fontSize = 10.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = AuraCyan.copy(alpha = 0.25f),
                        selectedLabelColor = AuraCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Persentase Risiko
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RISIKO PER TRADE: ${params.riskPercent}%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AuraGoldLight,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "= ${CurrencyFormatter.formatDualBalance(params.balance * params.riskPercent / 100.0)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = SafeEmerald,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(0.5, 1.0, 1.5, 2.0, 3.0).forEach { r ->
                FilterChip(
                    selected = params.riskPercent == r,
                    onClick = { viewModel.updateRiskPercent(r) },
                    label = { Text("$r%", fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = AuraCyan.copy(alpha = 0.25f),
                        selectedLabelColor = AuraCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Arah Order & Entry Price
        Row(modifier = Modifier.fillMaxWidth()) {
            // Tombol BUY
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(
                        if (params.direction == OrderDirection.BUY) SafeEmerald.copy(alpha = 0.22f) else SlateCard,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (params.direction == OrderDirection.BUY) SafeEmerald else SlateBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.updateDirection(OrderDirection.BUY) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "BUY",
                        tint = if (params.direction == OrderDirection.BUY) SafeEmerald else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BUY / Long",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (params.direction == OrderDirection.BUY) SafeEmerald else TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Tombol SELL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(
                        if (params.direction == OrderDirection.SELL) DangerRuby.copy(alpha = 0.22f) else SlateCard,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        if (params.direction == OrderDirection.SELL) DangerRuby else SlateBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.updateDirection(OrderDirection.SELL) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "SELL",
                        tint = if (params.direction == OrderDirection.SELL) DangerRuby else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SELL / Short",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (params.direction == OrderDirection.SELL) DangerRuby else TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = entryPriceText,
            onValueChange = {
                entryPriceText = it
                it.toDoubleOrNull()?.let { p -> viewModel.updateEntryPrice(p) }
            },
            label = { Text("Harga Entry Saat Ini (${spec.digits} Digit Desimal)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard
            ),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Input Stop Loss
        Text(
            text = "STOP LOSS (SL)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AuraGoldLight,
                fontFamily = FontFamily.Monospace
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SlInputMode.values().forEach { mode ->
                FilterChip(
                    selected = params.slMode == mode,
                    onClick = { viewModel.updateSlMode(mode) },
                    label = { Text(mode.label, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = AuraCyan.copy(alpha = 0.25f),
                        selectedLabelColor = AuraCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = slInputText,
            onValueChange = {
                slInputText = it
                it.toDoubleOrNull()?.let { sl -> viewModel.updateSlInput(sl) }
            },
            label = {
                Text(
                    when (params.slMode) {
                        SlInputMode.POINTS -> "Jarak Stop Loss (Points, misal: 500)"
                        SlInputMode.PRICE -> "Level Harga SL (misal: 4098.059)"
                        SlInputMode.MONEY -> "Batas Rugi Maksimal (USC)"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AuraCyan,
                unfocusedBorderColor = SlateBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard
            ),
            shape = RoundedCornerShape(10.dp),
            supportingText = {
                Text(
                    text = "Spread (${spec.spreadPoints} pts) dihitung: Total SL efektif = ${result.effectiveSlPoints.toInt()} pts",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                    fontFamily = FontFamily.Monospace
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Input Take Profit
        Text(
            text = "TAKE PROFIT (TP)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AuraGoldLight,
                fontFamily = FontFamily.Monospace
            )
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TpInputMode.values().forEach { mode ->
                FilterChip(
                    selected = params.tpMode == mode,
                    onClick = { viewModel.updateTpMode(mode) },
                    label = { Text(mode.label, fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = AuraCyan.copy(alpha = 0.25f),
                        selectedLabelColor = AuraCyan
                    )
                )
            }
        }

        if (params.tpMode != TpInputMode.NONE) {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = tpInputText,
                onValueChange = {
                    tpInputText = it
                    it.toDoubleOrNull()?.let { tp -> viewModel.updateTpInput(tp) }
                },
                label = {
                    Text(
                        when (params.tpMode) {
                            TpInputMode.RATIO -> "Rasio R:R (misal: 2.0 untuk 1:2)"
                            TpInputMode.POINTS -> "Jarak TP dalam Points (misal: 1000)"
                            TpInputMode.PRICE -> "Level Harga TP (misal: 4103.500)"
                            TpInputMode.NONE -> ""
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AuraCyan,
                    unfocusedBorderColor = SlateBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SlateCard,
                    unfocusedContainerColor = SlateCard
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ================= KARTU HASIL KALKULASI HUD KUANTUM =================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, AuraGoldPrimary.copy(alpha = 0.7f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header Rekomendasi Lot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LOT REKOMENDASI (EXNESS CENT)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraGoldLight,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                        Text(
                            text = "${CurrencyFormatter.formatLot(result.recommendedLot)} Lot",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = AuraCyan
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(AuraGreenBull.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, AuraGreenBull, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "R:R 1 : ${result.riskRewardRatio}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuraGreenBull,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }

                // Peringatan jika ada
                if (result.warningMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WarnAmber.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, WarnAmber, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarnAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = result.warningMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Metrik Keuangan Nyata
                ResultRow(
                    label = "Risiko Toleransi (SL)",
                    value = "${CurrencyFormatter.formatUsc(result.riskAmountUsc)} ($${String.format(Locale.US, "%.2f", result.riskAmountUsd)})",
                    valueColor = DangerRuby
                )
                ResultRow(
                    label = "Potensi Keuntungan (TP)",
                    value = "${CurrencyFormatter.formatUsc(result.potentialProfitUsc)} ($${String.format(Locale.US, "%.2f", result.potentialProfitUsd)})",
                    valueColor = SafeEmerald
                )
                ResultRow(
                    label = "Level Harga Stop Loss",
                    value = CurrencyFormatter.formatPrice(result.slPrice, spec.digits),
                    valueColor = DangerRuby
                )
                if (params.tpMode != TpInputMode.NONE) {
                    ResultRow(
                        label = "Level Harga Take Profit",
                        value = CurrencyFormatter.formatPrice(result.tpPrice, spec.digits),
                        valueColor = SafeEmerald
                    )
                }
                ResultRow(
                    label = "Estimasi Margin Digunakan",
                    value = "${CurrencyFormatter.formatUsc(result.requiredMarginUsc)} ($${String.format(Locale.US, "%.2f", result.requiredMarginUsd)})",
                    valueColor = TextPrimary
                )
                ResultRow(
                    label = "Ketahanan Stop Out (Buffer)",
                    value = "${result.stopOutBufferPoints.toInt()} pts ($${String.format(Locale.US, "%.2f", result.stopOutBufferPoints * spec.point)})",
                    valueColor = when {
                        result.stopOutBufferPoints > 3000.0 -> SafeEmerald
                        result.stopOutBufferPoints > 1200.0 -> WarnAmber
                        else -> DangerRuby
                    }
                )
                ResultRow(
                    label = "Nilai per 1 Point (1 Lot)",
                    value = "${result.pointValuePerLot} USC ($0.10 USD)",
                    valueColor = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun RiskRadarAssessmentTab(
    uiState: RiskRadarUiState,
    viewModel: RiskRadarViewModel
) {
    val assessment = uiState.assessment
    val snapshot = uiState.marketSnapshot

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Tombol Sinkron Margin Akun Terkini
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x2200F2FE), RoundedCornerShape(10.dp))
                .border(1.dp, AuraCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .clickable { viewModel.syncWithMarket() }
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(16.dp))
                    Text(
                        text = "SINKRONKAN DATA AKUN & PASAR",
                        color = AuraCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "ML: ${if (snapshot.marginLevel > 0) "${snapshot.marginLevel.toInt()}%" else "Normal"} • ATR $${String.format(Locale.US, "%.2f", snapshot.atr14)}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Notifikasi & Peringatan Jam Rawan Gold
        DangerousHoursAlertCard(assessment = assessment)

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Gauge Skor Risiko Akun 0-100
        RiskScoreGauge(assessment = assessment)

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Visual Radar / Spider Chart (6 Dimensi)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AuraGoldPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🕸️ Spider Chart Ketahanan Akun",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraGoldPrimary,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Text(
                    text = "Menganalisis 6 dimensi risiko nyata: Drawdown, Margin, RPT, Exposure, Korelasi, dan ATR",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )

                RadarSpiderChart(factors = assessment.factors)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Rincian Faktor Risiko
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RINCIAN INDIKATOR RISIKO REAL-TIME",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuraGoldLight,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                assessment.factors.forEach { factor ->
                    FactorRow(factor = factor)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Heatmap Korelasi Pasangan Mata Uang & Emas
        CorrelationHeatmap()

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
fun FactorRow(
    factor: com.surgatrader.feature.riskradar.domain.model.RiskFactor
) {
    val barColor = when {
        factor.score <= 35 -> SafeEmerald
        factor.score <= 70 -> WarnAmber
        else -> DangerRuby
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = factor.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "${factor.score.toInt()}% (${factor.valueLabel})",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = barColor
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress bar horizontal
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(SlateBorder, RoundedCornerShape(3.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((factor.score / factor.maxScore).toFloat().coerceIn(0.05f, 1f))
                    .height(6.dp)
                    .background(barColor, RoundedCornerShape(3.dp))
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = factor.description,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 10.sp
            )
        )
    }
}
