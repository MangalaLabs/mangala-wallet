package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.transaction.history.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearEVMTransactionHistoryUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            transactionRepository.clearAllUserTransactions().getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}