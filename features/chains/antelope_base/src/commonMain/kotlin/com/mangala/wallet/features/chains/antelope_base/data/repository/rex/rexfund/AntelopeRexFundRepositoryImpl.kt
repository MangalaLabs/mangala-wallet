package com.mangala.wallet.features.chains.antelope_base.data.repository.rex.rexfund

import com.mangala.antelope.base.api.model.BaseGetTableRowsRequest
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.RexFundRowResponse
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexfund.AntelopeRexFundLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexFundInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.rex.AntelopeRexFundRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexFundEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

internal class AntelopeRexFundRepositoryImpl(
    private val remoteDataSource: AntelopeRemoteDataSource,
    private val localDataSource: AntelopeRexFundLocalDataSource
): AntelopeRexFundRepository {

    override suspend fun getTableRowsRexFund(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<AntelopeRexFundInfo?> {
        return cachedResource(
            query = { localDataSource.getRexFund(accountName, blockchainType.uid) },
            fetch = {
                fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                saveFetchResult(it, accountName, blockchainType)
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it?.toAntelopeRexFund() }
        )
    }

    override fun getTableRowsRexFundFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeRexFundInfo?>> = networkBoundResource(
        query = {
            localDataSource.getRexFundFlow(accountName, blockchainType.uid)
        },
        fetch = {
            fetch(blockchainType, accountName)
        },
        saveFetchResult = {
            saveFetchResult(it, accountName, blockchainType)
        },
        shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
        entityToDomain = { it?.toAntelopeRexFund() }
    )

    private suspend fun fetch(
        blockchainType: BlockchainType,
        accountName: String
    ) = remoteDataSource.getTableRowsRexFund(
        blockchainType,
        BaseGetTableRowsRequest(
            code = "eosio",
            scope = "eosio",
            table = "rexfund",
            lowerBound = accountName,
            upperBound = accountName,
            limit = 1,
            json = true,
            keyType = "",
            indexPosition = 1,
            reverse = false,
            showPayer = false
        )
    )

    private suspend fun saveFetchResult(
        it: BaseGetTableRowsResponse<RexFundRowResponse>,
        accountName: String,
        blockchainType: BlockchainType
    ) {
        it.toAntelopeRexFundEntity(
            accountName,
            blockchainType.uid
        )?.let {
            localDataSource.insertRexFund(it)
        }
    }

    private fun shouldFetch(
        cachedResponse: AntelopeRexFundEntity?,
        forceRefresh: Boolean
    ): Boolean {
        return cachedResponse == null || forceRefresh || isCacheExpired(cachedResponse)
    }

    private fun isCacheExpired(cachedResponse: AntelopeRexFundEntity): Boolean {
        return cachedResponse.last_updated + CACHE_EXPIRATION_TIME < Clock.System.now().toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}