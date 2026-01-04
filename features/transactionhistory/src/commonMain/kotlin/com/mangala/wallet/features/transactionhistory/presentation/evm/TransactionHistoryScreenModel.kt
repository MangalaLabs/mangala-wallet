package com.mangala.wallet.features.transactionhistory.presentation.evm

import app.cash.paging.PagingData
import app.cash.paging.map // Do not remove this import, even though AS may automatically deletes it
import app.cash.paging.insertSeparators
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.domain.transaction.history.usecases.GetTransactionHistoryUseCase
import com.mangala.wallet.features.transactionhistory.presentation.utils.getFormattedAddress
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.features.transactionhistory.presentation.TransactionHistoryFilters
import com.mangala.wallet.features.transactionhistory.presentation.TransactionHistoryItem
import com.mangala.wallet.features.transactionhistory.presentation.TransactionUi
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.formatDate
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class TransactionHistoryScreenModel(
    private val accountId: String,
    private val getTransactionHistoryUseCase: GetTransactionHistoryUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase
): BaseScreenModel() {

    private val _transactionTypeFilter = MutableStateFlow<TransactionType?>(null)
    val transactionTypeFilter: StateFlow<TransactionType?> get() = _transactionTypeFilter.asStateFlow()

    private val _transactionStatusFilter = MutableStateFlow<TransactionStatus?>(null)
    val transactionStatusFilter: StateFlow<TransactionStatus?> get() = _transactionStatusFilter.asStateFlow()

    private val _startDateFilter = MutableStateFlow<Instant?>(null)
    val startDateFilter: StateFlow<Instant?> get() = _startDateFilter.asStateFlow()

    private val _endDateFilter = MutableStateFlow<Instant?>(null)
    val endDateFilter: StateFlow<Instant?> get() = _endDateFilter.asStateFlow()

    val list: Flow<PagingData<TransactionHistoryItem>> = getTransactionsList()

    private var _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun pullToRefresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
//            delay(3000L)
            _isRefreshing.value = false
        }
    }

    private fun getTransactionsList(): Flow<PagingData<TransactionHistoryItem>> =
        combine(
            transactionTypeFilter,
            transactionStatusFilter,
            startDateFilter,
            endDateFilter,
        ) { transactionTypeFilter, transactionStatusFilter, startDateFilter, endDateFilter ->
            TransactionHistoryFilters(transactionTypeFilter, transactionStatusFilter, startDateFilter, endDateFilter)
        }.flatMapLatest { filters ->
            val (transactionTypeFilter, transactionStatusFilter, startDateFilter, endDateFilter) = filters

            getTransactionHistoryUseCase(
                accountId,
                transactionTypeFilter,
                transactionStatusFilter,
                startDateFilter,
                endDateFilter
            ).map {
                val account = getAccountByIdUseCase(accountId)

                it.map { transaction ->
                    TransactionHistoryItem.TransactionItem(
                        TransactionUi(
                            transactionType = transaction.transactionType,
                            address = transaction.getFormattedAddress().orEmpty(),
                            time = transaction.blockSignedAt.toLocalDateTime(TimeZone.currentSystemDefault()),
                            amount = transaction.getAmountDecimal(account.bip44Address),
                            txHash = transaction.txHash.orEmpty(),
                            transaction = transaction
                        )
                    ) as TransactionHistoryItem
                }
                    .insertSeparators { before: TransactionHistoryItem?, after: TransactionHistoryItem? ->
                        val before = before as TransactionHistoryItem.TransactionItem?
                        val after = after as TransactionHistoryItem.TransactionItem?

                        if (before == null && after != null) {
                            return@insertSeparators after.transaction.time.getHeaderItem()
                        }

                        if (before != null && after != null) {
                            val beforeDate = before.transaction.time.date
                            val afterDate = after.transaction.time.date
                            if (beforeDate != afterDate) {
                                return@insertSeparators after.transaction.time.getHeaderItem()
                            }
                        }

                        null
                    }
            }
        }

    fun onTransactionTypeFilterSelected(transactionType: TransactionType?) {
        _transactionTypeFilter.value = transactionType
    }

    fun onTransactionStatusFilterSelected(transactionStatus: TransactionStatus?) {
        _transactionStatusFilter.value = transactionStatus
    }

    fun onStartDateFilterSelected(startDate: Instant?) {
        _startDateFilter.value = startDate
    }

    fun onEndDateFilterSelected(endDate: Instant?) {
        _endDateFilter.value = endDate
    }

    @Suppress("unused")
    private fun Transaction.getAmountDecimal(address: String): String {
        val valueTransacted = getValueTransacted(address)
        return (valueTransacted.first ?: BigDecimal.ZERO).toPlainString() + " " + valueTransacted.second
    }

    private fun LocalDateTime.getHeaderItem(): TransactionHistoryItem.HeaderItem {
        val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val yesterday = currentDate.minus(1, kotlinx.datetime.DateTimeUnit.DAY)

        return when (date) {
            currentDate -> TransactionHistoryItem.HeaderItem.Today
            yesterday -> TransactionHistoryItem.HeaderItem.Yesterday
            else -> TransactionHistoryItem.HeaderItem.Date(this.formatDate(TimeZone.currentSystemDefault()))
        }
    }
}