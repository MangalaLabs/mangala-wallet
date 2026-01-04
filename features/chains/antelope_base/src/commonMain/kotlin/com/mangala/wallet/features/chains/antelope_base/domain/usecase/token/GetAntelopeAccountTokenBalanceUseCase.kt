package com.mangala.wallet.features.chains.antelope_base.domain.usecase.token

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.features.chains.antelope_base.domain.repository.token.AntelopeAccountTokenBalanceRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAntelopeAccountTokenBalanceUseCase(
    private val antelopeTokenBalanceRepository: AntelopeAccountTokenBalanceRepository
) {
    @Deprecated("Use invokeFlow instead")
    suspend operator fun invoke(accountName: String, blockchainType: BlockchainType, forceRefresh: Boolean): Result<List<AntelopeTokenBalance>> {
        val response = antelopeTokenBalanceRepository.getAccountTokenBalance(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        )

        // Filter out EOS token, for some reason the token balances API for Jungle includes EOS
        if (blockchainType == BlockchainType.EosJungleTestnet && response.isSuccess) {
            return response.map { it.filter { it.symbol != "EOS" } }
        }

        return response
    }

    suspend fun invokeFlow(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeTokenBalance>?>> {
        return antelopeTokenBalanceRepository.getAccountTokenBalanceFlow(
            accountName = accountName,
            blockchainType = blockchainType,
            forceRefresh = forceRefresh
        ).map {
            it.map {
                // Filter out EOS token, for some reason the token balances API for Jungle includes EOS
                if (blockchainType == BlockchainType.EosJungleTestnet && it != null) {
                    it.filter { it.symbol != "EOS" }
                } else {
                    it
                }
            }
        }
    }
}