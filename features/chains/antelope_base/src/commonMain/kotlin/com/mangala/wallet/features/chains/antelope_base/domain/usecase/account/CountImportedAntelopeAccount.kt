package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class CountImportedAntelopeAccount(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val repository: AccountRepository
) {
    suspend operator fun invoke(
        includeTempAccounts: Boolean = false,
        includeIapInitializedAccounts: Boolean = false,
        blockchainType: BlockchainType? = null
    ): Int {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType

        return repository.countImportedAccounts(
            resolvedBlockchainType,
            includeTempAccounts,
            includeIapInitializedAccounts
        )
    }
}