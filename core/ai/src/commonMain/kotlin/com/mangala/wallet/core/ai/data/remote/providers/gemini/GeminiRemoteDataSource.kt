package com.mangala.wallet.core.ai.data.remote.providers.gemini

import com.mangala.wallet.core.ai.BuildKonfig
import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.data.remote.AiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.RemoteMessage
import com.mangala.wallet.core.ai.data.remote.providers.gemini.config.GeminiModels
import com.mangala.wallet.core.ai.data.remote.providers.gemini.mapping.toGeminiTool
import com.mangala.wallet.core.ai.data.remote.providers.gemini.request.GeminiGenerateContentRequest
import com.mangala.wallet.core.ai.data.remote.providers.gemini.request.GeminiGenerateContentRequest.Content.Part.FunctionCall
import com.mangala.wallet.core.ai.data.remote.providers.gemini.response.GeminiGenerateContentResponse
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.security.models.SecurityLevel
import kotlinx.serialization.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal class GeminiRemoteDataSource(
    private val geminiApi: GeminiApi,
    private val functionRegistry: FunctionRegistry
) : AiRemoteDataSource {

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse> {
        val model = getGeminiModel(isMultiModalEnabled)

        val generationConfig = getGenerationConfig(isMultiModalEnabled, model)

        val response = geminiApi.generateContent(
            apiKey = BuildKonfig.GEMINI_API_KEY,
            model = model.modelName,
            request = createRequest(conversationContext, generationConfig)
        )

        return mapToResponse(response, isMultiModalEnabled)

//        return geminiApi.streamGenerateContent(
//            apiKey = BuildKonfig.GEMINI_API_KEY,
//            model = GeminiModels.GEMINI_2_0_FLASH.modelName,
//            request = GeminiGenerateContentRequest(
//                contents = conversationContext.map { message ->
//                    GeminiGenerateContentRequest.Content(
//                        role = message.role,
//                        parts = when(message) {
//                            is Message.UserMessage -> listOf(
//                                GeminiGenerateContentRequest.Content.Part(text = message.content)
//                            )
//                            else -> emptyList()
//                        }
//                    )
//                },
//            )
//        ).asFlow<List<GeminiGenerateContentResponse>>(Json).map {
//            AIResponse.TextResponse(it.firstOrNull()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "")
//        }
    }

    override fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any {
        return functions.toGeminiTool()
    }

    private fun getGeminiModel(isMultiModalEnabled: Boolean) = if (isMultiModalEnabled) {
        GeminiModels.GEMINI_2_0_FLASH_PREVIEW_IMAGE_GENERATION
    } else {
        GeminiModels.GEMINI_2_0_FLASH
    }

    private fun getGenerationConfig(
        isMultiModalEnabled: Boolean,
        model: GeminiModels
    ) = if (isMultiModalEnabled) {
        GeminiGenerateContentRequest.GenerationConfig(
            responseModalities = model.supportedModalities?.map { it.value }
        )
    } else null

    private fun createRequest(
        conversationContext: List<RemoteMessage>,
        generationConfig: GeminiGenerateContentRequest.GenerationConfig?
    ) =
        GeminiGenerateContentRequest(
            contents = conversationContext.map { message ->
                // Remaps system prompt as user
                val role = if (message.role == "system") "user" else message.role

                GeminiGenerateContentRequest.Content(
                    role = role,
                    parts = when (message) {
                        is RemoteMessage.UserMessage -> {
                            if (message.contents.isEmpty()) {
                                listOf(GeminiGenerateContentRequest.Content.Part(text = message.text))
                            } else {
                                message.contents.map { content ->
                                    when (content) {
                                        is RemoteMessage.Content.Text -> {
                                            GeminiGenerateContentRequest.Content.Part(text = content.text)
                                        }

                                        is RemoteMessage.Content.FileData -> {
                                            GeminiGenerateContentRequest.Content.Part(
                                                fileData = GeminiGenerateContentRequest.Content.Part.FileData(
                                                    mimeType = content.mimeType,
                                                    fileUri = content.fileUri
                                                )
                                            )
                                        }

                                        is RemoteMessage.Content.InlineData -> {
                                            GeminiGenerateContentRequest.Content.Part(
                                                inlineData = GeminiGenerateContentRequest.Content.Part.InlineData(
                                                    mimeType = content.mimeType,
                                                    data = content.data
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is RemoteMessage.AssistantMessage -> listOf(
                            GeminiGenerateContentRequest.Content.Part(text = message.content)
                        )

                        is RemoteMessage.FunctionCallMessage -> {
                            listOf(
                                GeminiGenerateContentRequest.Content.Part(
                                    functionCall = FunctionCall(
                                        name = message.name,
                                        args = buildJsonObject {
                                            message.parameters.forEach {
                                                put(it.key, it.value.toString())
                                            }
                                        }
                                    )
                                )
                            )
                        }

                        is RemoteMessage.FunctionResultMessage -> {
                            val result = message.result

                            when (result) {
                                is FunctionResult.Success -> {
                                    listOf(
                                        GeminiGenerateContentRequest.Content.Part(
                                            functionResponse = GeminiGenerateContentRequest.Content.Part.FunctionResponse(
                                                name = message.name,
                                                response = result.data.let { result ->
                                                    buildJsonObject {
                                                        result.forEach {
                                                            put(
                                                                it.key,
                                                                it.value.toString()
                                                            ) // TODO: Check type
                                                        }
                                                    }
                                                }
                                            )
                                        )
                                    )
                                }
                                is FunctionResult.Error -> {
                                    listOf(
                                        GeminiGenerateContentRequest.Content.Part(
                                            functionResponse = GeminiGenerateContentRequest.Content.Part.FunctionResponse(
                                                name = message.name,
                                                response = buildJsonObject {
                                                    put("error", result.message)
                                                }
                                            )
                                        )
                                    )
                                }
                            }
                        }

                        is RemoteMessage.SystemMessage -> {
                            listOf(GeminiGenerateContentRequest.Content.Part(text = message.content))
                        }
                    }
                )
            },
            generationConfig = generationConfig,
            tools = listOf(functionRegistry.getFunctions().toGeminiTool())
        )

    @OptIn(ExperimentalEncodingApi::class)
    private fun mapToResponse(
        response: GeminiGenerateContentResponse,
        isMultiModalEnabled: Boolean,
    ): Flow<AIResponse> {
        val candidate = response.candidates?.firstOrNull()?.content
        val parts = candidate?.parts ?: emptyList()

        // Process all parts to handle mixed responses (text + function calls)
        val responses = mutableListOf<AIResponse>()
        var explanatoryText: String? = null
        var functionCallResponse: AIResponse? = null

        parts.forEach { part ->
            when {
                part?.text != null -> {
                    // Collect text parts - could be explanatory text before function call
                    if (explanatoryText == null) {
                        explanatoryText = part.text
                    } else {
                        explanatoryText += "\n${part.text}"
                    }
                }
                part?.functionCall != null -> {
                    val functionName = part.functionCall.name ?: ""
                    val functionArgs = part.functionCall.args ?: JsonObject(emptyMap())
                    
                    functionCallResponse = AIResponse.FunctionCallResponse(
                        FunctionCallRequest(
                            functionName,
                            functionArgs.toMap(),
                            securityLevel = functionRegistry.getFunctionByName(functionName)?.securityLevel ?: SecurityLevel.defaultSecurityLevel
                        ),
                        reasoning = explanatoryText // Include any explanatory text as reasoning
                    )
                }
                part?.inlineData != null -> {
                    val base64Data = part.inlineData.data
                    val mimeType = part.inlineData.mimeType

                    if (base64Data != null && mimeType != null) {
                        val imageData = Base64.decode(base64Data)
                        responses.add(AIResponse.ImageResponse(imageData, mimeType))
                    }
                }
            }
        }

        val functionCall = functionCallResponse

        // Build the final response based on what we found
        return when {
            // If we have both text and function call, return the function call with reasoning
            functionCall != null -> {
                flowOf(functionCall)
            }
            // If we only have text
            explanatoryText != null && responses.isEmpty() -> {
                flowOf(AIResponse.TextResponse(explanatoryText.orEmpty()))
            }
            // If we have multiple responses (images, etc.)
            responses.isNotEmpty() -> {
                if (explanatoryText != null) {
                    responses.add(0, AIResponse.TextResponse(explanatoryText.orEmpty()))
                }
                if (responses.size > 1) {
                    flowOf(AIResponse.MultiModalResponse(responses))
                } else {
                    flowOf(responses.first())
                }
            }
            // Default case
            else -> {
                flowOf(AIResponse.TextResponse(""))
            }
        }
    }
}