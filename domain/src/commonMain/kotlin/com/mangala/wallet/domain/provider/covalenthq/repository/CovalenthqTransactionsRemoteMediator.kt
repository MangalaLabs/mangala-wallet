package com.mangala.wallet.domain.provider.covalenthq.repository

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.wallet.domain.provider.const.Const
import com.mangala.wallet.domain.provider.covalenthq.mapper.toTransactionEntity
import com.mangala.wallet.local.cache.MetadataTargetCacheEntity
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.local.transaction.history.TransactionLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.provider.covalenthq.CovalenthqRemoteDataSource
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import commangalawalletdatabase.RemoteKeyEntity
import commangalawalletdatabase.TransactionMetadataEntity
import commangalawalletdatabase.TransactionsEntity
import kotlinx.datetime.toInstant

@OptIn(ExperimentalPagingApi::class)
class CovalenthqTransactionsRemoteMediator(
    private val accountId: String,
    private val blockchainType: BlockchainType,
    private val walletAddress: String,
    private val remoteDataSource: CovalenthqRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val localTransactionMetadataDataSource: TransactionMetadataLocalDataSource,
    private val remoteKeyDataSource: RemoteKeyLocalDataSource
): RemoteMediator<Int, TransactionsEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TransactionsEntity>
    ): RemoteMediatorMediatorResult {
        val blockchainUid = blockchainType.uid
        return try {

            val key = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND ->
                    return RemoteMediatorMediatorResultSuccess(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val nextPage = remoteKeyDataSource.getRemoteKeyByQuery(walletAddress, blockchainUid)

                    if (nextPage == null) {
                        return RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = true
                        )
                    }

                    nextPage
                }
            }

            val response = if (key == null) {
                remoteDataSource.getLatestTransactionsForAddress(
                    chainName = blockchainType,
                    walletAddress = walletAddress,
                )
            } else {
                remoteDataSource.getPaginatedTransactionsForAddress(
                    chainName = blockchainType,
                    walletAddress = walletAddress,
                    page = key.toInt() ?: 0
                )
            }

            if (loadType == LoadType.REFRESH) {
                remoteKeyDataSource.deleteRemoteKeyByQuery(walletAddress, blockchainUid)
            }

            ///////////
            if (response is ApiResponse.Success) {
                if (loadType == LoadType.REFRESH) {
                    localTransactionMetadataDataSource.insertTransactionMetadata(
                        TransactionMetadataEntity(
                            walletAddress,
                            blockchainType.uid,
                            currentTimeInMillis()
                        )
                    )
                }

                val prev = response.body.data?.links?.prev
                val nextKey = prev?.let {
                    try {
                        prev[prev.lastIndex - 1] - '0'
                    } catch (e: IndexOutOfBoundsException) {
                        null
                    }
                } // Since the API returns the data in reverse order (latest page has highest index), we need to swap the keys

                remoteKeyDataSource.insertOrReplaceKey(RemoteKeyEntity(
                    query = walletAddress,
                    lastRequestedPage = nextKey?.toString() ?: "0",
                    blockchain_uid = blockchainUid
                ))

                val data = response.body.data?.items?.mapNotNull { it }?.sortedByDescending { it.blockSignedAt?.toInstant()?.epochSeconds } ?: emptyList()
                val entityData = data.map { it.toTransactionEntity(
                    address = walletAddress,
                    accountId = accountId,
                    blockchainUid = blockchainUid
                ) }

                localDataSource.insertTransactions(entityData)

                RemoteMediatorMediatorResultSuccess(
                    endOfPaginationReached = nextKey == null
                )
            } else {
                // TODO: Maybe
                RemoteMediatorMediatorResultError(Exception("Network error in loading transactions $response"))
            }
        } catch (e: Exception) {
            RemoteMediatorMediatorResultError(e)
        }
    }

    override suspend fun initialize(): RemoteMediatorInitializeAction {
        val timeNow = currentTimeInMillis()
        val lastSynchedTimestamp = localTransactionMetadataDataSource.getLastUpdatedTimestamp(
            walletAddress,
            blockchainType.uid
        ).orZero()
        val shouldRefresh = timeNow - lastSynchedTimestamp > Const.TRANSACTION_CACHE_TIMEOUT_MILLIS

        return if (shouldRefresh) {
            RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
        }
    }

}