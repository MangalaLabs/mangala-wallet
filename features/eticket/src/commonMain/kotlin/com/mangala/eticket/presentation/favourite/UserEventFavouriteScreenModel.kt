package com.mangala.eticket.presentation.favourite

import app.cash.paging.PagingData
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import com.mangala.eticket.domain.usecases.favourite.ListUserEventFavouriteUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cache
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class UserEventFavouriteScreenModel(
    private val listUserEventFavouriteUseCase: ListUserEventFavouriteUseCase
) : BaseScreenModel() {
    val listData: Flow<PagingData<UserEventFavouriteResponse>> = loadData()

    private fun loadData(): Flow<PagingData<UserEventFavouriteResponse>> =
        listUserEventFavouriteUseCase.invoke()
            .shareIn(
                scope = screenModelScope, // Assuming this is within a ViewModel and 'scope' should be 'viewModelScope'.
                replay = 1, // Typically, you would replay 1 item to ensure that subscribers immediately receive the latest paging data.
                started = SharingStarted.Lazily // Corrected typo from 'SharingStared.Lazily' to 'SharingStarted.Lazily'
            )
}