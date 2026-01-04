package com.mangala.wallet.features.chains.antelope_base.data.repository.proposal

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.cache.AntelopeRemoteKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.proposal.ProposalLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.cache.AntelopeRemoteKeyTargetEntity
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ProposalData
import com.mangala.wallet.features.chains.antelopebase.AntelopeProposalEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeRemoteKey
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@ExperimentalPagingApi
class AntelopeProposalRemoteMediator(
    private val accountName: String,
    private val blockchainType: BlockchainType,
    private val remoteDataSource: EosRemoteDataSource,
    private val remoteKeyDataSource: AntelopeRemoteKeyLocalDataSource,
    private val localDataSource: ProposalLocalDataSource,
    private val type: ProposalData.Type,
    private val limit: Int,
) : RemoteMediator<Int, AntelopeProposalEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AntelopeProposalEntity>
    ): RemoteMediatorMediatorResult {
        return try {
            val targetCacheEntity = when (type) {
                ProposalData.Type.PROPOSAL -> AntelopeRemoteKeyTargetEntity.PROPOSAL
                ProposalData.Type.APPROVAL -> AntelopeRemoteKeyTargetEntity.APPROVAL
            }
            coroutineScope {
                val key = when (loadType) {
                    LoadType.REFRESH -> null
                    LoadType.PREPEND ->
                        return@coroutineScope RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = true
                        )

                    LoadType.APPEND -> {
                        val remoteKey =
                            remoteKeyDataSource.getRemoteKeyByQuery(
                                query = accountName,
                                blockchainUid = blockchainType.uid,
                                targetEntity = targetCacheEntity
                            )

                        if (remoteKey == null || remoteKey.end_of_pagination_reached == 1L) {
                            return@coroutineScope RemoteMediatorMediatorResultSuccess(
                                endOfPaginationReached = true
                            )
                        }

                        remoteKey
                    }
                }

                if (loadType == LoadType.REFRESH) {
                    val deleteRemoteKeyDeferred = async {
                        remoteKeyDataSource.deleteRemoteKeyByQuery(
                            query = accountName,
                            blockchainUid = blockchainType.uid,
                            targetEntity = targetCacheEntity
                        )
                    }
                    val invalidateCachedDeferred = async {
                        localDataSource.invalidateProposalCache(
                            accountName = accountName,
                            blockchainType = blockchainType,
                            type = type
                        )
                    }

                    deleteRemoteKeyDeferred.await()
                    invalidateCachedDeferred.await()
                }

                ///////////
                val proposalResponse = remoteDataSource.getListProposals(
                    proposer = if (type == ProposalData.Type.PROPOSAL) accountName else null,
                    blockchainType = blockchainType,
                    skip = key?.last_requested_key?.toInt() ?: 0,
                    limit = limit,
                    executed = false,
                    requested = if (type == ProposalData.Type.APPROVAL) accountName else null
                )

                when {
                    proposalResponse is ApiResponse.Success -> {
                        val data = proposalResponse.body.proposals ?: emptyList()
                        val entityData = data
                            .asSequence()
                            .filter {
                                if (type == ProposalData.Type.APPROVAL)
                                    it.proposer != accountName
                                else true
                            }
                            .map {
                                it.toProposalEntity(
                                    blockchainType = blockchainType,
                                    accountName = accountName
                                )
                            }
                            .toList()
                        val isEndOfPagination = data.size < limit

                        val nextKey = key?.last_requested_key?.toInt()?.plus(limit) ?: 0
                        remoteKeyDataSource.insertOrReplaceRemoteKey(
                            remoteKey = AntelopeRemoteKey(
                                query = accountName,
                                blockchain_uid = blockchainType.uid,
                                last_requested_key = nextKey.toLong(),
                                target_cache_entity = targetCacheEntity,
                                last_updated_at = if (loadType == LoadType.REFRESH) currentTimeInMillis()
                                else key?.last_updated_at ?: currentTimeInMillis(),
                                end_of_pagination_reached = if (isEndOfPagination) 1 else 0
                            )
                        )

                        localDataSource.insertProposals(entityData)

                        RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = isEndOfPagination
                        )
                    }

                    else -> {
                        RemoteMediatorMediatorResultError(Exception("Network error in loading ${type.name} $proposalResponse"))
                    }
                }

            }
        } catch (e: Exception) {
            println("Error in loading antelope ${type.name}: $e")
            RemoteMediatorMediatorResultError(e)
        }
    }

    override suspend fun initialize(): RemoteMediatorInitializeAction {
        val timeNow = currentTimeInMillis()
        val lastSyncedTimestamp = remoteKeyDataSource.getLastUpdateTimeStamp(
            query = accountName,
            blockchainUid = blockchainType.uid,
            targetEntity = when (type) {
                ProposalData.Type.PROPOSAL -> AntelopeRemoteKeyTargetEntity.PROPOSAL
                ProposalData.Type.APPROVAL -> AntelopeRemoteKeyTargetEntity.APPROVAL
            }
        ).orZero()
        val shouldRefresh =
            timeNow - lastSyncedTimestamp > PROPOSAL_CACHE_TIMEOUT_MILLIS

        return if (shouldRefresh) {
            RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    companion object {
        private const val PROPOSAL_CACHE_TIMEOUT_MILLIS = 24 * 60 * 60 * 1000L
    }
}