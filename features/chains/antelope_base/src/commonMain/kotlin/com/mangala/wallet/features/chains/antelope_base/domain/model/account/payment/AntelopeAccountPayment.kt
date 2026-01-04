package com.mangala.wallet.features.chains.antelope_base.domain.model.account.payment

import com.mangala.wallet.model.blockchain.BlockchainType

data class AntelopeAccountPayment(
    val accountName: String,
    val purchaseToken: String,
    val blockchainType: BlockchainType,
    val status: AccountPaymentStatus
) {
    enum class AccountPaymentStatus {
        PENDING,
        COMPLETED
    }
}