package com.mangala.wallet.local.token.exchangerate

import commangalawalletdatabase.TokenExchangeRateEntity
import kotlinx.coroutines.flow.Flow

interface TokenExchangeRateLocalDataSource {
    suspend fun insertOrReplaceTokenExchangeRate(tokenExchangeRate: List<TokenExchangeRateEntity>)
    suspend fun getTokenExchangeRateByCoinUid(coinUid: String): List<TokenExchangeRateEntity>
    fun getTokenExchangeRateByCoinUidFlow(coinUid: String): Flow<List<TokenExchangeRateEntity>>
    suspend fun deleteTokenExchangeRateByCoinUid(coinUid: String)
}