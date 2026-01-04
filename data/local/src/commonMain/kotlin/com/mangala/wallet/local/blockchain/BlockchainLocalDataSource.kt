package com.mangala.wallet.local.blockchain

import com.mangala.wallet.model.blockchain.BlockchainEntity
import com.mangala.wallet.model.person.local.PersonEntity

interface BlockchainLocalDataSource {

    suspend fun clearDatabase()

    suspend fun getAllBlockchain(): List<BlockchainEntity>

    suspend fun getBlockchainById(uid: String): List<BlockchainEntity>

    suspend fun insertBlockchain(blockchain: List<BlockchainEntity>)

    suspend fun countBlockchain(): Long

}