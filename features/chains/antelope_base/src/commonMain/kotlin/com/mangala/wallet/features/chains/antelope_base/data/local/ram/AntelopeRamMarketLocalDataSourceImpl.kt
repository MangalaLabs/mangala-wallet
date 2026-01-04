package com.mangala.wallet.features.chains.antelope_base.data.local.ram

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeRamMarketEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeRamMarketLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AntelopeRamMarketLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getRamPrice(blockchainUid: String): AntelopeRamMarketEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectRamMarketRow(blockchainUid).executeAsOneOrNull()
    }

    override fun getRamPriceFlow(blockchainUid: String): Flow<AntelopeRamMarketEntity?> {
        return dbQuery.selectRamMarketRow(blockchainUid)
            .asFlow()
            .map { it.executeAsOneOrNull() }
            .flowOn(ioDispatcher)
    }

    override suspend fun insertRamPrice(ramMarketData: AntelopeRamMarketEntity) = withContext(ioDispatcher) {
        dbQuery.insertRamMarket(
            blockchain_uid = ramMarketData.blockchain_uid,
            last_updated = ramMarketData.last_updated,
            quote_weight = ramMarketData.quote_weight,
            base_weight = ramMarketData.base_weight,
            quote_balance = ramMarketData.quote_balance,
            base_balance = ramMarketData.base_balance,
            supply = ramMarketData.supply
        )
    }
    
    override suspend fun clearAllRamMarkets() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeRamMarkets()
    }
}