package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.selectpaymentaccount

sealed interface SelectPaymentAccountUiState {
    data object Loading : SelectPaymentAccountUiState
    data class Ready(
        val accounts: List<AccountsUiModel>,
        val selectedAccountIndex: Int
    ) : SelectPaymentAccountUiState {
        val selectedAccount get() = accounts[selectedAccountIndex]
    }
}

data class AccountsUiModel(
    val accountName: String,
    val nativeCoinBalance: String?
)