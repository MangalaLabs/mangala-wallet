package com.mangala.wallet.features.chains.antelope_base.data.local.cache

import com.mangala.wallet.features.chains.antelope_base.domain.model.cache.AntelopeRemoteKeyTargetEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRemoteKey

interface AntelopeRemoteKeyLocalDataSource {

    suspend fun insertOrReplaceRemoteKey(remoteKey: AntelopeRemoteKey)

    suspend fun getRemoteKeyByQuery(
        query: String,
        blockchainUid: String,
        targetEntity: AntelopeRemoteKeyTargetEntity,
    ): AntelopeRemoteKey?

    suspend fun deleteRemoteKeyByQuery(
        query: String,
        blockchainUid: String,
        targetEntity: AntelopeRemoteKeyTargetEntity,
    )

    suspend fun getLastUpdateTimeStamp(
        query: String,
        blockchainUid: String,
        targetEntity: AntelopeRemoteKeyTargetEntity,
    ): Long?
    
    suspend fun clearAllRemoteKeys()
}