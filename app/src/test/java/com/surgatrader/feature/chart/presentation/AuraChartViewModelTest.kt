package com.surgatrader.feature.chart.presentation

import com.google.common.truth.Truth.assertThat
import com.surgatrader.feature.aura.domain.AuraMarketStateHolder
import com.surgatrader.feature.aura.domain.MarketSnapshot
import com.surgatrader.feature.chart.domain.model.ChartTimeframe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuraChartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val marketStateHolder = AuraMarketStateHolder()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initializes chart with 40 candles and calculated EMA lines`() = runTest(testDispatcher) {
        val viewModel = AuraChartViewModel(marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.candles.size).isEqualTo(40)
        assertThat(state.selectedTimeframe).isEqualTo(ChartTimeframe.M5)
        assertThat(state.ema20Values.size).isEqualTo(40)
        assertThat(state.ema50Values.size).isEqualTo(40)
        assertThat(state.pivotLevels).isNotEmpty()
    }

    @Test
    fun `selectTimeframe regenerates candles for new interval`() = runTest(testDispatcher) {
        val viewModel = AuraChartViewModel(marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectTimeframe(ChartTimeframe.H1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.selectedTimeframe).isEqualTo(ChartTimeframe.H1)
        assertThat(viewModel.uiState.value.candles.size).isEqualTo(40)
    }

    @Test
    fun `market tick updates latest candle close, high, and low`() = runTest(testDispatcher) {
        val viewModel = AuraChartViewModel(marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        val prevLastCandle = viewModel.uiState.value.candles.last()

        marketStateHolder.updateSnapshot(
            MarketSnapshot(
                bidPrice = 4135.500
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val updatedLastCandle = viewModel.uiState.value.candles.last()
        assertThat(updatedLastCandle.close).isEqualTo(4135.500)
        assertThat(updatedLastCandle.high).isAtLeast(4135.500)
        assertThat(updatedLastCandle.close).isNotEqualTo(prevLastCandle.close)
    }

    @Test
    fun `toggle overlays changes visibility flags properly`() = runTest(testDispatcher) {
        val viewModel = AuraChartViewModel(marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.overlays.showEma20).isTrue()
        viewModel.toggleEma20()
        assertThat(viewModel.uiState.value.overlays.showEma20).isFalse()

        viewModel.togglePivots()
        assertThat(viewModel.uiState.value.overlays.showPivots).isFalse()
    }
}
