package com.mangala.wallet.features.wallet.presentation.addaccount

data class AddAccountScreenUiModel(val accountName: String = "") {
    val isButtonEnabled = accountName.isNotBlank()
}