package com.mangala.browser.presentation.qr

import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EncodeSignTransactionRequestUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TransactionQrScreenModel(
    signTransactionRequest: SignTransactionRequest,
    encodeSignTransactionRequestUseCase: EncodeSignTransactionRequestUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<TransactionQrScreenUiState> = MutableStateFlow(TransactionQrScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        val encodedSignTransactionRequest = encodeSignTransactionRequestUseCase(signTransactionRequest)
        _uiState.value = TransactionQrScreenUiState.Success(encodedSignTransactionRequest)
    }
}