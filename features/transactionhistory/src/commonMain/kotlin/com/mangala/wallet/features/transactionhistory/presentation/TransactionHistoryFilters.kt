package com.mangala.wallet.features.transactionhistory.presentation

import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import kotlinx.datetime.Instant

data class TransactionHistoryFilters(
    val transactionTypeFilter: TransactionType?,
    val transactionStatusFilter: TransactionStatus?,
    val startDateFilter: Instant?,
    val endDateFilter: Instant?
)
