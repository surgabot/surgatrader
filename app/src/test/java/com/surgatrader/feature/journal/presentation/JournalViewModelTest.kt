package com.surgatrader.feature.journal.presentation

import com.google.common.truth.Truth.assertThat
import com.surgatrader.feature.aura.domain.AuraMarketStateHolder
import com.surgatrader.feature.aura.domain.MarketSnapshot
import com.surgatrader.feature.aura.domain.council.CouncilConsensusResult
import com.surgatrader.feature.aura.domain.council.SetupType
import com.surgatrader.feature.aura.domain.model.CouncilBias
import com.surgatrader.feature.journal.data.TradeJournalDao
import com.surgatrader.feature.journal.data.TradeJournalEntity
import com.surgatrader.feature.journal.data.TradeJournalRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class JournalViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockDao = mockk<TradeJournalDao>(relaxed = true)
    private lateinit var repository: TradeJournalRepository
    private val marketStateHolder = AuraMarketStateHolder()

    private val sampleTrades = listOf(
        TradeJournalEntity(
            id = 1,
            ticket = 101,
            symbol = "XAUUSDc",
            type = "BUY",
            openPrice = 4100.0,
            closePrice = 4110.0,
            volumeLot = 0.02,
            pnlUsc = 2000.0 // WIN
        ),
        TradeJournalEntity(
            id = 2,
            ticket = 102,
            symbol = "XAUUSDc",
            type = "SELL",
            openPrice = 4115.0,
            closePrice = 4120.0,
            volumeLot = 0.01,
            pnlUsc = -500.0 // LOSS
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { mockDao.getAllTrades() } returns flowOf(sampleTrades)
        coEvery { mockDao.countTrades() } returns 2
        repository = TradeJournalRepository(mockDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calculates win rate and net profit accurately from real trades`() = runTest(testDispatcher) {
        val viewModel = JournalViewModel(repository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.totalTrades).isEqualTo(2)
        assertThat(state.winningTrades).isEqualTo(1)
        assertThat(state.losingTrades).isEqualTo(1)
        assertThat(state.winRatePercent).isEqualTo(50.0) // 1 win / 2 total = 50%
        assertThat(state.netPnlUsc).isEqualTo(1500.0)    // 2000 - 500 = 1500 USC
        assertThat(state.netPnlUsd).isEqualTo(15.0)      // 1500 / 100 = $15.00 USD
        assertThat(state.profitFactor).isEqualTo(4.0)    // 2000 / 500 = 4.0
    }

    @Test
    fun `addTradeFromAegisSignal uses consensus parameters and inserts trade`() = runTest(testDispatcher) {
        val mockConsensus = CouncilConsensusResult(
            timestamp = System.currentTimeMillis(),
            memberReports = emptyMap(),
            consensusBias = CouncilBias.BULLISH,
            isConsensusAgreed = true,
            confluenceScore = 90,
            setupType = SetupType.BREAKOUT,
            verdictTitle = "BREAKOUT EMAS",
            verdictSummary = "Sinyal BUY",
            entryPriceMin = 4102.500,
            entryPriceMax = 4104.000,
            stopLossPrice = 4099.000,
            takeProfit1Price = 4108.000,
            takeProfit2Price = 4112.000,
            riskRewardRatio1 = 1.8,
            riskRewardRatio2 = 3.2,
            recommendedLotCent = 0.05
        )

        marketStateHolder.updateSnapshot(
            MarketSnapshot(
                bidPrice = 4102.500,
                latestConsensus = mockConsensus
            )
        )

        val viewModel = JournalViewModel(repository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.addTradeFromAegisSignal(pnlUsc = 1200.0, notes = "Sukses TP1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockDao.insertTrade(any()) }
        assertThat(viewModel.uiState.value.statusMessage).contains("berhasil dijurnal")
    }

    @Test
    fun `exportToCsv outputs valid RFC 4180 headers and row values`() = runTest(testDispatcher) {
        val viewModel = JournalViewModel(repository, marketStateHolder)
        testDispatcher.scheduler.advanceUntilIdle()

        val csv = viewModel.exportToCsv()
        assertThat(csv).contains("Ticket,Waktu,Simbol,Tipe,OpenPrice,ClosePrice,Lot,PnL_USC,PnL_USD")
        assertThat(csv).contains("XAUUSDc")
        assertThat(csv).contains("2000.0")
    }
}
