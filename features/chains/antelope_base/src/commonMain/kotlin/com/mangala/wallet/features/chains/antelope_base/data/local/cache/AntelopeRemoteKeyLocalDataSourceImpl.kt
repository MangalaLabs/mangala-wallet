package com.mangala.wallet.features.chains.antelope_base.data.local.cache

import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelope_base.domain.model.cache.AntelopeRemoteKeyTargetEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRemoteKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AntelopeRemoteKeyLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AntelopeRemoteKeyLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun insertOrReplaceRemoteKey(remoteKey: AntelopeRemoteKey) = withContext(ioDispatcher) {
        dbQuery.insertAntelopeRemoteKey(
            query = remoteKey.query,
            blockchain_uid = remoteKey.blockchain_uid,
            last_requested_key = remoteKey.last_requested_key,
            target_cache_entity = remoteKey.target_cache_entity,
            last_updated_at = remoteKey.last_updated_at,
            end_of_pagination_reached = remoteKey.end_of_pagination_reached
        )
    }

    override suspend fun getRemoteKeyByQuery(
        query: String,
        blockchainUid: String,
        targetEntity: AntelopeRemoteKeyTargetEntity
    ): AntelopeRemoteKey? = withContext(ioDispatcher) {
        return@withContext dbQuery.getAntelopeRemoteKey(
            query = query,
            blockchain_uid = blockchainUid,
            target_cache_entity = targetEntity
        ).executeAsOneOrNull()
    }

    override suspend fun deleteRemoteKeyByQuery(
        query: String,
        blockchainUid: String,
        targetEntity: AntelopeRemoteKeyTargetEntity
    ) = withContext(ioDispatcher) {
        dbQuery.deleteAntelopeRemoteKey(
            query = query,
            blockchain_uid = blockchainUid,
            target_cache_entity = targetEntity
        )
    }

    override suspend fun getLastUpdateTimeStamp(
        query: String,
        blockchainUid: String,
        targetEntity: AntelopeRemoteKeyTargetEntity
    ): Long? = withContext(ioDispatcher) {
        return@withContext dbQuery.getLastUpdateTimeStamp(
            query = query,
            blockchain_uid = blockchainUid,
            target_cache_entity = targetEntity
        ).executeAsOneOrNull()
    }
    
    override suspend fun clearAllRemoteKeys() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeRemoteKeys()
    }
}