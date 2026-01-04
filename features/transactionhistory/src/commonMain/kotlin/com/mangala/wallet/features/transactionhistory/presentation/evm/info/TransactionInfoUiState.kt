package com.mangala.wallet.features.transactionhistory.presentation.evm.info

sealed interface TransactionInfoUiState {
    object Loading: TransactionInfoUiState
    data class Loaded(val uiModel: TransactionInfoUi): TransactionInfoUiState
}