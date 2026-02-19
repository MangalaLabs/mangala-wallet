package com.mangala.wallet.features.wallet.presentation.addaccount.evm

data class AddAccountScreenUiModel(
    val accountName: String = "",
    val isCreating: Boolean = false
) {
    val isButtonEnabled = accountName.isNotBlank() && !isCreating
}
