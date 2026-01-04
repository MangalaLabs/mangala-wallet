package com.mangala.wallet.domain.token.repository

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import com.mangala.wallet.domain.CACHE_FOR_BALANCE
import com.mangala.wallet.local.token.TokenLocalDataSource
import com.mangala.wallet.local.token.balance.TokenBalanceLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.coin.Coin
import com.mangala.wallet.model.provider.BaseBalanceResponse
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class TokenRepositoryImpl(
    private val tokenLocalDataSource: TokenLocalDataSource,
    private val tokenBalanceLocalDataSource: TokenBalanceLocalDataSource,
    private val getBlockExplorerRemoteDataSource: (BlockchainType) -> BaseBlockExplorerRemoteDataSource
): TokenRepository {

    override suspend fun deleteTokenById(id: Long) {
        tokenLocalDataSource.deleteTokenById(id)
    }

    override suspend fun getTokenById(id: Long): List<TokenEntity> {
        return tokenLocalDataSource.getTokenById(id)
    }

    override fun getNativeCoin(blockchainUid: String): TokenEntity {
        return tokenLocalDataSource.getNativeCoin(blockchainUid)
    }

    override suspend fun getTokenByCoinUidAndBlockchainUid(
        coinUid: String,
        blockchainUid: String
    ): List<TokenEntity> {
        return tokenLocalDataSource.getTokenByCoinUidAndBlockchainUid(coinUid, blockchainUid)
    }

    override suspend fun getFirst2TokenByBlockchainUid(blockchainUid: String): List<TokenEntity> {
        return tokenLocalDataSource.getFirst2TokenByBlockchainUid(blockchainUid)
    }

    override fun getPaginatedTokenByBlockchainUid(blockchainUid: String): Flow<PagingData<TokenEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 20)
        ){
            tokenLocalDataSource.getTokenByBlockchainUidPagingSource(blockchainUid)
        }.flow
    }

    override suspend fun getTokenByReference(reference: String): List<TokenEntity> {
        return tokenLocalDataSource.getTokenByReference(reference)
    }

    override suspend fun getTokenByReference(reference: String, blockchainUid: String): List<TokenEntity> {
        return tokenLocalDataSource.getTokenByReference(reference, blockchainUid)
    }

    override suspend fun getTokenByReferences(references: List<String>): List<TokenEntity> {
        return tokenLocalDataSource.getTokenByReferences(references)
    }

    override suspend fun insertToken(tokens: List<TokenEntity>) {
        return tokenLocalDataSource.insertToken(tokens)
    }

    override suspend fun insertToken(token: TokenEntity): Long {
        return tokenLocalDataSource.insertToken(token)
    }

    private suspend fun insertToken(token: TokenEntity, coin: Coin): Long {
        return tokenLocalDataSource.insertToken(token, coin)
    }

    override suspend fun deleteTokenBalanceByTokenIdAndAccountId(tokenId: Long, accountId: String) {
        tokenBalanceLocalDataSource.deleteTokenBalanceByTokenIdAndAccountId(tokenId, accountId)
    }

    override suspend fun deleteTokenBalanceByAccountIdAndBlockchainUid(
        accountId: String,
        blockchainUid: String
    ) {
        tokenBalanceLocalDataSource.deleteTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainUid)
    }

    override suspend fun deleteTokenBalanceByAccountId(accountId: String) {
        tokenBalanceLocalDataSource.deleteTokenBalanceByAccountId(accountId)
    }

    override fun getTokenBalanceByTokenIdAndAccountId(
        tokenId: Long,
        accountId: String
    ): List<TokenBalanceEntity> {
        return tokenBalanceLocalDataSource.getTokenBalanceByTokenIdAndAccountId(tokenId, accountId)
    }

    override suspend fun getTokenBalanceByAccountId(accountId: String): List<TokenBalanceEntity> {
        return tokenBalanceLocalDataSource.getTokenBalanceByAccountId(accountId)
    }

    override suspend fun getTokenBalanceByAccountIdAndBlockchainUid(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Map<String, TokenBalanceEntity> {
        val tokenMap = mutableMapOf<String, TokenBalanceEntity>()
        val localData = tokenBalanceLocalDataSource.getTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainType.uid)
        if(localData.isNotEmpty()) {
            localData.forEach { tokenBalanceEntity ->
                val reference= if (tokenBalanceEntity.isCoin) tokenBalanceEntity.contractSymbol else tokenBalanceEntity.contractAddress
                val tokenEntity = getTokenByReference(reference).getOrNull(0) // TODO: What if we have coins on multiple blockchains that shares the same address? Need to pass in blockchain uid to prevent this
                tokenEntity?.let {
                    tokenMap[tokenEntity.coinUid] = tokenBalanceEntity
                }
            }
        }

        if(forceReload || localData.isEmpty() || (localData.isNotEmpty() && !isCachedTokenBalance(localData.first().lastUpdated))){
            val fetchData = fetchBalanceAndSaveToDatabase(
                blockchainType = blockchainType,
                accountId = accountId,
                address = address
            )
            if(fetchData.isNotEmpty()){
                tokenMap.clear()
                tokenMap.putAll(fetchData)
            }
        }
        return tokenMap
    }

    override fun getTokenBalanceByAccountIdAndBlockchainUidFlow(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Map<String, TokenBalanceEntity>> {
        val tokenMap = mutableMapOf<String, TokenBalanceEntity>()
        var hasLoaded = false
        return tokenBalanceLocalDataSource.getTokenBalanceByAccountIdAndBlockchainUidFlow(
            accountId,
            blockchainType.uid
        ).map { localData ->
            if (localData.isNotEmpty()) {
                localData.forEach { tokenBalanceEntity ->
                    val reference =
                        if (tokenBalanceEntity.isCoin) tokenBalanceEntity.contractSymbol else tokenBalanceEntity.contractAddress
                    val tokenEntity = getTokenByReference(reference)
                        .getOrNull(0) // TODO: What if we have coins on multiple blockchains that shares the same address? Need to pass in blockchain uid to prevent this
                    tokenEntity?.let {
                        tokenMap[tokenEntity.coinUid] = tokenBalanceEntity
                    }
                }
            }

            if ((forceReload && !hasLoaded) || localData.isEmpty() || (localData.isNotEmpty() && !isCachedTokenBalance(localData.first().lastUpdated))) {
                hasLoaded = true
                val fetchData = fetchBalanceAndSaveToDatabase(
                    blockchainType = blockchainType,
                    accountId = accountId,
                    address = address
                )
                if (fetchData.isNotEmpty()) {
                    tokenMap.clear()
                    tokenMap.putAll(fetchData)
                }
            }
            tokenMap
        }
    }

    override fun getTokenBalanceByAccountIdAndBlockchainUidResource(
        forceReload: Boolean,
        address: String,
        blockchainType: BlockchainType,
        accountId: String
    ): Flow<Resource<Map<String, TokenBalanceEntity>>> = networkBoundResource(
        query = {
            tokenBalanceLocalDataSource.getTokenBalanceByAccountIdAndBlockchainUidFlow(
                accountId,
                blockchainType.uid
            )
        },
        fetch = {
            val remoteDataSource = getBlockExplorerRemoteDataSource(blockchainType)
            remoteDataSource.getBalanceByNetWorkAndAddress( // TODO: Pass in the blockchainType directly, and let each provider get their own network name
                address = address,
                chainNetWork = blockchainType.uid
            )
        },
        saveFetchResult = { apiResponse ->
            val tokenBalances = mutableListOf<TokenBalanceEntity>()
            apiResponse.items?.forEach { item ->
                val isNativeToken = item.nativeToken == true
                val reference = if (isNativeToken)
                    item.contractTickerSymbol ?: item.contractAddress.orEmpty()
                else
                    item.contractAddress.orEmpty()

                val tokenEntity = if (isNativeToken) {
                    // Prevent mixing up between mainnet and testnet coins, since we use the same reference for both
                    getTokenByReference(reference, blockchainType.uid).getOrNull(0)
                } else {
                    getTokenByReference(item.contractAddress.orEmpty(), blockchainType.uid).getOrNull(0)
                }

                tokenEntity?.let { entity ->
                    tokenBalances.add(mapToTokenBalanceEntity(blockchainType.uid, accountId, item, entity.id))
                } ?: run {
                    val newTokenEntity = TokenEntity(
                        id = 0L,
                        coinUid = item.contractAddress.orEmpty() + item.contractName.orEmpty(), // Just to make this unique
                        blockchainUid = blockchainType.uid,
                        type = "",
                        decimals = item.contractDecimals,
                        reference = item.contractAddress
                    )
                    val coin = Coin(
                        uid = item.contractAddress.orEmpty() + item.contractName.orEmpty(), // Just to make this unique
                        name = item.contractName.orEmpty(),
                        code = item.contractTickerSymbol.orEmpty(),
                    )
                    val insertedTokenId = insertToken(newTokenEntity, coin)
                    tokenBalances.add(mapToTokenBalanceEntity(blockchainType.uid, accountId, item, insertedTokenId))
                }
            }

            //after scan token, we need to save token to database
            deleteTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainType.uid)
            insertTokenBalance(tokenBalances)
        },
        shouldFetch = { localData ->
            forceReload || localData.isEmpty() || (localData.isNotEmpty() && !isCachedTokenBalance(localData.first().lastUpdated))
        },
        entityToDomain = { localData ->
            val tokenMap = mutableMapOf<String, TokenBalanceEntity>()
            localData.forEach { tokenBalanceEntity ->
                val reference = if (tokenBalanceEntity.isCoin)
                    tokenBalanceEntity.contractSymbol
                else
                    tokenBalanceEntity.contractAddress

                val tokenEntity = getTokenByReference(reference).getOrNull(0) // TODO: What if we have coins on multiple blockchains that shares the same address? Need to pass in blockchain uid to prevent this
                tokenEntity?.let {
                    tokenMap[tokenEntity.coinUid] = tokenBalanceEntity
                }
            }
            tokenMap
        }
    )

    override suspend fun insertTokenBalance(tokenBalance: List<TokenBalanceEntity>) {
        tokenBalanceLocalDataSource.insertTokenBalance(tokenBalance)
    }

    override suspend fun insertOrReplaceTokenBalance(tokenBalance: List<TokenBalanceEntity>) {
        tokenBalanceLocalDataSource.insertOrReplaceTokenBalance(tokenBalance)
    }

    override suspend fun updateTokenBalance(tokenBalance: List<TokenBalanceEntity>) {
        tokenBalanceLocalDataSource.updateTokenBalance(tokenBalance)
    }

    private fun isCachedTokenBalance(lastUpdated: Long): Boolean{
        val currentTime = Clock.System.now()
        val cacheExpirationDuration = CACHE_FOR_BALANCE.minutes
        val lastUpdatedTime = Instant.fromEpochMilliseconds(lastUpdated)
        return currentTime - lastUpdatedTime < cacheExpirationDuration
    }

    private suspend fun fetchBalanceAndSaveToDatabase(
        blockchainType: BlockchainType,
        accountId: String,
        address: String
    ): Map<String, TokenBalanceEntity> {
        val remoteDataSource = getBlockExplorerRemoteDataSource(blockchainType)
        val tokenMap = mutableMapOf<String, TokenBalanceEntity>()
//        TODO: need to check condition then get chain network base on blockchainType
        val apiResponse = remoteDataSource.getBalanceByNetWorkAndAddress(
            address = address,
            chainNetWork = blockchainType.uid // TODO: Pass in the blockchainType directly, and let each provider get their own network name
        )
        if(apiResponse is ApiResponse.Success) {
            val data = apiResponse.body
            if (data.items != null) {
                data.items?.forEach {
                    val isNativeToken = it.nativeToken == true
                    val reference = if (isNativeToken) it.contractTickerSymbol ?: it.contractAddress.orEmpty() else it.contractAddress.orEmpty()

                    val tokenEntity = if (isNativeToken) {
                        // Prevent mixing up between mainnet and testnet coins, since we use the same reference for both
                        getTokenByReference(reference, blockchainType.uid).getOrNull(0)
                    } else {
                        getTokenByReference(it.contractAddress.orEmpty(), blockchainType.uid).getOrNull(0)
                    }
                    tokenEntity?.let { _ ->
                        val tokenBalance = mapToTokenBalanceEntity(blockchainType.uid, accountId, it, tokenEntity.id)
                        tokenMap[tokenEntity.coinUid] = tokenBalance
                    } ?: run {
                        val tokenEntity = TokenEntity(
                            id = 0L,
                            coinUid = it.contractAddress.orEmpty() + it.contractName.orEmpty(), // Just to make this unique
                            blockchainUid = blockchainType.uid,
                            type = "", // TODO: fill this in if necessary
                            decimals = it.contractDecimals,
                            reference = it.contractAddress
                        )
                        val coin = Coin(
                            uid = it.contractAddress.orEmpty() + it.contractName.orEmpty(), // Just to make this unique
                            name = it.contractName.orEmpty(),
                            code = it.contractTickerSymbol.orEmpty(),
                        )
                        val insertedTokenId = insertToken(tokenEntity, coin)
                        tokenMap[it.contractAddress.orEmpty()] = mapToTokenBalanceEntity(blockchainType.uid, accountId, it, insertedTokenId)
                    }
                }
                //after scan token, we need to save token to database
                deleteTokenBalanceByAccountIdAndBlockchainUid(accountId, blockchainType.uid)
                insertTokenBalance(tokenMap.values.toList())
            }
        }
        return tokenMap
    }

    override suspend fun clearAllUserTokenBalances(): Result<Unit> {
        return try {
            tokenBalanceLocalDataSource.clearAllTokenBalances()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToTokenBalanceEntity(
        blockchainUid: String,
        accountId: String,
        it: BaseBalanceResponse.Item,
        tokenId: Long,
    ): TokenBalanceEntity {
        return TokenBalanceEntity(
            tokenId = tokenId,
            accountId = accountId,
            blockchainUid = blockchainUid,
            balance = it.balance ?: "",
            balance24h = it.balance24h ?: "",
            balanceLocked = "",
            orderNumber = 0,
            contractAddress = it.contractAddress ?: "",
            contractDecimals = it.contractDecimals ?: 0,
            contractName = it.contractName ?: "",
            contractSymbol = it.contractTickerSymbol ?: "",
            logoUrl = it.logoUrl ?: "",
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
    }
}