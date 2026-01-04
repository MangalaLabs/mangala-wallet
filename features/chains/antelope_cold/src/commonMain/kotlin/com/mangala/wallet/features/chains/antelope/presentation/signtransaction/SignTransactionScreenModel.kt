package com.mangala.wallet.features.chains.antelope.presentation.signtransaction

import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignTransactionScreenModel(
    private val signTransactionRequest: SignTransactionRequest,
    private val signTransactionUseCase: SignTransactionUseCase
): BaseScreenModel() {

    private val _uiState: MutableStateFlow<SignTransactionUiState> = MutableStateFlow(
        SignTransactionUiState.NotSigned(signTransactionRequest)
    )
    val uiState: StateFlow<SignTransactionUiState> = _uiState.asStateFlow()

    fun signTransaction() {
//        _uiState.update {
//            val oldUiState = it as? SignTransactionUiState.NotSigned ?: return

//            val signedTransaction = signTransactionUseCase(oldUiState.transaction.chainId, oldUiState.transaction.toTransactionAbi(), oldUiState.transaction.)

//            SignTransactionUiState.Signed(
//                transaction = oldUiState.transaction,
//                signedTransactionQr =
//            )
//        }
    }
}