package com.mangala.wallet.wallet.presentation.restore

sealed interface RestoreRecoveryPhraseScreenUiState {
    data object NoImported : RestoreRecoveryPhraseScreenUiState
    data class Imported(
        val mnemonicWords: List<String>,
        val blockchainUid: String,
        val antelopeAccountName: String?
    ) : RestoreRecoveryPhraseScreenUiState
}