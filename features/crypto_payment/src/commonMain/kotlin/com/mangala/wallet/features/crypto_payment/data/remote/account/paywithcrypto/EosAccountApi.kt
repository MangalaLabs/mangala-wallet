package com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto

import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.CreateEosAccountRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface EosAccountApi {
    @POST("api/v1/eos-accounts")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun createAccount(
        @Header("deviceId") deviceId: String,
        @Body request: CreateEosAccountRequest
    )

    @POST("api/v1/sign-messages")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun signMessage(@Body request: SignMessageRequest): SignMessageResponse
}