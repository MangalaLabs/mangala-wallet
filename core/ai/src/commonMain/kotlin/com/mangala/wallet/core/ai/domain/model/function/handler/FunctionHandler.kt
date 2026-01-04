package com.mangala.wallet.core.ai.domain.model.function.handler

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult

/**
 * Handler interface for executing functions
 */
interface FunctionHandler {
    /**
     * The name of the function this handler handles
     */
    val functionName: String
    
    /**
     * Execute the function with the given parameters
     * 
     * @param parameters The parameters to pass to the function
     * @return The result of the function execution
     */
    suspend fun execute(parameters: Map<String, Any?>): FunctionResult
}