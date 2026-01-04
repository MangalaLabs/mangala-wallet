package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull

sealed interface Step1ImportAccountPrivateKeyScreenUiState {
    data class NotImported(
        val privateKey: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    ) : Step1ImportAccountPrivateKeyScreenUiState {
        val isValid = privateKey.toEosPrivateKeyOrNull() != null
        val isImportButtonEnabled = isValid && !isLoading
    }
    data class Imported(
        val privateKey: String,
        val accountsByAuthorizers: List<AntelopeAccountByAuthorizer>
    ): Step1ImportAccountPrivateKeyScreenUiState
}