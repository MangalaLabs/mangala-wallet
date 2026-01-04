package com.mangala.wallet.features.wallet.presentation.syncaccount

sealed interface SyncAccountScreenUiState {
    data object Loading: SyncAccountScreenUiState
    data class Success(val qrCode: String): SyncAccountScreenUiState
    data object Error: SyncAccountScreenUiState
}