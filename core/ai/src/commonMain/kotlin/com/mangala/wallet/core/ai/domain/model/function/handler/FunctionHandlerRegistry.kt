package com.mangala.wallet.core.ai.domain.model.function.handler

/**
 * Registry for function handlers that execute functions
 */
interface FunctionHandlerRegistry {
    /**
     * Register a function handler plugin with the registry
     * 
     * @param plugin The function handler plugin to register
     */
    fun registerPlugin(plugin: FunctionHandlerPlugin)
    
    /**
     * Register a single function handler with the registry
     * 
     * @param handler The function handler to register
     */
    fun registerHandler(handler: FunctionHandler)
    
    /**
     * Get all registered function handlers
     * 
     * @return List of all registered function handlers
     */
    fun getHandlers(): List<FunctionHandler>
    
    /**
     * Get a function handler by its function name
     * 
     * @param functionName The name of the function
     * @return The function handler, or null if not found
     */
    fun getHandlerByName(functionName: String): FunctionHandler?
    
    /**
     * Check if a handler exists for a function
     * 
     * @param functionName The name of the function
     * @return True if a handler exists, false otherwise
     */
    fun hasHandler(functionName: String): Boolean
}