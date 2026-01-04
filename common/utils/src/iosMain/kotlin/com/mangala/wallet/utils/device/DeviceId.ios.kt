package com.mangala.wallet.utils.device

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.installations.installations

actual suspend fun getDeviceId(): String {
    return try {
        Firebase.installations.getId()
    } catch (e: Exception) {
        "unknown_ios"
    }
}