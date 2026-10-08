package com.surgatrader.core.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun `usc to usd conversion correctly handles 100 to 1 ratio`() {
        val usc = 50000.0
        val usd = CurrencyFormatter.uscToUsd(usc)
        assertThat(usd).isEqualTo(500.0)

        val convertedBack = CurrencyFormatter.usdToUsc(usd)
        assertThat(convertedBack).isEqualTo(50000.0)
    }

    @Test
    fun `formatDualBalance displays both USC and USD clearly`() {
        val dual = CurrencyFormatter.formatDualBalance(50000.0)
        assertThat(dual).contains("50,000.00 USC")
        assertThat(dual).contains("$500.00")
    }

    @Test
    fun `formatPrice handles 3 decimal digits for XAUUSD cent account`() {
        val price = 4100.234
        val formatted = CurrencyFormatter.formatPrice(price, digits = 3)
        assertThat(formatted).isEqualTo("4,100.234")
    }

    @Test
    fun `formatLot outputs two decimal places`() {
        assertThat(CurrencyFormatter.formatLot(0.01)).isEqualTo("0.01")
        assertThat(CurrencyFormatter.formatLot(0.1)).isEqualTo("0.10")
        assertThat(CurrencyFormatter.formatLot(2.5)).isEqualTo("2.50")
    }
}
