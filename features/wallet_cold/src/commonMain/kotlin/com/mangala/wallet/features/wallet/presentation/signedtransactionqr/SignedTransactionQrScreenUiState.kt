package com.mangala.wallet.features.wallet.presentation.signedtransactionqr

import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.formattedAddress

sealed interface SignedTransactionQrScreenUiState {
    data object Loading: SignedTransactionQrScreenUiState
    data class PendingApprove(val pendingSignTransactionUiModel: PendingSignTransactionUiModel): SignedTransactionQrScreenUiState
    data class Success(val qrCode: String): SignedTransactionQrScreenUiState
}

data class PendingSignTransactionUiModel(
    val contactName: String?,
    val contactAddress: String?,
    val recipientAddress: String,
//    val account: AccountModel,
//    val selectedToken: TokenBalanceEntity,
    val estimatedGasLimit: Long?,
    val gasPrice: GasPrice?,
//    val selectedTransactionFee: FeeOptionUiModel?,
    val blockchainType: BlockchainType,
    val transactionType: SignTransactionType,
//    val tokenFiatValue: String,
//    val totalTransactionFiatValue: String
    val qrCode : String
) {
    val recipient = contactName ?: recipientAddress
    val addressCompact = contactAddress?.formattedAddress(
        leadingCharsCount = 10,
        trailingCharsCount = 10
    )
}