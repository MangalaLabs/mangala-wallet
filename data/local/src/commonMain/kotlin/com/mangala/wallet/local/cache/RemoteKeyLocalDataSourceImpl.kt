package com.mangala.wallet.local.cache

import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import commangalawalletdatabase.RemoteKeyEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class RemoteKeyLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): RemoteKeyLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun insertOrReplaceKey(remoteKey: RemoteKeyEntity) = withContext(ioDispatcher) {
        dbQuery.insertRemoteKey(remoteKey.query, remoteKey.lastRequestedPage, remoteKey.blockchain_uid)
    }

    override suspend fun getRemoteKeyByQuery(query: String, blockchainUid: String): String? = withContext(ioDispatcher) {
        return@withContext dbQuery.getRemoteKeyByQuery(query, blockchain_uid = blockchainUid).executeAsOneOrNull()
    }

    override suspend fun deleteRemoteKeyByQuery(query: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.deleteRemoteKeyByQuery(query, blockchainUid)
    }

    override suspend fun clearAllRemoteKeys() = withContext(ioDispatcher) {
        dbQuery.clearAllRemoteKeys()
    }
}