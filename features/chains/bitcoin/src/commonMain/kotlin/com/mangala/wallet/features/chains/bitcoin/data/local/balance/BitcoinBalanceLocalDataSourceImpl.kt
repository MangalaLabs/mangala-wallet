package com.mangala.wallet.features.chains.bitcoin.data.local.balance

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mangala.wallet.features.chains.bitcoin.BitcoinBalanceEntity
import com.mangala.wallet.features.chains.bitcoin.database.BitcoinDatabase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BitcoinBalanceLocalDataSourceImpl(
    private val database: BitcoinDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BitcoinBalanceLocalDataSource {

    private val balanceQueries = database.bitcoinBalanceEntityQueries

    override suspend fun insertOrUpdateBalance(
        accountId: String,
        blockchainType: BlockchainType,
        confirmedBalance: Long,
        unconfirmedBalance: Long,
        lastUpdated: Long
    ) = withContext(ioDispatcher) {
        balanceQueries.insertOrUpdateBalance(
            account_id = accountId,
            blockchain_type = blockchainType.name,
            confirmed_balance = confirmedBalance,
            unconfirmed_balance = unconfirmedBalance,
            last_updated = lastUpdated
        )
    }

    override fun getBalance(
        accountId: String,
        blockchainType: BlockchainType
    ): Flow<BitcoinBalanceEntity?> {
        return balanceQueries.getBalanceByAccountAndBlockchain(
            accountId,
            blockchainType.name
        )
        .asFlow()
        .mapToOneOrNull(ioDispatcher)
    }

    override fun getAllBalances(): Flow<List<BitcoinBalanceEntity>> {
        return balanceQueries.getAllBalances()
            .asFlow()
            .map { it.executeAsList() }
    }

    override suspend fun deleteBalance(
        accountId: String,
        blockchainType: BlockchainType
    ) = withContext(ioDispatcher) {
        balanceQueries.deleteBalance(accountId, blockchainType.name)
    }

    override suspend fun deleteAllBalances() = withContext(ioDispatcher) {
        balanceQueries.deleteAllBalances()
    }
}