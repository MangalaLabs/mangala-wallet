package com.mangala.wallet.features.chains.antelope_base.data.local.ram

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeRamOhlcEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class AntelopeRamChartLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AntelopeRamChartLocalDataSource {
    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getRamOhlcData(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
    ): List<AntelopeRamOhlcEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectRamOhlcByBlockchainUidAndSamplingInterval(
            blockchain_uid = blockchainType.uid,
            sampling_interval = samplingInterval.value
        ).executeAsList().map {
            AntelopeRamOhlcEntity(
                blockchain_uid = it.blockchain_uid,
                sampling_interval = it.sampling_interval,
                timestamp = it.timestamp,
                open_ = it.open_,
                high = it.high,
                low = it.low,
                close = it.close,
                usd = it.usd,
                volume = it.volume,
                last_updated = it.last_updated
            )
        }
    }

    override fun getRamOhlcDataFlow(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval
    ): Flow<List<AntelopeRamOhlcEntity>> {
        return dbQuery.selectRamOhlcByBlockchainUidAndSamplingInterval(
            blockchain_uid = blockchainType.uid,
            sampling_interval = samplingInterval.value
        ).asFlow().map { it.executeAsList() }.map {
            it.map {
                AntelopeRamOhlcEntity(
                    blockchain_uid = it.blockchain_uid,
                    sampling_interval = it.sampling_interval,
                    timestamp = it.timestamp,
                    open_ = it.open_,
                    high = it.high,
                    low = it.low,
                    close = it.close,
                    usd = it.usd,
                    volume = it.volume,
                    last_updated = it.last_updated
                )
            }
        }.flowOn(ioDispatcher)
    }

    override suspend fun insertRamOhlcData(data: AntelopeRamOhlcEntity) = withContext(ioDispatcher) {
        insertRamOhlcDataWithoutSuspend(data)
    }


    override suspend fun insertRamOhlcData(data: List<AntelopeRamOhlcEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            if (data.isEmpty()) return@transaction
            dbQuery.deleteRamOhlcByBlockchainUidAndSamplingInterval(
                blockchain_uid = data.first().blockchain_uid,
                sampling_interval = data.first().sampling_interval
            )

            data.forEach {
                insertRamOhlcDataWithoutSuspend(it)
            }
        }
    }
    
    override suspend fun clearAllRamOhlc() = withContext(ioDispatcher) {
        dbQuery.clearAllAntelopeRamOhlc()
    }

    private fun insertRamOhlcDataWithoutSuspend(data: AntelopeRamOhlcEntity) {
        dbQuery.insertRamOhlc(
            blockchain_uid = data.blockchain_uid,
            sampling_interval = data.sampling_interval,
            timestamp = data.timestamp,
            open_ = data.open_,
            high = data.high,
            low = data.low,
            close = data.close,
            usd = data.usd,
            volume = data.volume,
            last_updated = data.last_updated
        )
    }
}