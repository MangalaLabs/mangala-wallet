package com.mangala.wallet.core.ai.data.remote.providers.ollama

import com.mangala.wallet.core.ai.data.remote.providers.ollama.request.OllamaChatRequest
import com.mangala.wallet.core.ai.data.remote.providers.ollama.response.OllamaChatResponse
import io.ktor.client.statement.HttpStatement
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Streaming

interface OllamaApi {
    @POST("chat")
    @Headers("Content-Type: application/json")
    suspend fun chat(
        @Body request: OllamaChatRequest
    ): OllamaChatResponse

    @POST("chat")
    @Headers("Content-Type: application/json")
    @Streaming
    suspend fun chatStream(
        @Body request: OllamaChatRequest
    ): HttpStatement
}