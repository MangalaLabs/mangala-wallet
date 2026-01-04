package com.mangala.wallet.pin.presentation

import com.mangala.wallet.utils.ISystemInfoManager
import kotlinx.cinterop.*
import platform.Foundation.NSBundle
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlinx.cinterop.ObjCObjectBase
import kotlinx.cinterop.initBy
import platform.Foundation.NSCoder
import platform.LocalAuthentication.*
import platform.darwin.NSObjectProtocol

class SystemInfoManager : ISystemInfoManager {
    override fun isDeviceSecure(): Boolean {
        val context = LAContext()
//        return context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, null)
        return true
    }
}
