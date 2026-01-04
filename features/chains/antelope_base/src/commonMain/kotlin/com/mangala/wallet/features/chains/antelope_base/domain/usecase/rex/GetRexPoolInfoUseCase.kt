package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexPoolRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetRexPoolInfoUseCase(private val repository: AntelopeRexPoolRepository) {
    suspend operator fun invoke(blockchainType: BlockchainType, forceRefresh: Boolean) =
        repository.getTableRowsRexPool(blockchainType, forceRefresh)

    fun invokeFlow(blockchainType: BlockchainType, forceRefresh: Boolean) =
        repository.getTableRowsRexPoolFlow(blockchainType, forceRefresh)
}