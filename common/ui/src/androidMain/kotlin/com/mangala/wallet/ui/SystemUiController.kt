package com.mangala.wallet.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class AndroidSystemUiController(
    private val activity: Activity
) : SystemUiController {
    
    private val windowInsetsController = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
    
    override fun setStatusBarColor(color: Color, darkIcons: Boolean) {
        activity.window.statusBarColor = color.toArgb()
        windowInsetsController.isAppearanceLightStatusBars = darkIcons
    }
    
    override fun setNavigationBarColor(color: Color, darkIcons: Boolean) {
        activity.window.navigationBarColor = color.toArgb()
        windowInsetsController.isAppearanceLightNavigationBars = darkIcons
    }
    
    override fun setSystemBarsColor(color: Color, darkIcons: Boolean) {
        setStatusBarColor(color, darkIcons)
        setNavigationBarColor(color, darkIcons)
    }
}

@Composable
actual fun rememberSystemUiController(): SystemUiController {
    val currentContext = LocalContext.current
    return remember(currentContext) {
        val activity = currentContext as? Activity
            ?: throw IllegalStateException("AndroidSystemUiController requires an Activity context. Current context: $currentContext")
        AndroidSystemUiController(activity)
    }
}