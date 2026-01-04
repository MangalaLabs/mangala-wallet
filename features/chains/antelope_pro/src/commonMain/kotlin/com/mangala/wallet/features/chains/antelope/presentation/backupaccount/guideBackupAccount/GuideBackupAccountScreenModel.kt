package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.guideBackupAccount

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GuideBackupAccountScreenModel(

) : BaseScreenModel() {
    private val _uiModel = MutableStateFlow(GuideBackupAccountUiModel(0))
    val uiModel = _uiModel.asStateFlow()

    init {
        nextStep()
    }

    fun nextStep() {
        _uiModel.update { it.copy(currentStep = it.currentStep + 1, isEnableNextButton = false) }
        screenModelScope.launch {
            delay(1500)
            _uiModel.update { it.copy(isEnableNextButton = true) }
        }
    }

    fun previousStep() {
        _uiModel.update { it.copy(currentStep = it.currentStep - 1, isEnableNextButton = false) }
        screenModelScope.launch {
            delay(1500)
            _uiModel.update { it.copy(isEnableNextButton = true) }
        }
    }
}