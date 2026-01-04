package com.mangala.wallet.features.chains.antelope.ram.presentation.transfer

import com.mangala.wallet.ui.utils.WrappedStringResource

sealed interface RamTransferUiState {
    data class Success(val uiModel: RamTransferUiModel) : RamTransferUiState
    data class ExecuteRamTransferSuccess(val txHash: String) : RamTransferUiState
    data class Error(val message: WrappedStringResource) : RamTransferUiState
}