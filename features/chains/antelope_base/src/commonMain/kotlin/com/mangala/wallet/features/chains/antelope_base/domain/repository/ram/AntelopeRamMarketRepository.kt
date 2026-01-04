package com.mangala.wallet.features.chains.antelope_base.domain.repository.ram

import com.mangala.wallet.features.chains.antelope_base.domain.model.ram.AntelopeRamMarketInfo
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AntelopeRamMarketRepository {
    suspend fun getRamPrice(blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeRamMarketInfo?>
    fun getRamPriceFlow(blockchainType: BlockchainType, forceRefresh: Boolean): Flow<Resource<AntelopeRamMarketInfo?>>
}