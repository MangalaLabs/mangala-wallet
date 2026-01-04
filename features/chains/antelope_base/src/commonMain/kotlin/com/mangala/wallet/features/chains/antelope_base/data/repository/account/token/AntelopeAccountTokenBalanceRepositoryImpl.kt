package com.mangala.wallet.features.chains.antelope_base.data.repository.account.token

import com.mangala.antelope.base.api.model.tokenbalance.BaseAntelopeTokenBalanceResponse
import com.mangala.antelope.base.api.remote.tokenbalance.AntelopeTokenBalanceRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.token.AntelopeAccountTokenBalanceLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.repository.token.AntelopeAccountTokenBalanceRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountTokenBalanceEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

internal class AntelopeAccountTokenBalanceRepositoryImpl(
    private val localDataSource: AntelopeAccountTokenBalanceLocalDataSource,
    private val remoteDataSource: AntelopeTokenBalanceRemoteDataSource
) : AntelopeAccountTokenBalanceRepository {

    override suspend fun getAccountTokenBalance(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeTokenBalance>> {
        return cachedResource(
            query = {
                localDataSource.getAccountTokenBalance(
                    accountName = accountName,
                    blockchainUid = blockchainType.uid
                )
            },
            fetch = {
                fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                saveFetchResult(accountName, blockchainType, it)
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it.toAntelopeTokenBalanceList() }
        )
    }

    override suspend fun getAccountTokenBalanceFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeTokenBalance>?>> {
        return networkBoundResource(
            query = {
                localDataSource.getAccountTokenBalanceFlow(
                    accountName = accountName,
                    blockchainUid = blockchainType.uid
                )
            },
            fetch = { fetch(blockchainType, accountName) },
            saveFetchResult = {
                saveFetchResult(accountName, blockchainType, it)
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it.toAntelopeTokenBalanceList() }
        )
    }

    private suspend fun fetch(
        blockchainType: BlockchainType,
        accountName: String
    ) = remoteDataSource.getTokenBalance(blockchainType, accountName)

    private suspend fun saveFetchResult(
        accountName: String,
        blockchainType: BlockchainType,
        it: BaseAntelopeTokenBalanceResponse
    ) {
        localDataSource.deleteAccountTokenBalanceByAccount(
            accountName,
            blockchainType.uid
        )
        it.toAntelopeAccountTokenBalanceEntityList(
            accountName = accountName,
            blockchainUid = blockchainType.uid
        ).let {
            localDataSource.insertAccountTokenBalance(it)
        }
    }

    private fun shouldFetch(
        cachedResponse: List<AntelopeAccountTokenBalanceEntity>,
        forceRefresh: Boolean
    ): Boolean {
        return cachedResponse.isEmpty() || forceRefresh || isCacheExpired(cachedResponse)
    }

    private fun isCacheExpired(cachedResponse: List<AntelopeAccountTokenBalanceEntity>): Boolean {
        return cachedResponse.minOf { it.last_updated } + CACHE_EXPIRATION_TIME < Clock.System.now()
            .toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}