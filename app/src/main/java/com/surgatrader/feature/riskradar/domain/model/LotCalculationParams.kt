package com.surgatrader.feature.riskradar.domain.model

enum class SlInputMode(val label: String) {
    POINTS("Points / Pips"),
    PRICE("Level Harga (Price)"),
    MONEY("Nominal Uang (USC / $)")
}

enum class TpInputMode(val label: String) {
    NONE("Tanpa TP"),
    RATIO("Risk:Reward Ratio"),
    POINTS("Points"),
    PRICE("Level Harga (Price)")
}

enum class OrderDirection(val label: String) {
    BUY("BUY / Long"),
    SELL("SELL / Short")
}

data class LotCalculationParams(
    val balance: Double = 50000.0,       // Saldo akun (dalam USC untuk akun cent)
    val riskPercent: Double = 1.0,       // Risiko per trade (% dari saldo)
    val direction: OrderDirection = OrderDirection.BUY,
    val entryPrice: Double = 4125.500,   // Harga entry saat ini
    val slMode: SlInputMode = SlInputMode.POINTS,
    val slInput: Double = 500.0,         // Nilai SL sesuai slMode (points, price, atau USC)
    val tpMode: TpInputMode = TpInputMode.RATIO,
    val tpInput: Double = 2.0,           // Nilai TP (1:2 ratio, points, atau price)
    val includeSpread: Boolean = true,   // Perhitungkan spread ke dalam perhitungan SL
    val isCentAccount: Boolean = true    // Tampilkan konversi USC dan USD
)

data class LotCalculationResult(
    val recommendedLot: Double,          // Ukuran lot yang dinormalisasi (step & min/max)
    val rawLot: Double,                  // Ukuran lot murni sebelum rounding
    val riskAmountUsc: Double,           // Risiko aktual dalam USC
    val riskAmountUsd: Double,           // Risiko aktual dalam USD
    val effectiveSlPoints: Double,       // Jarak SL dalam points
    val slPrice: Double,                 // Level harga Stop Loss
    val tpPrice: Double,                 // Level harga Take Profit
    val potentialProfitUsc: Double,      // Potensi profit dalam USC
    val potentialProfitUsd: Double,      // Potensi profit dalam USD
    val riskRewardRatio: Double,         // Rasio R:R aktual
    val requiredMarginUsc: Double,       // Estimasi margin yang dibutuhkan (USC)
    val requiredMarginUsd: Double,       // Estimasi margin yang dibutuhkan (USD)
    val pointValuePerLot: Double,        // Nilai per point untuk 1 lot
    val stopOutBufferPoints: Double = 0.0, // Ketahanan jarak ke Stop Out (points)
    val warningMessage: String? = null   // Peringatan jika lot menyentuh min/max atau margin berlebih
)
