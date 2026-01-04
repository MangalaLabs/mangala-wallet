package com.mangala.wallet.features.chains.antelope_base.domain.repository.notification

import com.mangala.wallet.model.blockchain.BlockchainType

interface AntelopeNotificationRepository {
    suspend fun registerNotification(
        accountName: String,
        appVersion: String,
        deviceId: String,
        deviceModel: String,
        fcmToken: String,
        osVersion: String,
        blockchainType: BlockchainType,
    ): Result<Unit>

    suspend fun unregisterNotification(
        accountName: String,
        deviceId: String,
        blockchainType: BlockchainType,
    ): Result<Unit>
}