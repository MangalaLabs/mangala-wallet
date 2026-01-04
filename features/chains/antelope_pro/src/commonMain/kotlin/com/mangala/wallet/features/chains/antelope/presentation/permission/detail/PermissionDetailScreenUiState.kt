package com.mangala.wallet.features.chains.antelope.presentation.permission.detail

import com.mangala.antelope.base.api.model.Permission


sealed class PermissionDetailScreenUiState {
    object Loading : PermissionDetailScreenUiState()
    data class Success(val data: List<Permission>) : PermissionDetailScreenUiState()
    data class Error(val message: String) : PermissionDetailScreenUiState()
}
