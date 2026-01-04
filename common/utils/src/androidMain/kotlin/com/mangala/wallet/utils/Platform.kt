package com.mangala.wallet.utils

class AndroidPlatform : Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val type: PlatformType
        get() = PlatformType.ANDROID
}

actual fun getPlatform(): Platform = AndroidPlatform()