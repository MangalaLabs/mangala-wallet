package com.mangala.wallet.features.chains.antelope.presentation.giftram

import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.features.chains.antelope.presentation.giftram.GiftRamScreenModel.Companion.BUY_RAM_BYTES_SCALE
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.component.RamSuggestionInputUiModel
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.isNotNullOrBlank
import com.mangala.wallet.utils.truncateDecimal

data class GiftRamUiModel(
    val accountInfo: AntelopeAccount? = null,
    val resourceRequiredBreakdown: FeeBreakdown? = null,
    val resourceRequiredTotal: String? = null,
    val showPinPrompt: Boolean = false,
    val isLoading: Boolean = false,
    val ramAmountText: String? = null,
    val memoText: String? = null,
    val recipientAccountText: String? = null,
    val suggestionInputUiModels: List<RamSuggestionInputUiModel> = listSuggestionInput,
    val inputAmountError: WrappedStringResource? = null,
    val recipientAccountNameValidationStatus: RecipientValidationStatus = RecipientValidationStatus.NotValidated
) {
    private val totalRam: Double = (accountInfo?.ramQuota?.toDouble() ?: 0.0) / 1024.0
    val ramAvailable: Double = (accountInfo?.ramAvailable?.toDouble() ?: 0.0) / 1024
    val ramAvailableString: String = ramAvailable.toString().truncateDecimal(BUY_RAM_BYTES_SCALE)
    private val ramUsage = (accountInfo?.ramUsage ?: 0) / 1024
    private val percentRamUsage = (ramUsage.toFloat() / totalRam.toFloat())
    val percentRamUsageOnTotal = if (percentRamUsage.isNaN()) 0f else percentRamUsage
    val percentRamUsageOnTotalString = (percentRamUsageOnTotal * 100).toInt().toString()
    val isEnableExecuteButton =
        ramAmountText?.isNotNullOrBlank() == true
                && recipientAccountText?.isNotNullOrBlank() == true
                && isLoading.not()
                && inputAmountError == null
                && recipientAccountNameValidationStatus == RecipientValidationStatus.Valid

    companion object {
        private val listSuggestionInput = listOf(
            RamSuggestionInputUiModel(10, RamSuggestionInputUiModel.Quantity.Percent),
            RamSuggestionInputUiModel(15, RamSuggestionInputUiModel.Quantity.Percent),
            RamSuggestionInputUiModel(25, RamSuggestionInputUiModel.Quantity.Percent),
            RamSuggestionInputUiModel(50, RamSuggestionInputUiModel.Quantity.Percent),
            RamSuggestionInputUiModel(75, RamSuggestionInputUiModel.Quantity.Percent),
            RamSuggestionInputUiModel(100, RamSuggestionInputUiModel.Quantity.Percent)
        )
    }
}