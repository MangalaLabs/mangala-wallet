package com.mangala.wallet.utils.device

actual fun getOsVersion(): String {
    return "${System.getProperty("os.name")} ${System.getProperty("os.version")}"
}