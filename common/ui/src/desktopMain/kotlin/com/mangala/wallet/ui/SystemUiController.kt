package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

class DesktopSystemUiController : SystemUiController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // No-op on desktop - no system status bar to control
    }
    
    override fun setNavigationBarColor(color: Color, darkIcons: Boolean) {
        // No-op on desktop - no system navigation bar to control
    }
    
    override fun setSystemBarsColor(color: Color, darkIcons: Boolean) {
        // No-op on desktop - no system bars to control
    }
}

@Composable
actual fun rememberSystemUiController(): SystemUiController {
    return remember { DesktopSystemUiController() }
}