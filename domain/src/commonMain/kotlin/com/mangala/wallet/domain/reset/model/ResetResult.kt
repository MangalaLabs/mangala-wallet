package com.mangala.wallet.domain.reset.model

sealed class ResetResult {
    object Success : ResetResult()
    data class Error(val message: String) : ResetResult()
}