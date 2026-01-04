package com.mangala.wallet.utils.device

actual fun getDeviceModel(): String {
    return System.getProperty("os.name") + " " + System.getProperty("os.version")
}