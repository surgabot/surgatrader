package com.surgatrader.feature.aura.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.surgatrader.feature.aura.presentation.components.AuraBottomDock
import com.surgatrader.feature.aura.presentation.components.AuraHeader
import com.surgatrader.feature.aura.presentation.components.AuraLeftPanel
import com.surgatrader.feature.aura.presentation.components.AuraQuantumCanvas
import com.surgatrader.feature.aura.presentation.components.AuraRightPanel
import com.surgatrader.feature.aura.presentation.components.AuraStartModal
import com.surgatrader.feature.aura.presentation.components.AuraTerminalDrawer
import com.surgatrader.feature.connection.ConnectionScreen

@Composable
fun AuraQuantumScreen(
    viewModel: AuraQuantumViewModel = hiltViewModel(),
    onNavigateToLotCalculator: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var isConnectionScreenOpen by remember { mutableStateOf(false) }

    if (isConnectionScreenOpen) {
        ConnectionScreen(
            onNavigateBack = {
                isConnectionScreenOpen = false
                viewModel.refreshConnectionAndMode()
            }
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF02050E))
        ) {
            // LAYER 1: Full-screen Holographic 2D Canvas
            AuraQuantumCanvas(
                state = state,
                modifier = Modifier.fillMaxSize(),
                onEntityTapped = { entity ->
                    viewModel.selectEntity(entity)
                }
            )

            // LAYER 2: Cyber Scanline & Vignette Effect
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Subtle Radial Vignette
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xAA01030A)
                        ),
                        radius = maxOf(size.width, size.height) * 0.7f
                    )
                )
            }

            // LAYER 3: Main Cyber UI Overlay
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWideScreen = maxWidth > 720.dp

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Header (Brand + Ticker + DEMO/LIVE Indicator)
                    AuraHeader(
                        state = state,
                        onOpenConnectionSettings = { isConnectionScreenOpen = true },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Middle Workspace (Panels)
                    if (isWideScreen) {
                        // Landscape / Tablet layout: Left and Right panels side by side
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            AuraLeftPanel(
                                state = state,
                                onToggleMinimize = { viewModel.toggleLeftPanel() },
                                onPrevStep = { viewModel.prevStep() },
                                onReplayStep = { viewModel.replayStep() },
                                onNextStep = { viewModel.nextStep() },
                                modifier = Modifier.widthIn(max = 420.dp)
                            )

                            AuraRightPanel(
                                state = state,
                                onToggleMinimize = { viewModel.toggleRightPanel() },
                                onToggleAutoPlay = { viewModel.toggleAutoPlay() },
                                onStopAudio = { viewModel.stopAudio() },
                                onToggleMute = { viewModel.toggleMute() },
                                onSpeedSelected = { viewModel.setSpeed(it) },
                                onVolumeChanged = { viewModel.setVolume(it) },
                                onExecuteQuantumOrder = { viewModel.executeQuantumOrder() },
                                onNavigateToLotCalculator = onNavigateToLotCalculator,
                                onOpenConnectionSettings = { isConnectionScreenOpen = true },
                                modifier = Modifier.widthIn(max = 340.dp)
                            )
                        }
                    } else {
                        // Portrait Mobile layout: Compact stacked / collapsible panels
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 14.dp)
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AuraLeftPanel(
                                state = state,
                                onToggleMinimize = { viewModel.toggleLeftPanel() },
                                onPrevStep = { viewModel.prevStep() },
                                onReplayStep = { viewModel.replayStep() },
                                onNextStep = { viewModel.nextStep() },
                                modifier = Modifier.fillMaxWidth()
                            )

                            AuraRightPanel(
                                state = state,
                                onToggleMinimize = { viewModel.toggleRightPanel() },
                                onToggleAutoPlay = { viewModel.toggleAutoPlay() },
                                onStopAudio = { viewModel.stopAudio() },
                                onToggleMute = { viewModel.toggleMute() },
                                onSpeedSelected = { viewModel.setSpeed(it) },
                                onVolumeChanged = { viewModel.setVolume(it) },
                                onExecuteQuantumOrder = { viewModel.executeQuantumOrder() },
                                onNavigateToLotCalculator = onNavigateToLotCalculator,
                                onOpenConnectionSettings = { isConnectionScreenOpen = true },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Bottom Council Dock
                    AuraBottomDock(
                        currentSpeaker = state.currentSpeaker,
                        isSpeaking = state.isSpeaking,
                        onEntitySelected = { viewModel.selectEntity(it) },
                        onToggleTerminal = { viewModel.toggleTerminal() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // OVERLAY 1: Terminal Drawer
            AnimatedVisibility(
                visible = state.isTerminalOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                AuraTerminalDrawer(
                    logs = state.terminalLogs,
                    onClose = { viewModel.toggleTerminal() }
                )
            }

            // OVERLAY 2: Start Prompt Modal
            if (state.isStartModalVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xCC000000)),
                    contentAlignment = Alignment.Center
                ) {
                    AuraStartModal(
                        state = state,
                        onCommence = {
                            viewModel.dismissStartModal()
                        },
                        onOpenConnection = {
                            isConnectionScreenOpen = true
                        }
                    )
                }
            }
        }
    }
}
