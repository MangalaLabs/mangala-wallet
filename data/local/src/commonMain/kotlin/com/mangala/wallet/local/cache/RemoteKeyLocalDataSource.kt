package com.mangala.wallet.local.cache

import commangalawalletdatabase.RemoteKeyEntity

interface RemoteKeyLocalDataSource {

    suspend fun insertOrReplaceKey(remoteKey: RemoteKeyEntity)
    suspend fun getRemoteKeyByQuery(query: String, blockchainUid: String): String?
    suspend fun deleteRemoteKeyByQuery(query: String, blockchainUid: String)
    suspend fun clearAllRemoteKeys()
}