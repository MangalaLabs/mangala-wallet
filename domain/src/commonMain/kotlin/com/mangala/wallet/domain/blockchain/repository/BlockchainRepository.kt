package com.mangala.wallet.domain.blockchain.repository

import com.mangala.wallet.model.blockchain.BlockchainEntity

interface BlockchainRepository {

    suspend fun clearDatabase()

    suspend fun getAllBlockchain(): List<BlockchainEntity>

    suspend fun getBlockchainById(uid: String): List<BlockchainEntity>

    suspend fun insertBlockchain(blockchain: List<BlockchainEntity>)

    suspend fun countBlockchain(): Long
}