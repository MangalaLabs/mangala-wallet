package com.mangala.wallet.local.transaction.history

import app.cash.paging.PagingSource
import commangalawalletdatabase.TransactionsEntity

interface TransactionLocalDataSource {

     suspend fun insertTransactions(transactions: List<TransactionsEntity>)
     suspend fun insertTransaction(transaction: TransactionsEntity)
     suspend fun getPendingTransactions(blockchainUid: String): List<TransactionsEntity>
     fun getTransactionPagingSource(
          accountId: String,
          blockchainUid: String,
          transactionType: String?,
          transactionStatus: String?,
          startDateFilter: Long?,
          endDateFilter: Long?
     ): PagingSource<Int, TransactionsEntity>

     suspend fun getTransactionByTxHash(accountId: String, blockchainUid: String, txHash: String): TransactionsEntity
     suspend fun updateTransactionStatus(accountId: String, blockchainUid: String, txHashes: List<String>, status: String)
     suspend fun clearAllTransactions()
}