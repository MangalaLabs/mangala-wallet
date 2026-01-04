package com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterNotificationRequest(
    @SerialName("accountName") val accountName: String,
    @SerialName("appVersion") val appVersion: String,
    @SerialName("deviceId") val deviceId: String,
    @SerialName("deviceModel") val deviceModel: String,
    @SerialName("fcmToken") val fcmToken: String,
    @SerialName("osVersion") val osVersion: String,
    @SerialName("platform") val platform: Int
)