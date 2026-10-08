package com.surgatrader.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.surgatrader.feature.aura.presentation.AuraQuantumScreen
import com.surgatrader.feature.calendar.presentation.CalendarScreen
import com.surgatrader.feature.connection.ConnectionScreen
import com.surgatrader.feature.journal.presentation.JournalScreen
import com.surgatrader.feature.riskradar.presentation.RiskRadarScreen
import com.surgatrader.feature.riskradar.presentation.SymbolSpecScreen
import com.surgatrader.feature.roadmap.presentation.RoadmapScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CommandRoom.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.CommandRoom.route) {
            AuraQuantumScreen(
                onNavigateToLotCalculator = { navController.navigate(Screen.LotCalculator.route) }
            )
        }

        composable(Screen.Chart.route) {
            com.surgatrader.feature.chart.presentation.AuraChartScreen(
                onNavigateToLotCalculator = { navController.navigate(Screen.LotCalculator.route) }
            )
        }

        composable(Screen.RiskRadar.route) {
            RiskRadarScreen(
                onNavigateToSymbolSpec = { navController.navigate(Screen.SymbolSpec.route) },
                initialTab = 1
            )
        }

        composable(Screen.LotCalculator.route) {
            RiskRadarScreen(
                onNavigateToSymbolSpec = { navController.navigate(Screen.SymbolSpec.route) },
                initialTab = 0
            )
        }

        composable(Screen.Journal.route) {
            JournalScreen()
        }

        composable(Screen.Roadmap.route) {
            RoadmapScreen()
        }

        composable(Screen.Connection.route) {
            ConnectionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SymbolSpec.route) {
            SymbolSpecScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
