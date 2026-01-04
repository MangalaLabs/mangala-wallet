package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.antelope.base.api.model.GetTableByScopeRequest
import com.mangala.antelope.base.domain.usecase.SearchAccountByQueryUseCase
import com.mangala.antelope.base.model.SystemContracts.EOS_SYSTEM_CONTRACT
import com.mangala.wallet.model.blockchain.BlockchainType

class GetAccountsByQueryUseCase(
    private val searchAccountByQueryUseCase: SearchAccountByQueryUseCase
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        accountName: String,
        filterByPayer: Boolean = true
    ): Result<List<String>> {
        val response = searchAccountByQueryUseCase(
            blockchainType,
            GetTableByScopeRequest(
                code = EOS_SYSTEM_CONTRACT,
                limit = 10,
                lowerBound = accountName,
                table = "userres",
                upperBound = accountName.padEnd(12, 'z')
            )
        )

        val exception = response.exceptionOrNull()
        if (exception != null) {
            return Result.failure(exception)
        }

        return Result.success(response.getOrNull()?.rows?.mapNotNull { if (filterByPayer) it.payer else it.scope } ?: emptyList())
    }
}