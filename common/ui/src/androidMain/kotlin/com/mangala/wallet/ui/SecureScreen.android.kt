package com.mangala.wallet.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Process-level reference counter for FLAG_SECURE.
 *
 * Multiple [SecureScreen] composables can be active simultaneously (e.g., during
 * Voyager navigation transitions where both the outgoing and incoming screens are
 * composed). Using a counter ensures the flag is only added on the first entry and
 * only removed when the last instance leaves the composition — preventing the
 * "sibling clears sibling's protection" defect.
 */
private val secureScreenCount = AtomicInteger(0)

/**
 * Traverses the [ContextWrapper] chain to find the underlying [Activity].
 * In Compose, [LocalContext] may be a [ContextWrapper] rather than the [Activity]
 * directly, so a simple `as? Activity` cast returns null silently.
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

    DisposableEffect(activity) {
        if (secureScreenCount.incrementAndGet() == 1) {
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
        onDispose {
            if (secureScreenCount.decrementAndGet() == 0) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }
    }

    content()
}
