package com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexpool

import com.mangala.antelope.base.api.model.BaseGetTableRowsRequest
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RexPoolRowResponse
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexpool.AntelopeRexPoolLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexPoolInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexPoolRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexPoolEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

internal class AntelopeRexPoolRepositoryImpl(
    private val remoteDataSource: AntelopeRemoteDataSource,
    private val localDataSource: AntelopeRexPoolLocalDataSource
): AntelopeRexPoolRepository {

    override suspend fun getTableRowsRexPool(blockchainType: BlockchainType, forceRefresh: Boolean): Result<AntelopeRexPoolInfo?> {
        return cachedResource(
            query = { localDataSource.getTableRowsRexPool(blockchainType.uid) },
            fetch = {
                fetch(blockchainType)
            },
            saveFetchResult = {
                saveFetchResult(it, blockchainType)
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it?.toAntelopeRexPool() }
        )
    }

    override fun getTableRowsRexPoolFlow(
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeRexPoolInfo?>> = networkBoundResource(
        query = {
            localDataSource.getTableRowsRexPoolFlow(blockchainType.uid)
        },
        fetch = {
            fetch(blockchainType)
        },
        saveFetchResult = {
            saveFetchResult(it, blockchainType)
        },
        shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
        entityToDomain = { it?.toAntelopeRexPool() }
    )

    private suspend fun fetch(blockchainType: BlockchainType) =
        remoteDataSource.getTableRowsRexPool(
            blockchainType,
            BaseGetTableRowsRequest(
                code = SystemContracts.EOS_SYSTEM_CONTRACT,
                scope = SystemContracts.EOS_SYSTEM_CONTRACT,
                table = "rexpool",
                lowerBound = "",
                upperBound = "",
                limit = 1,
                json = true,
                keyType = "",
                indexPosition = 1,
                reverse = false,
                showPayer = false
            )
        )

    private suspend fun saveFetchResult(
        it: BaseGetTableRowsResponse<RexPoolRowResponse>,
        blockchainType: BlockchainType
    ) {
        it.toAntelopeRexPoolEntity(blockchainType.uid)?.let {
            localDataSource.insertRexPool(it)
        }
    }

    private fun shouldFetch(
        cachedResponse: AntelopeRexPoolEntity?,
        forceRefresh: Boolean
    ): Boolean {
        return cachedResponse == null || forceRefresh || isCacheExpired(cachedResponse)
    }

    private fun isCacheExpired(cachedResponse: AntelopeRexPoolEntity): Boolean {
        return cachedResponse.last_updated + CACHE_EXPIRATION_TIME < Clock.System.now().toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}