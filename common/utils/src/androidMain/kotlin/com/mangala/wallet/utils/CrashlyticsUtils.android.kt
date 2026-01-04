package com.mangala.wallet.utils

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

actual object CrashlyticsUtils {
    actual fun logNonFatal(throwable: Throwable) {
        Firebase.crashlytics.recordException(throwable)
    }
}