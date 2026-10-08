package com.surgatrader.core.navigation

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NavigationRoutesTest {

    @Test
    fun `bottom navigation screens contains core modules`() {
        val routes = Screen.bottomNavScreens.map { it.route }
        assertThat(routes).containsExactly(
            Screen.CommandRoom.route,
            Screen.Chart.route,
            Screen.RiskRadar.route,
            Screen.LotCalculator.route,
            Screen.Journal.route
        ).inOrder()
    }

    @Test
    fun `all screen routes are unique and valid`() {
        val allScreens = listOf(
            Screen.CommandRoom,
            Screen.Chart,
            Screen.RiskRadar,
            Screen.LotCalculator,
            Screen.Journal,
            Screen.Roadmap,
            Screen.Connection,
            Screen.SymbolSpec
        )

        val routes = allScreens.map { it.route }
        assertThat(routes.distinct().size).isEqualTo(allScreens.size)

        routes.forEach { route ->
            assertThat(route).isNotEmpty()
        }
    }
}
