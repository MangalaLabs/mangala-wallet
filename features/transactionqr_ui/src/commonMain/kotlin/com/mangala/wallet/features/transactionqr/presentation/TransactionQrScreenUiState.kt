package com.mangala.wallet.features.transactionqr.presentation

import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse

sealed interface TransactionQrScreenUiState {
    data object Loading : TransactionQrScreenUiState
    data class Success(
        val qrCode: String,
        val qrCodeData: SignedTransactionResponse?
    ) : TransactionQrScreenUiState
    data class Error(val message: String) : TransactionQrScreenUiState
}