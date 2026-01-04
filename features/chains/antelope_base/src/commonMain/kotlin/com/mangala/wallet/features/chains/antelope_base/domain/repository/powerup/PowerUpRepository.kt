package com.mangala.wallet.features.chains.antelope_base.domain.repository.powerup

import com.mangala.wallet.features.chains.antelope_base.domain.model.powerup.AntelopeTableRowPowerUpInfo
import com.mangala.wallet.model.blockchain.BlockchainType

interface PowerUpRepository {

    suspend fun getTableRowsPowerUp(
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
    ): Result<AntelopeTableRowPowerUpInfo?>
}