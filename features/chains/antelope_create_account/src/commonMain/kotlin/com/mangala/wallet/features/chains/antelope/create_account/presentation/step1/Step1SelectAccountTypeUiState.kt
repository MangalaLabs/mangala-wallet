package com.mangala.wallet.features.chains.antelope.create_account.presentation.step1

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.account.domain.eos.AccountNameType

sealed interface Step1SelectAccountTypeUiState {
    val selectedAccountType: AccountNameType

    data class Loading(override val selectedAccountType: AccountNameType = AccountNameType.None) :
        Step1SelectAccountTypeUiState
    data class Success(override val selectedAccountType: AccountNameType = AccountNameType.Standard, val isCreateForFriendAccountAvailable: Boolean) :
        Step1SelectAccountTypeUiState {
        val createButtonEnabled = selectedAccountType != AccountNameType.None && (selectedAccountType == AccountNameType.Friend && !isCreateForFriendAccountAvailable).not()
    }
}