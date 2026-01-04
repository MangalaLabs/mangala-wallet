package com.mangala.wallet.core.ai.domain.model.function.config

import com.mangala.wallet.core.ai.domain.model.function.definition.DefaultFunctionRegistry
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * An extension of [DefaultFunctionRegistry] that supports loading function configurations
 * from external sources.
 */
open class ConfigurableFunctionRegistry(
    plugins: List<FunctionPlugin>,
    private val configSource: FunctionConfigSource
) : DefaultFunctionRegistry(plugins), FunctionRegistry {
    
    private val TAG = "ConfigurableFunctionRegistry"
    protected val coroutineScope = CoroutineScope(Dispatchers.Default)
    
    /**
     * Initialize the registry by loading functions from plugins and then applying any
     * configurations from the config source.
     */
    init {
        coroutineScope.launch {
            try {
                loadAndApplyConfigurations()
            } catch (e: Exception) {
                println("Error loading configurations: ${e.message}")
            }
        }
    }
    
    /**
     * Load configurations from the config source and apply them to the registered functions.
     */
    protected open suspend fun loadAndApplyConfigurations() {
        configSource.loadConfigurations()
            .catch { e ->
                println("Error loading configurations: ${e.message}")
            }
            .collect { config ->
                val functionName = config.name
                val function = super.getFunctionByName(functionName)
                
                if (function != null) {
                    // Apply the configuration to the function
                    val updatedFunction = configSource.applyConfiguration(function, config)
                    
                    // Replace the function in the registry
                    replaceFunction(updatedFunction)

                    println("Applied configuration for function: $functionName")
                } else {
                    println("Function not found for configuration: $functionName")
                }
            }
    }
    
    /**
     * Replace a function in the registry with an updated version.
     * This directly updates the parent's registry to ensure that
     * the changes are visible to all consumers of getFunctions().
     */
    protected open fun replaceFunction(function: FunctionDefinition) {
        // Re-register the function with the superclass, which will update the main functions map
        super.registerFunction(function)
        
        println("Function ${function.name} updated with new configuration")
    }
    
    /**
     * Refresh configurations from the config source.
     * This can be called to update configurations at runtime.
     */
    suspend fun refreshConfigurations() {
        loadAndApplyConfigurations()
    }
}