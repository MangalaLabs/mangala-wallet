package com.mangala.eticket.presentation.event.list

import app.cash.paging.PagingData
import com.mangala.eticket.domain.usecases.event.GetEventsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import commangalaeticketdatabase.EventListEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class EventListScreenModel(
    private val getEventsUseCase: GetEventsUseCase,
    private val categoryId: String?
) : BaseScreenModel() {
    private val _uiState: MutableStateFlow<EventListScreenUIState> =
        MutableStateFlow(EventListScreenUIState.Loading)
    val uiState: StateFlow<EventListScreenUIState> get() = _uiState.asStateFlow()

    private var _categoriesIdFilter = categoryId?.let { MutableStateFlow(mutableListOf(categoryId)) } ?: MutableStateFlow(mutableListOf())
    val categoriesIdFilter: StateFlow<MutableList<String>> = _categoriesIdFilter.asStateFlow()

    private val _titleFilter = MutableStateFlow<String?>(null)
    val titleFilter: StateFlow<String?> get() = _titleFilter.asStateFlow()

    init {
        getEventList()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getEventList() {
        val a = combine(
            categoriesIdFilter,
            titleFilter
        ) { categoriesIdFilter, titleFilter ->
            EventListFilters(
                categoriesIdFilter = categoriesIdFilter,
                titleFilter = titleFilter
            )
        }.flatMapLatest { filters ->
            getEventsUseCase(
                eventTitle = filters.titleFilter,
                categoriesId = filters.categoriesIdFilter,
                eventVenue = null,
                eventStartTime = null
            ).catch { e ->
                _uiState.value = EventListScreenUIState.Error(e.message ?: "Unknown Error")
            }
        }

        _uiState.value = EventListScreenUIState.Success(a)
    }

    fun onCategoriesFilterSelected(categoryId: String?) {
        categoryId?.let {
            val updatedList = _categoriesIdFilter.value.toMutableList()
            updatedList.add(it)
            _categoriesIdFilter.value = updatedList
        }
    }

    fun onTitleFilterSelected(title: String?) {
        categoryId?.let {
            _titleFilter.value = title
        }
    }
}

sealed class EventListScreenUIState {
    object Loading : EventListScreenUIState()
    data class Success(val data: Flow<PagingData<EventListEntity>>) : EventListScreenUIState()
    data class Error(val message: String) : EventListScreenUIState()
}