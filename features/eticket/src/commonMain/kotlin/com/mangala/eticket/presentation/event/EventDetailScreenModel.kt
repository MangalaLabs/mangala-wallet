package com.mangala.eticket.presentation.event

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.eticket.domain.usecases.event.GetEventUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class EventDetailScreenModel(
    private val getEventDetailUseCase: GetEventUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    private val eventId: String,
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<EventScreenUiState> =
        MutableStateFlow(EventScreenUiState.Loading)
    val uiState: StateFlow<EventScreenUiState> get() = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            _uiState.value = EventScreenUiState.Loading
            try {
                val eventDetailResponse = getEventDetailUseCase(eventId)
                val currencyCode = getCurrentCurrencyCodeUseCase()
                val currencySymbol = Currency.valueOf(currencyCode).symbol
                if (eventDetailResponse is ApiResponse.Success) {
                    _uiState.value = EventScreenUiState.Success(
                        EventScreenUiModel(
                            eventDetailResponse.body.data,
                            currencySymbol
                        )
                    )
                } else {
                    _uiState.value = EventScreenUiState.Error(WrappedStringResource.StringRes(MR.strings.message_event_detail_screen_model_failed_to_load_event_details))
                }
            } catch (e: Exception) {
                val message = e.message?.let { WrappedStringResource.PlainString(it) }?: WrappedStringResource.StringRes(MR.strings.message_event_detail_screen_model_unknown_error)
                _uiState.value = EventScreenUiState.Error(message)
            }
        }
    }


}