package com.mangala.wallet.local.cache

import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import commangalawalletdatabase.TransactionMetadataEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TransactionMetadataLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    TransactionMetadataLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun insertTransactionMetadata(metadata: TransactionMetadataEntity) = withContext(ioDispatcher) {
        dbQuery.insertTransactionMetadata(
            metadata.address,
            metadata.blockchainUid,
            metadata.lastSynchedTimestamp
        )
    }

    override suspend fun getLastUpdatedTimestamp(address: String, blockchainUid: String): Long? = withContext(ioDispatcher) {
        return@withContext dbQuery.getLastSynchedTimestamp(address, blockchainUid).executeAsOneOrNull()
    }

    override suspend fun clearAllTransactionMetadata() = withContext(ioDispatcher) {
        dbQuery.clearAllTransactionMetadata()
    }
}