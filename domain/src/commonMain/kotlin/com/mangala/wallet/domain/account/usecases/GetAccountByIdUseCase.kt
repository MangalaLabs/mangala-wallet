package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.model.account.domain.AccountModel
import kotlinx.coroutines.flow.Flow

class GetAccountByIdUseCase(
    private val accountRepository: AccountRepository
) {
    @Deprecated("Use invokeSuspend instead", ReplaceWith("getAccountById(accountId)"))
    operator fun invoke(accountId: String): AccountModel {
        return accountRepository.getAccountByIdUseCase(accountId)
    }

    suspend fun invokeSuspend(accountId: String): AccountModel {
        return accountRepository.getAccountById(accountId)
    }

    fun invokeFlow(
        accountId: String
    ): Flow<AccountModel> {
        return accountRepository.getAccountByIdFlow(accountId)
    }
}