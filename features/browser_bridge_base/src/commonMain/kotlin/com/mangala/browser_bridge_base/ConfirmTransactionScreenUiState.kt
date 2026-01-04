package com.mangala.browser_bridge_base

import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.model.account.domain.AccountModel

sealed interface ConfirmTransactionScreenUiState {
    data object Loading: ConfirmTransactionScreenUiState
    data class Data(
        val account: AccountModel,
        val estimatedGasLimit: Long?,
        val gasValue: String,
        val gasPrice: GasPrice?,
        val txHash: String?,
        val selectedTransactionFee: FeeOptionUiModel?,
        val transactionFeeOptions: List<FeeOptionUiModel>,
        val estimateGasErrorVisible: Boolean = false
    ): ConfirmTransactionScreenUiState
}