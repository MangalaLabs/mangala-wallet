package com.mangala.wallet.domain.token.historicalprice.repository

import com.mangala.wallet.model.token.TokenHistoricalPrice
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface TokenHistoricalPriceRepository {
    suspend fun fetchHistoricalTokenPrice(coinGeckoId: String, date: String): Result<TokenHistoricalPrice>
    fun fetchHistoricalTokenPriceFlow(
        coinGeckoId: String,
        date: String,
        forceRefresh: Boolean
    ): Flow<Resource<TokenHistoricalPrice?>>
}