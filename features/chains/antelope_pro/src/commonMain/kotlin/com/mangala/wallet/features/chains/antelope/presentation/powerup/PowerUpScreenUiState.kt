package com.mangala.wallet.features.chains.antelope.presentation.powerup

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.powerup.AntelopeTableRowPowerUpInfo
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetPowerUpRateUseCase
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.DecimalFormatRoundingMode

sealed interface PowerUpScreenUiState {
    data object Loading : PowerUpScreenUiState
    data class Loaded(
        val amount: String = "", // resource amount
        val amountUnit: String,
        val powerUpRate: GetPowerUpRateUseCase.PowerUpRate = defaultPowerUpRate(),
        val powerUpCost: BigDecimal? = null,
        val balance: Balance? = null,
        val error: String? = null,
        val resourceUsedPercentage: Double? = null,
        val buttonEnabled: Boolean = false,
        val isLoading: Boolean = false,
        val promptConfirmTransaction: Boolean = false,
        val resourceRequiredBreakdown: FeeBreakdown? = null,
        val resourceRequiredTotal: String? = null,
        val inputSectionEnabled: Boolean = false
    ) : PowerUpScreenUiState {
        private val decimalFormat =
            DecimalFormat("#.##", DecimalFormatRoundingMode.CEILING, ignoreLocale = false)

        val balanceFormatted =
            balance?.let { BalanceFormatter.formatEosBalance(it, ignoreLocale = false) }
        val powerUpRateFormatted = powerUpRate.rate.roundToDigitPositionAfterDecimalPoint(
            powerUpRate.sampleUsage.nativeCoinPrecision.toLong(),
            RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
        ).toPlainString() + " ${powerUpRate.minPowerUpFee.symbol}/$amountUnit"
        val resourceUsedFormatted = decimalFormat.format(resourceUsedPercentage ?: 0.0) + "%"
    }
    data class ExecutePowerUpSuccess(val txHash: String) : PowerUpScreenUiState
}

private fun defaultPowerUpRate(): GetPowerUpRateUseCase.PowerUpRate {
    return GetPowerUpRateUseCase.PowerUpRate(
        rate = BigDecimal.ZERO,
        minPowerUpFee = Balance(0.0, "", 0),
        sampleUsage = GetPowerUpRateUseCase.SampleUsage( BigDecimal.ZERO, 0),
        powerUpStateResource = AntelopeTableRowPowerUpInfo.Resource(
            weight = BigDecimal.ZERO,
            utilization = BigDecimal.ZERO,
            utilizationTimestamp = 0,
            adjustedUtilization = BigDecimal.ZERO,
            decaySecs = 0,
            minPrice = Balance(0.0, "", 0),
            maxPrice = Balance(0.0, "", 0),
            exponent = BigDecimal.ZERO
        )
    )
}