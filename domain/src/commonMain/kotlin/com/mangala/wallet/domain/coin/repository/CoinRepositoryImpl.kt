package com.mangala.wallet.domain.coin.repository

import com.mangala.wallet.local.coin.CoinLocalDataSource
import com.mangala.wallet.model.coin.Coin

class CoinRepositoryImpl(private val coinLocalDataSource: CoinLocalDataSource): CoinRepository {

    override suspend fun clearDatabase() {
        coinLocalDataSource.clearDatabase()
    }

    override suspend fun deleteCoinById(uid: String) {
        coinLocalDataSource.deleteCoinById(uid)
    }

    override suspend fun getAllCoin(): List<Coin> {
        return coinLocalDataSource.getAllCoin()
    }

    override suspend fun getCoinById(uid: String): List<Coin> {
        return coinLocalDataSource.getCoinById(uid)
    }

    override suspend fun getCoinByCode(code: String): List<Coin> {
        return coinLocalDataSource.getCoinByCode(code)
    }

    override suspend fun insertCoin(coins: List<Coin>) {
        coinLocalDataSource.insertCoin(coins)
    }
}