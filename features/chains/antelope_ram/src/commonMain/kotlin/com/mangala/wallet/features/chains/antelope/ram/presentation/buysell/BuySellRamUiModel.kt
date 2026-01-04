package com.mangala.wallet.features.chains.antelope.ram.presentation.buysell

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.antelope.base.model.RamMarketData
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope.ram.presentation.buysell.BuySellRamScreenModel.Companion.BUY_RAM_BYTES_DISPLAY_SCALE
import com.mangala.wallet.features.chains.antelope.ram.presentation.buysell.BuySellRamScreenModel.Companion.BUY_RAM_BYTES_SCALE
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.component.RamSuggestionInputUiModel
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.Constants
import com.mangala.wallet.utils.displayRoundingMode
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.formatWithSign
import com.mangala.wallet.utils.isDecimalPlace
import com.mangala.wallet.utils.isSignificantValue
import com.mangala.wallet.utils.truncateDecimal

data class BuySellRamUiModel(
    internal val accountInfo: AntelopeAccount? = null,
    val eosBalance: Balance = Balance(0.0, ""),
    internal val ramMarketData: RamMarketData? = null,
    val nativeToken: String = "",
    val ramAmount: String = "",
    val nativeCoinAmount: String = "",
    val receiveAccountName: String = "",
    val suggestionInputUiModels: List<RamSuggestionInputUiModel> = emptyList(),
    val isSelectMax: Boolean = false,
    val isInputtingEos: Boolean = false,
    val isBuyForOther: Boolean = false,
    val inputAmountError: WrappedStringResource? = null,
    val resourceRequiredBreakdown: FeeBreakdown? = null,
    val resourceRequiredTotal: String? = null,
    val showPinPrompt: Boolean = false,
    val isLoading: Boolean = false,
    val receiverAccountNameValidationStatus: RecipientValidationStatus = RecipientValidationStatus.NotValidated,
    internal val ram24hPnl: Double? = 0.0,
) {
    private val formattingPrecision = eosBalance.precision.toLong()

    // RAM Balance
    private val totalRam: BigDecimal? = accountInfo?.totalRamKilobytes
    val totalRamString: String? = totalRam?.format(BUY_RAM_BYTES_DISPLAY_SCALE, RoundingMode.ROUND_HALF_FLOOR)

    val ramAvailable: BigDecimal? = accountInfo?.ramAvailableKilobytes
    val ramAvailableString: String? =
        ramAvailable?.format(BUY_RAM_BYTES_DISPLAY_SCALE, RoundingMode.ROUND_HALF_FLOOR)

    private val percentRamUsage = accountInfo?.percentRamUsage
    val percentRamUsageOnTotal = percentRamUsage
    val percentRamUsageOnTotalString = percentRamUsageOnTotal?.times(100)?.format(1, displayRoundingMode)

    // Input
    val formattedInputNativeCoinAmount =
        if (nativeCoinAmount.isBlank()) nativeCoinAmount else nativeCoinAmount.truncateDecimal(
            eosBalance.precision + 1,
            ignoreLocale = true
        ) // for use in input field, to fix issue more decimal places after tapping swap from EOS input to RAM amount input
    val formattedInputRamAmount =
        if (ramAmount.isBlank()) ramAmount else ramAmount.truncateDecimal(
            BUY_RAM_BYTES_SCALE,
            ignoreLocale = true
        )
    val formattedNativeCoinAmount =
        if (nativeCoinAmount.isDecimalPlace()) "0" else if (nativeCoinAmount.isBlank()) nativeCoinAmount else nativeCoinAmount.truncateDecimal(
            eosBalance.precision + 1,
            ignoreParsingZeroValues = isInputtingEos
        )
    val formattedRamAmount =
        if (ramAmount.isDecimalPlace()) "0" else if (ramAmount.isBlank()) ramAmount else ramAmount.truncateDecimal(
            BUY_RAM_BYTES_SCALE,
            isInputtingEos.not()
        )

    val ramPrice = ramMarketData?.price ?: BigDecimal.ZERO
    val ramPriceFormatted = ramMarketData?.price?.format(eosBalance.precision.toLong())
    val formattedEosBalance = BalanceFormatter.formatEosBalance(eosBalance, ignoreLocale = false)
    val ram24hPnlFormatted = (ram24hPnl?.formatWithSign()?.truncateDecimal(2) ?: "0") + "%"

    val isEnableExecuteButton =
        ramAmount.isSignificantValue()
                && nativeCoinAmount.isSignificantValue()
                && inputAmountError == null && isLoading.not()
                && if (isBuyForOther) receiveAccountName.isNotBlank()
                && receiverAccountNameValidationStatus == RecipientValidationStatus.Valid
        else true

    val inputEnabled = accountInfo != null && ramMarketData != null
}
