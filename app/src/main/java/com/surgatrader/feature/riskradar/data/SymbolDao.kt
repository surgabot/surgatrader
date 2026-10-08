package com.surgatrader.feature.riskradar.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SymbolDao {

    @Query("SELECT * FROM symbol_specifications WHERE isDefault = 1 LIMIT 1")
    fun getDefaultSymbol(): Flow<SymbolEntity?>

    @Query("SELECT * FROM symbol_specifications WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultSymbolOnce(): SymbolEntity?

    @Query("SELECT * FROM symbol_specifications ORDER BY symbolName ASC")
    fun getAllSymbols(): Flow<List<SymbolEntity>>

    @Query("SELECT * FROM symbol_specifications WHERE id = :id LIMIT 1")
    suspend fun getSymbolById(id: Long): SymbolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbol(symbol: SymbolEntity): Long

    @Update
    suspend fun updateSymbol(symbol: SymbolEntity)

    @Delete
    suspend fun deleteSymbol(symbol: SymbolEntity)

    @Query("UPDATE symbol_specifications SET isDefault = 0")
    suspend fun clearDefaultFlags()

    @Query("UPDATE symbol_specifications SET isDefault = 1 WHERE id = :id")
    suspend fun setSymbolAsDefault(id: Long)

    @Transaction
    suspend fun updateActiveDefaultSymbol(id: Long) {
        clearDefaultFlags()
        setSymbolAsDefault(id)
    }

    @Query("SELECT COUNT(*) FROM symbol_specifications")
    suspend fun countSymbols(): Int
}
