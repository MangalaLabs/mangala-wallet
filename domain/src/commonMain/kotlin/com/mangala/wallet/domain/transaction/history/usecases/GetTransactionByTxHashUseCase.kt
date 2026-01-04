package com.mangala.wallet.domain.transaction.history.usecases

import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.repository.TransactionRepository

class GetTransactionByTxHashUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(accountId: String, blockchainUid: String, txHash: String): Transaction {
        return repository.getTransactionByTxHash(accountId, blockchainUid, txHash)
    }
}