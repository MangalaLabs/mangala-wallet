package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetAccountByNameUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(accountName: String): AntelopeAccount? {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        return accountRepository.getAccountByName(accountName = accountName, blockchainType)
    }

    suspend operator fun invoke(blockchainType: BlockchainType, accountName: String): AntelopeAccount? {
        return accountRepository.getAccountByName(accountName = accountName, blockchainType)
    }
}