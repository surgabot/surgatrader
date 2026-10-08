package com.surgatrader.feature.riskradar.data

import com.surgatrader.feature.riskradar.domain.model.SymbolSpecification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolRepository @Inject constructor(
    private val symbolDao: SymbolDao
) {
    fun getDefaultSymbol(): Flow<SymbolSpecification> {
        return symbolDao.getDefaultSymbol().map { entity ->
            entity?.toDomain() ?: DefaultSymbols.XAUUSD_EXNESS_CENT
        }
    }

    suspend fun getDefaultSymbolOnce(): SymbolSpecification {
        return symbolDao.getDefaultSymbolOnce()?.toDomain() ?: DefaultSymbols.XAUUSD_EXNESS_CENT
    }

    fun getAllSymbols(): Flow<List<SymbolSpecification>> {
        return symbolDao.getAllSymbols().map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun saveSymbol(spec: SymbolSpecification): Long {
        return symbolDao.insertSymbol(SymbolEntity.fromDomain(spec))
    }

    suspend fun updateSymbol(spec: SymbolSpecification) {
        symbolDao.updateSymbol(SymbolEntity.fromDomain(spec))
    }

    suspend fun setActiveDefault(id: Long) {
        symbolDao.updateActiveDefaultSymbol(id)
    }

    suspend fun deleteSymbol(spec: SymbolSpecification) {
        symbolDao.deleteSymbol(SymbolEntity.fromDomain(spec))
    }
}
