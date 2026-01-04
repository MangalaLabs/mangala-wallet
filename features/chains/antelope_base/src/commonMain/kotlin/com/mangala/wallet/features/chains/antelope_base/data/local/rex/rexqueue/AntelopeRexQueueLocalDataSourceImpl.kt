package com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexqueue

import app.cash.sqldelight.coroutines.asFlow
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexQueueEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeRexQueueLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AntelopeRexQueueLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getRexQueue(accountName: String, blockchainUid: String): List<AntelopeRexQueueEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectRexQueueByAccountNameAndBlockchainUid(
            account_name = accountName,
            blockchain_uid = blockchainUid
        ).executeAsList()
    }

    override fun getRexQueueFlow(
        accountName: String,
        blockchainUid: String
    ): Flow<List<AntelopeRexQueueEntity>> {
        return dbQuery.selectRexQueueByAccountNameAndBlockchainUid(
            account_name = accountName,
            blockchain_uid = blockchainUid
        ).asFlow().map { it.executeAsList() }.flowOn(ioDispatcher)
    }

    override suspend fun insertRexQueue(rexQueueEntity: List<AntelopeRexQueueEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            rexQueueEntity.forEach {
                dbQuery.insertRexQueue(
                    id = it.id,
                    account_name = it.account_name,
                    blockchain_uid = it.blockchain_uid,
                    order_time = it.order_time,
                    proceeds = it.proceeds,
                    rex_requested = it.rex_requested,
                    stake_change = it.stake_change,
                    last_updated = it.last_updated
                )
            }
        }
    }

    override suspend fun deleteRexQueue(accountName: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.deleteRexQueueByAccountNameAndBlockchainUid(
            account_name = accountName,
            blockchain_uid = blockchainUid
        )
    }
}