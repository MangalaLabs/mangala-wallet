package com.mangala.wallet.features.transactionhistory.presentation.bitcoin

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import app.cash.paging.PagingData
import app.cash.paging.insertSeparators
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction.GetBitcoinTransactionHistoryUseCase
import com.mangala.wallet.features.transactionhistory.presentation.utils.mapBitcoinTransactionType
import com.mangala.wallet.features.chains.bitcoin.domain.utils.formatBitcoin
import com.mangala.wallet.features.transactionhistory.presentation.utils.getTransactionAmount
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.formatDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class TransactionHistoryBitcoinScreenModel(
    private val bitcoinAddress: String,
    blockchainType: BlockchainType,
    getTransactionHistoryUseCase: GetBitcoinTransactionHistoryUseCase
) : BaseScreenModel() {

    // Filter states
    private val _transactionTypeFilter = MutableStateFlow<TransactionType?>(null)
    val transactionTypeFilter: StateFlow<TransactionType?> get() = _transactionTypeFilter.asStateFlow()

    private val _transactionStatusFilter = MutableStateFlow<TransactionStatus?>(null)
    val transactionStatusFilter: StateFlow<TransactionStatus?> get() = _transactionStatusFilter.asStateFlow()

    private val _startDateFilter = MutableStateFlow<Instant?>(null)
    val startDateFilter: StateFlow<Instant?> get() = _startDateFilter.asStateFlow()

    private val _endDateFilter = MutableStateFlow<Instant?>(null)
    val endDateFilter: StateFlow<Instant?> get() = _endDateFilter.asStateFlow()

    // Refreshing state
    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    private val _transactions = MutableStateFlow<List<BitcoinTransactionUi>>(emptyList())
    val transactions: StateFlow<List<BitcoinTransactionUi>> = _transactions.asStateFlow()

    val list: Flow<PagingData<BitcoinTransactionHistoryItem>> = getTransactionHistoryUseCase(address = bitcoinAddress, blockchainType = blockchainType).map {
        it.map { transaction ->
            BitcoinTransactionHistoryItem.TransactionItem(mapToUiModel(transaction))
        }
            .insertSeparators { before: BitcoinTransactionHistoryItem?, after: BitcoinTransactionHistoryItem? ->
                val before = before as BitcoinTransactionHistoryItem.TransactionItem?
                val after = after as BitcoinTransactionHistoryItem.TransactionItem?

                if (before == null && after != null) {
                    return@insertSeparators after.transaction.time.toLocalDateTime(TimeZone.currentSystemDefault()).getHeaderItem()
                }

                if (before != null && after != null) {
                    val beforeDate = before.transaction.time.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val afterDate = after.transaction.time.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    if (beforeDate != afterDate) {
                        return@insertSeparators after.transaction.time.toLocalDateTime(TimeZone.currentSystemDefault()).getHeaderItem()
                    }
                }

                null
            }
    }

    // Flow for errors
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun pullToRefresh() {
        // Actual refresh logic is already implemented in PagingData
        _isRefreshing.value = true
        screenModelScope.launch {
            try {
                delay(500)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun onTransactionTypeFilterSelected(transactionType: TransactionType?) {
        _transactionTypeFilter.value = transactionType
        // TODO: Implement filtering in paging source
    }

    /**
     * Update transaction status filter
     */
    fun onTransactionStatusFilterSelected(transactionStatus: TransactionStatus?) {
        _transactionStatusFilter.value = transactionStatus
        // TODO: Implement filtering in paging source
    }

    /**
     * Update start date filter
     */
    fun onStartDateFilterSelected(startDate: Instant?) {
        _startDateFilter.value = startDate
        // TODO: Implement filtering in paging source
    }

    /**
     * Update end date filter
     */
    fun onEndDateFilterSelected(endDate: Instant?) {
        _endDateFilter.value = endDate
        // TODO: Implement filtering in paging source
    }
    
    /**
     * Maps a single domain model transaction to UI model
     * Legacy method - will be replaced
     */
    private fun mapToUiModel(transaction: BitcoinTransaction): BitcoinTransactionUi {
        // Determine if this is an incoming or outgoing transaction
        val type = transaction.mapBitcoinTransactionType(bitcoinAddress)
        
        // Get the relevant address (sender or recipient)
        val address = when (type) {
            TransactionType.RECEIVE -> {
                // For incoming, get the first sender address
                transaction.vin.firstOrNull()?.prevout?.scriptpubkeyAddress ?: ""
            }
            else -> {
                // For outgoing, get the first recipient that is not our change address
                transaction.vout.firstOrNull { output ->
                    output.scriptpubkeyAddress != bitcoinAddress
                }?.scriptpubkeyAddress ?: ""
            }
        }
        
        val amount = transaction.getTransactionAmount(type, bitcoinAddress)
        
        val formattedAmount = if (type == TransactionType.RECEIVE)
            "+${amount.formatBitcoin()}"
        else 
            "-${amount.formatBitcoin()}"
        
        // Convert timestamp to instant
        val time = Instant.fromEpochSeconds(transaction.status.block_time ?: Clock.System.now().epochSeconds)
        
        return BitcoinTransactionUi(
            transactionType = type,
            address = address,
            txid = transaction.txid,
            time = time,
            amount = formattedAmount,
            fee = transaction.fee.formatBitcoin(),
            confirmed = transaction.status.confirmed,
            rawTransaction = transaction
        )
    }

    /**
     * Helper function to get the appropriate header item for a given date
     */
    fun LocalDateTime.getHeaderItem(): BitcoinTransactionHistoryItem.HeaderItem {
        val currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val yesterday = currentDate.minus(1, kotlinx.datetime.DateTimeUnit.DAY)

        return when (date) {
            currentDate -> BitcoinTransactionHistoryItem.HeaderItem.Today
            yesterday -> BitcoinTransactionHistoryItem.HeaderItem.Yesterday
            else -> BitcoinTransactionHistoryItem.HeaderItem.Date(this.formatDate(TimeZone.currentSystemDefault()))
        }
    }
}