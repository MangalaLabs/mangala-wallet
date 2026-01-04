package com.mangala.wallet.features.addressbook.data.local.transaction

import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.database.RecentTxRemoteKey
import com.mangala.wallet.utils.ext.toLong
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AddressBookRecentTxRemoteKeyLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AddressBookRecentTxRemoteKeyLocalDataSource {
    val dbQuery = databaseWrapper.database.addressBookDatabaseQueries

    override suspend fun getRemoteKeyByQuery(query: String): RecentTxRemoteKey? = withContext(ioDispatcher) {
            dbQuery.getRecentTxRemoteKeyByQuery(query).executeAsOneOrNull()
        }

    override suspend fun insertOrReplaceKey(
        query: String,
        lastRequestedPage: Long?,
        lastUpdatedAt: Long,
        endOfPaginationReached: Boolean
    ) = withContext(ioDispatcher) {
        dbQuery.insertOrReplaceRecentTxRemoteKey(
            query = query,
            last_requested_page = lastRequestedPage,
            last_updated_at = lastUpdatedAt,
            end_of_pagination_reached = endOfPaginationReached.toLong()

        )
    }

    override suspend fun deleteRemoteKeyByQuery(query: String) = withContext(ioDispatcher) {
            dbQuery.deleteRecentTxRemoteKeyByQuery(query)
        }

    override suspend fun getLastUpdatedTimestamp(query: String): Long? = withContext(ioDispatcher) {
            dbQuery.getRecentTxLastUpdatedTimestamp(query).executeAsOneOrNull()
        }

    override suspend fun clearAllRecentTxRemoteKeys(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            dbQuery.clearAllRecentTxRemoteKeys()
            true
        } catch (e: Exception) {
            false
        }
    }
}