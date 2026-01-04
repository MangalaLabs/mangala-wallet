package com.mangala.wallet.features.wallet.presentation.syncaccount

import com.mangala.wallet.features.wallet.domain.usecases.SyncAccountUseCase
import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SyncAccountScreenModel(
    private val syncAccountRequest: SyncAccountRequest,
    private val syncAccountUseCase: SyncAccountUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<SyncAccountUiState> = MutableStateFlow(SyncAccountUiState.Initial)
    val uiState: StateFlow<SyncAccountUiState> = _uiState.asStateFlow()

    fun onClickSyncAccount() {
        val result = syncAccountUseCase(syncAccountRequest)

        _uiState.value = when {
            result.isSuccess -> SyncAccountUiState.Success
            else -> {
                SyncAccountUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}