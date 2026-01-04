package com.mangala.wallet.features.chains.antelope_base.data.local.ram

import com.mangala.wallet.features.chains.antelopebase.AntelopeRamMarketEntity
import kotlinx.coroutines.flow.Flow

interface AntelopeRamMarketLocalDataSource {
    suspend fun getRamPrice(blockchainUid: String): AntelopeRamMarketEntity?
    fun getRamPriceFlow(blockchainUid: String): Flow<AntelopeRamMarketEntity?>
    suspend fun insertRamPrice(ramMarketData: AntelopeRamMarketEntity)
    suspend fun clearAllRamMarkets()
}