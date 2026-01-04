package com.mangala.wallet.domain.transaction.history.repository

import app.cash.paging.PagingData
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TransactionRepository {
    suspend fun getPendingTransactions(blockchainUid: String): List<Transaction>
    suspend fun getTransactionByTxHash(accountId: String, blockchainUid: String, txHash: String): Transaction
    suspend fun saveTransaction(accountId: String, blockchainUid: String, transaction: Transaction)
    suspend fun updateTransactionStatus(accountId: String, blockchainUid: String, txHashes: List<String>, status: TransactionStatus)
    suspend fun clearAllUserTransactions(): Result<Unit>
    fun getPaginatedTransactionsForAddress(
        accountId: String,
        blockchainType: BlockchainType,
        walletAddress: String,
        transactionTypeFilter: TransactionType?,
        transactionStatusFilter: TransactionStatus?,
        startDateFilter: Instant?,
        endDateFilter: Instant?
    ): Flow<PagingData<Transaction>>
}