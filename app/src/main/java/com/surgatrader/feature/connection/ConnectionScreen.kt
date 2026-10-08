package com.surgatrader.feature.connection

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.surgatrader.core.security.DataMode
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGlassBg
import com.surgatrader.core.theme.AuraGlassBorder
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.core.theme.AuraGreenBull
import com.surgatrader.core.theme.AuraRedBear
import java.util.Locale

@Composable
fun ConnectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConnectionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF02050E))
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(AuraGlassBg, CircleShape)
                        .border(1.dp, AuraGlassBorder, CircleShape)
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                Column {
                    Text(
                        text = "KONEKSI BRIDGE & DATA",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Pusat Pengaturan Keamanan & Mode Data",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Security Notice Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x1800FF88), RoundedCornerShape(12.dp))
                .border(1.dp, AuraGreenBull.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "🛡️", fontSize = 16.sp)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "PENYIMPANAN TERENKRIPSI LOKAL",
                        color = AuraGreenBull,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Kredensial URL dan token Anda disimpan menggunakan Android Keystore & EncryptedSharedPreferences pada perangkat ini. Tidak ada data yang dikirim ke server pihak ketiga.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Data Mode Switcher Tabs
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "PILIH MODE DATA:",
                color = AuraGoldLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live Tab
                val isLive = state.dataMode == DataMode.LIVE
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isLive) Brush.horizontalGradient(listOf(Color(0x3300FF88), Color(0x2200F2FE))) else SolidColor(AuraGlassBg),
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isLive) AuraGreenBull else Color(0x33FFFFFF),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setDataMode(DataMode.LIVE) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "🟢 MODE LIVE",
                            color = if (isLive) Color.White else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Bridge MetaTrader 5",
                            color = if (isLive) AuraGreenBull else Color(0xFF64748B),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Demo Tab
                val isDemo = state.dataMode == DataMode.DEMO
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isDemo) Brush.horizontalGradient(listOf(Color(0x33FFB92D), Color(0x22FFD700))) else SolidColor(AuraGlassBg),
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (isDemo) Color(0xFFFFB92D) else Color(0x33FFFFFF),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setDataMode(DataMode.DEMO) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚠️ MODE DEMO",
                            color = if (isDemo) Color.White else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Data Rekaman/Simulasi",
                            color = if (isDemo) Color(0xFFFFD15C) else Color(0xFF64748B),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Configuration Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuraGlassBg, RoundedCornerShape(16.dp))
                .border(1.dp, AuraGlassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "KONFIGURASI ENDPOINT BRIDGE MT5",
                    color = AuraGoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // URL Bridge Field
                OutlinedTextField(
                    value = state.bridgeUrl,
                    onValueChange = { viewModel.onUrlChanged(it) },
                    label = { Text("URL Bridge (REST/MCP)", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("http://10.0.2.2:22346/mcp", fontSize = 11.sp) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuraGoldPrimary,
                        unfocusedBorderColor = Color(0x44FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Token Field with Obscure Toggle
                OutlinedTextField(
                    value = state.bridgeToken,
                    onValueChange = { viewModel.onTokenChanged(it) },
                    label = { Text("Token Otorisasi Bridge", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    placeholder = { Text("Masukkan token otentikasi bridge", fontSize = 11.sp) },
                    singleLine = true,
                    visualTransformation = if (state.isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (state.isTokenVisible) "🙈" else "👁️",
                            modifier = Modifier
                                .clickable { viewModel.toggleTokenVisibility() }
                                .padding(8.dp),
                            fontSize = 14.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AuraGoldPrimary,
                        unfocusedBorderColor = Color(0x44FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Action Button: Uji Koneksi
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(listOf(AuraGoldPrimary, Color(0xFFFF8C00))),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(enabled = !state.isTesting) { viewModel.testConnection() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (state.isTesting) {
                            CircularProgressIndicator(
                                color = Color(0xFF02050E),
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(text = "⚡", fontSize = 14.sp)
                        }
                        Text(
                            text = if (state.isTesting) "MENGUJI KONEKSI..." else "UJI KONEKSI & UKUR LATENCY",
                            color = Color(0xFF02050E),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Status & Messages
                if (state.successMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x2200FF88), RoundedCornerShape(8.dp))
                            .border(1.dp, AuraGreenBull, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = state.successMessage ?: "", color = AuraGreenBull, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                if (state.errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x22FF3366), RoundedCornerShape(8.dp))
                            .border(1.dp, AuraRedBear, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = state.errorMessage ?: "", color = Color(0xFFFCA5A5), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }

                // Connected Account Summary Card
                if (state.isConnected && state.accountData != null) {
                    val acc = state.accountData!!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0x66000000), RoundedCornerShape(10.dp))
                            .border(1.dp, AuraCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "DATA AKUN TERDETEKSI:", color = AuraCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Broker / Server:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "${acc.broker} (${acc.server})", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Login & Tipe:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "${acc.login} • ${acc.type}", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Saldo Akun Cent:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "${String.format(Locale.US, "%.2f", acc.balance)} USC (~$${String.format(Locale.US, "%.2f", acc.balance / 100.0)})", color = AuraGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Latency Terukur:", color = Color(0xFF94A3B8), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "${state.latencyMs} ms", color = AuraGreenBull, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        // Security & App Lock Toggles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuraGlassBg, RoundedCornerShape(16.dp))
                .border(1.dp, AuraGlassBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "PENGATURAN KEAMANAN APLIKASI",
                    color = AuraGoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Biometric Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Kunci Aplikasi dengan Biometrik", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Wajibkan sidik jari atau PIN saat membuka aplikasi", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    Switch(
                        checked = state.isBiometricEnabled,
                        onCheckedChange = { viewModel.toggleBiometric(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AuraGoldPrimary,
                            checkedTrackColor = Color(0x44FFD700)
                        )
                    )
                }

                // Auto Reconnect Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Sambung Otomatis (Auto-Reconnect)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Koneksikan ulang ke bridge saat aplikasi dibuka", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                    Switch(
                        checked = state.isAutoReconnectEnabled,
                        onCheckedChange = { viewModel.toggleAutoReconnect(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AuraGoldPrimary,
                            checkedTrackColor = Color(0x44FFD700)
                        )
                    )
                }

                // Clear credentials button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x1AFF3366), RoundedCornerShape(8.dp))
                        .border(1.dp, AuraRedBear.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { viewModel.clearCredentials() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🗑️ HAPUS KREDENSIAL DARI PENYIMPANAN",
                        color = Color(0xFFFCA5A5),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
