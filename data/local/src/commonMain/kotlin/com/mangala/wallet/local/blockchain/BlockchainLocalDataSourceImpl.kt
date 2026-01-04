package com.mangala.wallet.local.blockchain

import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.blockchain.BlockchainEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class BlockchainLocalDataSourceImpl(
    val databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): BlockchainLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun clearDatabase() = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteAllBlockchain()
        }
    }

    override suspend fun getAllBlockchain(): List<BlockchainEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getAllBlockchainEntity(::mapBlockchain).executeAsList()
    }

    override suspend fun getBlockchainById(uid: String): List<BlockchainEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getBlockchainById(uid, ::mapBlockchain).executeAsList()
    }

    override suspend fun insertBlockchain(blockchains: List<BlockchainEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            blockchains.forEach { blockchain ->
                insertBlockchain(blockchain)
            }
        }
    }

    override suspend fun countBlockchain(): Long = withContext(ioDispatcher) {
        dbQuery.countBlockchain().executeAsOne().let { count ->
            return@let count
        }
    }

    private fun mapBlockchain(
        uid: String,
        name: String?,
        eip3091url: String?
    ): BlockchainEntity {
        return BlockchainEntity(
            uid,
            name ?: "",
            eip3091url
        )
    }

    private fun insertBlockchain(blockchainEntity: BlockchainEntity) {
        dbQuery.insertBlockchain(
            blockchainEntity.uid,
            blockchainEntity.name,
            blockchainEntity.eip3091url,
        )
    }
}