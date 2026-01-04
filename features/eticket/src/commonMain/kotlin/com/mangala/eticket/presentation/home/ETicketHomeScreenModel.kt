package com.mangala.eticket.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ETicketHomeScreenModel : BaseScreenModel() {
    private var _isNavToCategoryPage = MutableStateFlow(false)
    val isNavToCategoryPage = _isNavToCategoryPage.asStateFlow()

    fun onClickToCategory() {
        _isNavToCategoryPage.value = true
    }

    fun onNavToCategorySuccess() {
        _isNavToCategoryPage.value = false
    }
}