package com.mangala.wallet.local.coin

import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.blockchain.BlockchainEntity
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.mokoresources.MR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CoinLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoinLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun clearDatabase() = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteAllCoin()
        }
    }

    override suspend fun deleteCoinById(uid: String) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteCoinById(uid)
        }
    }

    override suspend fun getAllCoin(): List<Coin> = withContext(ioDispatcher) {
        return@withContext dbQuery.getAllCoin(::mapCoin).executeAsList()
    }

    override suspend fun getCoinById(uid: String): List<Coin> = withContext(ioDispatcher) {
        return@withContext dbQuery.getCoinById(uid,::mapCoin).executeAsList()
    }

    override suspend fun getCoinByCode(code: String): List<Coin> = withContext(ioDispatcher) {
        return@withContext dbQuery.getCoinByCode(code, ::mapCoin).executeAsList()
    }

    override suspend fun insertCoin(coins: List<Coin>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            coins.forEach { coin ->
                insertCoin(coin)
            }
        }
    }

    override suspend fun countCoin(): Long = withContext(ioDispatcher) {
        dbQuery.countCoin().executeAsOne().let { count ->
            return@let count
        }
    }

    private fun mapCoin(
        uid: String,
        name: String,
        code: String,
        marketCapRank: Long?,
        coinGeckoId: String?
    ): Coin {
        return Coin(
            uid,
            name,
            code,
            marketCapRank, coinGeckoId
        )
    }

    private fun insertCoin(coin: Coin) {
        dbQuery.insertCoin(
            coin.uid,
            coin.name,
            coin.code,
            coin.marketCapRank,
            coin.coinGeckoId,
        )
    }
}