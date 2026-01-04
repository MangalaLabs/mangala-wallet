package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu

import com.ionspin.kotlin.bignum.decimal.BigDecimal

sealed interface NetAndCpuScreenUiState {
    data object Loading : NetAndCpuScreenUiState
    data class Loaded(
        val powerUpRate: BigDecimal,
        val rexRate: BigDecimal,
        val stakingRate: BigDecimal,
        val nativeCoinPrecision: Int
    ) : NetAndCpuScreenUiState {
        val powerUpRateFormatted = minimumAmountOrValue(powerUpRate)
        val rexRateFormatted = minimumAmountOrValue(rexRate)
        val stakingRateFormatted = minimumAmountOrValue(stakingRate)

        private fun minimumAmountOrValue(value: BigDecimal): String {
            val minimumAmount = BigDecimal.ONE.times(10).pow(-nativeCoinPrecision)
            return if (minimumAmount > value) {
                "<${minimumAmount.toPlainString()}"
            } else {
                value.toPlainString()
            }
        }
    }
}