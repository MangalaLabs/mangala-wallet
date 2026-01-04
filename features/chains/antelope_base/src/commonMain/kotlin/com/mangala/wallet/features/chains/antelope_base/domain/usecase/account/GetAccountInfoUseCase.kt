package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.antelope.base.api.model.GetAccountResponse
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class GetAccountInfoUseCase(
    private val repository: AntelopeRepository,
    private val accountRepository: AccountRepository,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {
    @Deprecated("Use withResult instead, and change withResult to invoke when migration completes")
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        accountName: String
    ): GetAccountResponse? {
        val result = repository.getAccount(blockchainType, accountName)
        if (result is ApiResponse.Success) {
            return result.body
        }
        return null
    }

    // Pulls account data directly from the blockchain, ignoring any cached data
    // Useful for valid recipient checks, ...
    suspend fun withResult(
        blockchainType: BlockchainType,
        accountName: String
    ): Result<AntelopeAccount> {
        return accountRepository.getAccount(blockchainType, accountName)
    }

    // Pulls account data directly from the blockchain, ignoring any cached data
    // Useful for valid recipient checks, ...
    suspend fun withResult(
        accountName: String
    ): Result<AntelopeAccount> {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        return accountRepository.getAccount(blockchainType, accountName)
    }
}