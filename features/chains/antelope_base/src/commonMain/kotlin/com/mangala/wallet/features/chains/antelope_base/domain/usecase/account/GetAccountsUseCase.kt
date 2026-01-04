package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountBasicInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class GetAccountsUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        includeTempAccounts: Boolean = false,
        includeIapInitializedAccounts: Boolean = false,
        blockchainType: BlockchainType? = null
    ): List<AntelopeAccount> {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType

        return accountRepository.getAccounts(
            resolvedBlockchainType,
            includeTempAccounts,
            includeIapInitializedAccounts
        )
    }

    suspend fun invokeFlow(
        includeTempAccounts: Boolean = false,
        includeIapInitializedAccounts: Boolean = false,
        blockchainType: BlockchainType? = null
    ): Flow<List<AntelopeAccount>> {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType

        return accountRepository.getAccountsFlow(
            resolvedBlockchainType,
            includeTempAccounts,
            includeIapInitializedAccounts
        )
    }

    suspend fun invokeWithBasicInfo(
        includeTempAccounts: Boolean = false,
        includeIapInitializedAccounts: Boolean = false,
        blockchainType: BlockchainType? = null
    ): Flow<List<AntelopeAccountBasicInfo>> {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType

        return accountRepository.invokeWithBasicInfo(
            includeTempAccounts = includeTempAccounts,
            includeIapInitializedAccounts = includeIapInitializedAccounts,
            blockchainType = resolvedBlockchainType
        ).map {
            println("invokeWithBasicInfo $it")
            it
        }.distinctUntilChanged()
    }

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean = false,
        includeIapInitializedAccounts: Boolean = false
    ): List<AntelopeAccount> {
        return accountRepository.getAccounts(
            blockchainType,
            includeTempAccounts,
            includeIapInitializedAccounts
        )
    }
}