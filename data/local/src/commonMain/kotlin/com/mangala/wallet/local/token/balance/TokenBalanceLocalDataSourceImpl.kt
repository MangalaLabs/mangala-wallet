package com.mangala.wallet.local.token.balance

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.token.TokenBalanceEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TokenBalanceLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    TokenBalanceLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun deleteTokenBalanceByTokenIdAndAccountId(tokenId: Long, accountId: String) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteTokenBalanceByTokenIdAndAccountId(tokenId, accountId)
        }
    }

    override suspend fun deleteTokenBalanceByAccountIdAndBlockchainUid(
        accountId: String,
        blockchainUid: String
    ) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainUid)
        }
    }

    override suspend fun deleteTokenBalanceByAccountId(accountId: String) = withContext(ioDispatcher) {
        dbQuery.transaction {
            dbQuery.deleteTokenBalanceByAccountId(accountId)
        }
    }

    override fun getTokenBalanceByTokenIdAndAccountId(
        tokenId: Long,
        accountId: String
    ): List<TokenBalanceEntity> {
        return dbQuery.getTokenBalanceByTokenIdAndAccountId(tokenId, accountId,::mapTokenBalance).executeAsList()
    }

    override suspend fun getTokenBalanceByAccountId(accountId: String): List<TokenBalanceEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenBalanceByAccountId(accountId,::mapTokenBalance).executeAsList()
    }

    override suspend fun getTokenBalanceByAccountIdAndBlockchainUid(
        accountId: String,
        blockchainUid: String
    ): List<TokenBalanceEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainUid, ::mapTokenBalance).executeAsList()
    }

    override fun getTokenBalanceByAccountIdAndBlockchainUidFlow(
        accountId: String,
        blockchainUid: String
    ): Flow<List<TokenBalanceEntity>> {
        return dbQuery
            .getTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainUid, ::mapTokenBalance)
            .asFlow()
            .mapToList(ioDispatcher)
            .flowOn(ioDispatcher)
    }

    override suspend fun insertTokenBalance(tokenBalance: List<TokenBalanceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            tokenBalance.forEach { token ->
                insertTokenBalance(token)
            }
        }
    }

    override suspend fun updateTokenBalance(tokenBalance: List<TokenBalanceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            tokenBalance.forEach { token ->
                updateTokenBalance(token)
            }
        }
    }

    override suspend fun insertOrReplaceTokenBalance(tokenBalance: List<TokenBalanceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            tokenBalance.forEach { token ->
                insertOrReplaceTokenBalance(token)
            }
        }
    }

    override suspend fun clearAllTokenBalances() = withContext(ioDispatcher) {
        dbQuery.clearAllTokenBalances()
    }

    private fun mapTokenBalance(
        tokenId: Long,
        accountId: String,
        blockchainUid: String,
        balance: String,
        balance24h: String?,
        balanceLocked: String,
        orderNumber: Long,
        contractDecimals: Long,
        contractName: String,
        contractSymbol: String,
        contractAddress: String,
        logoUrl: String?,
        lastUpdated: Long?,
    ): TokenBalanceEntity {
        return TokenBalanceEntity(
            tokenId,
            accountId,
            blockchainUid,
            balance,
            balance24h ?: "",
            balanceLocked,
            orderNumber.toInt(),
            contractDecimals,
            contractName,
            contractSymbol,
            contractAddress,
            logoUrl ?: "",
            lastUpdated ?: 0L
        )
    }

    private fun insertOrReplaceTokenBalance(token: TokenBalanceEntity) {
        dbQuery.insertOrReplaceTokenBalance(
            token.tokenId,
            token.accountId,
            token.blockchainUid,
            token.balance,
            token.balance24h,
            token.balanceLocked,
            token.orderNumber.toLong(),
            token.contractDecimals,
            token.contractName,
            token.contractSymbol,
            token.contractAddress,
            token.logoUrl,
            token.lastUpdated
        )
    }

    private fun insertTokenBalance(token: TokenBalanceEntity) {
        dbQuery.insertTokenBalance(
            token.tokenId,
            token.accountId,
            token.blockchainUid,
            token.balance,
            token.balance24h,
            token.balanceLocked,
            token.orderNumber.toLong(),
            token.contractDecimals,
            token.contractName,
            token.contractSymbol,
            token.contractAddress,
            token.logoUrl,
            token.lastUpdated
        )
    }

    private fun updateTokenBalance(token: TokenBalanceEntity) {
        dbQuery.updateTokenBalanceByTokenIdAndAccountId(
            token.blockchainUid,
            token.balance,
            token.balance24h,
            token.balanceLocked,
            token.orderNumber.toLong(),
            token.contractDecimals,
            token.contractName,
            token.contractSymbol,
            token.contractAddress,
            token.logoUrl,
            token.tokenId,
            token.accountId,
        )
    }
}