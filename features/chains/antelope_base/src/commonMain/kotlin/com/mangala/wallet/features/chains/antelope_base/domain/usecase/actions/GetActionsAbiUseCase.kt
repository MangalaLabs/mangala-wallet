package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.abis.ActionAbiRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetActionsAbiUseCase(
    private val actionAbiRepository: ActionAbiRepository
) {

    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>> {
        return actionAbiRepository.getActionsAbi(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        )

    }
}