package com.wallet.iap.purchases.device

import com.google.firebase.installations.FirebaseInstallations
import com.soywiz.krypto.sha256
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun getFirebaseInstallationId() = suspendCancellableCoroutine<String?> {
    FirebaseInstallations.getInstance().id.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            it.resume(task.result)
        } else {
            it.resume(null)
        }
    }
}