package com.mangala.wallet.biometry.effect

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mangala.wallet.biometry.BiometryAuthenticator
import com.mangala.wallet.biometry.effect.BiometryAuthenticatorFactory

@Composable
actual fun rememberBiometryAuthenticatorFactory(): BiometryAuthenticatorFactory {
    val context: Context = LocalContext.current
    return remember(context) {
        BiometryAuthenticatorFactory {
            BiometryAuthenticator(applicationContext = context.applicationContext)
        }
    }
}
