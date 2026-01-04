package com.mangala.wallet.pin.presentation

import android.app.Activity
import android.app.KeyguardManager
import com.mangala.wallet.utils.ISystemInfoManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SystemInfoManager : ISystemInfoManager, KoinComponent {

    val appContext: android.content.Context by inject()

    override fun isDeviceSecure(): Boolean {
        val keyguardManager = appContext.getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isDeviceSecure
    }
}