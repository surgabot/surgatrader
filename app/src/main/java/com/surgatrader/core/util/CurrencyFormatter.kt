package com.surgatrader.core.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ','
        decimalSeparator = '.'
    }

    private val moneyFormat = DecimalFormat("#,##0.00", symbols)
    private val lotFormat = DecimalFormat("0.00", symbols)

    fun uscToUsd(usc: Double): Double = usc / 100.0

    fun usdToUsc(usd: Double): Double = usd * 100.0

    fun formatUsc(usc: Double): String {
        return "${moneyFormat.format(usc)} USC"
    }

    fun formatUsd(usd: Double): String {
        return "$${moneyFormat.format(usd)}"
    }

    /**
     * Tampilkan saldo dalam USC dan konversi ke USD (100 USC = 1 USD).
     * Contoh: "50,000.00 USC ($500.00 USD)"
     */
    fun formatDualBalance(usc: Double): String {
        val usd = uscToUsd(usc)
        return "${moneyFormat.format(usc)} USC ($${moneyFormat.format(usd)})"
    }

    /**
     * Format harga sesuai digit simbol (misal 3 desimal untuk XAUUSDc Exness: 4100.234)
     */
    fun formatPrice(price: Double, digits: Int): String {
        val pattern = buildString {
            append("#,##0")
            if (digits > 0) {
                append(".")
                repeat(digits) { append("0") }
            }
        }
        val df = DecimalFormat(pattern, symbols)
        return df.format(price)
    }

    fun formatLot(lot: Double): String {
        return lotFormat.format(lot)
    }

    fun formatPoints(points: Double): String {
        val sign = if (points > 0) "+" else ""
        return "$sign${points.toInt()} pts"
    }
}
