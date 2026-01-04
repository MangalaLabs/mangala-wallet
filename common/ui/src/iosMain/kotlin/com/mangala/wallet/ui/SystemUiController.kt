package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

class IosSystemUiController : SystemUiController {
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        // No-op on iOS - status bar styling is handled differently
    }
    
    override fun setNavigationBarColor(color: Color, darkIcons: Boolean) {
        // No-op on iOS - no system navigation bar to control
    }
    
    override fun setSystemBarsColor(color: Color, darkIcons: Boolean) {
        // No-op on iOS - status bar styling is handled differently
    }
}

@Composable
actual fun rememberSystemUiController(): SystemUiController {
    return remember { IosSystemUiController() }
}