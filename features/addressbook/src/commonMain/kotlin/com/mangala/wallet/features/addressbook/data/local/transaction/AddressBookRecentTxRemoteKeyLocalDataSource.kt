package com.mangala.wallet.features.addressbook.data.local.transaction

import com.mangala.wallet.features.addressbook.database.RecentTxRemoteKey

interface AddressBookRecentTxRemoteKeyLocalDataSource {
    suspend fun getRemoteKeyByQuery(query: String): RecentTxRemoteKey?
    suspend fun insertOrReplaceKey(query: String, lastRequestedPage: Long?, lastUpdatedAt: Long, endOfPaginationReached: Boolean)
    suspend fun deleteRemoteKeyByQuery(query: String)
    suspend fun getLastUpdatedTimestamp(query: String): Long?
    suspend fun clearAllRecentTxRemoteKeys(): Boolean
}