package com.surgatrader.feature.riskradar.data

import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification

object DefaultSymbols {

    val XAUUSD_EXNESS_CENT = SymbolSpecification(
        id = 1,
        symbolName = "XAUUSDc",
        profileName = "XAUUSDc Exness Cent",
        isDefault = true,
        contractSize = 100.0,
        digits = 3,
        point = 0.001,
        tickSize = 0.001,
        tickValue = 10.0, // 10 USC per point per lot (0.10 USD)
        minLot = 0.01,
        lotStep = 0.01,
        maxLot = 200.0,
        leverage = 2000.0,
        spreadPoints = 25,
        swapLong = -0.5,
        swapShort = 0.1,
        notes = "Default Exness Cent XAUUSDc (3 digit desimal, 1 pt = 0.001). Pastikan mencocokkan dengan MT5: Market Watch > Spesifikasi."
    )

    val XAUUSD_STANDARD = SymbolSpecification(
        id = 2,
        symbolName = "XAUUSD",
        profileName = "XAUUSD Standard (USD)",
        isDefault = false,
        contractSize = 100.0,
        digits = 2,
        point = 0.01,
        tickSize = 0.01,
        tickValue = 1.0, // $1.00 USD per point per lot
        minLot = 0.01,
        lotStep = 0.01,
        maxLot = 100.0,
        leverage = 500.0,
        spreadPoints = 20,
        swapLong = -5.0,
        swapShort = 1.0,
        notes = "Akun Standar USD untuk Gold (2 digit desimal, 1 pt = 0.01)."
    )

    val ALL = listOf(
        XAUUSD_EXNESS_CENT,
        XAUUSD_STANDARD
    )
}
