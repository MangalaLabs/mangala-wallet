package com.mangala.eticket.data.local.cache

import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import commangalaeticketdatabase.RemotePagingKeyEntity

class RemotePagingKeyLocalDataSourceImpl(
    databaseWrapper: ETicketDatabaseWrapper
): RemotePagingKeyLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.eTicketDatabaseQueries

    override suspend fun insertOrReplace(remoteKey: RemotePagingKeyEntity) {
        dbQuery.insertRemotePagingKeyEntity(
            remoteKey.id,
            remoteKey.lastRequestedPage,
            remoteKey.lastSyncedTimestamp
        )
    }

    override suspend fun findById(id: String): RemotePagingKeyEntity? {
        return dbQuery.findRemotePagingKeyEntity(id).executeAsOneOrNull()
    }

    override suspend fun deleteById(id: String) {
        dbQuery.transaction {
            dbQuery.deleteRemotePagingKeyEntity(id)
        }
    }
    
    override suspend fun clearAll() {
        dbQuery.transaction {
            dbQuery.clearAllRemotePagingKeyEntities()
        }
    }
}