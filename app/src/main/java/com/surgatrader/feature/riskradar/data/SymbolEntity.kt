package com.surgatrader.feature.riskradar.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification

@Entity(tableName = "symbol_specifications")
data class SymbolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbolName: String,
    val profileName: String,
    val isDefault: Boolean,
    val contractSize: Double,
    val digits: Int,
    val point: Double,
    val tickSize: Double,
    val tickValue: Double,
    val minLot: Double,
    val lotStep: Double,
    val maxLot: Double,
    val leverage: Double,
    val spreadPoints: Int,
    val swapLong: Double,
    val swapShort: Double,
    val notes: String
) {
    fun toDomain(): SymbolSpecification {
        return SymbolSpecification(
            id = id,
            symbolName = symbolName,
            profileName = profileName,
            isDefault = isDefault,
            contractSize = contractSize,
            digits = digits,
            point = point,
            tickSize = tickSize,
            tickValue = tickValue,
            minLot = minLot,
            lotStep = lotStep,
            maxLot = maxLot,
            leverage = leverage,
            spreadPoints = spreadPoints,
            swapLong = swapLong,
            swapShort = swapShort,
            notes = notes
        )
    }

    companion object {
        fun fromDomain(spec: SymbolSpecification): SymbolEntity {
            return SymbolEntity(
                id = spec.id,
                symbolName = spec.symbolName,
                profileName = spec.profileName,
                isDefault = spec.isDefault,
                contractSize = spec.contractSize,
                digits = spec.digits,
                point = spec.point,
                tickSize = spec.tickSize,
                tickValue = spec.tickValue,
                minLot = spec.minLot,
                lotStep = spec.lotStep,
                maxLot = spec.maxLot,
                leverage = spec.leverage,
                spreadPoints = spec.spreadPoints,
                swapLong = spec.swapLong,
                swapShort = spec.swapShort,
                notes = spec.notes
            )
        }
    }
}
