package com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexfund

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeRexFundEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeRexFundLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AntelopeRexFundLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getRexFund(accountName: String, blockchainUid: String): AntelopeRexFundEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectRexFund(accountName, blockchainUid).executeAsOneOrNull()
    }

    override fun getRexFundFlow(
        accountName: String,
        blockchainUid: String
    ): Flow<AntelopeRexFundEntity?> {
        return dbQuery.selectRexFund(accountName, blockchainUid).asFlow().map { it.executeAsOneOrNull() }.flowOn(ioDispatcher)
    }

    override suspend fun insertRexFund(rexFundEntity: AntelopeRexFundEntity) = withContext(ioDispatcher) {
        dbQuery.insertRexFund(
            account_name = rexFundEntity.account_name,
            blockchain_uid = rexFundEntity.blockchain_uid,
            balance = rexFundEntity.balance,
            last_updated = rexFundEntity.last_updated
        )
    }
}