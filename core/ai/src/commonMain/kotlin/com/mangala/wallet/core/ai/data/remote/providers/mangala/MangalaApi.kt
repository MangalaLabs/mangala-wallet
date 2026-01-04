package com.mangala.wallet.core.ai.data.remote.providers.mangala

import com.mangala.wallet.core.ai.data.remote.providers.mangala.request.MangalaRequest
import com.mangala.wallet.core.ai.data.remote.providers.mangala.response.MangalaResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST

interface MangalaApi {

    @POST("webhooks/rest/webhook")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun sendMessage(
        @Body request: MangalaRequest
    ): MangalaResponse
}