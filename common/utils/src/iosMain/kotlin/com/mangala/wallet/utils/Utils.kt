package com.mangala.wallet.utils

import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> callbackToCoroutine(callbackCall: ((T?, NSError?) -> Unit) -> Unit): T {
    return suspendCoroutine { continuation ->
        callbackCall { data, error ->
            if (data != null) {
                continuation.resume(data)
            } else {
                continuation.resumeWithException(error.toException())
            }
        }
    }
}

internal fun NSError?.toException(): Exception {
    if (this == null) return NullPointerException("NSError is null")

    return Exception(this.description())
}
