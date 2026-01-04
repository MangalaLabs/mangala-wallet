package com.mangala.wallet.features.chains.antelope.presentation.permission.updatepermission

import cafe.adriel.voyager.core.model.ScreenModel

sealed class UpdatePermissionScreenUiState {
    object Loading : UpdatePermissionScreenUiState()
    data class Success(val data: String) : UpdatePermissionScreenUiState()
    data class Error(val message: String) : UpdatePermissionScreenUiState()
}

data class EventScreenUiModel(
    val data: Any,
    val currencySymbol: String
) : ScreenModel