package com.mangala.wallet.features.chains.antelope.presentation.createkeypair

import com.mangala.wallet.ui.utils.WrappedStringResource

sealed class CreateKeyPairScreenUiState {
    data object KeyNotGenerated : CreateKeyPairScreenUiState()
    data class KeyGenerated(
        val ownerPublicKey: String,
        val ownerPrivateKey: String,
        val activePublicKey: String,
        val activePrivateKey: String,
        val encodedQrCode: String,
        val syncAccountError: WrappedStringResource? = null
    ) : CreateKeyPairScreenUiState()
    data class AccountSynced(
        val accountName: String,
        val ownerPublicKey: String,
        val activePublicKey: String,
        val error: WrappedStringResource? = null
    ) : CreateKeyPairScreenUiState()
}