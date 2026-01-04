package com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexqueue

import com.mangala.wallet.features.chains.antelopebase.AntelopeRexQueueEntity
import kotlinx.coroutines.flow.Flow

internal interface AntelopeRexQueueLocalDataSource {
    suspend fun getRexQueue(accountName: String, blockchainUid: String): List<AntelopeRexQueueEntity>
    fun getRexQueueFlow(accountName: String, blockchainUid: String): Flow<List<AntelopeRexQueueEntity>>
    suspend fun insertRexQueue(rexQueueEntity: List<AntelopeRexQueueEntity>)
    suspend fun deleteRexQueue(accountName: String, blockchainUid: String)
}