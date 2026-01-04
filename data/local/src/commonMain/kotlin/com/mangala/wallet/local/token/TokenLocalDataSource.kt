package com.mangala.wallet.local.token

import app.cash.paging.PagingSource
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.token.TokenEntity

interface TokenLocalDataSource {

    suspend fun deleteTokenById(id: Long)

    suspend fun getTokenById(id: Long): List<TokenEntity>
    fun getNativeCoin(blockchainUid: String): TokenEntity

    suspend fun getTokenByCoinUidAndBlockchainUid(coinUid: String, blockchainUid: String): List<TokenEntity>

    suspend fun getFirst2TokenByBlockchainUid(blockchainUid: String): List<TokenEntity>

    fun getTokenByBlockchainUidPagingSource(blockchainUid: String): PagingSource<Int, TokenEntity>

    suspend fun getTokenByReference(reference: String): List<TokenEntity>
    suspend fun getTokenByReference(reference: String, blockchainUid: String): List<TokenEntity>
    suspend fun getTokenByReferences(references: List<String>): List<TokenEntity>

    suspend fun insertToken(tokens: List<TokenEntity>)
    suspend fun insertToken(token: TokenEntity): Long
    suspend fun insertToken(token: TokenEntity, coin: Coin): Long

    suspend fun countCoin(): Long
}