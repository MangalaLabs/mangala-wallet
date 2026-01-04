package com.mangala.wallet.features.wallet.presentation.syncaccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.wallet.domain.usecases.GetSyncAccountQrUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SyncAccountScreenModel(
    accountId: String,
    getSyncAccountQrUseCase: GetSyncAccountQrUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<SyncAccountScreenUiState> = MutableStateFlow(SyncAccountScreenUiState.Loading)
    val uiState: StateFlow<SyncAccountScreenUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            val qrCode = getSyncAccountQrUseCase(accountId)
            _uiState.value = qrCode?.let { SyncAccountScreenUiState.Success(it) } ?: SyncAccountScreenUiState.Error
        }
    }
}