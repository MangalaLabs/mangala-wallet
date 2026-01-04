package com.mangala.wallet.features.wallet.presentation.syncaccount

sealed interface SyncAccountUiState {
    data object Initial : SyncAccountUiState
    data object Loading : SyncAccountUiState
    data class Error(val message: String) : SyncAccountUiState
    data object Success : SyncAccountUiState
}