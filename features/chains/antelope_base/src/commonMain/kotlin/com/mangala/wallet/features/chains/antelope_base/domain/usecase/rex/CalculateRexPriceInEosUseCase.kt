package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.rex.AntelopeRexPoolInfo
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CalculateRexPriceInEosUseCase(
    private val getRexPoolInfoUseCase: GetRexPoolInfoUseCase
) {

    suspend operator fun invoke(blockchainType: BlockchainType, forceRefresh: Boolean): Balance? {
        return getRexPoolInfoUseCase(blockchainType, forceRefresh).getOrNull()?.toBalance()
    }

    fun invokeFlow(blockchainType: BlockchainType, forceRefresh: Boolean): Flow<Resource<Balance?>> {
        return getRexPoolInfoUseCase.invokeFlow(blockchainType, forceRefresh).map {
            it.map {
                it?.toBalance()
            }
        }
    }

    private fun AntelopeRexPoolInfo.toBalance(): Balance {
        val totalLent = BalanceFormatter.deserialize(totalLent)
        val totalRex = BalanceFormatter.deserialize(totalRex)
        val totalUnlent = BalanceFormatter.deserialize(totalUnlent)

        val result = (totalLent.amount + totalUnlent.amount) / totalRex.amount

        println("CalculateRexPriceInEosUseCase totalLent: $totalLent totalUnlent $totalUnlent totalRex $totalRex result $result")

        return Balance(result, totalLent.symbol)
    }
}