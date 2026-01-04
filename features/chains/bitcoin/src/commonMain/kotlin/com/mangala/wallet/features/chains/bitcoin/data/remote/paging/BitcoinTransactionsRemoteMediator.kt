package com.mangala.wallet.features.chains.bitcoin.data.remote.paging

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.wallet.domain.provider.const.Const
import com.mangala.wallet.features.chains.bitcoin.BitcoinTransactionEntity
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.BitcoinTransactionLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.MempoolRemoteDataSource
import com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.mapper.toTransactionEntity
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import commangalawalletdatabase.RemoteKeyEntity
import commangalawalletdatabase.TransactionMetadataEntity

@OptIn(ExperimentalPagingApi::class)
class BitcoinTransactionsRemoteMediator(
    private val blockchainType: BlockchainType,
    private val walletAddress: String,
    private val mempoolRemoteDataSource: MempoolRemoteDataSource,
    private val bitcoinTransactionLocalDataSource: BitcoinTransactionLocalDataSource,
    private val transactionMetadataLocalDataSource: TransactionMetadataLocalDataSource,
    private val remoteKeyDataSource: RemoteKeyLocalDataSource
) : RemoteMediator<Int, BitcoinTransactionEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BitcoinTransactionEntity>
    ): RemoteMediatorMediatorResult {
        val blockchainUid = blockchainType.uid
        
        return try {
            val lastTxId = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> {
                    // Bitcoin transactions are returned newest first, no need to prepend
                    return RemoteMediatorMediatorResultSuccess(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDataSource.getRemoteKeyByQuery(walletAddress, blockchainUid)
                    
                    if (remoteKey.isNullOrEmpty()) {
                        return RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = true
                        )
                    }
                    
                    remoteKey
                }
            }
            
            val response = if (lastTxId == null) {
                mempoolRemoteDataSource.getTransactionsByAddress(
                    blockchainType = blockchainType,
                    address = walletAddress
                )
            } else {
                mempoolRemoteDataSource.getTransactionsByAddressAfterTxid(
                    blockchainType = blockchainType,
                    address = walletAddress,
                    afterTxid = lastTxId
                )
            }
            
            if (loadType == LoadType.REFRESH) {
                remoteKeyDataSource.deleteRemoteKeyByQuery(walletAddress, blockchainUid)
            }
            
            if (response is ApiResponse.Success) {
                if (loadType == LoadType.REFRESH) {
                    transactionMetadataLocalDataSource.insertTransactionMetadata(
                        TransactionMetadataEntity(
                            walletAddress,
                            blockchainType.uid,
                            currentTimeInMillis()
                        )
                    )
                }
                
                val transactions = response.body
                
                val lastTransactionId = transactions.lastOrNull()?.txid
                
                remoteKeyDataSource.insertOrReplaceKey(
                    RemoteKeyEntity(
                        query = walletAddress,
                        lastRequestedPage = lastTransactionId ?: "",
                        blockchain_uid = blockchainUid
                    )
                )
                
                transactions.forEach { txResponse ->
                    txResponse.toTransactionEntity(blockchainType)?.let { entity ->
                        bitcoinTransactionLocalDataSource.upsertTransaction(entity)
                    }
                }
                
                // For Mempool API, we've reached the end if we got fewer than the expected number of transactions
                val endOfPaginationReached = transactions.size < state.config.pageSize || lastTransactionId == null
                
                RemoteMediatorMediatorResultSuccess(
                    endOfPaginationReached = endOfPaginationReached
                )
            } else {
                RemoteMediatorMediatorResultError(Exception("Network error in loading Bitcoin transactions: $response"))
            }
        } catch (e: Exception) {
            RemoteMediatorMediatorResultError(e)
        }
    }
    
    override suspend fun initialize(): RemoteMediatorInitializeAction {
        val timeNow = currentTimeInMillis()
        val lastSynchedTimestamp = transactionMetadataLocalDataSource.getLastUpdatedTimestamp(
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