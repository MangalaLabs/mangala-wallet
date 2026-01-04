
package com.mangala.wallet.biometry.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mangala.wallet.biometry.BiometryAuthenticator

@Composable
actual fun rememberBiometryAuthenticatorFactory(): BiometryAuthenticatorFactory {
    return remember {
        BiometryAuthenticatorFactory { BiometryAuthenticator() }
    }
}
