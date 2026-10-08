package com.surgatrader.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CandlestickChart
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object CommandRoom : Screen(
        route = "command_room",
        title = "Komando",
        selectedIcon = Icons.Filled.Shield,
        unselectedIcon = Icons.Outlined.Shield
    )

    object Chart : Screen(
        route = "chart",
        title = "Chart",
        selectedIcon = Icons.Filled.CandlestickChart,
        unselectedIcon = Icons.Outlined.CandlestickChart
    )

    object RiskRadar : Screen(
        route = "risk_radar",
        title = "Radar",
        selectedIcon = Icons.Filled.Security,
        unselectedIcon = Icons.Outlined.Security
    )

    object LotCalculator : Screen(
        route = "lot_calculator",
        title = "Lot Calc",
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate
    )

    object Journal : Screen(
        route = "journal",
        title = "Jurnal",
        selectedIcon = Icons.Filled.MenuBook,
        unselectedIcon = Icons.Outlined.MenuBook
    )

    object Roadmap : Screen(
        route = "roadmap",
        title = "Roadmap",
        selectedIcon = Icons.Filled.Timeline,
        unselectedIcon = Icons.Outlined.Timeline
    )

    object Connection : Screen(
        route = "connection",
        title = "Koneksi",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )

    object SymbolSpec : Screen(
        route = "symbol_spec",
        title = "Spesifikasi",
        selectedIcon = Icons.Filled.AccountBalanceWallet,
        unselectedIcon = Icons.Outlined.AccountBalanceWallet
    )

    companion object {
        val bottomNavScreens: List<Screen>
            get() = listOf(
                CommandRoom,
                Chart,
                RiskRadar,
                LotCalculator,
                Journal
            )
    }
}
