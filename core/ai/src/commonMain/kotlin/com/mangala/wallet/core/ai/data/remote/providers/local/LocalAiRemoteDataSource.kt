package com.mangala.wallet.core.ai.data.remote.providers.local

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.data.remote.AiRemoteDataSource
import com.mangala.wallet.core.ai.data.remote.RemoteMessage
import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.security.models.SecurityLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * AI service implementation for locally-run models
 *
 * @property modelPath Path to the local model file
 * @property functionRegistry Registry for available functions
 * @property functionExecutor Executor for functions
 */
internal class LocalAiRemoteDataSource(
    private val modelPath: String,
    private val functionRegistry: FunctionRegistry,
) : AiRemoteDataSource {
    
    override suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse> = flow {
        try {
            // In a real implementation, this would load and run a local model
            // For this implementation, we'll just emit a placeholder response
            emit(AIResponse.TextResponse("This is a placeholder response from Local AI service"))
            
            // Example of a function call response
            emit(
                AIResponse.FunctionCallResponse(
                    FunctionCallRequest(
                        name = "example_function",
                        parameters = mapOf("param1" to "value1"),
                        securityLevel = functionRegistry.getFunctionByName("example_function")?.securityLevel ?: SecurityLevel.defaultSecurityLevel
                    ),
                    reasoning = "This is a placeholder reasoning"
                )
            )
        } catch (e: Exception) {
            emit(AIResponse.ErrorResponse("Error processing message: ${e.message}"))
        }
    }
    
    override fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any {
        // In a real implementation, this would convert the function definitions to a format appropriate
        // for the local model. For this implementation, we'll just return a placeholder
        return functions.map { function ->
            mapOf(
                "name" to function.name,
                "description" to function.description,
                "parameters" to function.parameters
            )
        }
    }
}