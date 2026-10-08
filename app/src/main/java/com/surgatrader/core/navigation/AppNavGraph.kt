package com.surgatrader.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.surgatrader.feature.calendar.presentation.CalendarScreen
import com.surgatrader.feature.dashboard.presentation.DashboardScreen
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
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToRiskRadar = { navController.navigate(Screen.RiskRadar.route) },
                onNavigateToRoadmap = { navController.navigate(Screen.Roadmap.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToMt5Bridge = { navController.navigate(Screen.Mt5Bridge.route) }
            )
        }

        composable(Screen.Roadmap.route) {
            RoadmapScreen()
        }

        composable(Screen.RiskRadar.route) {
            RiskRadarScreen(
                onNavigateToSymbolSpec = { navController.navigate(Screen.SymbolSpec.route) }
            )
        }

        composable(Screen.Journal.route) {
            JournalScreen()
        }

        composable(Screen.Calendar.route) {
            CalendarScreen()
        }

        composable(Screen.SymbolSpec.route) {
            SymbolSpecScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Mt5Bridge.route) {
            com.surgatrader.feature.advanced.Mt5BridgeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
