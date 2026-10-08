package com.surgatrader.feature.journal.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TradeJournalRepository @Inject constructor(
    private val journalDao: TradeJournalDao
) {

    fun getAllTrades(): Flow<List<TradeJournalEntity>> {
        return journalDao.getAllTrades()
    }

    suspend fun getTradeById(id: Long): TradeJournalEntity? {
        return journalDao.getTradeById(id)
    }

    suspend fun addTrade(trade: TradeJournalEntity): Long {
        return journalDao.insertTrade(trade)
    }

    suspend fun updateTrade(trade: TradeJournalEntity) {
        journalDao.updateTrade(trade)
    }

    suspend fun deleteTradeById(id: Long) {
        journalDao.deleteTradeById(id)
    }

    suspend fun seedSampleTradesIfEmpty() {
        if (journalDao.countTrades() == 0) {
            val now = System.currentTimeMillis()
            val sampleTrades = listOf(
                TradeJournalEntity(
                    id = 1,
                    ticket = 1045231,
                    timestampMillis = now - 3600000 * 2,
                    symbol = "XAUUSDc",
                    type = "BUY",
                    openPrice = 4118.250,
                    closePrice = 4128.500,
                    volumeLot = 0.02,
                    pnlUsc = 2050.0,
                    setupTag = "Breakout Sesi London",
                    dewanConfluenceScore = 92,
                    emotionTag = "Disiplin & Tenang",
                    notes = "Rejection di support 4118 pas London open, target TP1 tercapai."
                ),
                TradeJournalEntity(
                    id = 2,
                    ticket = 1045180,
                    timestampMillis = now - 3600000 * 8,
                    symbol = "XAUUSDc",
                    type = "SELL",
                    openPrice = 4132.100,
                    closePrice = 4136.200,
                    volumeLot = 0.01,
                    pnlUsc = -410.0,
                    setupTag = "Supply Zone Reversal",
                    dewanConfluenceScore = 78,
                    emotionTag = "Sesuai Plan",
                    notes = "Kena SL 410 pts, disiplin tidak digeser dan risiko tetap terkontrol."
                ),
                TradeJournalEntity(
                    id = 3,
                    ticket = 1044992,
                    timestampMillis = now - 3600000 * 24,
                    symbol = "XAUUSDc",
                    type = "BUY",
                    openPrice = 4105.000,
                    closePrice = 4122.300,
                    volumeLot = 0.03,
                    pnlUsc = 5190.0,
                    setupTag = "Trend Continuation US",
                    dewanConfluenceScore = 88,
                    emotionTag = "Disiplin",
                    notes = "Riding trend kuat sesi New York setelah rilis data AS netral."
                ),
                TradeJournalEntity(
                    id = 4,
                    ticket = 1044750,
                    timestampMillis = now - 3600000 * 36,
                    symbol = "XAUUSDc",
                    type = "SELL",
                    openPrice = 4125.400,
                    closePrice = 4115.100,
                    volumeLot = 0.02,
                    pnlUsc = 2060.0,
                    setupTag = "Pullback Area Likuiditas",
                    dewanConfluenceScore = 85,
                    emotionTag = "Tenang & Percaya Diri",
                    notes = "Retest resistance H1 pasca rejection EMA 50."
                ),
                TradeJournalEntity(
                    id = 5,
                    ticket = 1044610,
                    timestampMillis = now - 3600000 * 48,
                    symbol = "XAUUSDc",
                    type = "BUY",
                    openPrice = 4098.500,
                    closePrice = 4095.000,
                    volumeLot = 0.01,
                    pnlUsc = -350.0,
                    setupTag = "Breakout False",
                    dewanConfluenceScore = 65,
                    emotionTag = "Evaluasi Setup",
                    notes = "False breakout area Asia, kena SL 350 pts sebelum memantul."
                )
            )
            journalDao.insertTrades(sampleTrades)
        }
    }

    fun exportToCsv(trades: List<TradeJournalEntity>): String {
        val sb = StringBuilder()
        sb.append("Ticket,Waktu,Simbol,Tipe,OpenPrice,ClosePrice,Lot,PnL_USC,PnL_USD,Setup,SkorDewan,Emosi,Catatan\n")
        trades.forEach { t ->
            sb.append("${t.ticket},\"${t.formattedDateWib}\",${t.symbol},${t.type},${t.openPrice},${t.closePrice},${t.volumeLot},${t.pnlUsc},${t.pnlUsd},\"${t.setupTag}\",${t.dewanConfluenceScore},\"${t.emotionTag}\",\"${t.notes.replace("\"", "\"\"")}\"\n")
        }
        return sb.toString()
    }
}
