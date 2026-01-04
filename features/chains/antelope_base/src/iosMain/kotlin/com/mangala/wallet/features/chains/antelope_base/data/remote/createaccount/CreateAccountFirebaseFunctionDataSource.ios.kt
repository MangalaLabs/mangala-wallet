package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import cocoapods.FirebaseFunctions.FIRHTTPSCallableOptions
import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.functions.FirebaseFunctions
import dev.gitlive.firebase.functions.functions
import dev.gitlive.firebase.internal.encode
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.Foundation.NSURL
import kotlin.coroutines.resume

actual class CreateAccountFirebaseFunctionDataSource actual constructor() {

    @OptIn(ExperimentalForeignApi::class)
    private val functions = Firebase.functions.ios

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun createAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        activePublicKey: String,
        ownerPublicKey: String
    ): ApiResponse<CreateAccountResponse, CustomError> = suspendCancellableCoroutine { continuation ->
        val data = CreateAccountRequestIos(purchaseToken,
        accountName,
        activePublicKey,
        ownerPublicKey,
        iapPlatform = 2
    )

        try {
            if (blockchainType == BlockchainType.EosJungleTestnet) {
                functions
                    .HTTPSCallableWithURL(
                        NSURL.URLWithString(CREATE_ACCOUNT_FUNCTION_JUNGLE_TESTNET)!!,
                        options = FIRHTTPSCallableOptions(requireLimitedUseAppCheckTokens = true)
                    )
                    .callWithObject(encode(data)) { result, error ->
                        if (result == null) {
                            println("CreateAccountFirebaseFunctionDataSource error $error")
                            continuation.resume(
                                ApiResponse.Error.UnknownError( // TODO: Return custom error
                                    error?.localizedDescription() ?: "Error"
                                )
                            )
                        } else {
                            println("CreateAccountFirebaseFunctionDataSource result $result")
                            continuation.resume(ApiResponse.Success((result as CreateAccountResponseIos).toCreateAccountResponse()))
                        }
                    }
            } else {
                functions
                    .HTTPSCallableWithURL(
                        NSURL.URLWithString(CREATE_ACCOUNT_FUNCTION_EOS_MAINNET_URL)!!,
                        options = FIRHTTPSCallableOptions(requireLimitedUseAppCheckTokens = true)
                    )
                    .callWithObject(data) { result, error ->
                        if (result == null) {
                            println("CreateAccountFirebaseFunctionDataSource error $error")
                            continuation.resume(
                                ApiResponse.Error.UnknownError(
                                    error?.localizedDescription() ?: "Error"
                                )
                            )
                        } else {
                            println("CreateAccountFirebaseFunctionDataSource result $result")
                            continuation.resume(ApiResponse.Success((result as CreateAccountResponseIos).toCreateAccountResponse()))
                        }
                    }
            }
        } catch (e: Exception) {
            println("CreateAccountFirebaseFunctionDataSource error ${e.message}")
            continuation.resume(ApiResponse.Error.UnknownError(e.message ?: "Error"))
        }
    }
}