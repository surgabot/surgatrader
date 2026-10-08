package com.surgatrader.feature.riskradar.presentation

import com.google.common.truth.Truth.assertThat
import com.surgatrader.feature.aura.domain.AuraMarketStateHolder
import com.surgatrader.feature.aura.domain.MarketSnapshot
import com.surgatrader.feature.aura.domain.council.CouncilConsensusResult
import com.surgatrader.feature.aura.domain.council.SetupType
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.riskradar.data.DefaultSymbols
import com.surgatrader.feature.riskradar.data.SymbolRepository
import com.surgatrader.feature.riskradar.domain.model.OrderDirection
import com.surgatrader.feature.riskradar.domain.model.SlInputMode
import com.surgatrader.feature.riskradar.domain.model.TpInputMode
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RiskRadarViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockSymbolRepository = mockk<SymbolRepository>()
    private val marketStateHolder = AuraMarketStateHolder()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockSymbolRepository.getDefaultSymbol() } returns flowOf(DefaultSymbols.XAUUSD_EXNESS_CENT)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads default exness cent symbol and valid calculations`() = runTest(testDispatcher) {
        val viewModel = RiskRadarViewModel(mockSymbolRepository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.symbolSpec.symbolName).isEqualTo("XAUUSDc")
        assertThat(state.symbolSpec.digits).isEqualTo(3)
        assertThat(state.lotResult.recommendedLot).isAtLeast(0.01)
        assertThat(state.activeTab).isEqualTo(0)
    }

    @Test
    fun `syncWithMarket copies live market snapshot balance and entry price into lot params`() = runTest(testDispatcher) {
        val viewModel = RiskRadarViewModel(mockSymbolRepository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        // Push market update to state holder
        marketStateHolder.updateSnapshot(
            MarketSnapshot(
                balanceUsc = 75000.0,
                bidPrice = 4112.345,
                marginLevel = 2200.0,
                atr14 = 1.850
            )
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.syncWithMarket()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.lotParams.balance).isEqualTo(75000.0)
        assertThat(state.lotParams.entryPrice).isEqualTo(4112.345)
        assertThat(state.statusMessage).isNotNull()
        assertThat(state.statusMessage).contains("Tersinkronisasi")
    }

    @Test
    fun `importAegisRecommendation sets order direction, entry, SL, TP from council consensus`() = runTest(testDispatcher) {
        val viewModel = RiskRadarViewModel(mockSymbolRepository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        val mockConsensus = CouncilConsensusResult(
            timestamp = System.currentTimeMillis(),
            memberReports = emptyMap(),
            consensusBias = CouncilBias.BULLISH,
            isConsensusAgreed = true,
            confluenceScore = 88,
            setupType = SetupType.BREAKOUT,
            verdictTitle = "BREAKOUT EMAS DIKONFIRMASI",
            verdictSummary = "Sinyal BUY kuat.",
            entryPriceMin = 4100.234,
            entryPriceMax = 4101.500,
            stopLossPrice = 4097.800,
            takeProfit1Price = 4105.000,
            takeProfit2Price = 4109.500,
            riskRewardRatio1 = 2.1,
            riskRewardRatio2 = 4.1,
            recommendedLotCent = 0.08
        )

        marketStateHolder.updateConsensus(mockConsensus)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.importAegisRecommendation()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.lotParams.direction).isEqualTo(OrderDirection.BUY)
        assertThat(state.lotParams.entryPrice).isEqualTo(4100.234)
        assertThat(state.lotParams.slMode).isEqualTo(SlInputMode.PRICE)
        assertThat(state.lotParams.slInput).isEqualTo(4097.800)
        assertThat(state.lotParams.tpMode).isEqualTo(TpInputMode.PRICE)
        assertThat(state.lotParams.tpInput).isEqualTo(4105.000)
        assertThat(state.statusMessage).contains("Rekomendasi Aegis Diterapkan")
    }

    @Test
    fun `selectTab updates activeTab state correctly`() = runTest(testDispatcher) {
        val viewModel = RiskRadarViewModel(mockSymbolRepository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectTab(1)
        assertThat(viewModel.uiState.value.activeTab).isEqualTo(1)

        viewModel.selectTab(0)
        assertThat(viewModel.uiState.value.activeTab).isEqualTo(0)
    }
}
