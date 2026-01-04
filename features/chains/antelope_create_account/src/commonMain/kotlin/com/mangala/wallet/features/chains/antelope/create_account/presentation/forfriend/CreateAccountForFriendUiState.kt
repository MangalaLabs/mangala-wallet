package com.mangala.wallet.features.chains.antelope.create_account.presentation.forfriend

import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount

sealed interface CreateAccountForFriendUiState {
    data class Initial(
        val createAccountForFriendRequest: CreateAccountForFriendRequest
    ) : CreateAccountForFriendUiState
    data object NoAccount: CreateAccountForFriendUiState
    data class Loaded(
        val createAccountForFriendRequest: CreateAccountForFriendRequest,
        val accounts: List<AntelopeAccount>,
        val selectedAccountIndex: Int,
        val isLoading: Boolean = false,
        val error: String? = null,
        val promptConfirmTransaction: Boolean = false,
        val resourceRequiredBreakdown: FeeBreakdown? = null,
        val resourceRequiredTotal: String? = null,
    ): CreateAccountForFriendUiState {
        val selectedAccount = accounts.getOrNull(selectedAccountIndex)
    }
    data class Created(val txHash: String) : CreateAccountForFriendUiState
}