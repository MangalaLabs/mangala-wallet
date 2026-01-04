package com.mangala.wallet.features.chains.antelope_base.data.remote.notification

import cocoapods.FirebaseFunctions.FIRHTTPSCallableOptions
import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model.RegisterNotificationRequest
import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model.RegisterNotificationResponse
import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model.UnRegisterNotificationRequest
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.internal.encode
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSURL
import kotlin.coroutines.resume

actual class AntelopeAccountNotificationFirebaseFunctionDataSource {

    @OptIn(ExperimentalForeignApi::class)
    private val functions = Firebase.functions.ios

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun registerNotification(
        accountName: String,
        appVersion: String,
        deviceId: String,
        deviceModel: String,
        fcmToken: String,
        osVersion: String,
        blockchainType: BlockchainType
    ): ApiResponse<RegisterNotificationResponse, CustomError> =
        suspendCancellableCoroutine { continuation ->

            val data = RegisterNotificationRequest(
                accountName,
                appVersion,
                deviceId,
                deviceModel,
                fcmToken,
                osVersion,
                AntelopeNotificationPlatform.IOS.value
            )

            try {
                functions
                    .HTTPSCallableWithURL(
                        NSURL.URLWithString(REGISTER_NOTIFICATION_TESTNET)!!,
                        options = FIRHTTPSCallableOptions(requireLimitedUseAppCheckTokens = true)
                    )
                    .callWithObject(encode(data)) { result, error ->
                        if (result == null) {
                            continuation.resume(
                                ApiResponse.Error.UnknownError(
                                    error?.localizedDescription() ?: "Error"
                                )
                            )
                        } else {
                            continuation.resume(
                                ApiResponse.Success(
                                    (result as? RegisterNotificationResponse)
                                        ?: RegisterNotificationResponse(false)
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                continuation.resume(ApiResponse.Error.UnknownError(e.message ?: "Error"))
            }
        }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun unregisterNotification(
        accountName: String,
        deviceId: String,
        blockchainType: BlockchainType
    ): ApiResponse<Boolean, CustomError> =
        suspendCancellableCoroutine { continuation ->

            val data = UnRegisterNotificationRequest(
                accountName = accountName,
                deviceId = deviceId
            )

            try {
                functions
                    .HTTPSCallableWithURL(
                        NSURL.URLWithString(UNREGISTER_NOTIFICATION_TESTNET)!!,
                        options = FIRHTTPSCallableOptions(requireLimitedUseAppCheckTokens = true)
                    )
                    .callWithObject(encode(data)) { result, error ->
                        if (result == null) {
                            continuation.resume(
                                ApiResponse.Error.UnknownError(
                                    error?.localizedDescription() ?: "Error"
                                )
                            )
                        } else {
                            continuation.resume(ApiResponse.Success(result as? Boolean ?: false))
                        }
                    }
            } catch (e: Exception) {
                continuation.resume(ApiResponse.Error.UnknownError(e.message ?: "Error"))
            }
        }

}