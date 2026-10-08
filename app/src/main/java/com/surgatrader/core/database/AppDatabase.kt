package com.surgatrader.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.surgatrader.feature.riskradar.data.DefaultSymbols
import com.surgatrader.feature.riskradar.data.SymbolDao
import com.surgatrader.feature.riskradar.data.SymbolEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SymbolEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun symbolDao(): SymbolDao

    companion object {
        const val DATABASE_NAME = "surga_trader.db"

        fun buildDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    scope.launch(Dispatchers.IO) {
                        // Pre-populate with default symbols (XAUUSDc Exness Cent)
                        DefaultSymbols.ALL.forEach { spec ->
                            db.execSQL(
                                """
                                INSERT INTO symbol_specifications (
                                    id, symbolName, profileName, isDefault, contractSize, 
                                    digits, point, tickSize, tickValue, minLot, lotStep, 
                                    maxLot, leverage, spreadPoints, swapLong, swapShort, notes
                                ) VALUES (
                                    ${spec.id}, '${spec.symbolName}', '${spec.profileName}', ${if (spec.isDefault) 1 else 0},
                                    ${spec.contractSize}, ${spec.digits}, ${spec.point}, ${spec.tickSize},
                                    ${spec.tickValue}, ${spec.minLot}, ${spec.lotStep}, ${spec.maxLot},
                                    ${spec.leverage}, ${spec.spreadPoints}, ${spec.swapLong}, ${spec.swapShort},
                                    '${spec.notes}'
                                )
                                """.trimIndent()
                            )
                        }
                    }
                }
            }).build()
        }
    }
}
