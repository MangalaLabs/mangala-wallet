package com.mangala.wallet.domain.token.repository

import app.cash.paging.PagingData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface TokenRepository {

    suspend fun deleteTokenById(id: Long)

    suspend fun getTokenById(id: Long): List<TokenEntity>
    fun getNativeCoin(blockchainUid: String): TokenEntity

    suspend fun getTokenByCoinUidAndBlockchainUid(coinUid: String, blockchainUid: String): List<TokenEntity>

    suspend fun getFirst2TokenByBlockchainUid(blockchainUid: String): List<TokenEntity>

    fun getPaginatedTokenByBlockchainUid(blockchainUid: String): Flow<PagingData<TokenEntity>>

    suspend fun getTokenByReference(reference: String): List<TokenEntity>
    suspend fun getTokenByReference(reference: String, blockchainUid: String): List<TokenEntity>
    suspend fun getTokenByReferences(references: List<String>): List<TokenEntity>

    suspend fun insertToken(tokens: List<TokenEntity>)
    suspend fun insertToken(token: TokenEntity): Long

    suspend fun deleteTokenBalanceByTokenIdAndAccountId(tokenId: Long, accountId: String)
    suspend fun deleteTokenBalanceByAccountIdAndBlockchainUid(accountId: String, blockchainUid: String)
    suspend fun deleteTokenBalanceByAccountId(accountId: String)
    fun getTokenBalanceByTokenIdAndAccountId(tokenId: Long, accountId: String): List<TokenBalanceEntity>
    suspend fun getTokenBalanceByAccountId(accountId: String): List<TokenBalanceEntity>
    suspend fun getTokenBalanceByAccountIdAndBlockchainUid(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Map<String, TokenBalanceEntity>

    fun getTokenBalanceByAccountIdAndBlockchainUidFlow(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Map<String, TokenBalanceEntity>>
    fun getTokenBalanceByAccountIdAndBlockchainUidResource(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Resource<Map<String, TokenBalanceEntity>>>

    suspend fun insertTokenBalance(tokenBalance: List<TokenBalanceEntity>)
    suspend fun insertOrReplaceTokenBalance(tokenBalance: List<TokenBalanceEntity>)
    suspend fun updateTokenBalance(tokenBalance: List<TokenBalanceEntity>)
    suspend fun clearAllUserTokenBalances(): Result<Unit>
}