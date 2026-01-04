package com.mangala.wallet.domain.transaction.history.repository

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.wallet.domain.provider.covalenthq.mapper.entityToTransaction
import com.mangala.wallet.domain.provider.covalenthq.mapper.toTransactionEntity
import com.mangala.wallet.domain.provider.covalenthq.repository.CovalenthqTransactionsRemoteMediator
import com.mangala.wallet.domain.provider.eosEVM.repository.EosEvmTransactionsRemoteMediator
import com.mangala.wallet.domain.provider.moralis.repository.MoralisTransactionsRemoteMediator
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.local.transaction.history.TransactionLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import com.mangala.wallet.remote.provider.covalenthq.CovalenthqRemoteDataSource
import com.mangala.wallet.remote.provider.eosEVM.EosEvmRemoteDataSource
import com.mangala.wallet.remote.provider.moralis.MoralisRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class TransactionRepositoryImpl(
    private val transactionLocalDataSource: TransactionLocalDataSource,
    private val getBlockExplorerRemoteDataSource: (BlockchainType) -> BaseBlockExplorerRemoteDataSource,
    private val localTransactionMetadataDataSource: TransactionMetadataLocalDataSource,
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource
) : TransactionRepository {

    override suspend fun getPendingTransactions(blockchainUid: String): List<Transaction> {
        return transactionLocalDataSource.getPendingTransactions(blockchainUid)
            .map { it.entityToTransaction() }
    }

    override suspend fun getTransactionByTxHash(
        accountId: String,
        blockchainUid: String,
        txHash: String
    ): Transaction {
        return transactionLocalDataSource.getTransactionByTxHash(accountId, blockchainUid, txHash)
            .entityToTransaction()
    }

    override suspend fun saveTransaction(
        accountId: String,
        blockchainUid: String,
        transaction: Transaction
    ) {
        transactionLocalDataSource.insertTransaction(
            transaction.toTransactionEntity(
                accountId = accountId,
                blockchainUid = blockchainUid
            )
        )
    }

    override suspend fun updateTransactionStatus(
        accountId: String,
        blockchainUid: String,
        txHashes: List<String>,
        status: TransactionStatus
    ) {
        transactionLocalDataSource.updateTransactionStatus(
            accountId,
            blockchainUid,
            txHashes,
            status.name
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedTransactionsForAddress(
        accountId: String,
        blockchainType: BlockchainType,
        walletAddress: String,
        transactionTypeFilter: TransactionType?,
        transactionStatusFilter: TransactionStatus?,
        startDateFilter: Instant?,
        endDateFilter: Instant?
    ): Flow<PagingData<Transaction>> {
        val remoteDataSource = getBlockExplorerRemoteDataSource(blockchainType)
        return Pager(
            // Page size from PagingConfig is currently ignored. For Covalent the page size is fixed to 100, for EOS EVM it's configured in RemoteMediator
            config = PagingConfig(pageSize = 20),
            remoteMediator = when (remoteDataSource) {
                is EosEvmRemoteDataSource -> EosEvmTransactionsRemoteMediator(
                    accountId = accountId,
                    blockchainType = blockchainType,
                    walletAddress = walletAddress,
                    remoteDataSource = remoteDataSource,
                    localDataSource = transactionLocalDataSource,
                    localTransactionMetadataDataSource = localTransactionMetadataDataSource,
                    remoteKeyDataSource = remoteKeyLocalDataSource
                )

                is CovalenthqRemoteDataSource -> CovalenthqTransactionsRemoteMediator(
                    accountId = accountId,
                    blockchainType = blockchainType,
                    walletAddress = walletAddress,
                    remoteDataSource = remoteDataSource,
                    localDataSource = transactionLocalDataSource,
                    localTransactionMetadataDataSource = localTransactionMetadataDataSource,
                    remoteKeyDataSource = remoteKeyLocalDataSource
                )

                is MoralisRemoteDataSource -> MoralisTransactionsRemoteMediator(
                    json = Json,
                    accountId = accountId,
                    blockchainType = blockchainType,
                    walletAddress = walletAddress,
                    remoteDataSource = remoteDataSource,
                    localDataSource = transactionLocalDataSource,
                    localTransactionMetadataDataSource = localTransactionMetadataDataSource,
                    remoteKeyDataSource = remoteKeyLocalDataSource
                )

                else -> throw IllegalArgumentException("Remote data source not supported")
            },
        ) {
            transactionLocalDataSource.getTransactionPagingSource(
                accountId,
                blockchainType.uid,
                transactionTypeFilter?.name,
                transactionStatusFilter?.name,
                startDateFilter?.toEpochMilliseconds(),
                endDateFilter?.toEpochMilliseconds()
            )
        }.flow.map { pagingData ->
            pagingData.map { entity ->
                entity.entityToTransaction()
            }
        }
    }

    override suspend fun clearAllUserTransactions(): Result<Unit> {
        return try {
            transactionLocalDataSource.clearAllTransactions()
            remoteKeyLocalDataSource.clearAllRemoteKeys()
            localTransactionMetadataDataSource.clearAllTransactionMetadata()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}