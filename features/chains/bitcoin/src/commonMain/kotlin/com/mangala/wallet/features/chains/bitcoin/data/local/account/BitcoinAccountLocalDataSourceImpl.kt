package com.mangala.wallet.features.chains.bitcoin.data.local.account

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mangala.wallet.features.chains.bitcoin.BitcoinAccountEntity
import com.mangala.wallet.features.chains.bitcoin.database.BitcoinDatabase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BitcoinAccountLocalDataSourceImpl(
    private val database: BitcoinDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BitcoinAccountLocalDataSource {

    private val queries = database.bitcoinAccountEntityQueries

    override suspend fun insertBitcoinAccount(
        bitcoinAccountEntity: BitcoinAccountEntity
    ) = withContext(ioDispatcher) {
        with(bitcoinAccountEntity) {
            queries.insertBitcoinAccount(
                account_id = account_id,
                blockchain_uid = blockchain_uid,
                bip_44_address = bip_44_address,
                bip_49_address = bip_49_address,
                bip_84_address = bip_84_address
            )
        }
    }

    override fun getBitcoinAccounts(blockchainType: BlockchainType, accountIds: List<String>): Flow<List<BitcoinAccountEntity>> {
        return queries
            .getBitcoinAccountsByIds(accountIds, blockchainType.uid)
            .asFlow()
            .mapToList(ioDispatcher)
    }

    override suspend fun getBitcoinAccount(blockchainType: BlockchainType, accountId: String): BitcoinAccountEntity? =
        withContext(ioDispatcher) {
            queries
                .getBitcoinAccountById(accountId, blockchainType.uid)
                .executeAsOneOrNull()
        }
    
    override suspend fun getActiveAccountId(): String? = withContext(ioDispatcher) {
        queries.getAllBitcoinAccounts()
            .executeAsList()
            .firstOrNull()
            ?.account_id
    }
    
    override suspend fun clearAllBitcoinAccounts() = withContext(ioDispatcher) {
        queries.clearAllBitcoinAccounts()
    }
}
