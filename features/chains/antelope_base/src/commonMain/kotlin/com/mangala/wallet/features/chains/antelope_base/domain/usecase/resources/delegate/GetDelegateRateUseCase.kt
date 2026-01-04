package com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.delegate

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.antelope.base.api.model.ResourceLimit
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.GetSampleUsageUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.ResourcesConstants.CALCULATING_DECIMAL_PRECISION
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toBigDecimal
import com.mangala.wallet.utils.ext.weiToEth

class GetDelegateRateUseCase(
    private val getSampleUsageUseCase: GetSampleUsageUseCase
) {
    suspend fun getCpuPricePerMs(blockchainType: BlockchainType): Result<BigDecimal> {
        val sampleAccount =
            getSampleUsageUseCase.getSampleAccountInfo(blockchainType) ?: return Result.failure(
                Exception("Cannot get sample account info")
            )

        return calculatePrice(
            sampleAccount.cpuWeight,
            sampleAccount.cpuLimit,
            sampleAccount.coreLiquidBalance,
            MICROSECONDS_PER_MS
        )
    }

    suspend fun getNetPricePerKb(blockchainType: BlockchainType): Result<BigDecimal> {
        val sampleAccount =
            getSampleUsageUseCase.getSampleAccountInfo(blockchainType) ?: return Result.failure(
                Exception("Cannot get sample account info")
            )

        return calculatePrice(
            sampleAccount.netWeight,
            sampleAccount.netLimit,
            sampleAccount.coreLiquidBalance,
            BYTES_PER_KB
        )
    }

    private fun calculatePrice(
        weight: Long?,
        limit: ResourceLimit?,
        coreLiquidBalance: String?,
        unit: Int
    ): Result<BigDecimal> {
        val precision = BalanceFormatter.deserializeOrNull(coreLiquidBalance.orEmpty())?.precision
            ?: return Result.failure(Exception("Cannot get decimals"))

        val weightConverted = weight?.toBigDecimal()?.weiToEth(precision)
        val limitMax = limit?.max?.toBigDecimal()

        if (weightConverted == null || limitMax == null) return Result.failure(Exception("Cannot get cpu weight or cpu limit max"))

        val result = weightConverted.divide(
            limitMax,
            DecimalMode(
                CALCULATING_DECIMAL_PRECISION,
                roundingMode = RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
            )
        ).times(unit)

        return Result.success(result)
    }

    companion object {
        private const val MICROSECONDS_PER_MS = 1000
        private const val BYTES_PER_KB = 1000
    }
}