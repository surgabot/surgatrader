package com.surgatrader.feature.aura.domain.council

import com.surgatrader.core.util.DateTimeUtils
import com.surgatrader.feature.aura.domain.model.CouncilBias
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class EventImpact {
    LOW,
    MEDIUM,
    HIGH
}

data class EconomicEvent(
    val id: String,
    val title: String,
    val currency: String,
    val impact: EventImpact,
    val scheduledTimeWib: ZonedDateTime,
    val forecast: String = "-",
    val previous: String = "-"
)

interface EconomicCalendarRepository {
    fun getUpcomingEvents(now: ZonedDateTime): List<EconomicEvent>
}

class DefaultEconomicCalendarRepository : EconomicCalendarRepository {
    override fun getUpcomingEvents(now: ZonedDateTime): List<EconomicEvent> {
        // Contoh data kalender institusional USD/Gold yang realistis
        return listOf(
            EconomicEvent(
                id = "nfp",
                title = "US Non-Farm Payrolls (NFP)",
                currency = "USD",
                impact = EventImpact.HIGH,
                scheduledTimeWib = now.withHour(19).withMinute(30).withSecond(0),
                forecast = "175K",
                previous = "182K"
            ),
            EconomicEvent(
                id = "cpi",
                title = "US CPI YoY (Indeks Inflasi)",
                currency = "USD",
                impact = EventImpact.HIGH,
                scheduledTimeWib = now.plusDays(1).withHour(19).withMinute(30).withSecond(0),
                forecast = "2.9%",
                previous = "3.1%"
            ),
            EconomicEvent(
                id = "fomc",
                title = "Keputusan Suku Bunga The Fed (FOMC)",
                currency = "USD",
                impact = EventImpact.HIGH,
                scheduledTimeWib = now.plusDays(2).withHour(1).withMinute(0).withSecond(0),
                forecast = "5.25%",
                previous = "5.50%"
            ),
            EconomicEvent(
                id = "pce",
                title = "Core PCE Price Index",
                currency = "USD",
                impact = EventImpact.HIGH,
                scheduledTimeWib = now.plusDays(3).withHour(19).withMinute(30).withSecond(0),
                forecast = "0.2%",
                previous = "0.2%"
            )
        )
    }
}

object SentimentAthenaAnalyzer {

    private val repository: EconomicCalendarRepository = DefaultEconomicCalendarRepository()

    /**
     * Menganalisis kalender ekonomi makro AS, mendeteksi zona bahaya rilis berita
     * dalam rentang 30 menit sebelum dan sesudah rilis, serta menghitung countdown WIB.
     */
    fun analyze(
        now: ZonedDateTime = DateTimeUtils.getCurrentWibTime(),
        calendarRepo: EconomicCalendarRepository = repository
    ): CouncilMemberReport {
        val events = calendarRepo.getUpcomingEvents(now)
            .filter { it.impact == EventImpact.HIGH }
            .sortedBy { it.scheduledTimeWib }

        val nearestEvent = events.firstOrNull()

        var isDangerZoneActive = false
        var dangerEvent: EconomicEvent? = null
        var minutesToEvent = 9999L

        for (event in events) {
            val diffMinutes = Duration.between(now, event.scheduledTimeWib).toMinutes()
            // Zona bahaya: 30 menit sebelum hingga 30 menit setelah rilis
            if (diffMinutes in -30..30) {
                isDangerZoneActive = true
                dangerEvent = event
                minutesToEvent = diffMinutes
                break
            } else if (diffMinutes > 0 && diffMinutes < minutesToEvent) {
                minutesToEvent = diffMinutes
            }
        }

        val (bias, confidence) = when {
            isDangerZoneActive -> {
                // Selama zona bahaya berita, dewan wajib netral dan menahan transaksi
                CouncilBias.NEUTRAL to 95
            }
            minutesToEvent < 120 -> {
                // Mendekati rilis dalam 2 jam ke depan -> kewaspadaan meningkat
                CouncilBias.NEUTRAL to 70
            }
            else -> {
                // Aman dari intervensi rilis besar
                CouncilBias.BULLISH to 80
            }
        }

        val importance = when {
            isDangerZoneActive -> ImportanceLevel.CRITICAL
            minutesToEvent < 120 -> ImportanceLevel.HIGH
            else -> ImportanceLevel.LOW
        }

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm 'WIB'", Locale("id", "ID"))
        val nearestTimeStr = nearestEvent?.scheduledTimeWib?.format(timeFormatter) ?: "-"
        val nearestTitle = nearestEvent?.title ?: "Tidak ada rilis berita berdampak tinggi"

        val headline = if (isDangerZoneActive) {
            "⚠️ ZONA BAHAYA BERITA AKTIF: ${dangerEvent?.title}"
        } else {
            "Zona Makro Aman: Berita Terdekat $nearestTitle ($nearestTimeStr)"
        }

        val detailedReason = buildString {
            if (isDangerZoneActive) {
                append("STATUS ZONA BAHAYA BERITA AKTIF! ")
                append("Peristiwa '${dangerEvent?.title}' berlangsung dalam jendela kritis ±30 menit. ")
                append("Likuiditas pasar dan spread dapat mengalami lonjakan tak terprediksi (slippage tinggi). ")
                append("Protokol dewan mewajibkan posisi baru ditahan.")
            } else {
                append("Pasar saat ini berada di luar zona bahaya rilis makro. ")
                append("Agenda berita berdampak tinggi terdekat adalah $nearestTitle pada pukul $nearestTimeStr ")
                val hours = minutesToEvent / 60
                val mins = minutesToEvent % 60
                append("(countdown: $hours jam $mins menit lagi). ")
                append("Sentimen geopolitik safe-haven tetap kondusif.")
            }
        }

        val spokenNarration = if (isDangerZoneActive) {
            "Sentiment-Athena mengeluarkan peringatan kritis. Status zona bahaya berita saat ini sedang aktif untuk rilis ${dangerEvent?.title}. Dilarang membuka posisi baru sampai lonjakan volatilitas pasca berita mereda."
        } else {
            val hours = minutesToEvent / 60
            val mins = minutesToEvent % 60
            val countdownStr = if (hours > 0) "$hours jam $mins menit" else "$mins menit"
            "Sentiment-Athena memindai kalender ekonomi global. Pasar berada dalam zona aman dari berita makro berperingkat tinggi. Agenda terdekat adalah $nearestTitle pada pukul $nearestTimeStr, atau sekitar $countdownStr lagi. Aliran likuiditas safe-haven berjalan normal."
        }

        val keyLevels = listOf(
            KeyLevel("Countdown Berita (Menit)", minutesToEvent.toDouble()),
            KeyLevel("Status Bahaya", if (isDangerZoneActive) 1.0 else 0.0)
        )

        return CouncilMemberReport(
            entityId = "sentiment",
            entityName = "SENTIMENT-ATHENA",
            role = "Geopolitik & Kalender",
            bias = bias,
            confidenceScore = confidence,
            importance = importance,
            keyLevels = keyLevels,
            headline = headline,
            detailedReason = detailedReason,
            spokenNarration = spokenNarration
        )
    }
}
