package com.mangala.wallet.features.chains.antelope.presentation.giftram

sealed interface GiftRamUiState {
    data class Success(val uiModel: GiftRamUiModel) : GiftRamUiState
    data class ExecuteRamTransferSuccess(val txHash: String) : GiftRamUiState
    data class Error(val message: String) : GiftRamUiState
}