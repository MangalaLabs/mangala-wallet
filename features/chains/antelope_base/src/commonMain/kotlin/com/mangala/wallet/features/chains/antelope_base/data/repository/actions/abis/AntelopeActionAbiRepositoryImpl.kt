package com.mangala.wallet.features.chains.antelope_base.data.repository.actions.abis

import com.mangala.antelope.base.api.model.GetAbiResponse
import com.mangala.antelope.base.api.model.GetAccountRequest
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis.AntelopeActionAbiLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.abis.ActionAbiRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionAbiEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class AntelopeActionAbiRepositoryImpl(
    private val antelopeRemoteDataSource: AntelopeRemoteDataSource,
    private val antelopeActionAbiLocalDataSource: AntelopeActionAbiLocalDataSource
) : ActionAbiRepository {

    override suspend fun getActionsAbi(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>> {
        return cachedResource(
            query = {
                antelopeActionAbiLocalDataSource.getActionAbiByAccountName(accountName)
            },
            fetch = {
                this.fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                this.saveFetchResult(accountName, it)
            },
            shouldFetch = { cachedResponse -> this.shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = {
                it.toAntelopeActionsAbi()

            }
        )
    }


    override suspend fun getActionsAbiFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeActionAbi>?>> {
        return networkBoundResource(
            query = {
                antelopeActionAbiLocalDataSource.getActionAbiByAccountNameFlow(accountName)
            },
            fetch = {
                this.fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                if (it != null) {
                    this.saveFetchResult(accountName, it)
                }
            },
            shouldFetch = { cachedResponse -> this.shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = {
                it.toAntelopeActionsAbi()

            }
        )
    }


    override suspend fun getActionAbiByContractAndActionName(
        accountName: String,
        actionName: String,
        forceRefresh: Boolean,
        blockchainType: BlockchainType,
    ):  Result<List<AntelopeActionAbi>> {
        val result = cachedResource(
            query = {
                antelopeActionAbiLocalDataSource.getActionAbiByAccountNameAndActionName(accountName, actionName)
            },
            fetch = {
                this.fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                this.saveFetchResult(accountName, it)
            },
            shouldFetch = { cachedResponse -> this.shouldFetch(cachedResponse, forceRefresh) },

            entityToDomain = {
                it.toAntelopeActionsAbi()

            }
        )
        return if (result.isSuccess) {
            result.map { list ->
                if (actionName.isEmpty()) {
                    list
                } else {
                    list.filter { it.actionName == actionName }
                }
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown error occurred"))
        }
    }




//            List<AntelopeActionAbi> {
//        return antelopeActionAbiLocalDataSource.getActionAbiByAccountNameAndActionName(
//            accountName,
//            actionName
//        ).toAntelopeActionsAbi()
//    }

    private suspend fun fetch(
        blockchainType: BlockchainType,
        accountName: String
    ) = antelopeRemoteDataSource.getAbi(blockchainType, GetAccountRequest(accountName))


    private suspend fun saveFetchResult(
        accountName: String,
        it: GetAbiResponse
    ) {
        antelopeActionAbiLocalDataSource.deleteActionAbiByAccountName(
            accountName
        )
        it.toAntelopeActionAbiEntities(accountName).let {
            antelopeActionAbiLocalDataSource.insertActionsAbi(it)
        }
    }

    private fun shouldFetch(
        cachedResponse: List<AntelopeActionAbiEntity>,
        forceRefresh: Boolean
    ): Boolean {
        return cachedResponse.isEmpty() || forceRefresh || this.isCacheExpired(cachedResponse)
    }

    private fun isCacheExpired(cachedResponse: List<AntelopeActionAbiEntity>): Boolean {
        return cachedResponse.minOf { it.created_at } + CACHE_EXPIRATION_TIME < Clock.System.now()
            .toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 60 * 24 // 1 day
    }
}