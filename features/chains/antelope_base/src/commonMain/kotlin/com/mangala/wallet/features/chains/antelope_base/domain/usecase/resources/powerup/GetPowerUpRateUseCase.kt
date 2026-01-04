/**
Copyright (c) 2021 Greymass Inc. All Rights Reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistribution of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistribution in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.

YOU ACKNOWLEDGE THAT THIS SOFTWARE IS NOT DESIGNED, LICENSED OR INTENDED FOR USE
IN THE DESIGN, CONSTRUCTION, OPERATION OR MAINTENANCE OF ANY MILITARY FACILITY.
 */

// Logic based on https://github.com/wharfkit/resources

package com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.features.chains.antelope_base.domain.model.powerup.AntelopeTableRowPowerUpInfo
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.GetSampleUsageUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.ResourcesConstants
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.millisecondsToSeconds
import com.mangala.wallet.utils.max
import com.mangala.wallet.utils.min
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.datetime.Clock
import kotlin.math.exp
import kotlin.math.pow

class GetPowerUpRateUseCase(
    private val getInfoUseCase: GetInfoUseCase,
    private val getTableRowsPowerUpUseCase: GetTableRowsPowerUpUseCase,
    private val getSampleUsageUseCase: GetSampleUsageUseCase,
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
    ): Result<PowerUpRate> = coroutineScope {
        val networkInfoAsync = async { getInfoUseCase(blockchainType) }
        val powerUpStateAsync = async { getTableRowsPowerUpUseCase(blockchainType, forceRefresh).getOrNull() }
        val sampleUsageAsync = async { getSampleUsageUseCase.getSampleUsageCpu(blockchainType) }

        val networkInfo = networkInfoAsync.await() ?: return@coroutineScope Result.failure(Exception("Cannot get network info"))
        val powerUpState = powerUpStateAsync.await() ?: return@coroutineScope Result.failure(
            Exception("Cannot get power up state")
        )
        val sampleUsage = sampleUsageAsync.await() ?: return@coroutineScope Result.failure(Exception("Cannot get sample usage"))

        val pricePerMs = getCpuPricePerMillisecond(
            sampleUsage.value,
            milliseconds,
            PowerUpStateOptions(
                null,
                networkInfo.virtualBlockCpuLimit,
                networkInfo.virtualBlockNetLimit
            ),
            powerUpState.cpu,
            maxPriceSymbolPrecision = sampleUsage.nativeCoinPrecision
        )

        return@coroutineScope pricePerMs.let {
            Result.success(
                PowerUpRate(
                    it,
                    powerUpState.cpu,
                    sampleUsage,
                    powerUpState.minPowerUpFee
                )
            )
        }
    }

    suspend fun getNetPricePerKb(
        blockchainType: BlockchainType,
        forceRefresh: Boolean,
        kilobytes: BigDecimal = BigDecimal.ONE
    ): Result<PowerUpRate> = coroutineScope {
        val networkInfoAsync = async { getInfoUseCase(blockchainType) }
        val powerUpStateAsync = async { getTableRowsPowerUpUseCase(blockchainType, forceRefresh).getOrNull() }
        val sampleUsageAsync = async { getSampleUsageUseCase.getSampleUsageNet(blockchainType) }

        val networkInfo = networkInfoAsync.await() ?: return@coroutineScope Result.failure(Exception("Cannot get network info"))
        val powerUpState = powerUpStateAsync.await() ?: return@coroutineScope Result.failure(
            Exception("Cannot get power up state")
        )
        val sampleUsage = sampleUsageAsync.await() ?: return@coroutineScope Result.failure(Exception("Cannot get sample usage"))
        val pricePerKb = getNetPricePerKilobytes(
            sampleUsage.value,
            kilobytes,
            PowerUpStateOptions(
                null,
                networkInfo.virtualBlockCpuLimit,
                networkInfo.virtualBlockNetLimit
            ),
            powerUpState.net,
            maxPriceSymbolPrecision = sampleUsage.nativeCoinPrecision
        )

        return@coroutineScope pricePerKb.let {
            Result.success(
                PowerUpRate(
                    it,
                    powerUpState.net,
                    sampleUsage,
                    powerUpState.minPowerUpFee
                )
            )
        }
    }

    // Frac generation by ms (milliseconds)
    fun cpuFracByMs(powerUpRate: PowerUpRate, milliseconds: BigDecimal): BigDecimal {
        return fracByMicrosecond(
            usage = powerUpRate.sampleUsage.value,
            powerUpStateResource = powerUpRate.powerUpStateResource,
            microsecond = milliseconds * 1000
        )
    }

    fun netFracByKilobytes(powerUpRate: PowerUpRate, kilobytes: BigDecimal): BigDecimal {
        return fracByBytes(
            usage = powerUpRate.sampleUsage.value,
            powerUpStateResource = powerUpRate.powerUpStateResource,
            bytes = kilobytes * 1000
        )
    }

    private fun getNetPricePerKilobytes(
        sampleUsage: BigDecimal,
        kilobytes: BigDecimal,
        options: PowerUpStateOptions,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        maxPriceSymbolPrecision: Int,
    ): BigDecimal {
        return getNetPricePerByte(
            sampleUsage,
            kilobytes * 1000,
            options,
            powerUpStateResource,
            maxPriceSymbolPrecision
        )
    }

    private fun getNetPricePerByte(
        sampleUsage: BigDecimal,
        bytes: BigDecimal,
        options: PowerUpStateOptions,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        maxPriceSymbolPrecision: Int
    ): BigDecimal {
        val frac = fracNet(sampleUsage, powerUpStateResource, bytes)
        val utilizationIncrease = utilizationIncrease(powerUpStateResource, frac)
        val adjustedUtilization = determineAdjustedUtilization(options, powerUpStateResource)
        val fee = fee(utilizationIncrease, adjustedUtilization, powerUpStateResource)
        val precision = 10.0.pow(maxPriceSymbolPrecision).toBigDecimal()

        return fee.times(precision)
            .divide(precision, decimalMode.copy(roundingMode = RoundingMode.CEILING))
    }

    private fun getCpuPricePerMillisecond(
        sampleUsage: BigDecimal,
        milliseconds: BigDecimal,
        options: PowerUpStateOptions,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        maxPriceSymbolPrecision: Int
    ): BigDecimal {
        return getPricePerMicroSecond(
            sampleUsage,
            milliseconds * 1000,
            options,
            powerUpStateResource,
            maxPriceSymbolPrecision
        )
    }

    private fun getPricePerMicroSecond(
        sampleUsage: BigDecimal,
        microSeconds: BigDecimal,
        options: PowerUpStateOptions,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        maxPriceSymbolPrecision: Int
    ): BigDecimal {
        val frac = fracCpu(sampleUsage, powerUpStateResource, microSeconds)
        val utilizationIncrease = utilizationIncrease(powerUpStateResource, frac)
        val adjustedUtilization = determineAdjustedUtilization(options, powerUpStateResource)
        val fee = fee(utilizationIncrease, adjustedUtilization, powerUpStateResource)
        val precision = 10.0.pow(maxPriceSymbolPrecision).toBigDecimal()

        return fee.times(precision)
            .divide(precision, decimalMode.copy(roundingMode = RoundingMode.CEILING))
    }

    // Default frac generation by smallest unit type
    private fun fracCpu(
        usage: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        microsecond: BigDecimal
    ): BigDecimal {
        return fracByMicrosecond(usage, powerUpStateResource, microsecond)
    }

    private fun fracNet(
        usage: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        bytes: BigDecimal
    ): BigDecimal {
        return fracByBytes(usage, powerUpStateResource, bytes)
    }

    private fun utilizationIncrease(
        powerUpResourceInfo: AntelopeTableRowPowerUpInfo.Resource,
        frac: BigDecimal
    ): BigDecimal {
        val utilizationIncrease =
            powerUpResourceInfo.weight.multiply(frac).divide(BigDecimal.TEN.pow(15))
        return utilizationIncrease.ceil()
    }

    private fun determineAdjustedUtilization(
        options: PowerUpStateOptions?,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource
    ): BigDecimal {
        val decaySecs = powerUpStateResource.decaySecs
        val utilization = powerUpStateResource.utilization
        val utilizationTimestamp = powerUpStateResource.utilizationTimestamp

        var adjustedUtilization = powerUpStateResource.adjustedUtilization

        if (utilization < adjustedUtilization) {
            val timestamp = options?.timestamp ?: Clock.System.now().toEpochMilliseconds()
                .millisecondsToSeconds()
            val diff = adjustedUtilization - utilization
            var delta =
                diff * exp(-(timestamp - utilizationTimestamp) / decaySecs.toDouble()).toBigDecimal()
            delta = min(max(delta, BigDecimal.ZERO), diff) // Clamp the delta
            adjustedUtilization = utilization + delta
        }

        return adjustedUtilization
    }

    private fun fee(
        utilizationIncrease: BigDecimal,
        adjustedUtilization: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource
    ): BigDecimal {
        val utilization = powerUpStateResource.utilization
        val weight = powerUpStateResource.weight

        var startUtilization = utilization
        val endUtilization = startUtilization + utilizationIncrease

        var fee = BigDecimal.ZERO
        if (startUtilization < adjustedUtilization) {
            val min = min(utilizationIncrease, adjustedUtilization - startUtilization)
            fee += priceFunction(adjustedUtilization, powerUpStateResource) * min.divide(
                weight,
                decimalMode
            )
            startUtilization = adjustedUtilization
        }

        if (startUtilization < endUtilization) {
            fee += priceIntegralDelta(startUtilization, endUtilization, powerUpStateResource)
        }

        return fee
    }

    // Frac generation by ms (milliseconds)
    fun fracByMs(
        usage: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        ms: BigDecimal
    ): BigDecimal {
        return fracByMicrosecond(usage, powerUpStateResource, ms * 1000)
    }

    // Frac generation by μs (microseconds)
    private fun fracByMicrosecond(
        usage: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        microsecond: BigDecimal
    ): BigDecimal {
        val weight = powerUpStateResource.weight
        val frac = microsecondToWeight(usage, microsecond).divide(weight, decimalMode = decimalMode)
        return frac.times(BigDecimal.TEN.pow(15)).floor()
    }

    private fun fracByBytes(
        usage: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        bytes: BigDecimal
    ): BigDecimal {
        val weight = powerUpStateResource.weight
        val frac = bytesToWeight(usage, bytes).divide(weight, decimalMode = decimalMode)
        return frac.times(BigDecimal.TEN.pow(15)).floor()
    }

    // Convert μs (microseconds) to weight
    private fun microsecondToWeight(sample: BigDecimal, us: BigDecimal): BigDecimal {
        return us.divide(sample, decimalMode = decimalMode).times(ResourcesConstants.BN_PRECISION).floor()
    }

    private fun bytesToWeight(sample: BigDecimal, bytes: BigDecimal): BigDecimal {
        return bytes.divide(sample, decimalMode = decimalMode).times(ResourcesConstants.BN_PRECISION).floor()
    }

    private fun priceFunction(
        utilization: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource
    ): BigDecimal {
        val exponent = powerUpStateResource.exponent
        val weight = powerUpStateResource.weight
        val maxPrice = powerUpStateResource.maxPrice.amount.toBigDecimal()
        val minPrice = powerUpStateResource.minPrice.amount.toBigDecimal()

        var price = minPrice
        val newExponent = exponent.minus(BigDecimal.ONE)
        if (newExponent <= 0.0) {
            return maxPrice
        } else {
            val utilWeight = utilization.divide(weight, decimalMode)
            price += (maxPrice - minPrice) * utilWeight.pow(newExponent.longValue(true))
        }
        return price
    }

    private fun priceIntegralDelta(
        startUtilization: BigDecimal,
        endUtilization: BigDecimal,
        powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource
    ): BigDecimal {
        val exponent = powerUpStateResource.exponent
        val weight = powerUpStateResource.weight
        val maxPrice = powerUpStateResource.maxPrice.amount.toBigDecimal()
        val minPrice = powerUpStateResource.minPrice.amount.toBigDecimal()
        val coefficient = (maxPrice - minPrice).divide(exponent, decimalMode)
        val startU = startUtilization.divide(weight, decimalMode)
        val endU = endUtilization.divide(weight, decimalMode)
        val delta = (minPrice * endU) -
                (minPrice * startU) +
                coefficient * endU.pow(exponent.longValue(true)) -
                coefficient * startU.pow(exponent.longValue(true))
        return delta
    }

    data class PowerUpStateOptions(
        val timestamp: Long?,
        val virtualBlockCpuLimit: Int?,
        val virtualBlockNetLimit: Int?
    )

    data class SampleUsage(
        val value: BigDecimal,
        val nativeCoinPrecision: Int
    )

    data class PowerUpRate(
        val rate: BigDecimal,
        val powerUpStateResource: AntelopeTableRowPowerUpInfo.Resource,
        val sampleUsage: SampleUsage,
        val minPowerUpFee: Balance
    )
}