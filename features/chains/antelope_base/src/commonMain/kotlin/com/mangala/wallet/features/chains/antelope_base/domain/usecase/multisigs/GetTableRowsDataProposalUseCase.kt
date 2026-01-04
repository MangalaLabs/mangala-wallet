package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs

import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsResponse
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class GetTableRowsDataProposalUseCase(private val repository: AntelopeRepository) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): GetTableRowsMultisigsResponse? {
        val result = repository.getTableRowsMultisigs(blockchainType, request)

        if (result is ApiResponse.Success) {
            return result.body
        }
        return null
    }
}