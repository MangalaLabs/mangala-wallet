package com.mangala.wallet.domain.provider.eosEVM.repository

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.wallet.domain.provider.const.Const
import com.mangala.wallet.domain.provider.eosEVM.mapper.toTransactionEntity
import com.mangala.wallet.local.cache.MetadataTargetCacheEntity
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.local.transaction.history.TransactionLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.provider.eosEVM.EosEvmRemoteDataSource
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.secondsTimestampToMillisecondTimestamp
import commangalawalletdatabase.RemoteKeyEntity
import commangalawalletdatabase.TransactionMetadataEntity
import commangalawalletdatabase.TransactionsEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.toInstant

@OptIn(ExperimentalPagingApi::class)
class EosEvmTransactionsRemoteMediator(
    private val accountId: String,
    private val blockchainType: BlockchainType,
    private val walletAddress: String,
    private val remoteDataSource: EosEvmRemoteDataSource,
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
            coroutineScope {
                val key = when (loadType) {
                    LoadType.REFRESH -> null
                    LoadType.PREPEND ->
                        return@coroutineScope RemoteMediatorMediatorResultSuccess(endOfPaginationReached = true)
                    LoadType.APPEND -> {
                        val nextPage = remoteKeyDataSource.getRemoteKeyByQuery(walletAddress, blockchainUid)

                        if (nextPage == null) {
                            return@coroutineScope RemoteMediatorMediatorResultSuccess(
                                endOfPaginationReached = true
                            )
                        }

                        nextPage
                    }
                }

                val asyncResponseTransactionList = async {
                    remoteDataSource.getPaginatedTransactionsForAddress(
                        chainName = blockchainType,
                        walletAddress = walletAddress,
                        page = key?.toInt() ?: 1
                    )
                }

                val asyncResponseTokenTransfer = async {
                    remoteDataSource.getPaginatedTokenTransferForAddress(
                        walletAddress = walletAddress,
                        page = key?.toInt() ?: 1
                    )
                }


                if (loadType == LoadType.REFRESH) {
                    remoteKeyDataSource.deleteRemoteKeyByQuery(walletAddress, blockchainUid)
                }

                ///////////
                val responseTransactionList = asyncResponseTransactionList.await()
                val responseTokenTransfer = asyncResponseTokenTransfer.await()
                when {
                    responseTransactionList is ApiResponse.Success -> {
                        if (loadType == LoadType.REFRESH) {
                            localTransactionMetadataDataSource.insertTransactionMetadata(
                                TransactionMetadataEntity(
                                    walletAddress,
                                    blockchainType.uid,
                                    currentTimeInMillis()
                                )
                            )
                        }

                        val nextKey = key?.toInt()?.plus(1)

                        remoteKeyDataSource.insertOrReplaceKey(
                            RemoteKeyEntity(
                                query = walletAddress,
                                lastRequestedPage = nextKey?.toString() ?: "0",
                                blockchain_uid = blockchainUid
                            )
                        )

                        val data = responseTransactionList.body.result?.mapNotNull { it }
                            ?.sortedByDescending { it.timeStamp?.toLongOrNull().secondsTimestampToMillisecondTimestamp() }
                            ?: emptyList()
                        val entityData = data.map {
                            it.toTransactionEntity(
                                accountId = accountId,
                                blockchainUid = blockchainUid,
                                address = walletAddress
                            )
                        }.toMutableList()

                        if (responseTokenTransfer is ApiResponse.Success) {
                            val tokenTransferData =
                                responseTokenTransfer.body.result?.mapNotNull { it }
                                    ?.sortedByDescending { it.timeStamp?.toLongOrNull().secondsTimestampToMillisecondTimestamp() }
                                    ?: emptyList()
                            val tokenTransferEntityData = tokenTransferData.map {
                                it.toTransactionEntity(
                                    accountId = accountId,
                                    blockchainUid = blockchainUid,
                                    address = walletAddress
                                )
                            }
                            entityData.addAll(tokenTransferEntityData)
                        }

                        localDataSource.insertTransactions(entityData)

                        RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached =
                            (responseTransactionList.body.result?.size ?: 0) < EosEvmRemoteDataSource.TRANSACTION_FETCH_PAGE_SIZE
                            && ((responseTokenTransfer as? ApiResponse.Success)?.body?.result?.size ?: 0) < EosEvmRemoteDataSource.TRANSACTION_FETCH_PAGE_SIZE
                        )
                    }

                    responseTokenTransfer is ApiResponse.Success -> {
                        if (loadType == LoadType.REFRESH) {
                            localTransactionMetadataDataSource.insertTransactionMetadata(
                                TransactionMetadataEntity(
                                    walletAddress,
                                    blockchainType.uid,
                                    currentTimeInMillis()
                                )
                            )
                        }

                        val nextKey = key?.toInt()?.plus(1)

                        remoteKeyDataSource.insertOrReplaceKey(
                            RemoteKeyEntity(
                                query = walletAddress,
                                lastRequestedPage = nextKey?.toString() ?: "0",
                                blockchain_uid = blockchainUid
                            )
                        )

                        val tokenTransferData = responseTokenTransfer.body.result?.mapNotNull { it }
                            ?.sortedByDescending { it.timeStamp?.toInstant()?.epochSeconds }
                            ?: emptyList()
                        val tokenTransferEntityData = tokenTransferData.map {
                            it.toTransactionEntity(
                                accountId = accountId,
                                blockchainUid = blockchainUid,
                                address = walletAddress
                            )
                        }

                        localDataSource.insertTransactions(tokenTransferEntityData)

                        RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = (responseTokenTransfer.body.result?.size ?: 0) < EosEvmRemoteDataSource.TRANSACTION_FETCH_PAGE_SIZE
                        )
                    }
                    else -> {
                    // TODO: Maybe
                    RemoteMediatorMediatorResultError(Exception("Network error in loading transactions $responseTransactionList and $responseTokenTransfer"))
                    }
                }

            }
        } catch (e: Exception) {
            println("Error in loading transactions: $e")
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