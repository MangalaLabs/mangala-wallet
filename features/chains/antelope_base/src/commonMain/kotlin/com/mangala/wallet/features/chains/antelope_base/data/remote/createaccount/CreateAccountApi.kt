package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountRequest
import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Url

interface CreateAccountApi {
    @POST("eos-accounts")
    suspend fun createAccount(
        @Body request: CreateAccountRequest,
        @Header("X-Firebase-Appcheck") authorizationToken: String
    ): CreateAccountResponse
}