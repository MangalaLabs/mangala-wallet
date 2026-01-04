package com.mangala.wallet.features.transactionhistory.presentation.antelope

import app.cash.paging.PagingData
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryItemAntelope
import kotlinx.coroutines.flow.Flow

sealed interface TransactionScreenUiState {
    data object Loading : TransactionScreenUiState
    data class Success(val listAction: Flow<PagingData<TransactionHistoryItemAntelope>>?) : TransactionScreenUiState
    data object Error : TransactionScreenUiState
}