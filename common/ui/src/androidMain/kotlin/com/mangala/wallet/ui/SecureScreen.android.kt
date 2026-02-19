package com.mangala.wallet.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

/**
 * Traverses the [ContextWrapper] chain to find the underlying [Activity].
 * In Compose, [LocalContext] may be a [ContextWrapper] rather than the [Activity] directly,
 * so a simple `as? Activity` cast returns null and FLAG_SECURE would never be applied.
 */
private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@Composable
actual fun SecureScreen(content: @Composable () -> Unit) {
    val activity = LocalContext.current.findActivity()
    DisposableEffect(Unit) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }
    content()
}
