package com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram

import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamChartRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.times

class GetRamChartUseCase(private val antelopeRamChartRepository: AntelopeRamChartRepository) {
    suspend fun getOhlc(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval
    ): Result<AntelopeRamOhlcData> {
        val endInstant = Clock.System.now()
        val endTimestamp = endInstant.toEpochMilliseconds()
        val startTimestamp = getStartTime(samplingInterval, endInstant)

        return antelopeRamChartRepository.getRamOhlcData(
            blockchainType,
            samplingInterval,
            startTimestamp,
            endTimestamp
        )
    }

    fun getOhlcFlow(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval
    ): Flow<Resource<AntelopeRamOhlcData>> {
        val endInstant = Clock.System.now()
        val endTimestamp = endInstant.toEpochMilliseconds()
        val startTimestamp = getStartTime(samplingInterval, endInstant)

        return antelopeRamChartRepository.getRamOhlcDataFlow(
            blockchainType,
            samplingInterval,
            startTimestamp,
            endTimestamp
        )
    }

    private fun getStartTime(samplingInterval: SamplingInterval, endInstant: Instant): Long {
        return when (samplingInterval) {
            SamplingInterval.FIVE_MINUTES -> {
                endInstant.minus(1.days).toEpochMilliseconds()
            }

            SamplingInterval.FIFTEEN_MINUTES -> {
                endInstant.minus(2.days).toEpochMilliseconds()
            }

            SamplingInterval.THIRTY_MINUTES -> {
                endInstant.minus(4.days).toEpochMilliseconds()
            }

            SamplingInterval.ONE_HOUR -> {
                endInstant.minus(7.days).toEpochMilliseconds()
            }

            SamplingInterval.FOUR_HOURS -> {
                endInstant.minus(30.days).toEpochMilliseconds()
            }

            SamplingInterval.ONE_DAY -> {
                endInstant.minus(30 * 5.days).toEpochMilliseconds() // 5 months
            }

            SamplingInterval.ONE_WEEK -> {
                endInstant.minus(53 * 7.days).toEpochMilliseconds() // 53 weeks
            }

            SamplingInterval.ONE_MONTH -> {
                1527786000000 // 2018-06-01
            }
        }
    }
}