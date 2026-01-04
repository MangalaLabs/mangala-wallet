package com.mangala.wallet.core.ai.domain.model.function.definition

/**
 * Registry for function definitions that can be executed by the function executor
 */
interface FunctionRegistry {
    /**
     * Register a function with the registry
     * 
     * @param function The function definition to register
     */
    fun registerFunction(function: FunctionDefinition)
    
    /**
     * Get all registered functions
     * 
     * @return List of all registered function definitions
     */
    fun getFunctions(): List<FunctionDefinition>
    
    /**
     * Get all functions registered for a specific module
     * 
     * @param moduleId The ID of the module
     * @return List of function definitions for the specified module
     */
    fun getFunctionsByModule(moduleId: String): List<FunctionDefinition>
    
    /**
     * Get a function by its name
     * 
     * @param name The name of the function
     * @return The function definition, or null if not found
     */
    fun getFunctionByName(name: String): FunctionDefinition?
}