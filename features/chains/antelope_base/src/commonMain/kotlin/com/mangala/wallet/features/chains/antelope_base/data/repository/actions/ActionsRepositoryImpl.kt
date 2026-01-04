package com.mangala.wallet.features.chains.antelope_base.data.repository.actions

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.antelope.base.api.model.GetAbiResponse
import com.mangala.antelope.base.api.model.GetAccountRequest
import com.mangala.antelope.base.api.model.EosAction
import com.mangala.antelope.base.api.model.GetActionsResponse
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.AntelopeActionsLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis.AntelopeActionAbiLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.abis.toAntelopeActionAbiEntities
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace.AntelopeActionTraceCacheMetadataLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace.AntelopeActionTraceLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.AntelopeActionTraceTransactionType
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.toAntelopeActionTraceEntity
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.toEosAction
import com.mangala.wallet.features.chains.antelope_base.domain.mapper.toActionPagingModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.ActionPagingModel
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.ActionId
import com.mangala.wallet.features.chains.antelope_base.domain.repository.actions.ActionsRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceCacheMetadataEntity
import com.mangala.wallet.local.cache.RemoteKeyLocalDataSource
import com.mangala.wallet.local.cache.TransactionMetadataLocalDataSource
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.parseUtcDateTimeToInstantOrNull
import com.mangala.wallet.utils.ext.toLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class ActionsRepositoryImpl(
    private val eosRemoteDataSource: EosRemoteDataSource,
    private val actionsLocalDataSource: AntelopeActionsLocalDataSource,
    private val remoteKeyLocalDataSource: RemoteKeyLocalDataSource,
    private val localTransactionMetadataDataSource: TransactionMetadataLocalDataSource,
    private val antelopeRemoteDataSource: AntelopeRemoteDataSource,
    private val antelopeActionAbiLocalDataSource: AntelopeActionAbiLocalDataSource,
    private val actionTraceCacheMetadataLocalDataSource: AntelopeActionTraceCacheMetadataLocalDataSource,
    private val actionTraceLocalDataSource: AntelopeActionTraceLocalDataSource,
    private val ignoreUnknownKeyJsonSerializer: Json
) : ActionsRepository {
    override suspend fun getActions(
        blockchainType: BlockchainType,
        accountName: String,
        filter: String?,
        skip: Int?,
        limit: Int?,
        sort: String,
        transferTo: String?,
        transferFrom: String?,
        after: String?,
        before: String?
    ): ApiResponse<GetActionsResponse, CustomError> {
        return eosRemoteDataSource.getActions(
            blockchainType = blockchainType,
            accountName = accountName,
            filter = filter,
            skip = skip,
            limit = limit,
            sort = sort,
            transferTo = transferTo,
            transferFrom = transferFrom,
            after = after,
            before = before
        )
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getPaginatedActionsForAccount(
        blockchainType: BlockchainType,
        accountName: String,
        limit: Int,
        sort: String,
        filter: String?,
        transferTo: String?,
        transferFrom: String?,
        after: String?,
        before: String?
    ): Flow<PagingData<ActionPagingModel>> {
        return Pager(
            config = PagingConfig(pageSize = limit),
            remoteMediator = AntelopeActionsRemoteMediator(
                limit = limit,
                accountName = accountName,
                blockchainType = blockchainType,
                remoteDataSource = eosRemoteDataSource,
                localDataSource = actionsLocalDataSource,
                remoteKeyDataSource = remoteKeyLocalDataSource,
                localTransactionMetadataDataSource = localTransactionMetadataDataSource,
                filter = filter,
                sort = sort,
                transferTo = transferTo,
                transferFrom = transferFrom,
                after = after,
                before = before
            ),
        ) {
            actionsLocalDataSource.getActionPagingSource(
                accountName = accountName,
                blockchainUid = blockchainType.uid
            )
        }.flow.map { pagingData ->
            pagingData.map { entity ->
                entity.toActionPagingModel()
            }
        }
    }

    override suspend fun getActionName(
        accountName: String,
        actionName: String,
        lastAccountCodeUpdatedTimestamp: Long,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<List<AntelopeActionAbi>> {
        return cachedResource(
            query = {
                actionsLocalDataSource.getActionsName(accountName)
            },
            fetch = {
                this.fetch(blockchainType, accountName)
            },
            saveFetchResult = {
                this.saveFetchResult(accountName, lastAccountCodeUpdatedTimestamp, it)
            },
            shouldFetch = { cachedResponse ->
                shouldFetch(
                    cachedResponse,
                    forceRefresh,
                    lastAccountCodeUpdatedTimestamp
                )
            },
            entityToDomain = {
                it.toAntelopeActionsAbi()
            }
        )
    }

    private suspend fun fetch(
        blockchainType: BlockchainType,
        accountName: String
    ) = antelopeRemoteDataSource.getAbi(
        blockchainType,
        GetAccountRequest(accountName)
    )


    private suspend fun saveFetchResult(
        accountName: String,
        lastAccountCodeUpdatedTimestamp: Long,
        it: GetAbiResponse
    ) {
        actionsLocalDataSource.deleteActionsByAccountName(
            accountName
        )
        antelopeActionAbiLocalDataSource.deleteActionAbiByAccountName(
            accountName
        )
        it.toAntelopeActionEntities(accountName, lastAccountCodeUpdatedTimestamp).let {
            actionsLocalDataSource.insertActionsByContract(it)
        }
        it.toAntelopeActionAbiEntities(accountName).let {
            antelopeActionAbiLocalDataSource.insertActionsAbi(it)
        }
    }


    private fun shouldFetch(
        cachedResponse: List<AntelopeActionEntity>,
        forceRefresh: Boolean,
        lastTimeUpdatedCode: Long
    ): Boolean {
        return cachedResponse.isEmpty() || forceRefresh ||
                cachedResponse.first().lastTimeUpdatedCode < lastTimeUpdatedCode
    }


    override suspend fun getBuyRamTransferActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return getActionTraceNetworkBoundResource(
            accountName = accountName,
            blockchainType = blockchainType,
            transactionType = AntelopeActionTraceTransactionType.RAM_BUY_TRANSFER,
            transferTo = SystemContracts.RAM,
            transferFrom = accountName,
            forceRefresh = forceRefresh
        )
    }

    override suspend fun getSellRamTransferActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return getActionTraceNetworkBoundResource(
            accountName = accountName,
            blockchainType = blockchainType,
            transactionType = AntelopeActionTraceTransactionType.RAM_SELL_TRANSFER,
            transferTo = accountName,
            transferFrom = SystemContracts.RAM,
            forceRefresh = forceRefresh
        )
    }

    override suspend fun getRamFeeActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return getActionTraceNetworkBoundResource(
            accountName = accountName,
            blockchainType = blockchainType,
            transactionType = AntelopeActionTraceTransactionType.RAM_FEE_TRANSFER,
            transferFrom = accountName,
            transferTo = SystemContracts.RAM_FEE,
            forceRefresh = forceRefresh
        )
    }

    override suspend fun getLogRamActions(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        return getActionTraceNetworkBoundResource(
            accountName = accountName,
            blockchainType = blockchainType,
            transactionType = AntelopeActionTraceTransactionType.LOG_RAM,
            filter = listOf(
                ActionId.BUY_RAM,
                ActionId.BUY_RAM_BYTES,
                ActionId.SELL_RAM,
                ActionId.LOG_BUY_RAM,
                ActionId.LOG_SELL_RAM,
                ActionId.RAM_BURN,
                ActionId.RAM_TRANSFER
            ).joinToString(","),
            forceRefresh = forceRefresh
        )
    }

    override suspend fun getLast24hLogRamChangeAction(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        val now = Clock.System.now()
        val last24hTimestampBefore = (now - 2.days).toString()
        val last24hTimestampAfter = (now - 1.days).toString()

        val metadata = actionTraceCacheMetadataLocalDataSource.getActionTraceCacheMetadata(
            accountName,
            blockchainType.uid,
            AntelopeActionTraceTransactionType.LOG_RAM_CHANGE
        )

        return networkBoundResource(
            query = {
                actionTraceLocalDataSource.getAntelopeActionTrace(
                    accountName,
                    blockchainType.uid,
                    AntelopeActionTraceTransactionType.LOG_RAM_CHANGE
                )
            },
            fetch = {
                eosRemoteDataSource.getActions(
                    blockchainType,
                    accountName,
                    limit = LIMIT,
                    sort = SORT,
                    skip = null,
                    transferTo = null,
                    transferFrom = null,
                    filter = ActionId.LOG_RAM_CHANGE,
                    after = last24hTimestampAfter,
                    before = last24hTimestampBefore
                )
            },
            saveFetchResult = { dto ->
                saveFetchResult(
                    dto,
                    accountName,
                    blockchainType,
                    AntelopeActionTraceTransactionType.LOG_RAM_CHANGE
                )
                actionTraceCacheMetadataLocalDataSource.insertActionTraceCacheMetadata(
                    AntelopeActionTraceCacheMetadataEntity(
                        account_name = accountName,
                        blockchain_uid = blockchainType.uid,
                        transaction_type = AntelopeActionTraceTransactionType.LOG_RAM_CHANGE.name,
                        last_updated = Clock.System.now().toEpochMilliseconds(),
                        lookup_before_timestamp = "",
                        past_data_load_finished = true.toLong()
                    )
                )
            },
            shouldFetch = { cachedResponse ->
                forceRefresh
                    || metadata?.last_updated == null
                    || (Clock.System.now()
                .toEpochMilliseconds() - metadata.last_updated > 5.minutes.inWholeMilliseconds)
            },
            entityToDomain = {
                it.map { entity ->
                    entity.toEosAction(ignoreUnknownKeyJsonSerializer)
                }
            }
        )
    }

    private suspend fun getActionTraceNetworkBoundResource(
        accountName: String,
        blockchainType: BlockchainType,
        transactionType: AntelopeActionTraceTransactionType,
        transferFrom: String? = null,
        transferTo: String? = null,
        filter: String? = null,
        forceRefresh: Boolean
    ): Flow<Resource<List<EosAction>?>> {
        val metadata = actionTraceCacheMetadataLocalDataSource.getActionTraceCacheMetadata(
            accountName,
            blockchainType.uid,
            transactionType
        )

        return networkBoundResource(
            query = {
                actionTraceLocalDataSource.getAntelopeActionTrace(
                    accountName,
                    blockchainType.uid,
                    transactionType
                )
            },
            fetch = {
                var result = eosRemoteDataSource.getActions(
                    blockchainType,
                    accountName,
                    limit = LIMIT,
                    sort = SORT,
                    skip = null,
                    transferTo = transferTo,
                    transferFrom = transferFrom,
                    filter = filter,
                    after = if (forceRefresh) null else metadata?.last_updated?.let { it1 -> Instant.fromEpochMilliseconds(it1).toString() },
                    before = null
                )

                // TODO: Retry loading interrupted items if past_data_load_finished is false
                // beforeQuery will be useful
                val beforeQuery = metadata?.lookup_before_timestamp?.let { it1 ->
                    it1.parseUtcDateTimeToInstantOrNull()?.minus(1.minutes)?.toString()
                }

                if (result is ApiResponse.Success) {
                    var newMetadata = AntelopeActionTraceCacheMetadataEntity(
                        account_name = accountName,
                        blockchain_uid = blockchainType.uid,
                        transaction_type = transactionType.name,
                        last_updated = Clock.System.now().toEpochMilliseconds(),
                        lookup_before_timestamp = result.body.actions?.lastOrNull()?.timestampSimple
                            ?: Clock.System.now().toEpochMilliseconds()
                                .toString(),
                        past_data_load_finished = (result.body.total?.value.orZero() <= LIMIT).toLong() // TODO: This only works for initial load. For interrupted load we can't check like this
                    )
                    actionTraceCacheMetadataLocalDataSource.insertActionTraceCacheMetadata(newMetadata)

                    // Handle case where we don't reach end of pagination yet with our page size
                    while (result is ApiResponse.Success && result.body.total?.value.orZero() > LIMIT) {
                        val newResult = eosRemoteDataSource.getActions(
                            blockchainType,
                            accountName,
                            limit = LIMIT,
                            sort = SORT,
                            skip = null,
                            transferTo = transferTo,
                            transferFrom = transferFrom,
                            filter = filter,
                            after = null,
                            before = result.body.actions?.lastOrNull()?.timestampSimple // Keep it as is, because sometimes actions from same tx can be spread between multiple pages
                        )

                        if (newResult is ApiResponse.Success) {
                            result = result.copy(
                                body = result.body.copy(
                                    actions = result.body.actions.orEmpty() + newResult.body.actions.orEmpty(),
                                    total = newResult.body.total
                                )
                            )
                            newMetadata = newMetadata.copy(
                                lookup_before_timestamp = newResult.body.actions?.lastOrNull()?.timestampSimple
                                    ?: newMetadata.lookup_before_timestamp
                            )
                            actionTraceCacheMetadataLocalDataSource.insertActionTraceCacheMetadata(
                                newMetadata
                            )
                        } else {
                            // Cache whatever we can, and then return result failure
                            saveFetchResult(result.body, accountName, blockchainType, transactionType)
                            return@networkBoundResource newResult
                        }
                    }
                }

                result
            },
            saveFetchResult = { dto ->
                saveFetchResult(dto, accountName, blockchainType, transactionType)
            },
            shouldFetch = { cachedResponse ->
                forceRefresh
                        || metadata == null
                        || metadata.past_data_load_finished == 0L
                        || (Clock.System.now()
                    .toEpochMilliseconds() - metadata.last_updated.orZero() > 5.minutes.inWholeMilliseconds)
            },
            entityToDomain = {
                it.map { entity ->
                    entity.toEosAction(ignoreUnknownKeyJsonSerializer)
                }
            }
        )
    }

    private suspend fun saveFetchResult(
        dto: GetActionsResponse?,
        accountName: String,
        blockchainType: BlockchainType,
        transactionType: AntelopeActionTraceTransactionType
    ) {
        dto?.let {
            actionTraceLocalDataSource.insertActionTraces(
                dto.actions?.toAntelopeActionTraceEntity(
                    accountName = accountName,
                    blockchainUid = blockchainType.uid,
                    transactionType = transactionType,
                    serializingJson = ignoreUnknownKeyJsonSerializer
                ) ?: emptyList()
            )
        }
    }

    companion object {
        const val LIMIT = 1000
        const val SORT = "desc"
    }
}