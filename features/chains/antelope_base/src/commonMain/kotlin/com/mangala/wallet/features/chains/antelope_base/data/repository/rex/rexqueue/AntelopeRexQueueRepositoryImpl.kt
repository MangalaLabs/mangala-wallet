package com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexqueue

import com.mangala.antelope.base.api.model.BaseGetTableRowsRequest
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RexQueueRowResponse
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.features.chains.antelope_base.data.local.cache.AntelopeRemoteKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexqueue.AntelopeRexQueueLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.cache.AntelopeRemoteKeyTargetEntity
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexQueueInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexQueueRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeRemoteKey
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexQueueEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.currentTimeInMillis
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

internal class AntelopeRexQueueRepositoryImpl(
    private val antelopeRemoteDataSource: AntelopeRemoteDataSource,
    private val antelopeRexQueueLocalDataSource: AntelopeRexQueueLocalDataSource,
    private val remoteKeyDataSource: AntelopeRemoteKeyLocalDataSource
) : AntelopeRexQueueRepository {

    override suspend fun getTableRowsRexQueue(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<AntelopeRexQueueInfo> {
        return cachedResource(
            query = { antelopeRexQueueLocalDataSource.getRexQueue(accountName, blockchainType.uid) },
            fetch = {
                fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                saveFetchResult(accountName, blockchainType, it)
            },
            shouldFetch = {
                cachedResponse -> shouldFetch(accountName, blockchainType, cachedResponse, forceRefresh)
            },
            entityToDomain = { it.toAntelopeRexQueueInfo() }
        )
    }

    override fun getTableRowsRexQueueFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeRexQueueInfo>> = networkBoundResource(
        query = {
            antelopeRexQueueLocalDataSource.getRexQueueFlow(accountName, blockchainType.uid)
        },
        fetch = {
            fetch(blockchainType, accountName)
        },
        saveFetchResult = {
            saveFetchResult(accountName, blockchainType, it)
        },
        shouldFetch = {
            cachedResponse -> shouldFetch(accountName, blockchainType, cachedResponse, forceRefresh)
        },
        entityToDomain = { it.toAntelopeRexQueueInfo() }
    )

    private suspend fun fetch(
        blockchainType: BlockchainType,
        accountName: String
    ) = antelopeRemoteDataSource.getTableRowsRexQueue(
        blockchainType,
        BaseGetTableRowsRequest(
            code = SystemContracts.EOS_SYSTEM_CONTRACT,
            scope = SystemContracts.EOS_SYSTEM_CONTRACT,
            table = "rexqueue",
            lowerBound = accountName,
            upperBound = accountName,
            limit = 100,
            json = true,
            keyType = "",
            indexPosition = 1,
            reverse = false,
            showPayer = false
        )
    )

    private suspend fun saveFetchResult(
        accountName: String,
        blockchainType: BlockchainType,
        it: BaseGetTableRowsResponse<RexQueueRowResponse>
    ) {
        antelopeRexQueueLocalDataSource.deleteRexQueue(accountName, blockchainType.uid)
        antelopeRexQueueLocalDataSource.insertRexQueue(
            it.toAntelopeRexQueueEntity(
                accountName,
                blockchainType.uid
            )
        )
        remoteKeyDataSource.insertOrReplaceRemoteKey(
            remoteKey = AntelopeRemoteKey(
                query = accountName,
                blockchain_uid = blockchainType.uid,
                last_requested_key = null,
                target_cache_entity = AntelopeRemoteKeyTargetEntity.REX_QUEUE,
                last_updated_at = currentTimeInMillis(),
                end_of_pagination_reached = 1
            )
        )
    }

    private suspend fun shouldFetch(
        accountName: String,
        blockchainType: BlockchainType,
        cachedResponse: List<AntelopeRexQueueEntity>,
        forceRefresh: Boolean,
    ): Boolean {
        if (forceRefresh) return true

        val lastFetchedTimestamp = remoteKeyDataSource.getRemoteKeyByQuery(accountName, blockchainType.uid, AntelopeRemoteKeyTargetEntity.REX_QUEUE)?.last_updated_at

        return isCacheExpired(lastFetchedTimestamp, cachedResponse)
    }

    private fun isCacheExpired(
        lastFetchedTimestamp: Long?,
        cachedResponse: List<AntelopeRexQueueEntity>
    ): Boolean {
        if (lastFetchedTimestamp == null) return true

        if (cachedResponse.isEmpty()) return lastFetchedTimestamp + CACHE_EXPIRATION_TIME < Clock.System.now().toEpochMilliseconds()

        return cachedResponse.minOf { it.last_updated } + CACHE_EXPIRATION_TIME < Clock.System.now().toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}