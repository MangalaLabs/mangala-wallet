package com.mangala.wallet.features.chains.antelope_base.data.repository.ram

import com.mangala.antelope.base.api.model.BaseGetTableRowsRequest
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RamMarketRowResponse
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamMarketLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.ram.AntelopeRamMarketInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamMarketRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeRamMarketEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

internal class AntelopeRamMarketRepositoryImpl(
    private val remoteDataSource: AntelopeRemoteDataSource,
    private val localDataSource: AntelopeRamMarketLocalDataSource,
) : AntelopeRamMarketRepository {

    override suspend fun getRamPrice(blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeRamMarketInfo?> {
        return cachedResource(
            query = { localDataSource.getRamPrice(blockchainType.uid) },
            fetch = {
                fetchRamPrice(blockchainType)
            },
            saveFetchResult = {
                saveFetchResult(it, blockchainType)
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it?.toAntelopeRamMarketInfo() }
        )
    }

    override fun getRamPriceFlow(
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeRamMarketInfo?>> = networkBoundResource(
        query = { localDataSource.getRamPriceFlow(blockchainType.uid) },
        fetch = { fetchRamPrice(blockchainType) },
        saveFetchResult = {
            it?.let {
                saveFetchResult(it, blockchainType)
            }
        },
        shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
        entityToDomain = { it?.toAntelopeRamMarketInfo() }
    )

    private suspend fun fetchRamPrice(blockchainType: BlockchainType) =
        remoteDataSource.getTableRowsRamMarket(
            blockchainType,
            BaseGetTableRowsRequest(
                code = SystemContracts.EOS_SYSTEM_CONTRACT,
                scope = SystemContracts.EOS_SYSTEM_CONTRACT,
                table = "rammarket",
                json = true,
                limit = 1,
                lowerBound = "",
                upperBound = "",
                indexPosition = 1,
                keyType = "",
                reverse = false,
                showPayer = false
            )
        )

    private suspend fun saveFetchResult(
        it: BaseGetTableRowsResponse<RamMarketRowResponse>,
        blockchainType: BlockchainType
    ) {
        it.toAntelopeRamMarketEntity(
            blockchainType.uid
        )?.let {
            localDataSource.insertRamPrice(it)
        }
    }

    private fun shouldFetch(
        cachedResponse: AntelopeRamMarketEntity?,
        forceRefresh: Boolean
    ): Boolean {
        return cachedResponse == null || forceRefresh || isCacheExpired(cachedResponse)
    }

    private fun isCacheExpired(cachedResponse: AntelopeRamMarketEntity): Boolean {
        return cachedResponse.last_updated + CACHE_EXPIRATION_TIME < Clock.System.now()
            .toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}