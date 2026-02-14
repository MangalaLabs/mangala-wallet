package com.mangala.wallet.features.onboarding.presentation.notification

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.datastore.usecases.SavePrePermissionDoneUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.launch

class PrePermissionScreenModel(
    private val savePrePermissionDoneUseCase: SavePrePermissionDoneUseCase
) : BaseScreenModel() {

    fun onMaybeLater(onSaved: () -> Unit) {
        screenModelScope.launch {
            savePrePermissionDoneUseCase(true)
            onSaved()
        }
    }

    fun onNotifyMeResult(onSaved: (Boolean) -> Unit, isGranted: Boolean) {
        screenModelScope.launch {
            savePrePermissionDoneUseCase(true)
            onSaved(isGranted)
        }
    }
}
