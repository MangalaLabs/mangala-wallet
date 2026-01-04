package com.mangala.wallet.core.ai.data.remote.providers.openai

import com.mangala.wallet.core.ai.BuildKonfig
import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.data.remote.AiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.RemoteMessage
import com.mangala.wallet.core.ai.data.remote.providers.openai.config.OpenAiModels
import com.mangala.wallet.core.ai.data.remote.providers.openai.mapping.toOpenAiTools
import com.mangala.wallet.core.ai.data.remote.providers.openai.request.OpenAiChatCompletionRequest
import com.mangala.wallet.core.ai.data.remote.providers.openai.request.OpenAiChatCompletionRequest.ContentItem
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.security.models.SecurityLevel
import kotlinx.serialization.json.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.uuid.ExperimentalUuidApi

internal class OpenAiRemoteDataSource(
    private val openAiApi: OpenAiApi,
    private val functionRegistry: FunctionRegistry
): AiRemoteDataSource {

    @OptIn(ExperimentalEncodingApi::class, ExperimentalUuidApi::class)
    override suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse> {
        val model = getModel(isMultiModalEnabled)

        val messages = mapMessages(conversationContext)

        val response = openAiApi.createChatCompletion(
            authorization = "Bearer ${BuildKonfig.OPENAI_API_KEY}",
            request = OpenAiChatCompletionRequest(
                model = model.modelName,
                input = messages,
                tools = functionRegistry.getFunctions().toOpenAiTools(),
                temperature = 0.7
            )
        )

        val outputs = response.outputs

        // Check for function calls in the output
        val functionCall = outputs?.find { it.type == "function_call" }
        if (functionCall != null) {
            val functionName = functionCall.name ?: ""
            val functionArgs = try {
                Json.parseToJsonElement(functionCall.arguments ?: "{}").jsonObject
            } catch (e: Exception) {
                JsonObject(emptyMap())
            }

            return flowOf(
                AIResponse.FunctionCallResponse(
                    FunctionCallRequest(
                        functionName,
                        functionArgs.toMap(),
                        callId = functionCall.callId,
                        functionRegistry.getFunctionByName("example_function")?.securityLevel ?: SecurityLevel.defaultSecurityLevel
                    )
                )
            )
        }

        // Check for text content
        val textOutput = outputs?.find { it.type == "message" }
        val textContent = textOutput?.content?.firstOrNull()?.text
        return flowOf(
            AIResponse.TextResponse(textContent ?: "")
        )
    }

    override fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any {
        return functions.toOpenAiTools()
    }

    private fun getModel(isMultiModalEnabled: Boolean) = if (isMultiModalEnabled) {
        OpenAiModels.GPT_4O
    } else {
        OpenAiModels.GPT_4O_MINI
    }

    private fun mapMessages(conversationContext: List<RemoteMessage>) =
        conversationContext.mapNotNull { message ->
            when (message) {
                is RemoteMessage.UserMessage -> {
                    val contentItems = if (message.contents.isEmpty()) {
                        // Legacy support for text-only messages
                        listOf(ContentItem(type = "input_text", text = message.text))
                    } else {
                        // Convert each content item
                        message.contents.map { content ->
                            when (content) {
                                is RemoteMessage.Content.Text -> {
                                    ContentItem(type = "input_text", text = content.text)
                                }

                                is RemoteMessage.Content.FileData -> {
                                    ContentItem(
                                        type = "image_url",
                                        imageUrl = content.fileUri,
                                    )
                                }

                                is RemoteMessage.Content.InlineData -> {
                                    ContentItem(
                                        type = "input_image",
                                        imageUrl = "data:${content.mimeType};base64,${content.data}",
                                    )
                                }
                            }
                        }
                    }
                    OpenAiChatCompletionRequest.Message(
                        role = "user",
                        content = contentItems
                    )
                }

                is RemoteMessage.AssistantMessage -> {
                    OpenAiChatCompletionRequest.Message(
                        role = "assistant",
                        content = listOf(ContentItem(type = "output_text", text = message.content))
                    )
                }

                is RemoteMessage.FunctionCallMessage -> {
                    OpenAiChatCompletionRequest.Message(
                        type = "function_call",
                        arguments = Json.encodeToString(
                            JsonObject.serializer(),
                            buildJsonObject {
                                message.parameters.forEach {
                                    put(it.key, it.value.toString())
                                }
                            }
                        ),
                        toolCallId = message.callId,
                        name = message.name
                    )
                }

                is RemoteMessage.FunctionResultMessage -> {
                    val result = when (val functionResult = message.result) {
                        is FunctionResult.Success -> functionResult.data.toString()
                        is FunctionResult.Error -> functionResult
                        else -> "Function execution completed"
                    }

                    OpenAiChatCompletionRequest.Message(
                        output = result.toString(),
                        type = "function_call_output",
                        toolCallId = message.callId,
                    )
                }

                else -> null
            }
        }

    private fun JsonObject.toMap(): Map<String, Any> {
        return entries.associate { (key, value) ->
            key to when (value) {
                is JsonPrimitive -> when {
                    value.isString -> value.content
                    else -> value
                }
                else -> value
            }
        }
    }
}