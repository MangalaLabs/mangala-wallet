package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.antelope.base.api.model.GetTableByScopeRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableByScopeUseCase

class GetContractNamesUseCase(
    private val getTableByScopeUseCase : GetTableByScopeUseCase
) {
    suspend operator fun invoke(): List<String>? {
        val response = getTableByScopeUseCase.invoke(
            GetTableByScopeRequest(
                code = "eosio",
                table = "userres",
                lowerBound ="eosio",
                upperBound = "eosiozzzzzzz",
                limit = 5
            )
        )

        if (response != null) {
            return response.rows?.mapNotNull { it.scope }
        }
        return emptyList()
    }
}