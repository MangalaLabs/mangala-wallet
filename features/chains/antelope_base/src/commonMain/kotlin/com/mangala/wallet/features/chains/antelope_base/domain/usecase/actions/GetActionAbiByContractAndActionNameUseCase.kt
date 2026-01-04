package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.abis.ActionAbiRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetActionAbiByContractAndActionNameUseCase(
    private val actionAbiRepository: ActionAbiRepository,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) {

    suspend operator fun invoke(
        accountName: String,
        actionName: String,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>> {
        val networkSelected = getSelectedNetworkUseCase()
        val blockchainType = networkSelected.blockchainType

        return invoke(
            accountName = accountName,
            actionName = actionName,
            forceRefresh = forceRefresh,
            blockchainType = blockchainType
        )
    }

    suspend operator fun invoke(
        accountName: String,
        actionName: String,
        forceRefresh: Boolean,
        blockchainType: BlockchainType
    ): Result<List<AntelopeActionAbi>> = actionAbiRepository.getActionAbiByContractAndActionName(
        accountName = accountName,
        actionName = actionName,
        forceRefresh = forceRefresh,
        blockchainType = blockchainType
    )
}