package com.mangala.wallet.passkey.repository

import android.content.Context
import com.mangala.wallet.passkey.BuildConfig
import com.mangala.wallet.passkey.PasskeyDebugInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android-specific implementation to get the dynamic origin based on APK certificate
 */
fun PasskeyRepositoryImpl.getAndroidOrigin(): String {
    return try {
        // PasskeyRepositoryImpl implements KoinComponent, so we can inject context
        val context: Context by inject()
        PasskeyDebugInfo.getExpectedAndroidOrigin(context)
    } catch (e: Exception) {
        // Fallback based on build configuration if dynamic retrieval fails
        getAndroidOriginFallback()
    }
}

/**
 * Android-specific implementation to detect debug build
 */
fun PasskeyRepositoryImpl.isDebugBuild(): Boolean {
    return BuildConfig.DEBUG
}