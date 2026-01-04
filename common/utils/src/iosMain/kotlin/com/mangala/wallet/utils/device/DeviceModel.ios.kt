package com.mangala.wallet.utils.device

import platform.UIKit.UIDevice

actual fun getDeviceModel(): String {
    return UIDevice.currentDevice.model
}