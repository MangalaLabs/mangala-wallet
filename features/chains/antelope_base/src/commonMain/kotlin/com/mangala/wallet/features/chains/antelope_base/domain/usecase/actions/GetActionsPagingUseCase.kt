package com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions

import com.mangala.antelope.base.api.model.GetActionsPagingRequest
import com.mangala.antelope.base.api.model.GetActionsPagingResponse
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class GetActionsPagingUseCase(private val repository: AntelopeRepository) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        request: GetActionsPagingRequest
    ): GetActionsPagingResponse? {
        val result = repository.getActionsPaging(
            blockchainType = blockchainType,
            request = request
        )
        if (result is ApiResponse.Success) {
            return result.body
        }
        return null
    }


}
