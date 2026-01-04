package com.mangala.wallet.utils.app

import platform.Foundation.NSBundle

actual class AppVersionUtils {
    actual fun getAppVersion(): String {
        val versionName = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "Unknown"
        val versionCode = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String ?: "Unknown"

        return "$versionName ($versionCode)"
    }
}