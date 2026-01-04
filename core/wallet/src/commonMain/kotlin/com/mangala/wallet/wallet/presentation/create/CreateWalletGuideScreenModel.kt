package com.mangala.wallet.wallet.presentation.create

import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class CreateWalletGuideScreenModel(private val getIsPinSetupUseCase: GetIsPinSetupUseCase) :
    BaseScreenModel() {
    val isPinSetup: Boolean by lazy {
        getIsPinSetupUseCase()
    }
}