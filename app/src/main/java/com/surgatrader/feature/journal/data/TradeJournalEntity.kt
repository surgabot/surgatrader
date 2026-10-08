package com.surgatrader.feature.journal.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Entitas database Room untuk riwayat dan jurnal transaksi trading nyata
 */
@Entity(tableName = "trade_journal")
data class TradeJournalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ticket: Long = System.currentTimeMillis() % 10000000,
    val timestampMillis: Long = System.currentTimeMillis(),
    val symbol: String = "XAUUSDc",
    val type: String = "BUY", // BUY / SELL
    val openPrice: Double = 4100.234,
    val closePrice: Double = 4105.500,
    val volumeLot: Double = 0.02,
    val pnlUsc: Double = 105.32,
    val setupTag: String = "Sinyal Dewan Kuantum Aegis",
    val dewanConfluenceScore: Int = 85,
    val emotionTag: String = "Disiplin & Sesuai Plan",
    val notes: String = "Eksekusi sesuai sinyal konsensus dewan kuantum."
) {
    val pnlUsd: Double
        get() = pnlUsc / 100.0

    val isProfit: Boolean
        get() = pnlUsc >= 0.0

    val formattedDateWib: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy • HH:mm 'WIB'", Locale("id", "ID"))
            sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            return sdf.format(Date(timestampMillis))
        }
}
