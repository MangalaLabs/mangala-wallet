package com.mangala.wallet.utils

expect object CrashlyticsUtils {
    fun logNonFatal(throwable: Throwable)
}