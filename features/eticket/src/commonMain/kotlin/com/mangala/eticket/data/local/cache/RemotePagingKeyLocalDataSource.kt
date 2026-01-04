package com.mangala.eticket.data.local.cache

import commangalaeticketdatabase.RemotePagingKeyEntity

interface RemotePagingKeyLocalDataSource {
    suspend fun insertOrReplace(remoteKey: RemotePagingKeyEntity)
    suspend fun findById(id: String): RemotePagingKeyEntity?
    suspend fun deleteById(id: String)
    suspend fun clearAll()
}