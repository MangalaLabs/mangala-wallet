package com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto

import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.CreateEosAccountRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall


class EosAccountDataSource(private val apiFactory: EosAccountApiFactory) {
    suspend fun signMessage(
        blockchainType: BlockchainType,
        body: SignMessageRequest
    ): ApiResponse<SignMessageResponse, PayWithCryptoError> {
        return safeApiCall {
            val api = getApi(blockchainType)
            api.signMessage(body)
        }
    }

    suspend fun createAccount(
        blockchainType: BlockchainType,
        deviceId: String,
        body: CreateEosAccountRequest
    ): ApiResponse<Unit, PayWithCryptoError> {
        return safeApiCall {
            val api = getApi(blockchainType)
            api.createAccount(deviceId, body)
        }
    }

    private fun getApi(blockchainType: BlockchainType): EosAccountApi {
        return apiFactory.createEosAccountApi(blockchainType)
    }
}