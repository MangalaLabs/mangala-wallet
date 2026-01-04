package com.mangala.wallet.local.token.exchangerate

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import commangalawalletdatabase.TokenExchangeRateEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TokenExchangeRateLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TokenExchangeRateLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun insertOrReplaceTokenExchangeRate(tokenExchangeRate: List<TokenExchangeRateEntity>) {
        withContext(ioDispatcher) {
            database.transaction {
                tokenExchangeRate.forEach { tokenExchangeRate ->
                    dbQuery.insertOrReplaceTokenExchangeRate(
                        tokenExchangeRate.coin_uid,
                        tokenExchangeRate.quote_currency,
                        tokenExchangeRate.rate,
                        tokenExchangeRate.updated_at
                    )
                }
            }
        }
    }

    override suspend fun getTokenExchangeRateByCoinUid(coinUid: String): List<TokenExchangeRateEntity> {
        return withContext(ioDispatcher) {
            return@withContext dbQuery.selectAllTokenExchangeRateByCoinUid(
                coinUid,
                ::mapTokenExchangeRate
            ).executeAsList()
        }
    }

    override fun getTokenExchangeRateByCoinUidFlow(coinUid: String): Flow<List<TokenExchangeRateEntity>> {
        return dbQuery
            .selectAllTokenExchangeRateByCoinUid(coinUid, ::mapTokenExchangeRate)
            .asFlow()
            .map { it.executeAsList() }
            .flowOn(ioDispatcher)
    }

    override suspend fun deleteTokenExchangeRateByCoinUid(coinUid: String) {
        withContext(ioDispatcher) {
            dbQuery.deleteTokenExchangeRateDataByCoinUid(coinUid)
        }
    }

    private fun mapTokenExchangeRate(
        coin_uid: String,
        quote_currency: String,
        rate: String,
        updated_at: Long
    ): TokenExchangeRateEntity {
        return TokenExchangeRateEntity(
            coin_uid = coin_uid,
            quote_currency = quote_currency,
            rate = rate,
            updated_at = updated_at
        )
    }
}