package com.mangala.wallet.features.transactionhistory.presentation

sealed interface TransactionHistoryItem {
    data class TransactionItem(val transaction: TransactionUi) : TransactionHistoryItem
    sealed interface HeaderItem : TransactionHistoryItem {
        data class Date(val date: String) : HeaderItem
        object Today : HeaderItem
        object Yesterday: HeaderItem
    }
}