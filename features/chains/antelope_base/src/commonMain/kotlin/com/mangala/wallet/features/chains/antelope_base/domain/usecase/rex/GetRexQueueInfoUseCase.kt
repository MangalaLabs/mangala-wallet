package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexQueueRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetRexQueueInfoUseCase(private val repository: AntelopeRexQueueRepository) {
    suspend operator fun invoke(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean) =
        repository.getTableRowsRexQueue(accountName, blockchainType, forceRefresh)

    fun invokeFlow(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean) =
        repository.getTableRowsRexQueueFlow(accountName, blockchainType, forceRefresh)
}