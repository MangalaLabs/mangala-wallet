package com.mangala.wallet.features.send_base.pickaccount

sealed interface ReceiveTokenPickAccountScreenUiState {
    object Loading: ReceiveTokenPickAccountScreenUiState
    data class Success(val uiModel: ReceiveTokenPickAccountScreenUiModel): ReceiveTokenPickAccountScreenUiState
    object Error: ReceiveTokenPickAccountScreenUiState
}