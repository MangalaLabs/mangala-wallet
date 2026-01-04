package com.mangala.wallet.wallet.presentation.reset.model

sealed class ResetState {
    data object Idle : ResetState()
    data object Loading : ResetState()
    data object Success : ResetState()
    data class Error(val message: String) : ResetState()
}