package com.mangala.wallet.biometry

import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.Foundation.NSError
import platform.LocalAuthentication.*
import platform.UIKit.UIDevice

@OptIn(ExperimentalForeignApi::class)
actual class BiometryAuthenticator constructor() {

    actual suspend fun checkBiometryAuthentication(
        requestTitle: StringDesc,
        requestReason: StringDesc,
        failureButtonText: StringDesc
    ): Boolean {
        val laContext = LAContext()
        laContext.setLocalizedFallbackTitle(failureButtonText.localized())

        val (canEvaluate: Boolean?, error: NSError?) = memScoped {
            val p = alloc<ObjCObjectVar<NSError?>>()
            val canEvaluate: Boolean? = runCatching {
                laContext.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, error = p.ptr)
            }.getOrNull()
            canEvaluate to p.value
        }

        if (error != null) throw error.toException()
        if (canEvaluate == null) return false

        return callbackToCoroutine { callback ->
            laContext.evaluatePolicy(
                policy = LAPolicyDeviceOwnerAuthentication,
                localizedReason = requestReason.localized(),
                reply = mainContinuation { result: Boolean, error: NSError? ->
                    callback(result, error)
                }
            )
        }
    }

    actual fun isBiometricAvailable(): Boolean {
        val laContext = LAContext()
        return laContext.canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            error = null
        )
    }
}
