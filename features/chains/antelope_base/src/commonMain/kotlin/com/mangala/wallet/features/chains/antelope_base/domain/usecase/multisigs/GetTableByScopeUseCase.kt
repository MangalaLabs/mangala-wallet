package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs

import com.mangala.antelope.base.api.model.GetTableByScopeRequest
import com.mangala.antelope.base.api.model.GetTableByScopeResponse
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class GetTableByScopeUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val repository: AntelopeRepository
) {
    suspend operator fun invoke(
        request: GetTableByScopeRequest
    ): GetTableByScopeResponse? {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        val result = repository.getTableByScope(blockchainType, request)

        if (result is ApiResponse.Success) {
            return result.body
        }
        return null
    }
}