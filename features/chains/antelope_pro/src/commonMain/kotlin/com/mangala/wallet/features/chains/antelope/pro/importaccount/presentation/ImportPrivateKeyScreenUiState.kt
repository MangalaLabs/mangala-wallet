package com.mangala.wallet.features.chains.antelope.pro.importaccount.presentation

import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer

sealed interface ImportPrivateKeyUiState {
    val contentVisible: Boolean
    val isTermsAgreed: Boolean
    val isKeyVisible: Boolean

    data class InputPhase(
        val privateKey: String = "",
        val validation: PrivateKeyValidation = PrivateKeyValidation(),
        val isValidating: Boolean = false,
        val error: String? = null,
        override val contentVisible: Boolean = false,
        override val isTermsAgreed: Boolean = false,
        override val isKeyVisible: Boolean = false
    ) : ImportPrivateKeyUiState

    data class AccountsFound(
        val privateKey: String,
        val accounts: List<String>,
        val accountsByAuthorizers: List<AntelopeAccountByAuthorizer>,
        val selectedAccount: String? = null,
        val selectedAccountBlockchainUid: String? = null,
        val error: String? = null,
        val hasActiveKey: Boolean = false,
        val hasOwnerKey: Boolean = false,
        override val contentVisible: Boolean = false,
        override val isTermsAgreed: Boolean = false,
        override val isKeyVisible: Boolean = false
    ) : ImportPrivateKeyUiState

    data class Importing(
        val privateKey: String,
        val accountName: String,
        override val contentVisible: Boolean = true,
        override val isTermsAgreed: Boolean = true,
        override val isKeyVisible: Boolean = false
    ) : ImportPrivateKeyUiState

    data class AccountCreated(
        val accountName: String,
        val isPinSetup: Boolean,
        override val contentVisible: Boolean = true,
        override val isTermsAgreed: Boolean = true,
        override val isKeyVisible: Boolean = false
    ) : ImportPrivateKeyUiState

    data class ImportError(
        val privateKey: String = "",
        val error: String,
        override val contentVisible: Boolean = true,
        override val isTermsAgreed: Boolean = false,
        override val isKeyVisible: Boolean = false
    ) : ImportPrivateKeyUiState
}