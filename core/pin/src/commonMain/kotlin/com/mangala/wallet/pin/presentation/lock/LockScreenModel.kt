package com.mangala.wallet.pin.presentation.lock

import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ISystemInfoManager

class LockScreenModel(
    private val systemInfoManager: ISystemInfoManager
): BaseScreenModel() {

    fun isDeviceSecure(): Boolean {
        return systemInfoManager.isDeviceSecure()
    }
}