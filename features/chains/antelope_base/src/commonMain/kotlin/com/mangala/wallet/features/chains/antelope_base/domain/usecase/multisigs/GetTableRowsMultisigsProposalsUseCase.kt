package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs

import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class GetTableRowsMultisigsProposalsUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val repository: AntelopeRepository
) {

    suspend operator fun invoke(request: GetTableRowsMultisigsRequest): GetMultisigProposalTableRowResponse? {
        val blockchainType = getSelectedNetworkUseCase().blockchainType
        return invoke(request, blockchainType)
    }

    suspend operator fun invoke(
        request: GetTableRowsMultisigsRequest,
        blockchainType: BlockchainType,
    ): GetMultisigProposalTableRowResponse? {
        val result = repository.getTableRowsMultisigsProposals(blockchainType, request)
        if (result is ApiResponse.Success) {
            return result.body
        }
        return null
    }
}