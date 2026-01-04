package com.mangala.wallet.domain.coin.repository

import com.mangala.wallet.model.coin.Coin

interface CoinRepository {

    suspend fun clearDatabase()

    suspend fun deleteCoinById(uid: String)

    suspend fun getAllCoin(): List<Coin>

    suspend fun getCoinById(uid: String): List<Coin>

    suspend fun getCoinByCode(code: String): List<Coin>

    suspend fun insertCoin(coins: List<Coin>)

}