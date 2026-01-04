package com.mangala.wallet.features.transactionhistory.presentation.utils

import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionType

@Suppress("unused")
fun Transaction.getFormattedAddress(): String {
    return when (transactionType) {
        TransactionType.SEND, TransactionType.CONTRACT_CALL -> toAddress
        TransactionType.RECEIVE, TransactionType.SWAP, TransactionType.CONTRACT_DEPLOYMENT -> fromAddress
    }
}