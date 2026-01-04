package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.model.account.domain.AccountModel

class UpdateAccountsUseCase(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(accounts: List<AccountModel>) = accountRepository.updateAccounts(accounts)
}