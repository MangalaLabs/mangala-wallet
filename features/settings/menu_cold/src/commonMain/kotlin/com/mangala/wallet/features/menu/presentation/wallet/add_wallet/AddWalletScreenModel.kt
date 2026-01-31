package com.mangala.wallet.features.menu.presentation.wallet.add_wallet

import com.mangala.wallet.pin.domain.PINManager
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class AddWalletScreenModel(
    private val pinManager: PINManager
): BaseScreenModel() {

    fun isPinExist(): Boolean {
        return pinManager.isPINSetup()
    }

    override fun doOnComposableStarted() {

    }
}