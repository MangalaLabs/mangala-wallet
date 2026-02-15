package com.mangala.wallet.features.onboarding.presentation.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberNotificationPermissionRequester(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val permissionHost = LocalContext.current as? NotificationPermissionHost

    return remember(permissionHost, onPermissionResult) {
        {
            if (permissionHost != null) {
                permissionHost.requestNotificationPermission(onPermissionResult)
            } else {
                onPermissionResult(false)
            }
        }
    }
}
