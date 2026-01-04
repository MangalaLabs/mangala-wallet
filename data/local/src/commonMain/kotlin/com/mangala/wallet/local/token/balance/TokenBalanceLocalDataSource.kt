package com.mangala.wallet.local.token.balance

import com.mangala.wallet.model.token.TokenBalanceEntity
import kotlinx.coroutines.flow.Flow

interface TokenBalanceLocalDataSource {

    suspend fun deleteTokenBalanceByTokenIdAndAccountId(tokenId: Long, accountId: String)
    suspend fun deleteTokenBalanceByAccountIdAndBlockchainUid(accountId: String, blockchainUid: String)
    suspend fun deleteTokenBalanceByAccountId(accountId: String)

    fun getTokenBalanceByTokenIdAndAccountId(tokenId: Long, accountId: String): List<TokenBalanceEntity>

    suspend fun getTokenBalanceByAccountId(accountId: String): List<TokenBalanceEntity>

    suspend fun getTokenBalanceByAccountIdAndBlockchainUid(accountId: String, blockchainUid: String): List<TokenBalanceEntity>
    fun getTokenBalanceByAccountIdAndBlockchainUidFlow(accountId: String, blockchainUid: String): Flow<List<TokenBalanceEntity>>

    suspend fun insertTokenBalance(tokenBalance: List<TokenBalanceEntity>)

    suspend fun updateTokenBalance(tokenBalance: List<TokenBalanceEntity>)
    suspend fun insertOrReplaceTokenBalance(tokenBalance: List<TokenBalanceEntity>)
    suspend fun clearAllTokenBalances()
}
