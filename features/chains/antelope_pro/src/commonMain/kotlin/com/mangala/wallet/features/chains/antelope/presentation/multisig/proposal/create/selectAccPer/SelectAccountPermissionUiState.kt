package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer

sealed class SelectAccountPermissionUiState {
    object Loading : SelectAccountPermissionUiState()
    data class Success(val data: SelectAccountPermissionUiModel) : SelectAccountPermissionUiState()
    data class Error(val message: String) : SelectAccountPermissionUiState()
}