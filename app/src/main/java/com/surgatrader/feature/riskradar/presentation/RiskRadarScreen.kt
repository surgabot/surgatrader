package com.surgatrader.feature.riskradar.presentation

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.surgatrader.core.theme.WarnAmber
import com.surgatrader.core.util.CurrencyFormatter
import com.surgatrader.feature.riskradar.domain.model.OrderDirection
import com.surgatrader.feature.riskradar.domain.model.SlInputMode
import com.surgatrader.feature.riskradar.domain.model.TpInputMode
import com.surgatrader.feature.riskradar.presentation.components.CorrelationHeatmap
import com.surgatrader.feature.riskradar.presentation.components.DangerousHoursAlertCard
import com.surgatrader.feature.riskradar.presentation.components.RadarSpiderChart
import com.surgatrader.feature.riskradar.presentation.components.RiskScoreGauge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiskRadarScreen(
    onNavigateToSymbolSpec: () -> Unit,
    viewModel: RiskRadarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spec = uiState.symbolSpec
    val lotParams = uiState.lotParams
    val lotResult = uiState.lotResult
    val assessment = uiState.assessment

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Radar Risiko & Lot",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${spec.profileName} • ${spec.digits} Digits",
                            style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSymbolSpec) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Spesifikasi Simbol",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Segmented Tabs
            SecondaryTabRow(
                selectedTabIndex = uiState.activeTab,
                containerColor = ObsidianBg,
                contentColor = CyanAccent
            ) {
                Tab(
                    selected = uiState.activeTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = {
                        Text(
                            text = "Kalkulator Lot",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == 0) CyanAccent else TextSecondary
                            )
                        )
                    }
                )
                Tab(
                    selected = uiState.activeTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = {
                        Text(
                            text = "Radar & Skor Akun",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (uiState.activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.activeTab == 1) CyanAccent else TextSecondary
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

    var balanceText by remember(params.balance) { mutableStateOf(params.balance.toInt().toString()) }
    var entryPriceText by remember(params.entryPrice) { mutableStateOf(params.entryPrice.toString()) }
    var slInputText by remember(params.slInput) { mutableStateOf(params.slInput.toInt().toString()) }
    var tpInputText by remember(params.tpInput) { mutableStateOf(params.tpInput.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Badge Simbol Aktif
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard, RoundedCornerShape(12.dp))
                .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
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
                            .background(SafeEmerald, RoundedCornerShape(5.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${spec.symbolName} (${spec.profileName})",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                Text(
                    text = "Spread: ${spec.spreadPoints} pts >",
                    style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Saldo Akun (Dual display USC & USD)
        Text(
            text = "SALDO AKUN (CENT USC)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary
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
                focusedBorderColor = CyanAccent,
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
                    style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent)
                )
            }
        )

        // Preset Chips Saldo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(10000.0 to "$100", 25000.0 to "$250", 50000.0 to "$500", 100000.0 to "$1k").forEach { (usc, label) ->
                FilterChip(
                    selected = params.balance == usc,
                    onClick = {
                        balanceText = usc.toInt().toString()
                        viewModel.updateBalance(usc)
                    },
                    label = { Text("$label (${usc.toInt()}c)") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = CyanAccent.copy(alpha = 0.2f),
                        selectedLabelColor = CyanAccent
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
                    color = TextSecondary
                )
            )
            Text(
                text = "= ${CurrencyFormatter.formatDualBalance(params.balance * params.riskPercent / 100.0)}",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = SafeEmerald
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
                    label = { Text("$r%") },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = CyanAccent.copy(alpha = 0.25f),
                        selectedLabelColor = CyanAccent
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Arah Order & Entry Price
        Row(modifier = Modifier.fillMaxWidth()) {
            // Tombol BUY / SELL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(
                        if (params.direction == OrderDirection.BUY) SafeEmerald.copy(alpha = 0.2f) else SlateCard,
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
                            color = if (params.direction == OrderDirection.BUY) SafeEmerald else TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .background(
                        if (params.direction == OrderDirection.SELL) DangerRuby.copy(alpha = 0.2f) else SlateCard,
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
                            color = if (params.direction == OrderDirection.SELL) DangerRuby else TextSecondary
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
            label = { Text("Harga Entry Saat Ini (${spec.digits} Digit)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Input Stop Loss
        Text(
            text = "STOP LOSS (SL)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary
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
                    label = { Text(mode.label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = CyanAccent.copy(alpha = 0.25f),
                        selectedLabelColor = CyanAccent
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
                        SlInputMode.PRICE -> "Level Harga SL (misal: 4120.000)"
                        SlInputMode.MONEY -> "Batas Rugi Maksimal (USC)"
                    }
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
            shape = RoundedCornerShape(10.dp),
            supportingText = {
                Text(
                    text = "Spread broker (${spec.spreadPoints} pts) dihitung otomatis: Total SL = ${result.effectiveSlPoints.toInt()} pts",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Input Take Profit
        Text(
            text = "TAKE PROFIT (TP)",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = TextSecondary
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
                    label = { Text(mode.label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = SlateCard,
                        selectedContainerColor = CyanAccent.copy(alpha = 0.25f),
                        selectedLabelColor = CyanAccent
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
                            TpInputMode.PRICE -> "Level Harga TP (misal: 4135.000)"
                            TpInputMode.NONE -> ""
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ================= KARTU HASIL KALKULASI HUD =================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateCardElevated),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header Lot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "UKURAN LOT REKOMENDASI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        )
                        Text(
                            text = "${CurrencyFormatter.formatLot(result.recommendedLot)} Lot",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = CyanAccent
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CyanAccent.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, CyanAccent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "R:R 1 : ${result.riskRewardRatio}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SafeEmerald
                            )
                        )
                    }
                }

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
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Detail Baris
                ResultRow(
                    label = "Risiko Maksimal (SL)",
                    value = "${CurrencyFormatter.formatUsc(result.riskAmountUsc)} ($${String.format("%.2f", result.riskAmountUsd)})",
                    valueColor = DangerRuby
                )
                ResultRow(
                    label = "Potensi Keuntungan (TP)",
                    value = "${CurrencyFormatter.formatUsc(result.potentialProfitUsc)} ($${String.format("%.2f", result.potentialProfitUsd)})",
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
                    label = "Estimasi Margin Diperlukan",
                    value = "${CurrencyFormatter.formatUsc(result.requiredMarginUsc)} ($${String.format("%.2f", result.requiredMarginUsd)})",
                    valueColor = TextPrimary
                )
                ResultRow(
                    label = "Nilai per Point (1 Lot)",
                    value = "${result.pointValuePerLot} USC",
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
    val radarInput = uiState.radarInput

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
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
                .border(1.dp, SlateBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "🕸️ Spider Chart Faktor Risiko",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Menganalisis 6 aspek ketahanan akun forex & emas",
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
                    text = "RINCIAN INDIKATOR RISIKO",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
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
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor
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
                    color = TextPrimary
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
