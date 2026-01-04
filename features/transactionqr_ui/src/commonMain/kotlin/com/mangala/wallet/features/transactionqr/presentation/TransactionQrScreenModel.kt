package com.mangala.wallet.features.transactionqr.presentation

import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.EncodeSignTransactionRequestUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class TransactionQrScreenModel(
    private val signTransactionRequest: SignTransactionRequest,
    encodeSignTransactionRequestUseCase: EncodeSignTransactionRequestUseCase,
    private val parseQRCodeResultUseCase: ParseQRCodeResultUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<TransactionQrScreenUiState> = MutableStateFlow(
        TransactionQrScreenUiState.Loading
    )
    val uiState = _uiState.asStateFlow()

    init {
        val encodedSignTransactionRequest = encodeSignTransactionRequestUseCase(signTransactionRequest)
        _uiState.value = TransactionQrScreenUiState.Success(encodedSignTransactionRequest, null)
    }

    fun onScanQrCodeResult(qrCodeData: String): QrCodeData? {
        val qrCodeResult = parseQRCodeResultUseCase(qrCodeData)

        if (qrCodeResult is QrCodeData.SignedTransaction) {
            if (qrCodeResult.signedTransactionResponse.signTransactionRequest.requestId != signTransactionRequest.requestId) {
                _uiState.value = TransactionQrScreenUiState.Error("QR code scanned is from a different sign request")
                return null
            }

            _uiState.update {
                if (it is TransactionQrScreenUiState.Success) {
                    it.copy(qrCodeData = qrCodeResult.signedTransactionResponse)
                } else {
                    it
                }
            }
        } else {
            _uiState.value = TransactionQrScreenUiState.Error("Invalid QR code")
        }

        return qrCodeResult
    }
}