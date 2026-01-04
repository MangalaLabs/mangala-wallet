package com.mangala.wallet.utils.device

import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await

actual suspend fun getDeviceId(): String {
    return try {
        FirebaseInstallations.getInstance().id.await()
    } catch (e: Exception) {
        "unknown_android"
    }
}