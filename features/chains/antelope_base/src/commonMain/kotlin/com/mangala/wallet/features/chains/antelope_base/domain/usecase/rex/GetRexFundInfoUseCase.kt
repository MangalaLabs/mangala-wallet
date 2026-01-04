package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexFundRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetRexFundInfoUseCase(private val antelopeRexFundRepository: AntelopeRexFundRepository) {
    suspend operator fun invoke(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean) =
        antelopeRexFundRepository.getTableRowsRexFund(accountName, blockchainType, forceRefresh)

    fun invokeFlow(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean) =
        antelopeRexFundRepository.getTableRowsRexFundFlow(accountName, blockchainType, forceRefresh)
}