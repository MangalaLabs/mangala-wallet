package com.mangala.wallet.pin.presentation

import com.mangala.wallet.utils.ISystemInfoManager

class SystemInfoManager : ISystemInfoManager {
    override fun isDeviceSecure(): Boolean {
        return true
    }
}