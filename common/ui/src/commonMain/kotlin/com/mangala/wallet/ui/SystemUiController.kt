package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

interface SystemUiController {
    fun setStatusBarColor(color: Color, darkIcons: Boolean)
    fun setNavigationBarColor(color: Color, darkIcons: Boolean)
    fun setSystemBarsColor(color: Color, darkIcons: Boolean)
}

@Composable
expect fun rememberSystemUiController(): SystemUiController