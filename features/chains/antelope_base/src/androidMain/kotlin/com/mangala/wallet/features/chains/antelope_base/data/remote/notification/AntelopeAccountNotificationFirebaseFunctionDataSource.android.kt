package com.mangala.wallet.features.chains.antelope_base.data.remote.notification

import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.functions
import com.google.firebase.functions.getHttpsCallableFromUrl
import com.mangala.wallet.features.chains.antelope_base.data.remote.notification.model.RegisterNotificationResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.net.URL

actual class AntelopeAccountNotificationFirebaseFunctionDataSource {

    private val functions: FirebaseFunctions = Firebase.functions

    actual suspend fun registerNotification(
        accountName: String,
        appVersion: String,
        deviceId: String,
        deviceModel: String,
        fcmToken: String,
        osVersion: String,
        blockchainType: BlockchainType
    ): ApiResponse<RegisterNotificationResponse, CustomError> {
        val data = hashMapOf(
            "accountName" to accountName,
            "appVersion" to appVersion,
            "deviceId" to deviceId,
            "deviceModel" to deviceModel,
            "fcmToken" to fcmToken,
            "osVersion" to osVersion,
            "platform" to AntelopeNotificationPlatform.ANDROID.value
        )

        try {
            return withTimeout(5000L) {
                val result = functions
                    .getHttpsCallableFromUrl(
                        URL(
                            when (blockchainType) {
                                BlockchainType.EosJungleTestnet -> REGISTER_NOTIFICATION_TESTNET
                                BlockchainType.Eos -> REGISTER_NOTIFICATION_MAINNET
                                else -> return@withTimeout ApiResponse.Error.UnknownError("Blockchain type not supported")
                            }
                        )
                    ) {
                        limitedUseAppCheckTokens = true
                    }
                    .call(data)
                    .await()

                val rawResult = result.data as? Boolean

                return@withTimeout ApiResponse.Success(
                    RegisterNotificationResponse(
                        result = rawResult ?: false
                    )
                )
            }
        } catch (e: Exception) {
            return if (e is FirebaseFunctionsException) {
                val code = e.code
                if (code == FirebaseFunctionsException.Code.ALREADY_EXISTS)
                    ApiResponse.Success(RegisterNotificationResponse(result = true))
                else
                    ApiResponse.Error.CustomError(
                        code.ordinal,
                        CustomError(e.localizedMessage)
                    )
            } else if (e is TimeoutCancellationException) {
                ApiResponse.Error.CustomError(
                    -1,
                    CustomError("Request timed out")
                )
            } else {
                ApiResponse.Error.UnknownError(e.message ?: "Error")
            }
        }
    }

    actual suspend fun unregisterNotification(
        accountName: String,
        deviceId: String,
        blockchainType: BlockchainType
    ): ApiResponse<Boolean, CustomError> {
        val data = hashMapOf(
            "accountName" to accountName,
            "deviceId" to deviceId
        )

        try {
            val result = functions
                .getHttpsCallableFromUrl(
                    URL(
                        when (blockchainType) {
                            BlockchainType.EosJungleTestnet -> UNREGISTER_NOTIFICATION_TESTNET
                            BlockchainType.Eos -> UNREGISTER_NOTIFICATION_MAINNET
                            else -> return ApiResponse.Error.UnknownError("Blockchain type not supported")
                        }
                    )
                ) {
                    limitedUseAppCheckTokens = true
                }
                .call(data)
                .await()

            val isSuccess = result.data as? Boolean

            return ApiResponse.Success(isSuccess ?: false)
        } catch (e: Exception) {
            return if (e is FirebaseFunctionsException) {
                val code = e.code

                if (code == FirebaseFunctionsException.Code.NOT_FOUND)
                    ApiResponse.Success(true)
                else
                    ApiResponse.Error.CustomError(
                        code.ordinal,
                        CustomError(e.localizedMessage)
                    )
            } else {
                ApiResponse.Error.UnknownError(e.message ?: "Error")
            }
        }
    }
}