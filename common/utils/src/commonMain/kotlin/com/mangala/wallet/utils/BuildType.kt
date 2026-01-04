package com.mangala.wallet.utils

val isDebug = getBuildType() == BuildType.DEBUG

val currentFlavor = BuildKonfig.CURRENT_FLAVOR

enum class BuildType {
    DEBUG,
    RELEASE
}
