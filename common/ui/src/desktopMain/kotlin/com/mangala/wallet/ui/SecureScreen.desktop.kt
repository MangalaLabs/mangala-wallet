package com.mangala.wallet.ui

import androidx.compose.runtime.Composable

@Composable
actual fun SecureScreen(content: @Composable () -> Unit) {
    // No-op on desktop — no OS-level screenshot blocking available via Compose
    content()
}
