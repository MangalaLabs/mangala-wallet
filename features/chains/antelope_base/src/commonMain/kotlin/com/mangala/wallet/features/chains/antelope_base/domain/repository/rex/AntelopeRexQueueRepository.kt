package com.mangala.wallet.features.chains.antelope_base.domain.repository.rex

import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexQueueInfo
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AntelopeRexQueueRepository {
    suspend fun getTableRowsRexQueue(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeRexQueueInfo>
    fun getTableRowsRexQueueFlow(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Flow<Resource<AntelopeRexQueueInfo>>
}