package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.times
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.GetSampleUsageUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.ResourcesConstants
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex.GetRexPoolInfoUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlin.math.pow

class GetRexRateUseCase(
    private val getRexPoolInfoUseCase: GetRexPoolInfoUseCase,
    private val getSampleUsageUseCase: GetSampleUsageUseCase
) {
    private val decimalMode by lazy {
        DecimalMode.DEFAULT.copy(
            decimalPrecision = ResourcesConstants.CALCULATING_DECIMAL_PRECISION,
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        )
    }

    suspend fun getCpuPricePerMs(
        blockchainType: BlockchainType,
        forceRefresh: Boolean,
        milliseconds: BigDecimal = BigDecimal.ONE
    ): Result<BigDecimal> {
        val rexPoolInfo = getRexPoolInfoUseCase(blockchainType, forceRefresh).getOrNull()
            ?: return Result.failure(
                Throwable("Can't get REX pool info")
            )
        val sampleAccount =
            getSampleUsageUseCase.getSampleUsageCpu(blockchainType) ?: return Result.failure(
                Exception("Cannot get sample account info")
            )

        val cpuSampleUsage = sampleAccount.value

        // Sample token units
        val tokens = 10_000

        val totalRent = BalanceFormatter.deserialize(rexPoolInfo.totalRent)
        val totalUnlent = BalanceFormatter.deserialize(rexPoolInfo.totalUnlent)

        // Spending 1 EOS (10000 units) on REX gives this many tokens
        val bancor = tokens / (totalRent.amount / totalUnlent.amount)

        // The ratio of the number of tokens received vs the sampled values
        val unitPrice = bancor * (cpuSampleUsage.divide(
            ResourcesConstants.BN_PRECISION.toBigDecimal(),
            decimalMode
        ))

        // The token units spent per unit
        val perunit = tokens.toBigDecimal().divide(unitPrice, decimalMode)

        // Multiply the per unit cost by the units requested
        val cost = perunit * milliseconds * 1000

        // Converting to an Asset
        return Result.success(
            cost.divide(
                10.0.pow(sampleAccount.nativeCoinPrecision).toBigDecimal(), decimalMode
            )
        )
    }

    suspend fun getNetPricePerKb(
        blockchainType: BlockchainType,
        forceRefresh: Boolean,
        kilobytes: BigDecimal = BigDecimal.ONE
    ): Result<BigDecimal> {
        val rexPoolInfo = getRexPoolInfoUseCase(blockchainType, forceRefresh).getOrNull()
            ?: return Result.failure(
                Throwable("Can't get REX pool info")
            )
        val sampleAccount =
            getSampleUsageUseCase.getSampleUsageNet(blockchainType) ?: return Result.failure(
                Exception("Cannot get sample account info")
            )

        val cpuSampleUsage = sampleAccount.value

        // Sample token units
        val tokens = 10_000

        val totalRent = BalanceFormatter.deserialize(rexPoolInfo.totalRent)
        val totalUnlent = BalanceFormatter.deserialize(rexPoolInfo.totalUnlent)

        // Spending 1 EOS (10000 units) on REX gives this many tokens
        val bancor = tokens / (totalRent.amount / totalUnlent.amount)

        // The ratio of the number of tokens received vs the sampled values
        val unitPrice = bancor * (cpuSampleUsage.divide(
            ResourcesConstants.BN_PRECISION.toBigDecimal(),
            decimalMode
        ))

        // The token units spent per unit
        val perunit = tokens.toBigDecimal().divide(unitPrice, decimalMode)

        // Multiply the per unit cost by the units requested
        val cost = perunit * kilobytes * 1000

        // Converting to an Asset
        return Result.success(
            cost.divide(
                10.0.pow(sampleAccount.nativeCoinPrecision).toBigDecimal(), decimalMode
            )
        )
    }
}