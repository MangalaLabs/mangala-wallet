package com.mangala.wallet.biometry.effect

import androidx.compose.runtime.Composable
import com.mangala.wallet.biometry.BiometryAuthenticator

@Suppress("FunctionNaming")
@Composable
expect fun BindBiometryAuthenticatorEffect(biometryAuthenticator: BiometryAuthenticator)