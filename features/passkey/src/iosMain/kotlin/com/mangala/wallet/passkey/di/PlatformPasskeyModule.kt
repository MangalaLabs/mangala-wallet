package com.mangala.wallet.passkey.di

import org.koin.dsl.module

/**
 * iOS-specific passkey module
 */
actual fun platformPasskeyModule() = module {
    // iOS-specific dependencies can be added here if needed
    // For example: iOS-specific keychain managers, authentication services, etc.
}