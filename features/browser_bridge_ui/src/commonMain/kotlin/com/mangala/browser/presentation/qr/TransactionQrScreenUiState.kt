package com.mangala.browser.presentation.qr

sealed interface TransactionQrScreenUiState {
    data object Loading : TransactionQrScreenUiState
    data class Success(val qrCode: String) : TransactionQrScreenUiState
    data class Error(val message: String) : TransactionQrScreenUiState
}