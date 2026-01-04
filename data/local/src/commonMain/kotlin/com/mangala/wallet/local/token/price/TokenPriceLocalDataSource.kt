package com.mangala.wallet.local.token.price

import com.mangala.wallet.model.token.TokenPriceEntity
import kotlinx.coroutines.flow.Flow

interface TokenPriceLocalDataSource {
    suspend fun deleteTokenPriceByCoinId(coinId: String)

    suspend fun getTokenPriceByCoinId(coinId: String): List<TokenPriceEntity>

    suspend fun getTokenPriceByCoinIdAndCurrencyCode(coinId: String, currencyCode: String): List<TokenPriceEntity>
    suspend fun getTokenPriceByCoinIdsAndCurrencyCode(coinIds: List<String>, currencyCode: String): List<TokenPriceEntity>
    suspend fun getTokenWithSparklineByCoinUidsAndCurrencyCode(coinIds: List<String>, currencyCode: String): List<TokenPriceEntity>
    fun getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(coinIds: List<String>, currencyCode: String): Flow<List<TokenPriceEntity>>

    suspend fun insertTokenPrice(tokenPrices: List<TokenPriceEntity>)
    suspend fun insertOrReplaceTokenPrice(tokenPrices: List<TokenPriceEntity>)

    suspend fun updateTokenPrice(tokenPrices: List<TokenPriceEntity>)
}