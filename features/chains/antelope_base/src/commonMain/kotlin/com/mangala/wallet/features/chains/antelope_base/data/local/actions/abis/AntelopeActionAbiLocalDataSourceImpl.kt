package com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionAbiEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeActionAbiLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AntelopeActionAbiLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries


    override suspend fun insertActionsAbi(actions: List<AntelopeActionAbiEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            actions.forEach { action ->
                insertActionAbiWithoutSuspend(
                    action
                )
            }
        }
    }

    override suspend fun insertActionAbi(action: AntelopeActionAbiEntity) = withContext(ioDispatcher) {
        insertActionAbiWithoutSuspend(action)
    }

    override suspend fun getActionAbiByAccountName(accountName: String): List<AntelopeActionAbiEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getActionAbiByAccountName(accountName).executeAsList()
    }

    override fun getActionAbiByAccountNameFlow(accountName: String):
            Flow<List<AntelopeActionAbiEntity>> {
        println("getActionAbiByAccountNameFlow $accountName")
        val data = dbQuery.getActionAbiByAccountName(accountName).asFlow()
            .map { it.executeAsList() }.flowOn(ioDispatcher)
        return data
    }


    override suspend fun deleteActionAbiByAccountName(accountName: String) = withContext(ioDispatcher) {
        dbQuery.deleteActionAbiByAccountName(accountName)
    }

    override suspend fun getActionAbiByAccountNameAndActionName(
        accountName: String,
        actionName: String
    ): List<AntelopeActionAbiEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getActionAbiByAccountNameAndActionName(accountName, actionName)
            .executeAsList()
    }

    override suspend fun clearAllActionAbi() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeActionAbi()
    }

    private fun AntelopeActionAbiLocalDataSourceImpl.insertActionAbiWithoutSuspend(
        action: AntelopeActionAbiEntity
    ) {
        with(action) {
            dbQuery.insertActionsAbi(
                action_name = action_name,
                account_name = account_name,
                field_name = field_name,
                field_type = field_type,
                created_at = created_at,
                stt = stt,
                is_variant = is_variant
            )
        }
    }
}