package com.mangala.wallet.features.chains.antelope_base.domain.repository.token

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AntelopeAccountTokenBalanceRepository {
    suspend fun getAccountTokenBalance(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Result<List<AntelopeTokenBalance>>
    suspend fun getAccountTokenBalanceFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeTokenBalance>?>>
}