package com.mangala.wallet.features.chains.antelope_base.data.remote.notification

import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model.RegisterNotificationResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

actual class AntelopeAccountNotificationFirebaseFunctionDataSource {
    actual suspend fun registerNotification(
        accountName: String,
        appVersion: String,
        deviceId: String,
        deviceModel: String,
        fcmToken: String,
        osVersion: String,
        blockchainType: BlockchainType
    ): ApiResponse<RegisterNotificationResponse, CustomError> {
        TODO("Not yet implemented")
    }

    actual suspend fun unregisterNotification(
        accountName: String,
        deviceId: String,
        blockchainType: BlockchainType
    ): ApiResponse<Boolean, CustomError> {
        TODO("Not yet implemented")
    }
}