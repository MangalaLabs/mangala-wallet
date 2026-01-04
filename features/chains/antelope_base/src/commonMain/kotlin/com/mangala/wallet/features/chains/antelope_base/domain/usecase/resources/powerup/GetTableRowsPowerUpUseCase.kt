package com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup

import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.features.chains.antelope_base.domain.model.powerup.AntelopeTableRowPowerUpInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.powerup.PowerUpRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetTableRowsPowerUpUseCase(private val repository: PowerUpRepository) {
    suspend operator fun invoke(
        blockchainType: BlockchainType, forceRefresh: Boolean
    ): Result<AntelopeTableRowPowerUpInfo?> {
        val result = repository.getTableRowsPowerUp(
            blockchainType,
            true,
            SystemContracts.EOS_SYSTEM_CONTRACT,
            0,
            TableRowsPowerUpConstant.TABLE,
            "",
            "",
            1,
            "",
            1,
            false,
            false,
            forceRefresh
        )
        return result
    }
}