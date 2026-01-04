
package com.mangala.wallet.biometry.effect

import androidx.compose.runtime.Composable
import com.mangala.wallet.biometry.BiometryAuthenticator

// on iOS side we should not do anything to prepare BiometryAuthenticator to work
@Suppress("FunctionNaming")
@Composable
actual fun BindBiometryAuthenticatorEffect(biometryAuthenticator: BiometryAuthenticator) = Unit
