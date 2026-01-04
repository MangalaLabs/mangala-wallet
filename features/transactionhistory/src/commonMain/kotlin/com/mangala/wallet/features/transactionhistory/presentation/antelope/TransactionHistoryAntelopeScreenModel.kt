package com.mangala.wallet.features.transactionhistory.presentation.antelope

import app.cash.paging.PagingData
import app.cash.paging.insertSeparators
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryItemAntelope
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.getHeaderItem
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.toListActionDataUiModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.formatDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class TransactionHistoryAntelopeScreenModel(
    private val accountName: String,
    private val getActionsPagingUseCase: GetActionsUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
) : BaseScreenModel() {

    private val _listActions =
        MutableStateFlow<Flow<PagingData<TransactionHistoryItemAntelope>>>(emptyFlow())
    val listActions: Flow<PagingData<TransactionHistoryItemAntelope>> = loadActionByAccount()

    private val _startDateFilter = MutableStateFlow<Instant?>(null)
    val startDateFilter: StateFlow<Instant?> get() = _startDateFilter.asStateFlow()

    private val _endDateFilter = MutableStateFlow<Instant?>(null)
    val endDateFilter: StateFlow<Instant?> get() = _endDateFilter.asStateFlow()

    private var _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshListActions() {
        _listActions.value = flowOf(PagingData.empty())
        _listActions.value = loadActionByAccount()
    }

    fun pullToRefresh() {
        screenModelScope.launch {
            _isRefreshing.value = true
            refreshListActions()
            _isRefreshing.value = false
        }
    }

    init {
        refreshListActions()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadActionByAccount(): Flow<PagingData<TransactionHistoryItemAntelope>> = flow {
        val blockchainType = getSelectedNetworkUseCase().blockchainType
        emit(blockchainType)
    }.flatMapLatest { blockchainType ->
        getActionsPagingUseCase.getActionsPaginated(
            blockchainType = blockchainType,
            accountName = accountName,
            limit = 60,
            sort = "desc",
            filter = null,
        ).map { pagingData ->
            pagingData.map { transaction ->
                val listActionDataUiModel = transaction.toListActionDataUiModel(accountName)

                TransactionHistoryItemAntelope.TransactionItem(
                    listActionDataUiModel = listActionDataUiModel
                ) as TransactionHistoryItemAntelope
            }
                .insertSeparators { before: TransactionHistoryItemAntelope?, after: TransactionHistoryItemAntelope? ->
                    val beforeItem =
                        before as? TransactionHistoryItemAntelope.TransactionItem
                    val afterItem =
                        after as? TransactionHistoryItemAntelope.TransactionItem

                    if (beforeItem == null && afterItem != null) {
                        return@insertSeparators afterItem.listActionDataUiModel.actionDataUiModel.actionTraces.first().blockTime?.getHeaderItem()
                    }

                    if (beforeItem != null && afterItem != null) {
                        val beforeDate =
                            beforeItem.listActionDataUiModel.actionDataUiModel.actionTraces.first().blockTime?.getHeaderItem()
                        val afterDate =
                            afterItem.listActionDataUiModel.actionDataUiModel.actionTraces.first().blockTime?.getHeaderItem()

                        if (beforeDate != afterDate) {
                            return@insertSeparators afterDate
                        }
                    }
                    null
                }
        }
    }

    fun onStartDateFilterSelected(startDate: Instant?) {
        _startDateFilter.value = startDate
    }

    fun onEndDateFilterSelected(endDate: Instant?) {
        _endDateFilter.value = endDate
    }

    private fun instantToIsoString(instant: Instant): String {
        // Truncate the Instant to milliseconds
        val truncatedInstant =
            instant.toEpochMilliseconds().let { Instant.fromEpochMilliseconds(it) }
        val localDateTime = truncatedInstant.toLocalDateTime(TimeZone.UTC)
        return "${localDateTime.date}T00:00:00.000Z"
    }
}
