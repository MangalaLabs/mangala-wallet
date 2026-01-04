package com.mangala.wallet.utils.device

actual fun getDeviceModel(): String {
    return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
}