package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend

sealed interface CreateByFriendBottomSheetUiState {
    data object Loading : CreateByFriendBottomSheetUiState
    data class Ready(
        val encodedRequest: String,
        val isCheckingAccountCreated: Boolean = false,
        val checkAccountCreatedError: Throwable? = null
    ) : CreateByFriendBottomSheetUiState
    data object AccountCreated : CreateByFriendBottomSheetUiState
}