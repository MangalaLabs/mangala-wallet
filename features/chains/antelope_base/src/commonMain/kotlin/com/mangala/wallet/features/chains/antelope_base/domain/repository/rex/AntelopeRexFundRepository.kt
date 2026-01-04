package com.mangala.wallet.features.chains.antelope_base.domain.repository.rex

import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexFundInfo
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AntelopeRexFundRepository {
    suspend fun getTableRowsRexFund(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeRexFundInfo?>
    fun getTableRowsRexFundFlow(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Flow<Resource<AntelopeRexFundInfo?>>
}