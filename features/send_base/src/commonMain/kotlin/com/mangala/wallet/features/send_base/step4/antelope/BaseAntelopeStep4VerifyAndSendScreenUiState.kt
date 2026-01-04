package com.mangala.wallet.features.send_base.step4.antelope

import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.token.AntelopeTokenBalance
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.ui.utils.WrappedStringResource

data class TransactionError(
    val message: WrappedStringResource,
    val type: TransactionErrorType,
    val throwable: Throwable
)

enum class TransactionErrorType {
    INSUFFICIENT_RAM,
    INSUFFICIENT_CPU,
    INSUFFICIENT_NET,
    GENERIC
}

sealed interface BaseAntelopeStep4VerifyAndSendScreenUiState {
    data object Loading: BaseAntelopeStep4VerifyAndSendScreenUiState
    data class Data(
        val contact: ContactEntity?,
        val recipientAccount: String,
        val selectedToken: AntelopeTokenBalance,
        val tokenFiatValue: String,
        val txHash: String?,
        val totalTransactionFiatValue: String,
        val resourceRequiredBreakdown: FeeBreakdown? = null,
        val resourceRequiredTotal: String? = null,
        val promptConfirmTransaction: Boolean = false,
        val isLoading: Boolean,
        val error: String?,
        val errorDialog: TransactionError? = null
    ): BaseAntelopeStep4VerifyAndSendScreenUiState {
        val recipient = contact?.name ?: recipientAccount
        val contactAddress = contact?.address
    }
    data class Error(val message: WrappedStringResource): BaseAntelopeStep4VerifyAndSendScreenUiState
}