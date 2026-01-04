package com.mangala.wallet.features.chains.antelope_base.data.repository.notification

import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.AntelopeAccountNotificationFirebaseFunctionDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.repository.notification.AntelopeNotificationRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class AntelopeNotificationRepositoryImpl(
    private val remoteDataSource: AntelopeAccountNotificationFirebaseFunctionDataSource
) : AntelopeNotificationRepository {

    override suspend fun registerNotification(
        accountName: String,
        appVersion: String,
        deviceId: String,
        deviceModel: String,
        fcmToken: String,
        osVersion: String,
        blockchainType: BlockchainType,
    ): Result<Unit> {
        val result = remoteDataSource.registerNotification(
            accountName = accountName,
            appVersion = appVersion,
            deviceId = deviceId,
            deviceModel = deviceModel,
            fcmToken = fcmToken,
            osVersion = osVersion,
            blockchainType = blockchainType
        )

        return if (result is ApiResponse.Success && result.body.result) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to register notification"))
        }
    }

    override suspend fun unregisterNotification(
        accountName: String,
        deviceId: String,
        blockchainType: BlockchainType
    ): Result<Unit> {
        val result = remoteDataSource.unregisterNotification(
            accountName = accountName,
            deviceId = deviceId,
            blockchainType = blockchainType
        )

        return if (result is ApiResponse.Success && result.body) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to unregister notification"))
        }
    }
}