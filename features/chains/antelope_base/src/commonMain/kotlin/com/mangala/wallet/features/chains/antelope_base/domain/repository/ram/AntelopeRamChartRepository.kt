package com.mangala.wallet.features.chains.antelope_base.domain.repository.ram

import com.mangala.antelope.base.model.AntelopeRamOhlcData
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface AntelopeRamChartRepository {
    suspend fun getRamOhlcData(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
        start: Long,
        end: Long
    ): Result<AntelopeRamOhlcData>
    fun getRamOhlcDataFlow(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
        start: Long,
        end: Long
    ): Flow<Resource<AntelopeRamOhlcData>>
}