package com.mangala.wallet.passkey.repository

/**
 * JVM/Desktop implementation - not needed since we don't call this method on desktop
 */
fun PasskeyRepositoryImpl.getAndroidOrigin(): String {
    // This should never be called on desktop, but provide a fallback
    return "android:apk-key-hash:dHrL6qyLKnq8kNi6IYhwiK5lMXG2PcV7c7k9sCSYtes"
}