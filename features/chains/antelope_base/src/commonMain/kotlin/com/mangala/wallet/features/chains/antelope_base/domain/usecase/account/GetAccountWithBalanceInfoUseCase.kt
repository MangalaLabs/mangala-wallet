package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

class GetAccountWithBalanceInfoUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        accountName: String,
        forceRefresh: Boolean
    ): Result<AntelopeAccount?> {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        return invoke(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        )
    }

    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<AntelopeAccount?> {
        return accountRepository.getAccountWithBalanceInfo(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        )
    }

    suspend fun invokeFlow(
        accountName: String,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeAccount?>> {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        return invokeFlow(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        )
    }

    suspend fun invokeFlow(
        accountNames: List<String>,
        forceRefresh: Boolean,
        includeTempAccounts: Boolean
    ): Flow<Resource<List<AntelopeAccount>?>> {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        return invokeFlow(
            accountNames = accountNames,
            blockchainType = blockchainType,
            includeTempAccounts = includeTempAccounts,
            forceRefresh = forceRefresh
        )
    }

    suspend fun invokeFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean,
        includeIapInitializedAccounts: Boolean = false
    ): Flow<Resource<AntelopeAccount?>> {
        return accountRepository.getAccountWithBalanceInfoFlow(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh,
            includeIapInitializedAccounts = includeIapInitializedAccounts
        )
    }

    suspend fun invokeFlow(
        accountNames: List<String>,
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        forceRefresh: Boolean,
        includeIapInitializedAccounts: Boolean = false
    ): Flow<Resource<List<AntelopeAccount>?>> {
        return accountRepository.getAccountsWithBalanceInfoFlow(
            accountNames = accountNames,
            blockchainType = blockchainType,
            includeTempAccounts = includeTempAccounts,
            forceRefresh = forceRefresh,
            includeIapInitializedAccounts = includeIapInitializedAccounts
        )
    }
}