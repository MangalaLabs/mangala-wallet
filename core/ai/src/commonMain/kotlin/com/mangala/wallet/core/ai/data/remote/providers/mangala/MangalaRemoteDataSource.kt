package com.mangala.wallet.core.ai.data.remote.providers.mangala

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.data.remote.AiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.RemoteMessage
import com.mangala.wallet.core.ai.data.remote.providers.mangala.request.MangalaRequest
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.ai.domain.model.message.UiTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class MangalaRemoteDataSource(
    private val mangalaApi: MangalaApi,
    private val functionRegistry: FunctionRegistry
) : AiRemoteDataSource {

    override suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse> {
        val request = MangalaRequest(
            sender = userId,
            message = (conversationContext.last() as RemoteMessage.UserMessage).text.orEmpty()
        )

        val response = try {
            mangalaApi.sendMessage(request)
        } catch (e: Exception) {
            return flowOf(AIResponse.ErrorResponse("Error calling Mangala API: ${e.message}"))
        }
        
        val textResponses = mutableListOf<String>()
        val uiTags = mutableListOf<UiTag>()
        
        response.forEach { responseItem ->
            responseItem.text?.let { text ->
                textResponses.add(text)
            }
            
            responseItem.custom?.let { customAction ->
                when (customAction.actionType) {
                    "show_network_list" -> {
                        uiTags.add(UiTag.SelectNetwork)
                    }
                    "request_address_input" -> {
                        uiTags.add(UiTag.RequestAddressInput)
                    }
                }
            }
        }
        
        val combinedText = textResponses.joinToString("\n").ifEmpty { "No text in Mangala response" }
        
        return flowOf(AIResponse.TextResponse(combinedText, uiTags))
    }

    override fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any {
        return emptyList<Any>()
    }
}