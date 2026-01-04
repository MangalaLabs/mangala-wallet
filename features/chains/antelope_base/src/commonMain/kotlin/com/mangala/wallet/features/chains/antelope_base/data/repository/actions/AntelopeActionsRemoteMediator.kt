package com.mangala.wallet.features.chains.antelope_base.data.repository.actions

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.wallet.domain.provider.const.Const
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.AntelopeActionsLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.mapper.toAntelopeActionsEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionsEntity
import com.mangala.wallet.local.cache.MetadataTargetCacheEntity
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import commangalawalletdatabase.RemoteKeyEntity
import commangalawalletdatabase.TransactionMetadataEntity
import kotlinx.coroutines.coroutineScope

@OptIn(ExperimentalPagingApi::class)
class AntelopeActionsRemoteMediator(
    private val accountName: String,
    private val blockchainType: BlockchainType,
    private val remoteDataSource: EosRemoteDataSource,
    private val localDataSource: AntelopeActionsLocalDataSource,
    private val localTransactionMetadataDataSource: TransactionMetadataLocalDataSource,
    private val remoteKeyDataSource: RemoteKeyLocalDataSource,
    private val limit: Int,
    private val filter: String?,
    private val sort: String,
    private val transferTo: String?,
    private val transferFrom: String?,
    private val after: String?,
    private val before: String?
) : RemoteMediator<Int, AntelopeActionsEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AntelopeActionsEntity>
    ): RemoteMediatorMediatorResult {
        val blockchainUid = blockchainType.uid
        return try {
            coroutineScope {
                val key = when (loadType) {
                    LoadType.REFRESH -> null
                    LoadType.PREPEND ->
                        return@coroutineScope RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = true
                        )

                    LoadType.APPEND -> {
                        val nextPage =
                            remoteKeyDataSource.getRemoteKeyByQuery(accountName, blockchainUid)

                        if (nextPage == null) {
                            return@coroutineScope RemoteMediatorMediatorResultSuccess(
                                endOfPaginationReached = true
                            )
                        }

                        nextPage
                    }
                }

                if (loadType == LoadType.REFRESH) {
                    remoteKeyDataSource.deleteRemoteKeyByQuery(accountName, blockchainUid)
                }

                ///////////
                val actionsResponse = remoteDataSource.getActions(
                    blockchainType = blockchainType,
                    accountName = accountName,
                    filter = filter,
                    skip = key?.toInt() ?: 0,
                    limit = limit,
                    sort = sort,
                    transferTo = transferTo,
                    transferFrom = transferFrom,
                    after = after,
                    before = before
                )

                when {
                    actionsResponse is ApiResponse.Success -> {
                        if (loadType == LoadType.REFRESH) {
                            localTransactionMetadataDataSource.insertTransactionMetadata(
                                TransactionMetadataEntity(
                                    accountName,
                                    blockchainType.uid,
                                    currentTimeInMillis()
                                )
                            )
                        }

                        val nextKey = key?.toInt()?.plus(limit)

                        remoteKeyDataSource.insertOrReplaceKey(
                            RemoteKeyEntity(
                                query = accountName,
                                lastRequestedPage = nextKey?.toString() ?: "0",
                                blockchain_uid = blockchainUid
                            )
                        )

                        val data = actionsResponse.body.actions ?: emptyList()
                        val entityData = data.toAntelopeActionsEntity(
                                accountName = accountName,
                                blockchainUid = blockchainUid
                            )

                        localDataSource.insertActions(entityData)

                        RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = data.size < limit
                        )
                    }

                    else -> {
                        RemoteMediatorMediatorResultError(Exception("Network error in loading actions $actionsResponse"))
                    }
                }

            }
        } catch (e: Exception) {
            println("Error in loading antelope action: $e")
            RemoteMediatorMediatorResultError(e)
        }
    }

    override suspend fun initialize(): RemoteMediatorInitializeAction {
        val timeNow = currentTimeInMillis()
        val lastSynchedTimestamp = localTransactionMetadataDataSource.getLastUpdatedTimestamp(
            accountName,
            blockchainType.uid
        ).orZero()
        val shouldRefresh =
            timeNow - lastSynchedTimestamp > Const.TRANSACTION_CACHE_TIMEOUT_MILLIS

        return if (shouldRefresh) {
            RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}