package com.mangala.wallet.utils

actual fun getBuildType(): BuildType {
    return if (BuildConfig.DEBUG) BuildType.DEBUG else BuildType.RELEASE
}
