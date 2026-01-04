package com.mangala.wallet.features.chains.bitcoin.data.local.transaction

import app.cash.paging.PagingSource
import com.mangala.wallet.features.chains.bitcoin.BitcoinTransactionEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

interface BitcoinTransactionLocalDataSource {
    fun getTransactionById(txId: String, blockchainType: BlockchainType): Flow<BitcoinTransactionEntity?>
    
    suspend fun upsertTransaction(entity: BitcoinTransactionEntity)
    
    suspend fun deleteTransaction(txId: String, blockchainType: BlockchainType)
    
    fun getTransactionsByAddress(address: String, blockchainType: BlockchainType): Flow<List<BitcoinTransactionEntity>>
    
    fun getTransactionsByAddressPaged(address: String, blockchainType: BlockchainType): PagingSource<Int, BitcoinTransactionEntity>
    
    suspend fun deleteAllTransactionsByAddress(address: String, blockchainType: BlockchainType)
    
    suspend fun deleteOldTransactions(olderThan: Long)
    
    suspend fun clearAllTransactions()
}