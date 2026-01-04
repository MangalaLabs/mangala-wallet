package com.mangala.wallet.domain.provider.moralis.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.wallet.domain.provider.const.Const
import com.mangala.wallet.domain.provider.moralis.mapper.toTransactionEntity
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.local.transaction.history.TransactionLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.provider.moralis.MoralisRemoteDataSource
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import commangalawalletdatabase.RemoteKeyEntity
import commangalawalletdatabase.TransactionMetadataEntity
import commangalawalletdatabase.TransactionsEntity
import kotlinx.serialization.json.Json

@OptIn(ExperimentalPagingApi::class)
class MoralisTransactionsRemoteMediator(
    private val json: Json,
    private val accountId: String,
    private val blockchainType: BlockchainType,
    private val walletAddress: String,
    private val remoteDataSource: MoralisRemoteDataSource,
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
                    val nextPage =
                        remoteKeyDataSource.getRemoteKeyByQuery(walletAddress, blockchainUid)
                            ?: return RemoteMediatorMediatorResultSuccess(
                                endOfPaginationReached = true
                            )

                    nextPage
                }
            }

            val response = if (key == null) {
                remoteDataSource.getWalletHistory(
                    chainName = blockchainType,
                    walletAddress = walletAddress,
                    page = 0,
                    cursor = null
                )
            } else {
                remoteDataSource.getWalletHistory(
                    chainName = blockchainType,
                    walletAddress = walletAddress,
                    page = null,
                    cursor = "" // TODO: Fill in from the cache
                )
            }

            if (loadType == LoadType.REFRESH) {
                remoteKeyDataSource.deleteRemoteKeyByQuery(walletAddress, blockchainUid)
            }

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

                val nextCursor = response.body.cursor
                val data = response.body.result

                remoteKeyDataSource.insertOrReplaceKey(
                    RemoteKeyEntity(
                        query = walletAddress,
                        lastRequestedPage = nextCursor.orEmpty(),
                        blockchain_uid = blockchainUid
                    )
                )

                val entityData = data.map {
                    it.toTransactionEntity(
                        json = json,
                        accountId = accountId,
                        blockchainUid = blockchainUid
                    )
                }

                localDataSource.insertTransactions(entityData)

                RemoteMediatorMediatorResultSuccess(
                    endOfPaginationReached = nextCursor == null
                )
            } else {
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