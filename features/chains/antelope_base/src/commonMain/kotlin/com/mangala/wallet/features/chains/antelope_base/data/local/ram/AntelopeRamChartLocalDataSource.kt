package com.mangala.wallet.features.chains.antelope_base.data.local.ram

import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.features.chains.antelopebase.AntelopeRamOhlcEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

interface AntelopeRamChartLocalDataSource {
    suspend fun getRamOhlcData(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
    ): List<AntelopeRamOhlcEntity>

    fun getRamOhlcDataFlow(
        blockchainType: BlockchainType,
        samplingInterval: SamplingInterval,
    ): Flow<List<AntelopeRamOhlcEntity>>

    suspend fun insertRamOhlcData(data: AntelopeRamOhlcEntity)

    suspend fun insertRamOhlcData(data: List<AntelopeRamOhlcEntity>)
    
    suspend fun clearAllRamOhlc()
}