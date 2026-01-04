package com.mangala.wallet.domain.transaction.history

data class TransactionsList(
    val updatedAt: String,
    val nextUpdateAt: String,
    val currentPage: Int,
    val nextPage: Int,
    val transactions: List<Transaction>,
)