package com.mangala.wallet.features.send.presentation.step4.bitcoin

import com.mangala.wallet.features.chains.ui.BitcoinFeeOptionUiModel
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.contact.ContactEntity
import com.mangala.wallet.model.token.TokenBalanceEntity
import com.mangala.wallet.utils.formattedAddress

sealed interface BitcoinStep4VerifyAndSendScreenUiState {
    data object Loading: BitcoinStep4VerifyAndSendScreenUiState
    data class Data(
        val contact: ContactEntity?,
        val recipientAddress: String,
        val account: AccountModel,
        val selectedToken: TokenBalanceEntity,
        val txHash: String?,
        val transactionFeeOptions: List<BitcoinFeeOptionUiModel>,
        val selectedTransactionFee: BitcoinFeeOptionUiModel?,
        val tokenFiatValue: String,
        val totalTransactionFiatValue: String
    ): BitcoinStep4VerifyAndSendScreenUiState {
        val recipient = contact?.name ?: recipientAddress
        val addressCompact = contact?.address?.formattedAddress(
            leadingCharsCount = 10,
            trailingCharsCount = 10
        ) ?: recipientAddress.formattedAddress(
            leadingCharsCount = 10,
            trailingCharsCount = 10
        )
    }
    data object Error: BitcoinStep4VerifyAndSendScreenUiState
}