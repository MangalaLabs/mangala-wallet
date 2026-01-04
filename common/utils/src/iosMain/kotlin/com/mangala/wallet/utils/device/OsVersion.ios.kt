package com.mangala.wallet.utils.device

import platform.UIKit.UIDevice

actual fun getOsVersion(): String {
    return "iOS ${UIDevice.currentDevice.systemVersion}"
}