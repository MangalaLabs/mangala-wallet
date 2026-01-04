package com.mangala.wallet.features.wallet.presentation.addaccount.bitcoin

data class BitcoinCreateAccountScreenUiModel(val accountName: String = "") {
    val isButtonEnabled = accountName.isNotBlank()
}