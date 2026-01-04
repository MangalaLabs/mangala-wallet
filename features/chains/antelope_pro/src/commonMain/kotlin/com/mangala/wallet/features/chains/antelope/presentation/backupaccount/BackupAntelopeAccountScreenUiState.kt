package com.mangala.wallet.features.chains.antelope.presentation.backupaccount

import com.memtrip.eos.core.crypto.EosPrivateKey

sealed interface BackupAntelopeAccountScreenUiState {
    data object Loading: BackupAntelopeAccountScreenUiState
    data class Loaded(
        val accountName: String,
        val activeKey: EosPrivateKey?,
        val ownerKey: EosPrivateKey?
    ): BackupAntelopeAccountScreenUiState {
        val activePrivateKey: String
            get() = activeKey?.toString().orEmpty()

        val ownerPrivateKey: String
            get() = ownerKey?.toString().orEmpty()

        val activePublicKey: String
            get() = activeKey?.publicKey.toString()

        val ownerPublicKey: String
            get() = ownerKey?.publicKey.toString()
    }
}