package com.mangala.wallet.ui

import androidx.compose.runtime.Composable

/**
 * A composable wrapper that prevents screenshots and screen recordings of sensitive content.
 *
 * - Android: applies [android.view.WindowManager.LayoutParams.FLAG_SECURE] to the Activity window
 *   for the duration the composable is in the composition, blocking both screenshots and system
 *   screen recordings.
 * - iOS: observes [platform.UIKit.UIScreenCapturedDidChangeNotification] and renders a full-screen
 *   privacy overlay whenever the screen is being captured/recorded.
 * - Desktop: no-op (no OS-level screenshot blocking available via Compose).
 *
 * Usage: wrap any sensitive screen content inside [SecureScreen]:
 * ```kotlin
 * SecureScreen {
 *     // sensitive content here
 * }
 * ```
 */
@Composable
expect fun SecureScreen(content: @Composable () -> Unit)
