package com.mangala.wallet.utils

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual fun getBuildType(): BuildType {
    return if (Platform.isDebugBinary) {
        BuildType.DEBUG
    } else {
        BuildType.RELEASE
    }
}
