package com.mangala.wallet.local.token.historicalprice

import com.mangala.wallet.model.token.TokenHistoricalPrice
import commangalawalletdatabase.TokenHistoricalPriceEnitity
import kotlinx.coroutines.flow.Flow

interface TokenHistoricalPriceLocalDataSource {

    suspend fun saveHistoricalPrice(tokenHistoricalPrice: TokenHistoricalPrice)
    suspend fun getPriceByDateAndId(coingeckoId: String, date: String): TokenHistoricalPriceEnitity?
    fun getPriceByDateAndIdFlow(coingeckoId: String, date: String): Flow<TokenHistoricalPriceEnitity?>
}