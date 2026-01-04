package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu.rentviarex

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.DecimalFormatRoundingMode
import dev.icerock.moko.resources.desc.ResourceStringDesc

sealed interface RentViaRexUiState {
    data object Loading: RentViaRexUiState
    data class Loaded(
        val amount: String = "", // resource amount
        val amountUnit: String ,
        val rentViaRexRate: BigDecimal? = null,
        val buttonEnabled: Boolean = false,
        val error: ResourceStringDesc? = null,
        val isLoading: Boolean = false,
        val balance: Balance? = null,
        val promptConfirmTransaction: Boolean = false,
        val resourceRequiredBreakdown: FeeBreakdown? = null,
        val resourceRequiredTotal: String? = null,
        val resourceUsedPercentage: Double? = null,
        val nativeToken: String = "",
        val nativeTokenPrecision: Int = 0,
        val inputSectionEnabled : Boolean = false
    ) : RentViaRexUiState {
        private val decimalFormat =
            DecimalFormat("#.##", DecimalFormatRoundingMode.CEILING, ignoreLocale = false)

        val balanceFormatted =
            balance?.let { BalanceFormatter.formatEosBalance(it, ignoreLocale = false) }
        val rentViaRexRateFormatted =
            rentViaRexRate?.roundToDigitPositionAfterDecimalPoint(
                nativeTokenPrecision.toLong(),
                RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
            )?.toStringExpanded() + " $nativeToken/$amountUnit"
        val resourceUsedFormatted = decimalFormat.format(resourceUsedPercentage ?: 0.0) + "%"

    }
    data class Error(val message: String) : RentViaRexUiState
    data class ExecuteRentViaRexSuccess(val txHash : String): RentViaRexUiState
}
