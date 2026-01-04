package com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification

import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.notification.AntelopeNotificationRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.app.AppVersionUtils
import com.mangala.wallet.utils.device.getDeviceModel
import com.mangala.wallet.utils.device.getOsVersion
import com.mmk.kmpnotifier.notification.NotifierManager
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.installations.installations
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class RegisterAntelopeNotificationUseCase(
    private val antelopeNotificationRepository: AntelopeNotificationRepository,
    private val appVersionUtils: AppVersionUtils,
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<Unit> {
        val fcmToken = try {
            NotifierManager.getPushNotifier().getToken() ?: return Result.failure(Exception("Failed to get FCM token"))
        } catch (e: Exception) {
            return Result.failure(Exception("Failed to get FCM token"))
        }

        val result = antelopeNotificationRepository.registerNotification(
            accountName = accountName,
            appVersion = appVersionUtils.getAppVersion(),
            deviceId = Firebase.installations.getToken(forceRefresh = false),
            deviceModel = getDeviceModel(),
            fcmToken = fcmToken,
            osVersion = getOsVersion(),
            blockchainType = blockchainType
        )

        if (result.isSuccess) {
            accountRepository.updateNotificationRegistered(accountName, blockchainType, true)
        }

        return result
    }

    suspend fun retryAllRegisterNotification(blockchainType: BlockchainType) = coroutineScope {
        val accounts = accountRepository.getAccountsFailedToRegisterNotification(blockchainType)
        accounts.map {
            async {
                invoke(it.accountName, blockchainType)
            }
        }.awaitAll()
    }
}