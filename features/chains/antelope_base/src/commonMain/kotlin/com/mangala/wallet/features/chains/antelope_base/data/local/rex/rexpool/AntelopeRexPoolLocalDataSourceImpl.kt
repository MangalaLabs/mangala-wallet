package com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexpool

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexPoolEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeRexPoolLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AntelopeRexPoolLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun insertRexPool(rexPool: AntelopeRexPoolEntity) = withContext(ioDispatcher) {
        dbQuery.insertRexPool(
            blockchain_uid = rexPool.blockchain_uid,
            total_lent = rexPool.total_lent,
            total_unlent = rexPool.total_unlent,
            total_rent = rexPool.total_rent,
            total_lendable = rexPool.total_lendable,
            total_rex = rexPool.total_rex,
            namebid_proceeds = rexPool.namebid_proceeds,
            loan_num = rexPool.loan_num,
            last_updated = rexPool.last_updated,
        )
    }

    override suspend fun getTableRowsRexPool(blockchainUid: String): AntelopeRexPoolEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectRexPool(blockchainUid).executeAsOneOrNull()
    }

    override fun getTableRowsRexPoolFlow(blockchainUid: String): Flow<AntelopeRexPoolEntity?> {
        return dbQuery.selectRexPool(blockchainUid).asFlow().map { it.executeAsOneOrNull() }.flowOn(ioDispatcher)
    }
    
    override suspend fun clearAllRexPools() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeRexPools()
    }
}