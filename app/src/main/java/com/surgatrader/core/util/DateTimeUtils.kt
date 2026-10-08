package com.surgatrader.core.util

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class DangerousPeriod(
    val title: String,
    val description: String,
    val startHourWib: Int,
    val startMinuteWib: Int,
    val endHourWib: Int,
    val endMinuteWib: Int,
    val level: DangerLevel
)

enum class DangerLevel {
    MODERATE,
    HIGH,
    EXTREME
}

object DateTimeUtils {

    val ZONE_WIB: ZoneId = ZoneId.of("Asia/Jakarta")

    val DANGEROUS_GOLD_HOURS = listOf(
        DangerousPeriod(
            title = "Pembukaan Sesi London",
            description = "Lonjakan likuiditas Eropa & fakeout pergerakan awal",
            startHourWib = 14,
            startMinuteWib = 0,
            endHourWib = 16,
            endMinuteWib = 0,
            level = DangerLevel.HIGH
        ),
        DangerousPeriod(
            title = "Rilis Data AS & Open New York",
            description = "NFP / CPI / PPI / Retail Sales + lonjakan volume Wall Street",
            startHourWib = 19,
            startMinuteWib = 15,
            endHourWib = 21,
            endMinuteWib = 30,
            level = DangerLevel.EXTREME
        ),
        DangerousPeriod(
            title = "FOMC / Rilis Kebijakan The Fed",
            description = "Pernyataan suku bunga FOMC & pidato Jerome Powell (Rabu/Kamis malam)",
            startHourWib = 1,
            startMinuteWib = 0,
            endHourWib = 2,
            endMinuteWib = 30,
            level = DangerLevel.EXTREME
        ),
        DangerousPeriod(
            title = "Rollover / Daily Settlement Exness",
            description = "Likuiditas perbankan tipis, spread emas melebar tajam!",
            startHourWib = 4,
            startMinuteWib = 50,
            endHourWib = 5,
            endMinuteWib = 30,
            level = DangerLevel.HIGH
        )
    )

    fun getCurrentWibTime(): ZonedDateTime {
        return ZonedDateTime.now(ZONE_WIB)
    }

    /**
     * Memeriksa apakah saat ini berada dalam periode jam rawan trading emas
     */
    fun getActiveDangerousPeriod(now: ZonedDateTime = getCurrentWibTime()): DangerousPeriod? {
        val currentMinutes = now.hour * 60 + now.minute
        return DANGEROUS_GOLD_HOURS.firstOrNull { period ->
            val startMinutes = period.startHourWib * 60 + period.startMinuteWib
            val endMinutes = period.endHourWib * 60 + period.endMinuteWib
            currentMinutes in startMinutes..endMinutes
        }
    }

    fun formatWibDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID")).apply {
            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        }
        return sdf.format(Date(timestamp))
    }

    fun formatWibTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID")).apply {
            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        }
        return sdf.format(Date(timestamp))
    }

    /**
     * Mengembalikan nama sesi pasar internasional yang aktif dengan konversi otomatis
     * jam musim (DST) via ZoneId Europe/London, America/New_York, dan Asia/Tokyo.
     */
    fun getActiveTradingSession(now: ZonedDateTime = getCurrentWibTime()): String {
        val londonTime = now.withZoneSameInstant(ZoneId.of("Europe/London")).toLocalTime()
        val nyTime = now.withZoneSameInstant(ZoneId.of("America/New_York")).toLocalTime()
        val tokyoTime = now.withZoneSameInstant(ZoneId.of("Asia/Tokyo")).toLocalTime()

        val isLondonOpen = londonTime.hour in 8..16 || (londonTime.hour == 16 && londonTime.minute <= 30)
        val isNyOpen = nyTime.hour in 8..16
        val isTokyoOpen = tokyoTime.hour in 9..17

        return when {
            isLondonOpen && isNyOpen -> "OVERLAP LON/NY"
            isLondonOpen -> "SESI LONDON"
            isNyOpen -> "SESI NEW YORK"
            isTokyoOpen -> "SESI ASIA"
            else -> "PASIF / ROLLOVER"
        }
    }

    fun formatCurrentWibClock(now: ZonedDateTime = getCurrentWibTime()): String {
        val sdf = SimpleDateFormat("HH:mm:ss 'WIB'", Locale("id", "ID")).apply {
            timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        }
        return sdf.format(Date.from(now.toInstant()))
    }
}
