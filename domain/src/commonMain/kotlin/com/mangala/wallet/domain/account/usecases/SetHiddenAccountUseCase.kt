package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository

class SetHiddenAccountUseCase(private val accountRepository: AccountRepository) {
    operator fun invoke(accountId: String) = accountRepository.setHiddenAccount(accountId)
}