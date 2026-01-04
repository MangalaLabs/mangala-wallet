package com.mangala.wallet.features.chains.antelope_base.data.local.actions.actiontrace

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper.AntelopeActionTraceTransactionType
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class AntelopeActionTraceLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AntelopeActionTraceLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override fun getAntelopeActionTrace(
        accountName: String,
        blockchainUid: String,
        actionTraceTransactionType: AntelopeActionTraceTransactionType
    ): Flow<List<AntelopeActionTraceEntity>> {
        return dbQuery.selectAntelopeActionTraceByType(
            accountName,
            blockchainUid,
            actionTraceTransactionType.name
        ).asFlow().flowOn(ioDispatcher).mapToList(ioDispatcher)
    }

    override suspend fun insertActionTraces(actions: List<AntelopeActionTraceEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            actions.forEach { action ->
                dbQuery.insertAntelopeActionTrace(
                    trx_id = action.trx_id,
                    account_name = action.account_name,
                    blockchain_uid = action.blockchain_uid,
                    timestamp = action.timestamp,
                    block_num = action.block_num,
                    block_id = action.block_id,
                    act = action.act,
                    receipts = action.receipts,
                    global_sequence = action.global_sequence,
                    producer = action.producer,
                    action_ordinal = action.action_ordinal,
                    creator_action_ordinal = action.creator_action_ordinal,
                    cpu_usage_us = action.cpu_usage_us,
                    net_usage_words = action.net_usage_words,
                    signatures = action.signatures,
                    account_action_seq = action.account_action_seq,
                    action_trace = action.action_trace,
                    block_time = action.block_time,
                    global_action_sequence = action.global_action_sequence,
                    irreversible = action.irreversible,
                    transaction_type = action.transaction_type
                )
            }
        }
    }
}