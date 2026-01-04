package com.mangala.wallet.features.chains.antelope_base.data.local.powerup

import com.mangala.wallet.features.chains.antelopebase.AntelopeTableRowsPowerUpEntity

interface AntelopePowerUpLocalDataSource {
    suspend fun getTableRowsPowerUp(
        blockchainUid: String
    ): AntelopeTableRowsPowerUpEntity?

    suspend fun insertTableRowsPowerUp(tableRowsPowerUpEntities: AntelopeTableRowsPowerUpEntity)
    suspend fun deleteTableRowsPowerUp(blockchainUid: String)
    suspend fun clearAllTableRowsPowerUp()
}