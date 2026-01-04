package com.mangala.wallet.utils

class DesktopPlatform : Platform {
    override val name: String = "Desktop"
    override val type: PlatformType
        get() = PlatformType.DESKTOP
}

actual fun getPlatform(): Platform {
    return DesktopPlatform()
}