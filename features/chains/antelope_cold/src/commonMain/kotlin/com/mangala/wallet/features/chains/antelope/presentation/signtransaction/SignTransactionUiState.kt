package com.mangala.wallet.features.chains.antelope.presentation.signtransaction

import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest

sealed interface SignTransactionUiState {
    data class NotSigned(val transaction: SignTransactionRequest): SignTransactionUiState
    data class Signed(val transaction: SignTransactionRequest, val signedTransactionQr: String): SignTransactionUiState
}