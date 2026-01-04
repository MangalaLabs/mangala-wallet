package com.mangala.wallet.features.chains.antelope_base.data.local.actions

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionEntity
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionsEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AntelopeActionsLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    AntelopeActionsLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun insertActions(actions: List<AntelopeActionsEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            actions.forEach { action ->
                insertActionAndCombineActionTrace(action)
            }
        }
    }

    override suspend fun insertAction(action: AntelopeActionsEntity) = withContext(ioDispatcher) {
        insertActionAndCombineActionTrace(action)
    }

    override fun getActionPagingSource(
        accountName: String,
        blockchainUid: String
    ): PagingSource<Int, AntelopeActionsEntity> = QueryPagingSource(
        countQuery = dbQuery.countActions(
            accountName = accountName,
            blockchain_uid = blockchainUid,
        ),
        transacter = dbQuery,
        context = Dispatchers.IO,
        queryProvider = { limit, offset ->
            dbQuery.getAntelopeActionsByAccountNamePaged(
                accountName = accountName,
                blockchain_uid = blockchainUid,
                limit = limit,
                offset = offset
            )
        }
    )

    override suspend fun getActionsName(accountName: String): List<AntelopeActionEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getActionsByAccountName(
            accountName
        ).executeAsList()
    }

    override suspend fun deleteActionsByAccountName(accountName: String) = withContext(ioDispatcher) {
        dbQuery.deleteActionsByAccountName(accountName)
    }


    override suspend fun insertActionsByContract(
        antelopeActionEntities: List<AntelopeActionEntity>
    ) = withContext(ioDispatcher) {
        dbQuery.transaction {
            antelopeActionEntities.forEach { antelopeActionEntity ->
                insertAction(antelopeActionEntity)
            }
        }
    }


    override suspend fun clearAllActions() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeActions()
    }

    private fun insertAction(
        antelopeActionEntity: AntelopeActionEntity
    ) {
        dbQuery.insertActions(
            actionName = antelopeActionEntity.actionName,
            accountName = antelopeActionEntity.accountName,
            lastTimeUpdatedCode = antelopeActionEntity.lastTimeUpdatedCode
        )
    }

    private fun insertActionAndCombineActionTrace(action: AntelopeActionsEntity) {
        with(action) {
            dbQuery.transaction {
                val existingRow = dbQuery.getActionTraceFromAntelopeActionByTrxId(action.trxId).executeAsOneOrNull()

                val actionTrace = if (existingRow != null) {
                    // Since paging doesn't allow us to group the PagingData and only allowing us to map by keys, here's a workaround for it
                    // we merge the action traces of the same transaction id to prevent a case where paging loads a part of the transaction, over
                    // multiple pages

                    // Using hash to prevent adding the same action trace multiple times
                    (existingRow + action.actionTrace).distinctBy { it.hashCode() }
                } else {
                    action.actionTrace
                }

                dbQuery.insertAntelopeAction(
                    accountName = accountName,
                    blockchain_uid = blockchain_uid,
                    blockNum = blockNum,
                    trxId = trxId,
                    actionTrace = actionTrace,
                    accountActionSeq = accountActionSeq
                )
            }
        }
    }
}