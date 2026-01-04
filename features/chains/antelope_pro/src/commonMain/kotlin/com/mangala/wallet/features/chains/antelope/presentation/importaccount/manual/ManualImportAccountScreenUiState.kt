package com.mangala.wallet.features.chains.antelope.presentation.importaccount.manual

import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountCharacterValidationResult
import com.memtrip.eos.core.crypto.EosPrivateKey.Companion.toEosPrivateKeyOrNull

sealed interface ManualImportAccountScreenUiState {
    val accountLabel: String
    val activePrivateKey: String
    val ownerPrivateKey: String
    val accountName: String

    data class NotImported(
        override val accountLabel: String,
        override val activePrivateKey: String = "",
        val activePrivateKeyError: String? = null,
        override val ownerPrivateKey: String,
        val ownerPrivateKeyError: String? = null,
        override val accountName: String,
        val accountCharacterValidationResult: AccountCharacterValidationResult? = null,
        val error: String? = null
    ) : ManualImportAccountScreenUiState {
        val isValid = activePrivateKeyError == null && ownerPrivateKeyError == null && accountCharacterValidationResult?.isValid == true
    }
    data class Imported(
        override val accountLabel: String,
        override val activePrivateKey: String,
        override val ownerPrivateKey: String,
        override val accountName: String
    ): ManualImportAccountScreenUiState

    val activePublicKey: String? get() = activePrivateKey.toEosPrivateKeyOrNull()?.publicKey?.toString()
    val ownerPublicKey: String? get() = ownerPrivateKey.toEosPrivateKeyOrNull()?.publicKey?.toString()
}