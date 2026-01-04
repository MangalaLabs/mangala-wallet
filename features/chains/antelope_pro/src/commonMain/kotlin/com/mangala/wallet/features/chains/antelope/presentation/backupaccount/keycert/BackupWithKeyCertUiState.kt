package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.keycert

sealed interface BackupWithKeyCertUiState {
    data object Loading : BackupWithKeyCertUiState
    data class Error(val message: String) : BackupWithKeyCertUiState
    data class Success(
        val permissionName: String,
        val accountName: String,
        val keyCertString: String,
        val encryptionWords: String
    ) : BackupWithKeyCertUiState
}