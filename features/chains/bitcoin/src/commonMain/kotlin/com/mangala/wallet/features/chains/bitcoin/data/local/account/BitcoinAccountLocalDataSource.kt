package com.mangala.wallet.features.chains.bitcoin.data.local.account

import com.mangala.wallet.features.chains.bitcoin.BitcoinAccountEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

interface BitcoinAccountLocalDataSource {
    suspend fun insertBitcoinAccount(
        bitcoinAccountEntity: BitcoinAccountEntity
    )

    fun getBitcoinAccounts(
        blockchainType: BlockchainType,
        accountIds: List<String>
    ): Flow<List<BitcoinAccountEntity>>

    suspend fun getBitcoinAccount(
        blockchainType: BlockchainType,
        accountId: String
    ): BitcoinAccountEntity?
    
    suspend fun getActiveAccountId(): String?
    
    suspend fun clearAllBitcoinAccounts()
}