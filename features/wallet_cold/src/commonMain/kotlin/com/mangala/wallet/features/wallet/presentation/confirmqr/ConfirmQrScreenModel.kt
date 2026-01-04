package com.mangala.wallet.features.wallet.presentation.confirmqr

import com.mangala.wallet.features.wallet.presentation.signedtransactionqr.SignedTransactionQrScreenUiState
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfirmQrScreenModel(
    qrCode: String
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<ConfirmQrScreenUiState> = MutableStateFlow(ConfirmQrScreenUiState.Loading)
    val uiState: StateFlow<ConfirmQrScreenUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = ConfirmQrScreenUiState.Success(qrCode)
    }
}