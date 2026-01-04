package com.mangala.wallet.utils.device

import com.mangala.wallet.utils.getPlatform
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
suspend fun generateTempUserId(): String {
    val platform = getPlatform()
    val osName = platform.name.replace(" ", "_").replace(".", "_")
    val osVersion = getOsVersion().replace(" ", "_").replace(".", "_").replace("(", "_").replace(")", "_")
    val deviceModel = getDeviceModel().replace(" ", "_").replace(".", "_")
    val deviceId = getDeviceId()
    val timestamp = Clock.System.now().epochSeconds
    val random = Uuid.random().toString().take(12)
    
    return "temp_${osName}_${osVersion}_${deviceModel}_${deviceId}_${timestamp}_${random}"
}