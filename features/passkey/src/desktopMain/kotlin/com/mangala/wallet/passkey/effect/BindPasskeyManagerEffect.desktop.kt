package com.mangala.wallet.passkey.effect

import androidx.compose.runtime.Composable
import com.mangala.wallet.passkey.PasskeyManager

@Composable
actual fun BindPasskeyManagerEffect(passkeyManager: PasskeyManager) {
    // No-op on desktop - activity context not needed
}