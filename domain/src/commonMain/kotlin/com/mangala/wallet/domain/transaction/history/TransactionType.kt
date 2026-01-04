package com.mangala.wallet.domain.transaction.history

enum class TransactionType {
    SEND,
    RECEIVE,
    SWAP,
    CONTRACT_CALL,
    CONTRACT_DEPLOYMENT
}