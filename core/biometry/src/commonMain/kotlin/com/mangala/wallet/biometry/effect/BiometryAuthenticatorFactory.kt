package com.mangala.wallet.biometry.effect

import androidx.compose.runtime.Composable
import com.mangala.wallet.biometry.BiometryAuthenticator

fun interface BiometryAuthenticatorFactory {
    fun createBiometryAuthenticator(): BiometryAuthenticator
}

@Composable
expect fun rememberBiometryAuthenticatorFactory(): BiometryAuthenticatorFactory