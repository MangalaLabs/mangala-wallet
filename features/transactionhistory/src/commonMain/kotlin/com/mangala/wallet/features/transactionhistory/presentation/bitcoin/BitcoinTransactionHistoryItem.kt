package com.mangala.wallet.features.transactionhistory.presentation.bitcoin

/**
 * Represents different types of items in the Bitcoin transaction history list
 */
sealed interface BitcoinTransactionHistoryItem {
    /**
     * A transaction item in the list
     */
    data class TransactionItem(val transaction: BitcoinTransactionUi) : BitcoinTransactionHistoryItem
    
    /**
     * A header item in the list (Today, Yesterday, or a specific date)
     */
    sealed interface HeaderItem : BitcoinTransactionHistoryItem {
        data class Date(val date: String) : HeaderItem
        object Today : HeaderItem
        object Yesterday: HeaderItem
    }
}