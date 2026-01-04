package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import com.google.firebase.Firebase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.functions
import com.google.firebase.functions.getHttpsCallable
import com.google.firebase.functions.getHttpsCallableFromUrl
import com.mangala.wallet.features.chains.antelope_base.BuildKonfig.CREATE_ACCOUNT_FUNCTION
import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.coroutines.tasks.await
import java.net.URL

actual class CreateAccountFirebaseFunctionDataSource {

    private val functions: FirebaseFunctions = Firebase.functions

    actual suspend fun createAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        activePublicKey: String,
        ownerPublicKey: String
    ): ApiResponse<CreateAccountResponse, CustomError> {
        val data = hashMapOf(
            "token" to purchaseToken,
            "newAccountName" to accountName,
            "newPublicAccountActiveKey" to activePublicKey,
            "newPublicAccountOwnerKey" to ownerPublicKey,
            "iapPlatform" to 1
        )

        try {
            val result = if (blockchainType == BlockchainType.EosJungleTestnet) {
                functions
                    .getHttpsCallableFromUrl(URL(CREATE_ACCOUNT_FUNCTION_JUNGLE_TESTNET)) {
                        limitedUseAppCheckTokens = true
                    }
                    .call(data)
                    .await()
            } else {
                functions
                    .getHttpsCallableFromUrl(URL(CREATE_ACCOUNT_FUNCTION_EOS_MAINNET_URL)) {
                        limitedUseAppCheckTokens = true
                    }
                    .call(data)
                    .await()
            }

            val rawResult = result.data as Map<*, *>

            return ApiResponse.Success(
                CreateAccountResponse(
                    message = null,
                    newAccountName = rawResult["accountName"] as? String,
                    publicActiveKey = rawResult["activePublicKey"] as? String,
                    publicOwnerKey = rawResult["ownerPublicKey"] as? String
                )
            )
        } catch (e: Exception) {
            return if (e is FirebaseFunctionsException) {
                val code = e.code

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