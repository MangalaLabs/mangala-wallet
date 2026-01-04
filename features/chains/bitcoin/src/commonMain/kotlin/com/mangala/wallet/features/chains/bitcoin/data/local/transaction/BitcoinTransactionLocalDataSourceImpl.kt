package com.mangala.wallet.features.chains.bitcoin.data.local.transaction

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.paging3.QueryPagingSource
import com.mangala.wallet.features.chains.bitcoin.BitcoinTransactionEntity
import com.mangala.wallet.features.chains.bitcoin.database.BitcoinDatabase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BitcoinTransactionLocalDataSourceImpl(
    private val database: BitcoinDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BitcoinTransactionLocalDataSource {

    private val bitcoinTransactionEntityQueries = database.bitcoinTransactionEntityQueries

    override fun getTransactionById(
        txId: String, 
        blockchainType: BlockchainType
    ): Flow<BitcoinTransactionEntity?> {
        return bitcoinTransactionEntityQueries
            .getTransactionById(txId, blockchainType.uid)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
    }

    override suspend fun upsertTransaction(entity: BitcoinTransactionEntity) {
        withContext(ioDispatcher) {
            bitcoinTransactionEntityQueries.upsertTransaction(
                txid = entity.txid,
                blockchainType = entity.blockchainType,
                hash = entity.hash,
                version = entity.version,
                size = entity.size,
                weight = entity.weight,
                locktime = entity.locktime,
                fee = entity.fee,
                confirmed = entity.confirmed,
                block_height = entity.block_height,
                block_hash = entity.block_hash,
                block_time = entity.block_time,
                vin = entity.vin,
                vout = entity.vout,
                lastUpdated = entity.lastUpdated
            )
        }
    }

    override suspend fun deleteTransaction(txId: String, blockchainType: BlockchainType) {
        withContext(ioDispatcher) {
            bitcoinTransactionEntityQueries.deleteTransaction(
                txid = txId,
                blockchainType = blockchainType.uid
            )
        }
    }

    override fun getTransactionsByAddress(
        address: String, 
        blockchainType: BlockchainType
    ): Flow<List<BitcoinTransactionEntity>> {
        return bitcoinTransactionEntityQueries
            .getTransactionsByAddress(blockchainType.uid, address, address)
            .asFlow()
            .mapToList(ioDispatcher)
    }
    
    override fun getTransactionsByAddressPaged(
        address: String,
        blockchainType: BlockchainType
    ): PagingSource<Int, BitcoinTransactionEntity> {
        return QueryPagingSource(
            countQuery = bitcoinTransactionEntityQueries.countTransactionsByAddress(
                blockchainType.uid,
                address,
                address
            ),
            transacter = database,
            context = ioDispatcher,
            queryProvider = { limit, offset ->
                bitcoinTransactionEntityQueries.getTransactionsByAddressPaged(
                    blockchainType.uid,
                    address,
                    address,
                    limit,
                    offset
                )
            }
        )
    }
    
    override suspend fun deleteAllTransactionsByAddress(address: String, blockchainType: BlockchainType) {
        withContext(ioDispatcher) {
            bitcoinTransactionEntityQueries.deleteAllTransactionsByAddress(
                blockchainType.uid,
                address,
                address
            )
        }
    }

    override suspend fun deleteOldTransactions(olderThan: Long) {
        withContext(ioDispatcher) {
            bitcoinTransactionEntityQueries.deleteOldTransactions(olderThan)
        }
    }
    
    override suspend fun clearAllTransactions() {
        withContext(ioDispatcher) {
            bitcoinTransactionEntityQueries.clearAllTransactions()
        }
    }
}