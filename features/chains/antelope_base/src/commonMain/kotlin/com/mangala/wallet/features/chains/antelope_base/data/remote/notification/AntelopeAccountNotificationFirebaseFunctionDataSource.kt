package com.mangala.wallet.features.chains.antelope_base.data.remote.notification

import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model.RegisterNotificationResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

expect class AntelopeAccountNotificationFirebaseFunctionDataSource {

    suspend fun registerNotification(
        accountName: String,
        appVersion: String,
        deviceId: String,
        deviceModel: String,
        fcmToken: String,
        osVersion: String,
        blockchainType: BlockchainType,
    ): ApiResponse<RegisterNotificationResponse, CustomError>

    suspend fun unregisterNotification(
        accountName: String,
        deviceId: String,
        blockchainType: BlockchainType
    ): ApiResponse<Boolean, CustomError>
}

const val REGISTER_NOTIFICATION_TESTNET = "https://asia-southeast1-mangala-wallet-cb7c1.cloudfunctions.net/fcmTokenTestnet"
const val REGISTER_NOTIFICATION_MAINNET = "https://asia-southeast1-mangala-wallet-cb7c1.cloudfunctions.net/fcmTokenMainnet"
const val UNREGISTER_NOTIFICATION_TESTNET = "https://asia-southeast1-mangala-wallet-cb7c1.cloudfunctions.net/unsubFcmTokenTestnet"
const val UNREGISTER_NOTIFICATION_MAINNET = "https://asia-southeast1-mangala-wallet-cb7c1.cloudfunctions.net/unsubFcmTokenMainnet"

enum class AntelopeNotificationPlatform(val value: Int) {
    ANDROID(1),
    IOS(2)
}
