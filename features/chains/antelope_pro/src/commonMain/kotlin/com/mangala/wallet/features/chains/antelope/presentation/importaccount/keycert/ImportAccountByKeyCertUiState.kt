package com.mangala.wallet.features.chains.antelope.presentation.importaccount.keycert

import com.mangala.wallet.model.blockchain.BlockchainType

sealed interface ImportAccountByKeyCertUiState {
    data object Loading : ImportAccountByKeyCertUiState
    data class Error(val message: String) : ImportAccountByKeyCertUiState
    data class Success(
        val accountName: String,
        val permissionName: String,
        val blockchainType: BlockchainType,
        val isLoading: Boolean = false
    ) : ImportAccountByKeyCertUiState

    data class CreateSuccess(val isLoading: Boolean = false) : ImportAccountByKeyCertUiState
    data object CreatePermissionSuccess : ImportAccountByKeyCertUiState
}