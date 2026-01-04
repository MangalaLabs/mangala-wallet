package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearAccountsUseCase(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            accountRepository.deleteAllAccounts()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}