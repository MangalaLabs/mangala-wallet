package com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexpool

import com.mangala.wallet.features.chains.antelopebase.AntelopeRexPoolEntity
import kotlinx.coroutines.flow.Flow

interface AntelopeRexPoolLocalDataSource {
    suspend fun insertRexPool(rexPool: AntelopeRexPoolEntity)
    suspend fun getTableRowsRexPool(blockchainUid: String): AntelopeRexPoolEntity?
    fun getTableRowsRexPoolFlow(blockchainUid: String): Flow<AntelopeRexPoolEntity?>
    suspend fun clearAllRexPools()
}