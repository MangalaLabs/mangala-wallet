package com.mangala.wallet.features.wallet.presentation.addaccount.evm

data class AddAccountScreenUiModel(val accountName: String = "") {
    val isButtonEnabled = accountName.isNotBlank()
}