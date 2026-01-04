package com.mangala.wallet.utils.device

actual fun getOsVersion(): String {
    return "Android ${android.os.Build.VERSION.RELEASE} (SDK ${android.os.Build.VERSION.SDK_INT})"
}