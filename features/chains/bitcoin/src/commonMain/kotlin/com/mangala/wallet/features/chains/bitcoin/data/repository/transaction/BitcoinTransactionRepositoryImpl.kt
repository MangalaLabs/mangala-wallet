package com.mangala.wallet.features.chains.bitcoin.data.repository.transaction

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.BitcoinTransactionLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.mapper.createEmptyBitcoinTransactionEntity
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.mapper.toDomain
import com.mangala.wallet.features.chains.bitcoin.data.local.transaction.mapper.toEntity
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.MempoolRemoteDataSource
import com.mangala.wallet.features.chains.bitcoin.data.remote.paging.BitcoinTransactionsRemoteMediator
import com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.mapper.toBitcoinTransaction
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.features.chains.bitcoin.domain.repository.transaction.BitcoinTransactionRepository
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class BitcoinTransactionRepositoryImpl(
    private val mempoolRemoteDataSource: MempoolRemoteDataSource,
    private val bitcoinTransactionLocalDataSource: BitcoinTransactionLocalDataSource,
    private val transactionMetadataLocalDataSource: TransactionMetadataLocalDataSource,
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource
) : BitcoinTransactionRepository {

    override suspend fun sendTransaction(
        transactionHex: String,
        blockchainType: BlockchainType
    ): Result<String> {
        val response = (mempoolRemoteDataSource.sendTransaction(
            blockchainType,
            transactionHex
        ) as? ApiResponse.Success)?.body

        if (response == null) {
            return Result.failure(
                Exception("Failed to send transaction")
            )
        }

        return Result.success(response)
    }

    override suspend fun getTransactionLatestInfo(
        txId: String,
        blockchainType: BlockchainType
    ): Result<BitcoinTransaction> {
        val response = mempoolRemoteDataSource.getTransaction(blockchainType, txId)

        return when (response) {
            is ApiResponse.Success -> Result.success(response.body.toBitcoinTransaction())
            is ApiResponse.Error -> Result.failure(Exception("Failed to get transaction"))
        }
    }

    override fun getTransaction(
        txId: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<BitcoinTransaction>> {
        return networkBoundResource(
            query = {
                bitcoinTransactionLocalDataSource.getTransactionById(txId, blockchainType).map { entity ->
                    // If entity is null, return an empty entity that will be replaced by the network call
                    entity ?: createEmptyBitcoinTransactionEntity(txId, blockchainType)
                }
            },
            fetch = { cachedEntity ->
                mempoolRemoteDataSource.getTransaction(blockchainType, txId)
            },
            saveFetchResult = { response ->
                val transaction = response.toBitcoinTransaction()
                val entity = transaction.toEntity(blockchainType)
                bitcoinTransactionLocalDataSource.upsertTransaction(entity)
            },
            shouldFetch = { cachedEntity ->
                val isCacheEmpty = cachedEntity.lastUpdated <= 0
                val isCacheExpired = Clock.System.now().toEpochMilliseconds() - cachedEntity.lastUpdated > CACHE_TIMEOUT_MS
                forceRefresh || isCacheEmpty || isCacheExpired
            },
            entityToDomain = { entity -> entity.toDomain() }
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getTransactionHistoryByAddress(
        address: String,
        blockchainType: BlockchainType
    ): Flow<PagingData<BitcoinTransaction>> {
        val pagingConfig = PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = false
        )

        return Pager(
            config = pagingConfig,
            remoteMediator = BitcoinTransactionsRemoteMediator(
                blockchainType = blockchainType,
                walletAddress = address,
                mempoolRemoteDataSource = mempoolRemoteDataSource,
                bitcoinTransactionLocalDataSource = bitcoinTransactionLocalDataSource,
                transactionMetadataLocalDataSource = transactionMetadataLocalDataSource,
                remoteKeyDataSource = remoteKeyLocalDataSource
            ),
            pagingSourceFactory = {
                bitcoinTransactionLocalDataSource.getTransactionsByAddressPaged(address, blockchainType)
            }
        )
        .flow.map { it.map { it.toDomain() } }
    }

    override suspend fun clearTransactionCache(txId: String, blockchainType: BlockchainType) {
        bitcoinTransactionLocalDataSource.deleteTransaction(txId, blockchainType)
    }

    override suspend fun clearOldTransactionCache(olderThanMillis: Long) {
        bitcoinTransactionLocalDataSource.deleteOldTransactions(olderThanMillis)
    }

    companion object {
        private const val CACHE_TIMEOUT_MS = 30 * 60 * 1000L // Cache timeout: 30 minutes
        private const val PAGE_SIZE = 25 // Number of transactions per page
    }
}