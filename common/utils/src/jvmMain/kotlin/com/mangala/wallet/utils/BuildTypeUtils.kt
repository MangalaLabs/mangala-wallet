package com.mangala.wallet.utils

actual fun getBuildType(): BuildType {
    return if (BuildKonfig.DESKTOP_BUILD_TYPE == "debug") BuildType.DEBUG else BuildType.RELEASE
}
