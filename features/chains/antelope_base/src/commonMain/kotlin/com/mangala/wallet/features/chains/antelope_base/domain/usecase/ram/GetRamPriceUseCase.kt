package com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram

import com.mangala.antelope.base.model.RamMarketData
import com.mangala.wallet.features.chains.antelope_base.domain.model.ram.AntelopeRamMarketInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamMarketRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.utils.Constants
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.ext.toBigDecimal
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetRamPriceUseCase(
    private val repository: AntelopeRamMarketRepository
) {
    suspend operator fun invoke(blockchainType: BlockchainType, forceRefresh: Boolean): RamMarketData? {
        return withResult(blockchainType, forceRefresh).getOrNull()
    }

    fun invokeFlow(blockchainType: BlockchainType, forceRefresh: Boolean): Flow<Resource<RamMarketData?>> {
        return repository.getRamPriceFlow(blockchainType, forceRefresh).map {
            it.map {
                it.toRamMarketData()
            }
        }
    }

    suspend fun withResult(blockchainType: BlockchainType, forceRefresh: Boolean): Result<RamMarketData> {
        return runCatching {
            repository.getRamPrice(blockchainType, forceRefresh).fold(
                onSuccess = {
                    return it.toRamMarketData()?.let { ramMarketData ->
                        Result.success(ramMarketData)
                    } ?: Result.failure(Exception("Invalid RAM market data"))
                },
                onFailure = {
                    return Result.failure(it)
                }
            )
        }
    }

    private fun AntelopeRamMarketInfo?.toRamMarketData(): RamMarketData? {
        if (this == null) return null

        val quoteBalance = quote.balance.split(" ")
        val quoteSymbol = quoteBalance.getOrNull(1)

        val unallocatedRam = base.balance.split(" ").getOrNull(0)?.toLong()
        val allocatedRam = quoteBalance.getOrNull(0)?.toBigDecimalOrNull()
        val supplyRamCore = supply.split(" ").getOrNull(0)?.toBigDecimalOrNull()

        if (unallocatedRam == null || allocatedRam == null || supplyRamCore == null) {
            return null
        }

        val price = allocatedRam.divide(unallocatedRam.toBigDecimal().div(Constants.BYTES_PER_KB), calculatingDecimalMode)
        return RamMarketData(
            price = price,
            currency = quoteSymbol.orEmpty(),
            unallocatedRam = unallocatedRam,
            eosPool = allocatedRam,
            supplyRamCore = supplyRamCore
        )
    }
}