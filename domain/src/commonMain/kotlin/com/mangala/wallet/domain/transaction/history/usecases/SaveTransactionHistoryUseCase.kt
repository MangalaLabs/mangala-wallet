package com.mangala.wallet.domain.transaction.history.usecases

import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.repository.TransactionRepository

class SaveTransactionHistoryUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(accountId: String, blockchainUid: String, transaction: Transaction) {
        repository.saveTransaction(accountId, blockchainUid, transaction)
    }
}