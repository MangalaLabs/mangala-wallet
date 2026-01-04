package com.mangala.wallet.utils.app

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

actual class AppVersionUtils(private val context: Context) {
    actual fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "Unknown"
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            "$versionName ($versionCode)"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
}