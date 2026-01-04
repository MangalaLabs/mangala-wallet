package com.mangala.wallet.local.coin

import com.mangala.wallet.model.blockchain.BlockchainEntity
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.person.local.PersonEntity

interface CoinLocalDataSource {

    suspend fun clearDatabase()

    suspend fun deleteCoinById(uid: String)

    suspend fun getAllCoin(): List<Coin>

    suspend fun getCoinById(uid: String): List<Coin>

    suspend fun getCoinByCode(code: String): List<Coin>

    suspend fun insertCoin(coins: List<Coin>)

    suspend fun countCoin(): Long
}