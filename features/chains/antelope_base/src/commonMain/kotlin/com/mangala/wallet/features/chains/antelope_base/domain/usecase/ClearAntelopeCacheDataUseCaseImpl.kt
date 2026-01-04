package com.mangala.wallet.features.chains.antelope_base.domain.usecase

import com.mangala.wallet.domain.reset.usecases.ClearAntelopeCacheDataUseCase
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.AntelopeActionsLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.actions.abis.AntelopeActionAbiLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.cache.AntelopeRemoteKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.powerup.AntelopePowerUpLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamChartLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamMarketLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.rex.rexpool.AntelopeRexPoolLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearAntelopeCacheDataUseCaseImpl(
    private val antelopeRexPoolLocalDataSource: AntelopeRexPoolLocalDataSource,
    private val antelopeRamMarketLocalDataSource: AntelopeRamMarketLocalDataSource,
    private val antelopePowerUpLocalDataSource: AntelopePowerUpLocalDataSource,
    private val antelopeActionAbiLocalDataSource: AntelopeActionAbiLocalDataSource,
    private val antelopeActionsLocalDataSource: AntelopeActionsLocalDataSource,
    private val antelopeRamChartLocalDataSource: AntelopeRamChartLocalDataSource,
    private val antelopeRemoteKeyLocalDataSource: AntelopeRemoteKeyLocalDataSource
) : ClearAntelopeCacheDataUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            antelopeRexPoolLocalDataSource.clearAllRexPools()
            antelopeRamMarketLocalDataSource.clearAllRamMarkets()
            antelopePowerUpLocalDataSource.clearAllTableRowsPowerUp()
            antelopeActionAbiLocalDataSource.clearAllActionAbi()
            antelopeActionsLocalDataSource.clearAllActions()
            antelopeRamChartLocalDataSource.clearAllRamOhlc()
            antelopeRemoteKeyLocalDataSource.clearAllRemoteKeys()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}