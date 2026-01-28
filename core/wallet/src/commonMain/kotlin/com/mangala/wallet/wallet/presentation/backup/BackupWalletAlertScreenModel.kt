package com.mangala.wallet.wallet.presentation.backup

import com.mangala.wallet.domain.wallet.usecases.CreateWalletUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackupWalletAlertScreenModel(
    private val createWalletUseCase: CreateWalletUseCase,
) : BaseScreenModel() {

    private val _showRiskDialog = MutableStateFlow(false)
    val showRiskDialog: StateFlow<Boolean> = _showRiskDialog.asStateFlow()

    fun onRiskButtonClicked() {
        _showRiskDialog.value = true
    }

    fun dismissRiskDialog() {
        _showRiskDialog.value = false
    }
}
