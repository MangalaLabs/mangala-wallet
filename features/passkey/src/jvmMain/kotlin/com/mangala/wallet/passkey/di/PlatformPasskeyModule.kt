package com.mangala.wallet.passkey.di

import org.koin.dsl.module

/**
 * JVM/Desktop-specific passkey module
 */
actual fun platformPasskeyModule() = module {
    // JVM-specific dependencies can be added here if needed
    // For example: Desktop-specific credential managers, security providers, etc.
}