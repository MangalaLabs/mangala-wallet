package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import com.mangala.wallet.ui.utils.WrappedStringResource

sealed interface ImportAccountScreenUiState {
    val privateKey: String
    val publicKey: String?

    data class NotImported(
        override val privateKey: String = "",
        override val publicKey: String? = null,
        val error: String? = null
    ) : ImportAccountScreenUiState
    data class GeneratedKeyPair(
        override val privateKey: String,
        override val publicKey: String,
        val encodedRequest: String,
        val error: WrappedStringResource? = null
    ): ImportAccountScreenUiState
    data class Imported(
        override val privateKey: String,
        override val publicKey: String,
        val accountName: String
    ): ImportAccountScreenUiState
}