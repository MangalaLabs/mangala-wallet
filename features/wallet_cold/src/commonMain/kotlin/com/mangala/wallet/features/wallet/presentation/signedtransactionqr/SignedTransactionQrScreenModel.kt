package com.mangala.wallet.features.wallet.presentation.signedtransactionqr

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SignAndEncodeTransactionRequestUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignedTransactionQrScreenModel(
    private val request: SignTransactionRequest,
    private val signAndEncodeTransactionRequestUseCase: SignAndEncodeTransactionRequestUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<SignedTransactionQrScreenUiState> = MutableStateFlow(SignedTransactionQrScreenUiState.Loading)
    val uiState: StateFlow<SignedTransactionQrScreenUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = SignedTransactionQrScreenUiState.PendingApprove(
            PendingSignTransactionUiModel(
                recipientAddress = request.transactionTo,
                estimatedGasLimit = request.gasLimit,
                gasPrice = request.gasPrice,
                blockchainType = request.blockchainType,
                contactName = request.contactName,
                contactAddress = request.contactAddress,
                transactionType = request.transactionType,
                qrCode = ""
            ),
        )
    }

    fun generateSignedTransactionQr() {
        screenModelScope.launch {
            val encodedSignedTransaction = signAndEncodeTransactionRequestUseCase(request)
            _uiState.update {
                val oldUiState = (it as? SignedTransactionQrScreenUiState.PendingApprove) ?: return@launch
                oldUiState.copy(pendingSignTransactionUiModel = it.pendingSignTransactionUiModel.copy(qrCode = encodedSignedTransaction))
            }
        }
    }
}