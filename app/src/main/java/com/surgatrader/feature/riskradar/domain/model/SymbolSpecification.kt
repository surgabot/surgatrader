package com.surgatrader.feature.riskradar.domain.model

data class SymbolSpecification(
    val id: Long = 0,
    val symbolName: String = "XAUUSDc",
    val profileName: String = "XAUUSDc Exness Cent",
    val isDefault: Boolean = true,
    val contractSize: Double = 100.0,
    val digits: Int = 3,
    val point: Double = 0.001,
    val tickSize: Double = 0.001,
    val tickValue: Double = 10.0,      // Nilai per tick dalam mata uang akun (USC untuk akun Cent) per 1 lot
    val minLot: Double = 0.01,
    val lotStep: Double = 0.01,
    val maxLot: Double = 200.0,
    val leverage: Double = 2000.0,
    val spreadPoints: Int = 25,
    val swapLong: Double = -0.5,
    val swapShort: Double = 0.1,
    val notes: String = "Default Exness Cent XAUUSDc. Selalu cek MT5: Klik kanan XAUUSDc > Specification"
) {
    /**
     * Menghitung nilai per 1 point untuk 1 lot (dalam satuan mata uang akun: USC)
     */
    val pointValuePerLot: Double
        get() = if (tickSize > 0) {
            tickValue * (point / tickSize)
        } else {
            point * contractSize
        }
}
