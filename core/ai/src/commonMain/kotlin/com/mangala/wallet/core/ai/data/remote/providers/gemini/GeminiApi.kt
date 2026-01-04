package com.mangala.wallet.core.ai.data.remote.providers.gemini

import com.mangala.wallet.core.ai.BuildKonfig
import com.mangala.wallet.core.ai.data.remote.providers.gemini.request.GeminiGenerateContentRequest
import com.mangala.wallet.core.ai.data.remote.providers.gemini.response.GeminiGenerateContentResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface GeminiApi {

    @POST("models/{model}:generateContent")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Path("model") model: String,
        @Body request: GeminiGenerateContentRequest,
//        @Header("Helicone-Auth") heliconeAuth: String = "Bearer ${BuildKonfig.HELICONE_API_KEY}",
//        @Header("Helicone-Target-URL") heliconeTargetUrl: String = "https://generativelanguage.googleapis.com"
    ): GeminiGenerateContentResponse

    @POST("models/{model}:streamGenerateContent")
    @Headers("Content-Type: application/json", "Accept: application/json")
    @Streaming
    suspend fun streamGenerateContent(
        @Query("key") apiKey: String,
        @Path("model") model: String,
        @Body request: GeminiGenerateContentRequest
    ): HttpStatement
}