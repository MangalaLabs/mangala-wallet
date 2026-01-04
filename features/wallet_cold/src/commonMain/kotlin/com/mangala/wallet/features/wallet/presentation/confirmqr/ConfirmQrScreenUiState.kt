package com.mangala.wallet.features.wallet.presentation.confirmqr

sealed interface ConfirmQrScreenUiState {
    data object Loading : ConfirmQrScreenUiState
    data class Success(val qrCode: String) : ConfirmQrScreenUiState
}
