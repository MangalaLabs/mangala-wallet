package com.mangala.wallet.features.swap_base.presentation.preview

import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.ui.TextFieldState

data class PreviewSwapUiModel(
    val estimatedGasLimit: Long? = null,
    val selectedTransactionFeeOption: EvmFeeOptionUiModel? = null,
    val txHash: String? = null,
//    TODO: Implement logic check error code from node response to determine if insufficient coin to pay for gas
    val isInsufficientCoinToPayForGas: Boolean = false
){
    val isEnableSwapConfirmButton get() = estimatedGasLimit != null && selectedTransactionFeeOption != null && isInsufficientCoinToPayForGas.not()
}

data class PreviewSwapApproveUiModel(
    val estimatedGasLimit: Long? = null,
    val selectedTransactionFeeOption: EvmFeeOptionUiModel? = null,
    val isFocus: Boolean = false,
    val spendingCapTextFieldState: TextFieldState = TextFieldState.Empty,
    val isInsufficientCoinToPayForGas: Boolean = false
){
    val isEnableApproveButton get() = (estimatedGasLimit != null && selectedTransactionFeeOption != null && isInsufficientCoinToPayForGas.not() && spendingCapTextFieldState == TextFieldState.Correct) || isFocus
}