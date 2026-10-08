package com.surgatrader.feature.journal.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeJournalDao {

    @Query("SELECT * FROM trade_journal ORDER BY timestampMillis DESC")
    fun getAllTrades(): Flow<List<TradeJournalEntity>>

    @Query("SELECT * FROM trade_journal WHERE id = :id LIMIT 1")
    suspend fun getTradeById(id: Long): TradeJournalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeJournalEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrades(trades: List<TradeJournalEntity>)

    @Update
    suspend fun updateTrade(trade: TradeJournalEntity)

    @Delete
    suspend fun deleteTrade(trade: TradeJournalEntity)

    @Query("DELETE FROM trade_journal WHERE id = :id")
    suspend fun deleteTradeById(id: Long)

    @Query("SELECT COUNT(*) FROM trade_journal")
    suspend fun countTrades(): Int

    @Query("DELETE FROM trade_journal")
    suspend fun clearAllTrades()
}
