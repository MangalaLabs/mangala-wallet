package com.mangala.wallet.features.onboarding.presentation.notification

import androidx.compose.runtime.Composable

@Composable
expect fun rememberNotificationPermissionRequester(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit
