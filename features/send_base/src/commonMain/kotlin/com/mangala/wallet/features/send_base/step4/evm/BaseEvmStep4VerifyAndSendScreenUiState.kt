package com.mangala.wallet.features.send_base.step4.evm

import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.utils.formattedAddress

sealed interface BaseEvmStep4VerifyAndSendScreenUiState {
    data object Loading: BaseEvmStep4VerifyAndSendScreenUiState
    data class Data(
        val contact: ContactEntity?,
        val recipientAddress: String,
        val account: AccountModel,
        val selectedToken: TokenBalanceEntity,
        val estimatedGasLimit: Long?,
        val gasPrice: GasPrice?,
        val txHash: String?,
        val transactionFeeOptions: List<EvmFeeOptionUiModel>,
        val selectedTransactionFee: EvmFeeOptionUiModel?,
        val estimateGasErrorVisible: Boolean = false,
        val tokenFiatValue: String,
        val totalTransactionFiatValue: String
    ): BaseEvmStep4VerifyAndSendScreenUiState {
        val recipient = contact?.name ?: recipientAddress
        val addressCompact = contact?.address?.formattedAddress(
            leadingCharsCount = 10,
            trailingCharsCount = 10
        )
    }
    data object Error: BaseEvmStep4VerifyAndSendScreenUiState
}