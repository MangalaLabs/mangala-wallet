package com.mangala.wallet.features.chains.bitcoin.data.local.balance

import com.mangala.wallet.features.chains.bitcoin.BitcoinBalanceEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

interface BitcoinBalanceLocalDataSource {
    /**
     * Insert or update a balance record for a specific account and blockchain
     */
    suspend fun insertOrUpdateBalance(
        accountId: String,
        blockchainType: BlockchainType,
        confirmedBalance: Long,
        unconfirmedBalance: Long,
        lastUpdated: Long
    )

    /**
     * Get the balance for a specific account and blockchain
     */
    fun getBalance(
        accountId: String,
        blockchainType: BlockchainType
    ): Flow<BitcoinBalanceEntity?>

    /**
     * Get all stored balances
     */
    fun getAllBalances(): Flow<List<BitcoinBalanceEntity>>

    suspend fun deleteBalance(
        accountId: String,
        blockchainType: BlockchainType
    )

    suspend fun deleteAllBalances()
}