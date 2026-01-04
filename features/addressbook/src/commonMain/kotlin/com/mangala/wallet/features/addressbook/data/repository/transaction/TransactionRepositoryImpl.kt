package com.mangala.wallet.features.addressbook.data.repository.transaction

import com.mangala.wallet.features.addressbook.data.local.transaction.TransactionLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.transaction.AddressBookRecentTxRemoteKeyLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
    private val recentTxRemoteKeyLocalDataSource: AddressBookRecentTxRemoteKeyLocalDataSource,
) : TransactionRepository {
    override suspend fun getTransactionById(id: String): TransactionHistoryEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun getTransactionDetailById(id: String): TransactionDetailModel? =
        localDataSource.getTransactionDetailById(id)

    override fun getTransactionHistoryByContactId(
        contactId: String,
        limit: Int,
        offset: Int
    ): Flow<List<TransactionHistoryEntity>> {
        TODO("Not yet implemented")
    }

    override fun getTransactionHistoryByWalletAddress(
        address: String,
        limit: Int,
        offset: Int
    ): Flow<List<TransactionHistoryEntity>> {
        TODO("Not yet implemented")
    }

    override fun getTransactionDetailsByContactId(
        contactId: String,
        limit: Int,
        offset: Int
    ): Flow<List<TransactionDetailModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertTransaction(transaction: TransactionHistoryEntity): String {
        return localDataSource.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: TransactionHistoryEntity): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransaction(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun linkTransactionToContact(
        contactId: String,
        transactionId: String,
        walletAddressId: String,
        isSender: Boolean
    ): Boolean {
        return localDataSource.linkTransactionToContact(
            contactId = contactId,
            transactionId = transactionId,
            walletAddressId = walletAddressId,
            isSender = isSender
        )
    }

    override suspend fun getPendingTransactions(): List<TransactionHistoryEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminder(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun completeReminder(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun cancelReminder(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun processRecurringReminders(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun clearAllTransactionHistory(): Boolean {
        return localDataSource.clearAllTransactionHistory()
    }

    override suspend fun clearAllOfflineQueue(): Boolean {
        return localDataSource.clearAllOfflineQueue()
    }

    override suspend fun clearAllRecentTxRemoteKeys(): Boolean {
        return recentTxRemoteKeyLocalDataSource.clearAllRecentTxRemoteKeys()
    }
}