package com.mangala.wallet.features.chains.antelope_base.data.remote.esr

import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.model.EsrCallbackRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Url

interface EsrApi {
    @POST
    suspend fun postCallback(
        @Url url: String,
        @Body request: EsrCallbackRequest
    )
}