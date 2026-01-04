package com.mangala.wallet.features.chains.antelope_base.data.repository.ram

import com.mangala.antelope.base.api.model.eoseyes.RamOhlcResponse
import com.mangala.antelope.base.data.repository.mapper.toAntelopeRamOhlcDataPoints
import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamChartLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.RamChartRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamChartRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeRamOhlcEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class AntelopeRamChartRepositoryImpl(
    private val ramChartRemoteDataSource: RamChartRemoteDataSource,
    private val antelopeRamChartLocalDataSource: AntelopeRamChartLocalDataSource
) : AntelopeRamChartRepository {

    override suspend fun getRamOhlcData(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
        start: Long,
        end: Long
    ): Result<AntelopeRamOhlcData> {
        return cachedResource(
            query = {
                antelopeRamChartLocalDataSource.getRamOhlcData(
                    blockchainType,
                    samplingInterval
                )
            },
            fetch = {
                fetchData(blockchainType, samplingInterval, start, end)
            },
            saveFetchResult = {
                saveFetchResult(it, blockchainType, samplingInterval)
            },
            shouldFetch = {
                shouldFetch(it, samplingInterval)
            },
            entityToDomain = {
                entityToDomain(it, samplingInterval)
            }
        )
    }

    override fun getRamOhlcDataFlow(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
        start: Long,
        end: Long
    ): Flow<Resource<AntelopeRamOhlcData>> = networkBoundResource(
        query = {
            antelopeRamChartLocalDataSource.getRamOhlcDataFlow(
                blockchainType,
                samplingInterval
            )
        },
        fetch = {
            fetchData(blockchainType, samplingInterval, start, end)
        },
        saveFetchResult = {
            saveFetchResult(it, blockchainType, samplingInterval)
        },
        shouldFetch = {
            shouldFetch(it, samplingInterval)
        },
        entityToDomain = {
            entityToDomain(it, samplingInterval)
        }
    )

    private suspend fun fetchData(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
        start: Long,
        end: Long
    ) = ramChartRemoteDataSource.getRamOhlcData(
        blockchainType,
        samplingInterval.value,
        start,
        end
    )

    private suspend fun saveFetchResult(
        it: RamOhlcResponse,
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval
    ) {
        val lastUpdated = Clock.System.now().toEpochMilliseconds()
        antelopeRamChartLocalDataSource.insertRamOhlcData(
            it.toAntelopeRamOhlcDataPoints().map { ramOhlcPoint ->
                AntelopeRamOhlcEntity(
                    timestamp = ramOhlcPoint.date.toEpochMilliseconds(),
                    open_ = ramOhlcPoint.open,
                    high = ramOhlcPoint.high,
                    low = ramOhlcPoint.low,
                    close = ramOhlcPoint.close,
                    usd = ramOhlcPoint.usd,
                    volume = ramOhlcPoint.volume,
                    last_updated = lastUpdated,
                    blockchain_uid = blockchainType.uid,
                    sampling_interval = samplingInterval.value
                )
            }
        )
    }

    private fun shouldFetch(
        it: List<AntelopeRamOhlcEntity>,
        samplingInterval: SamplingInterval
    ) = it.isEmpty() || it.last().last_updated < Clock.System.now()
        .toEpochMilliseconds() - samplingInterval.cacheRefreshInterval

    private fun entityToDomain(
        it: List<AntelopeRamOhlcEntity>,
        samplingInterval: SamplingInterval
    ) = AntelopeRamOhlcData(
        dataPoints = it.map { entity ->
            AntelopeRamOhlcData.OhlcDataPoint(
                date = Instant.fromEpochMilliseconds(entity.timestamp),
                open = entity.open_,
                high = entity.high,
                low = entity.low,
                close = entity.close,
                usd = entity.usd,
                volume = entity.volume
            )
        },
        samplingInterval = samplingInterval
    )
}