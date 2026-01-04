package com.mangala.wallet.domain.token.price.repository

import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.token.TokenPriceEntity
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface TokenPriceRepository {

    suspend fun deleteTokenPriceByCoinUid(coinId: String)

    suspend fun getTokenPriceByCoinId(coinId: String): List<TokenPriceEntity>

    suspend fun getTokenPriceByCoinIdAndCurrencyCode(coinId: String, currencyCode: String): List<TokenPriceEntity>
    suspend fun getTokenPriceByCoinIdsAndCurrencyCode(coinIds: List<String>, currencyCode: String): List<TokenPriceEntity>
    suspend fun getTokenWithSparklineByCoinUidsAndCurrencyCode(coinIds: List<String>, currencyCode: String): List<TokenPriceEntity>
    fun getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
        coinIds: List<String>,
        currencyCode: String,
        forceReload: Boolean
    ): Flow<Resource<List<TokenPriceEntity>>>
    fun getTokenWithSparklineByCoinUidsAndCurrencyCodeFlow(
        tokenScan: Map<String, TokenBalanceEntity>,
        currencyCode: String,
        forceReload: Boolean
    ): Flow<Resource<List<TokenBalanceModel>>>

    suspend fun insertTokenPrice(tokenPrices: List<TokenPriceEntity>)
    suspend fun insertOrReplaceTokenPrice(tokenPrices: List<TokenPriceEntity>)

    suspend fun updateTokenPrice(tokenPrices: List<TokenPriceEntity>)
}