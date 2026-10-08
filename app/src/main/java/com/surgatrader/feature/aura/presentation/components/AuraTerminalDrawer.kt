package com.surgatrader.feature.aura.presentation.components

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.surgatrader.core.theme.AuraCyan
import com.surgatrader.core.theme.AuraGoldLight
import com.surgatrader.core.theme.AuraGoldPrimary
import com.surgatrader.feature.aura.domain.model.AuraTerminalLog

@Composable
fun AuraTerminalDrawer(
    logs: List<AuraTerminalLog>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    val formattedLogsText = logs.joinToString("\n") { log ->
        "[${log.timestamp}] ${log.speakerName}: ${log.message}"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 340.dp)
            .background(Color(0xF5020614), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .border(1.dp, AuraGoldPrimary, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header with Copy & Export actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LOG AUDIT EKSEKUSI KUANTUM (WIB)",
                    color = AuraGoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy button
                    Box(
                        modifier = Modifier
                            .background(Color(0x22FFFFFF), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(formattedLogsText))
                                Toast.makeText(context, "Log berhasil disalin ke clipboard", Toast.LENGTH_SHORT).show()
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "📋 Salin", color = AuraCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }

                    // Export button
                    Box(
                        modifier = Modifier
                            .background(Color(0x22FFFFFF), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(6.dp))
                            .clickable {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "--- AURA QUANTUM AUDIT LOG ---\n$formattedLogsText")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Ekspor Log Audit Kuantum")
                                context.startActivity(shareIntent)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = "📤 Ekspor", color = AuraGoldLight, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }

                    // Close button
                    Box(
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(4.dp)
                    ) {
                        Text(text = "✕", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Logs List
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(logs) { log ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "[${log.timestamp}]",
                            color = Color(0xFF64748B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${log.speakerName}:",
                            color = log.speakerColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = log.message,
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
