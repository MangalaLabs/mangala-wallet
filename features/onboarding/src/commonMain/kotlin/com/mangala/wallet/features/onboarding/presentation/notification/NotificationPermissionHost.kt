package com.mangala.wallet.features.onboarding.presentation.notification

interface NotificationPermissionHost {
    fun requestNotificationPermission(onPermissionResult: (Boolean) -> Unit)
}
