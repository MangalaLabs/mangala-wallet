package com.mangala.wallet.features.menu.presentation.wallet.add_wallet

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.local.securestorage.SecureStorageWrapperConstants.PIN_KEY
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel

class AddWalletScreenModel(private val secureStorageWrapper: SecureStorageWrapper): BaseScreenModel() {

    private fun getPin(): String {
        return secureStorageWrapper.getValue(PIN_KEY) ?: ""
    }

    fun isPinExist(): Boolean {
        return getPin().isNotEmpty()
    }

    override fun doOnComposableStarted() {

    }
}