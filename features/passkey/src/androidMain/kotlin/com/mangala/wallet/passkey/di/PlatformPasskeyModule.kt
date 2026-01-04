package com.mangala.wallet.passkey.di

import org.koin.dsl.module

/**
 * Android-specific passkey module
 */
actual fun platformPasskeyModule() = module {
    // Android-specific dependencies can be added here if needed
    // For example: Android-specific security providers, biometric managers, etc.
}