package com.mangala.wallet.utils.remoteconfig

expect object FirebaseRemoteConfigUtils {
    fun initialize()
}

const val FETCH_INTERVAL_SECONDS = 900L