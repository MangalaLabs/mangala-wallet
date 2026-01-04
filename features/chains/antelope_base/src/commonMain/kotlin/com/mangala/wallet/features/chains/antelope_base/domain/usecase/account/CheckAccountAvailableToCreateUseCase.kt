package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.features.chains.antelope_base.domain.repository.createaccount.CreateAccountRepository
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType

class CheckAccountAvailableToCreateUseCase(
    private val checkAccountNotExistsUseCase: CheckAccountNotExistsUseCase,
    private val createAccountRepository: CreateAccountRepository
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        accountName: String,
        accountNameType: AccountNameType
    ): Boolean {
        if (accountNameType == AccountNameType.Premium) {
            // Validate against our premium account blacklist
            val isInBlacklist = createAccountRepository.isInBlackListAccountName(accountName)
                .getOrDefault(true) // if fetch fails then default value = true to prevent user from creating

            if (isInBlacklist) return false
        }

        return checkAccountNotExistsUseCase(blockchainType, accountName)
    }
}