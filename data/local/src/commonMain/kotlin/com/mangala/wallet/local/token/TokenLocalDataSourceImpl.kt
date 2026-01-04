package com.mangala.wallet.local.token

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.token.TokenEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TokenLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TokenLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun deleteTokenById(id: Long) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteTokenById(id)
        }
    }

    override suspend fun getTokenById(id: Long): List<TokenEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenById(id,::mapToken).executeAsList()
    }

    override fun getNativeCoin(blockchainUid: String): TokenEntity {
        return dbQuery.getNativeCoin(blockchainUid,::mapToken).executeAsOne()
    }

    override suspend fun getTokenByCoinUidAndBlockchainUid(
        coinUid: String,
        blockchainUid: String
    ): List<TokenEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenByCoinUidAndBlockchainUid(coinUid, blockchainUid,::mapToken).executeAsList()
    }

    override suspend fun getFirst2TokenByBlockchainUid(blockchainUid: String): List<TokenEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getFirst2TokenByBlockchainUid(blockchainUid,::mapToken).executeAsList()
    }

    override fun getTokenByBlockchainUidPagingSource(blockchainUid: String): PagingSource<Int, TokenEntity> {
        return QueryPagingSource(
            countQuery = dbQuery.countTokenByBlockchainUid(blockchainUid),
            transacter = dbQuery,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                dbQuery.getTokenByBlockchainUidPaged(blockchainUid, limit, offset, ::mapToken)
            }
        )
    }

    override suspend fun getTokenByReference(reference: String): List<TokenEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenByReference(reference,::mapToken).executeAsList()
    }

    override suspend fun getTokenByReference(reference: String, blockchainUid: String): List<TokenEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenByReferenceAndBlockchainUid(reference, blockchainUid,::mapToken).executeAsList()
    }

    override suspend fun getTokenByReferences(references: List<String>): List<TokenEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenByReferences(references,::mapToken).executeAsList()
    }

    override suspend fun insertToken(tokens: List<TokenEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            tokens.forEach { token ->
                insertTokenWithoutSuspend(token)
            }
        }
    }

    override suspend fun insertToken(token: TokenEntity): Long = withContext(ioDispatcher) {
        return@withContext insertTokenWithoutSuspend(token)
    }

    override suspend fun insertToken(token: TokenEntity, coin: Coin): Long = withContext(ioDispatcher) {
        return@withContext dbQuery.transactionWithResult{
            dbQuery.insertOrReplaceCoin(
                coin.uid,
                coin.name,
                coin.code,
                coin.marketCapRank,
                coin.coinGeckoId
            )

            dbQuery.insertOrReplaceToken(
                token.coinUid,
                token.blockchainUid,
                token.type,
                token.decimals,
                token.reference,
            )
            dbQuery.getLastInsertedTokenRowId().executeAsOne()
        }
    }

    override suspend fun countCoin(): Long = withContext(ioDispatcher)  {
        dbQuery.countToken().executeAsOne().let { count ->
            return@let count
        }
    }

    private fun mapToken(
        id: Long,
        coinUid: String,
        blockchainUid: String,
        type: String,
        decimals: Long?,
        reference: String?
    ): TokenEntity {
        return TokenEntity(
            id,
            coinUid,
            blockchainUid,
            type,
            decimals,
            reference
        )
    }

    private fun insertTokenWithoutSuspend(token: TokenEntity): Long {
        return dbQuery.transactionWithResult {
            dbQuery.insertToken(
                token.coinUid,
                token.blockchainUid,
                token.type,
                token.decimals,
                token.reference,
            )
            dbQuery.getLastInsertedTokenRowId().executeAsOne()
        }
    }
}