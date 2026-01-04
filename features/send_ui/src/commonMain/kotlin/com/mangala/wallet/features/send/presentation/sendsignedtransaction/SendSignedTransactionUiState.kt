package com.mangala.wallet.features.send.presentation.sendsignedtransaction

import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse

sealed interface SendSignedTransactionUiState {
    data object Loading : SendSignedTransactionUiState
    data class Success(val signedTransactionResponse: SignedTransactionResponse) : SendSignedTransactionUiState
    data class Error(val message: String) : SendSignedTransactionUiState
}