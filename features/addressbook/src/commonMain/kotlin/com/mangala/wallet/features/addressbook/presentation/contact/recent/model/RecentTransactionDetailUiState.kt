package com.mangala.wallet.features.addressbook.presentation.contact.recent.model

import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel

sealed class RecentTransactionDetailUiState {
    data object Loading : RecentTransactionDetailUiState()

    data class Success(
        val transactionDetail: TransactionDetailModel,
        val txBlockExplorerLink: String,
    ) : RecentTransactionDetailUiState()

    data class Error(
        val exception: Exception,
    ) : RecentTransactionDetailUiState()
}