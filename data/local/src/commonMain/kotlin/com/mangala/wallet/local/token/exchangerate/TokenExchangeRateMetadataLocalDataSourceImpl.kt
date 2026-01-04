package com.mangala.wallet.local.token.exchangerate

import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import commangalawalletdatabase.TokenExchangeRateMetadataEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TokenExchangeRateMetadataLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TokenExchangeRateMetadataLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun insertTokenExchangeRateMetadata(metadata: TokenExchangeRateMetadataEntity) {
        withContext(ioDispatcher) {
            dbQuery.insertOrReplaceTokenExchangeRateMetadata(
                metadata.coin_uid,
                metadata.updated_at
            )
        }
    }

    override suspend fun getLastUpdatedTimestamp(coinUid: String): Long? {
        return withContext(ioDispatcher) {
            return@withContext dbQuery.getTokenExchangeRateMetadataByCoinUid(coinUid).executeAsOneOrNull()
        }
    }
}