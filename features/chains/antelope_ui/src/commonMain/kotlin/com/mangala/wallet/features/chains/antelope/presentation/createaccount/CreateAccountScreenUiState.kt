package com.mangala.wallet.features.chains.antelope.presentation.createaccount

import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountCharacterValidationResult
import com.mangala.wallet.ui.utils.WrappedStringResource

sealed interface CreateAccountScreenUiState {
    val ownerPublicKey: String
    val activePublicKey: String
    val accountName: String

    data class AccountNameNotConfirmed(
        override val ownerPublicKey: String,
        override val activePublicKey: String,
        override val accountName: String,
        val accountCharacterValidationResult: AccountCharacterValidationResult? = null,
        val isLoading: Boolean = false,
        val error: WrappedStringResource? = null
    ): CreateAccountScreenUiState
    data class SelectNewAccountResourcePaymentOption(
        override val ownerPublicKey: String,
        override val activePublicKey: String,
        override val accountName: String,
        val accountCreationPaymentType: AccountCreationPaymentType?,
        val isLoading: Boolean = false,
        val error: String? = null,
        val encodedSignRequest: String? = null
    ): CreateAccountScreenUiState
    data class AccountCreated(
        override val ownerPublicKey: String,
        override val activePublicKey: String,
        override val accountName: String,
        val encodedSyncAccountRequest: String
    ): CreateAccountScreenUiState
}

sealed interface AccountCreationPaymentType {
    data object InAppPurchase: AccountCreationPaymentType
    data class FromOwnAccount(val ramOption: CreateAccountRamOption): AccountCreationPaymentType
}
