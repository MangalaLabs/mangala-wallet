package com.mangala.wallet.features.chains.bitcoin.domain.repository.account

import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

interface BitcoinAccountRepository {
    suspend fun saveAccount(
        blockchainType: BlockchainType,
        accountId: String,
        bip44Address: String,
        bip49Address: String,
        bip84Address: String
    )

    fun getAccounts(blockchainType: BlockchainType, accountIds: List<String>): Flow<List<BitcoinAccount>>
    suspend fun getAccount(blockchainType: BlockchainType, accountId: String): BitcoinAccount?
    
    suspend fun getActiveAccount(blockchainType: BlockchainType): BitcoinAccount?
}