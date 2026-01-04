package com.mangala.wallet.core.ai.data.remote.providers.openai

import com.mangala.wallet.core.ai.data.remote.providers.openai.request.OpenAiChatCompletionRequest
import com.mangala.wallet.core.ai.data.remote.providers.openai.response.OpenAiChatCompletionResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

interface OpenAiApi {

    @POST("responses")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun createChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAiChatCompletionRequest,
//        @Header("Helicone-Auth") heliconeAuth: String = "Bearer ${BuildKonfig.HELICONE_API_KEY}",
//        @Header("Helicone-Target-URL") heliconeTargetUrl: String = "https://api.openai.com/v1"
    ): OpenAiChatCompletionResponse

    @POST("chat/completions")
    @Headers("Content-Type: application/json", "Accept: application/json")
    @Streaming
    suspend fun streamChatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: OpenAiChatCompletionRequest
    ): HttpStatement
}