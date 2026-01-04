package com.mangala.wallet.auth

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

actual suspend fun getFirebaseAppCheckToken(): String =
    suspendCancellableCoroutine { continuation ->
        if (BuildConfig.DEBUG) {
            continuation.resume("")
        } else {
            Firebase.appCheck.getAppCheckToken(true).addOnSuccessListener { appCheckToken ->
                Log.d("FirebaseAppCheckToken", "Generated token successfully")
                continuation.resume(appCheckToken.token)
            }.addOnFailureListener {
                Log.e(
                    "FirebaseAppCheckToken",
                    "Generate app check token error ${it.printStackTrace()}"
                )
                continuation.resumeWithException(it)
            }
        }
    }