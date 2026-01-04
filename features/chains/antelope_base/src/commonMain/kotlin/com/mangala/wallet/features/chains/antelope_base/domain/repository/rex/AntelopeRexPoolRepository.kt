package com.mangala.wallet.features.chains.antelope_base.domain.repository.rex

import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexPoolInfo
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AntelopeRexPoolRepository {
    suspend fun getTableRowsRexPool(blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeRexPoolInfo?>
    fun getTableRowsRexPoolFlow(blockchainType: BlockchainType, forceRefresh: Boolean): Flow<Resource<AntelopeRexPoolInfo?>>
}