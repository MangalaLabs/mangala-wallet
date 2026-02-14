package com.mangala.wallet.features.onboarding.presentation.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mmk.kmpnotifier.notification.NotifierManager

@Composable
actual fun rememberNotificationPermissionRequester(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit = remember(onPermissionResult) {
    {
        NotifierManager.getPermissionUtil().askNotificationPermission {
            onPermissionResult(true)
        }
    }
}
