package com.mangala.wallet.domain.blockchain.repository

import com.mangala.wallet.local.blockchain.BlockchainLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainEntity

class BlockchainRepositoryImpl(private val blockchainLocalDataSource: BlockchainLocalDataSource): BlockchainRepository {
    override suspend fun clearDatabase() {
        blockchainLocalDataSource.clearDatabase()
    }

    override suspend fun getAllBlockchain(): List<BlockchainEntity> {
        return blockchainLocalDataSource.getAllBlockchain()
    }

    override suspend fun getBlockchainById(uid: String): List<BlockchainEntity> {
        return blockchainLocalDataSource.getBlockchainById(uid)
    }

    override suspend fun insertBlockchain(blockchain: List<BlockchainEntity>) {
        blockchainLocalDataSource.insertBlockchain(blockchain)
    }

    override suspend fun countBlockchain(): Long {
        return blockchainLocalDataSource.countBlockchain()
    }
}