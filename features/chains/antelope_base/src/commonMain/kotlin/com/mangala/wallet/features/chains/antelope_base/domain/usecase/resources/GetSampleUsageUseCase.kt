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

package com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources

import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.api.model.GetAccountResponse
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetPowerUpRateUseCase.SampleUsage
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toBigDecimal

class GetSampleUsageUseCase(
    private val getAccountInfoUseCase: GetAccountInfoUseCase
) {

    suspend fun getSampleUsageCpu(
        blockchainType: BlockchainType,
    ): SampleUsage? {
        val sampleAccount = getSampleAccountInfo(blockchainType) ?: return null
        val microSecond =
            sampleAccount.cpuLimit?.max?.toBigDecimal()?.multiply((ResourcesConstants.BN_PRECISION).toBigDecimal())
                ?: return null

        val cpuWeight = sampleAccount.cpuWeight?.toBigDecimal()
            ?: return null

        val value = microSecond.divide(
            cpuWeight,
            DecimalMode.DEFAULT.copy(
                roundingMode = RoundingMode.CEILING,
                decimalPrecision = ResourcesConstants.CALCULATING_DECIMAL_PRECISION
            )
        )

        return SampleUsage(
            value,
            BalanceFormatter.deserializeOrNull(sampleAccount.coreLiquidBalance.orEmpty())?.precision
                ?: ResourcesConstants.DEFAULT_NATIVE_CURRENCY_PRECISION
        )
    }

    suspend fun getSampleUsageNet(
        blockchainType: BlockchainType,
    ): SampleUsage? {
        val sampleAccount = getSampleAccountInfo(blockchainType) ?: return null
        val byte =
            sampleAccount.netLimit?.max?.toBigDecimal()?.multiply((ResourcesConstants.BN_PRECISION).toBigDecimal())
                ?: return null

        val netWeight = sampleAccount.netWeight?.toBigDecimal()
            ?: return null

        val value = byte.divide(
            netWeight,
            DecimalMode.DEFAULT.copy(
                roundingMode = RoundingMode.CEILING,
                decimalPrecision = ResourcesConstants.CALCULATING_DECIMAL_PRECISION
            )
        )

        return SampleUsage(
            value,
            BalanceFormatter.deserializeOrNull(sampleAccount.coreLiquidBalance.orEmpty())?.precision
                ?: ResourcesConstants.DEFAULT_NATIVE_CURRENCY_PRECISION
        )
    }

    suspend fun getSampleAccountInfo(
        blockchainType: BlockchainType
    ): GetAccountResponse? {
        val sampleAccountBlockchainType = when (blockchainType) {
            BlockchainType.Eos, BlockchainType.EosJungleTestnet -> BlockchainType.Eos
            else -> throw IllegalArgumentException("Invalid blockchain type")
        }

        val sampleAccountName = if (blockchainType == BlockchainType.EosJungleTestnet) {
            ResourcesConstants.EOS_JUNGLE_SAMPLE_ACCOUNT_NAME
        } else {
            ResourcesConstants.EOS_SAMPLE_ACCOUNT_NAME
        }
        return getAccountInfoUseCase(sampleAccountBlockchainType, sampleAccountName)
    }
}