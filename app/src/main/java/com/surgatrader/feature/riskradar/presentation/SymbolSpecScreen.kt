package com.surgatrader.feature.riskradar.presentation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.surgatrader.core.theme.CyanAccent
import com.surgatrader.core.theme.ObsidianBg
import com.surgatrader.core.theme.SafeEmerald
import com.surgatrader.core.theme.SlateBorder
import com.surgatrader.core.theme.SlateCard
import com.surgatrader.core.theme.TextPrimary
import com.surgatrader.core.theme.TextSecondary
import com.surgatrader.core.theme.WarnAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymbolSpecScreen(
    onNavigateBack: () -> Unit,
    viewModel: SymbolSpecViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spec = uiState.currentSpec
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            snackbarHostState.showSnackbar("Spesifikasi Simbol berhasil disimpan!")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Spesifikasi Simbol (MT5)",
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
                actions = {
                    IconButton(onClick = { viewModel.resetToExnessDefault() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Default",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Kotak Info MT5
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
                        contentDescription = "Info",
                        tint = CyanAccent,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Profil: ${spec.profileName}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = spec.notes,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Inputs
            Text(
                text = "PARAMETER KONTRAK & HARGA",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SpecInputField(
                    label = "Simbol MT5",
                    value = spec.symbolName,
                    onValueChange = { viewModel.updateSpec(spec.copy(symbolName = it)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Contract Size",
                    value = spec.contractSize.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(contractSize = it.toDoubleOrNull() ?: spec.contractSize)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SpecInputField(
                    label = "Digits Desimal (misal: 3)",
                    value = spec.digits.toString(),
                    keyboardType = KeyboardType.Number,
                    onValueChange = { viewModel.updateSpec(spec.copy(digits = it.toIntOrNull() ?: spec.digits)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Nilai 1 Point (misal: 0.001)",
                    value = spec.point.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(point = it.toDoubleOrNull() ?: spec.point)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SpecInputField(
                    label = "Tick Size",
                    value = spec.tickSize.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(tickSize = it.toDoubleOrNull() ?: spec.tickSize)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Tick Value (USC per lot)",
                    value = spec.tickValue.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(tickValue = it.toDoubleOrNull() ?: spec.tickValue)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "VOLUME LOT & LEVERAGE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SpecInputField(
                    label = "Minimal Lot",
                    value = spec.minLot.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(minLot = it.toDoubleOrNull() ?: spec.minLot)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Langkah Lot (Step)",
                    value = spec.lotStep.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(lotStep = it.toDoubleOrNull() ?: spec.lotStep)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SpecInputField(
                    label = "Maksimal Lot",
                    value = spec.maxLot.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(maxLot = it.toDoubleOrNull() ?: spec.maxLot)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Leverage (1:x)",
                    value = spec.leverage.toInt().toString(),
                    keyboardType = KeyboardType.Number,
                    onValueChange = { viewModel.updateSpec(spec.copy(leverage = it.toDoubleOrNull() ?: spec.leverage)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "BIAYA TRANSAKSI (SPREAD & SWAP)",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                SpecInputField(
                    label = "Spread (Points)",
                    value = spec.spreadPoints.toString(),
                    keyboardType = KeyboardType.Number,
                    onValueChange = { viewModel.updateSpec(spec.copy(spreadPoints = it.toIntOrNull() ?: spec.spreadPoints)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Swap Long",
                    value = spec.swapLong.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(swapLong = it.toDoubleOrNull() ?: spec.swapLong)) },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                SpecInputField(
                    label = "Swap Short",
                    value = spec.swapShort.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onValueChange = { viewModel.updateSpec(spec.copy(swapShort = it.toDoubleOrNull() ?: spec.swapShort)) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Simpan
            Button(
                onClick = { viewModel.saveSpec() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = ObsidianBg
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Simpan")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simpan Perubahan ke Database",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SpecInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = modifier,
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
