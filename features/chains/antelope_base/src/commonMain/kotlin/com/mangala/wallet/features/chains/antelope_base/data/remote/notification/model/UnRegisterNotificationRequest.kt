package com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model

import kotlinx.serialization.Serializable

@Serializable
data class UnRegisterNotificationRequest(
    val accountName: String,
    val deviceId: String,
)
