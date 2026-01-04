package com.mangala.wallet.features.chains.bitcoin.domain.repository.transaction

import app.cash.paging.PagingData
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface BitcoinTransactionRepository {
    suspend fun sendTransaction(
        transactionHex: String,
        blockchainType: BlockchainType
    ): Result<String>

    suspend fun getTransactionLatestInfo(
        txId: String,
        blockchainType: BlockchainType
    ): Result<BitcoinTransaction>

    fun getTransaction(
        txId: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean = false
    ): Flow<Resource<BitcoinTransaction>>
    
    fun getTransactionHistoryByAddress(
        address: String,
        blockchainType: BlockchainType
    ): Flow<PagingData<BitcoinTransaction>>
    
    suspend fun clearTransactionCache(
        txId: String,
        blockchainType: BlockchainType
    )
    
    suspend fun clearOldTransactionCache(olderThanMillis: Long)
}