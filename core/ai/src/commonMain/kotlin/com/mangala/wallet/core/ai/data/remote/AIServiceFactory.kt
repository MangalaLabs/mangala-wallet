package com.mangala.wallet.core.ai.data.remote

import com.mangala.wallet.core.ai.data.remote.providers.anthropic.AnthropicService
import com.mangala.wallet.core.ai.data.remote.providers.gemini.GeminiApi
import com.mangala.wallet.core.ai.data.remote.providers.gemini.GeminiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.providers.local.LocalAiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.providers.mangala.MangalaApi
import com.mangala.wallet.core.ai.data.remote.providers.mangala.MangalaRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.providers.ollama.OllamaApi
import com.mangala.wallet.core.ai.data.remote.providers.ollama.OllamaRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.providers.openai.OpenAiApi
import com.mangala.wallet.core.ai.data.remote.providers.openai.OpenAiRemoteDataSource
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry

internal class AIServiceFactory(
    private val ollamaApi: OllamaApi,
    private val geminiApi: GeminiApi,
    private val mangalaApi: MangalaApi,
    private val openAiApi: OpenAiApi,
    private val functionRegistry: FunctionRegistry,
) {
    fun createAIService(config: AIServiceConfig): AiRemoteDataSource {
        return when (config.type) {
            AIServiceType.OPENAI -> {
                OpenAiRemoteDataSource(
                    functionRegistry = functionRegistry,
                    openAiApi = openAiApi
                )
            }
            AIServiceType.ANTHROPIC -> {
                AnthropicService(
                    apiKey = config.apiKey,
                    functionRegistry = functionRegistry,
                    modelName = config.modelName
                )
            }
            AIServiceType.LOCAL -> {
                LocalAiRemoteDataSource(
                    modelPath = config.modelPath,
                    functionRegistry = functionRegistry,
                )
            }
            AIServiceType.GEMINI -> {
                GeminiRemoteDataSource(
                    geminiApi = geminiApi,
                    functionRegistry = functionRegistry
                )
            }
            AIServiceType.OLLAMA -> {
                OllamaRemoteDataSource(
                    ollamaApi = ollamaApi,
                    functionRegistry = functionRegistry
                )
            }
            AIServiceType.MANGALA -> {
                MangalaRemoteDataSource(
                    mangalaApi = mangalaApi,
                    functionRegistry = functionRegistry
                )
            }
        }
    }
}