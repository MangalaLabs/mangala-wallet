package com.mangala.wallet.features.chains.antelope_base.data.repository.powerup

import com.mangala.antelope.base.api.model.BaseGetTableRowsRequest
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.powerup.AntelopePowerUpLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.powerup.AntelopeTableRowPowerUpInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.powerup.PowerUpRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeTableRowsPowerUpEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.utils.cachedResource
import kotlinx.datetime.Clock

internal class PowerUpRepositoryImpl(
    private val antelopeRemoteDataSource: AntelopeRemoteDataSource,
    private val antelopePowerUpLocalDataSource: AntelopePowerUpLocalDataSource
) : PowerUpRepository {
    override suspend fun getTableRowsPowerUp(
        blockchainType: BlockchainType,
        json: Boolean,
        code: String,
        scope: Int,
        table: String,
        lowerBound: String,
        upperBound: String,
        indexPosition: Int,
        keyType: String,
        limit: Int,
        reverse: Boolean,
        showPayer: Boolean,
        forceRefresh: Boolean
    ): Result<AntelopeTableRowPowerUpInfo?> {
        return cachedResource(query = { antelopePowerUpLocalDataSource.getTableRowsPowerUp(blockchainType.uid) },
            fetch = {
                antelopeRemoteDataSource.getTableRowsPowerUp(
                    blockchainType, BaseGetTableRowsRequest(
                        json,
                        code,
                        table,
                        "$scope",
                        lowerBound,
                        upperBound,
                        indexPosition,
                        keyType,
                        limit,
                        reverse,
                        showPayer
                    )
                )
            },
            saveFetchResult = {
                it.toAntelopeTableRowsPowerUpEntity(blockchainType.uid)
                    ?.let { antelopePowerUpLocalDataSource.insertTableRowsPowerUp(it) }

            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it?.toAntelopeTableRowsPowerUpInfo() })
    }

    private fun shouldFetch(
        cachedResponse: AntelopeTableRowsPowerUpEntity?, forceRefresh: Boolean
    ): Boolean {
        return cachedResponse == null || forceRefresh || isCacheExpired(cachedResponse)
    }

    private fun isCacheExpired(cachedResponse: AntelopeTableRowsPowerUpEntity): Boolean {
        return cachedResponse.last_updated + CACHE_EXPIRATION_TIME < Clock.System.now()
            .toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}