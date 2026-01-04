package com.mangala.wallet.features.chains.antelope.presentation.importaccount

sealed interface ImportAccountScreenUiState {
    data object NotScanned : ImportAccountScreenUiState
    data class Scanned(
        val publicKey: String,
        val accountName: String,
        val error: String? = null
    ): ImportAccountScreenUiState
    data class Imported(
        val publicKey: String,
        val accountName: String,
        val encodedImportedAccountRequest: String
    ): ImportAccountScreenUiState
}
