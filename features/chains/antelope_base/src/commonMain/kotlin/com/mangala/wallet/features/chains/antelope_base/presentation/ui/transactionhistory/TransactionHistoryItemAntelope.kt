package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

sealed interface TransactionHistoryItemAntelope {
    data class TransactionItem(val listActionDataUiModel: ListActionDataUiModel) :
        TransactionHistoryItemAntelope
    sealed interface HeaderItem : TransactionHistoryItemAntelope {
        data class Date(val date: String) : HeaderItem
        data object Today : HeaderItem
        data object Yesterday : HeaderItem
    }
}