package com.mangala.wallet.core.ai.domain.model.function.config

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Source for function configuration data that can be loaded from different sources.
 * This is part of the function configuration system that allows function descriptions
 * and parameters to be updated without code changes.
 */
interface FunctionConfigSource {
    /**
     * Load function configurations from the source.
     *
     * @return Flow of [FunctionConfig] objects that represent function configurations
     */
    suspend fun loadConfigurations(): Flow<FunctionConfig>
    
    /**
     * Get a configuration by function name.
     *
     * @param functionName The name of the function to get the configuration for
     * @return The function configuration, or null if not found
     */
    suspend fun getConfigurationByName(functionName: String): FunctionConfig?
    
    /**
     * Get all configurations.
     *
     * @return List of all function configurations
     */
    suspend fun getAllConfigurations(): List<FunctionConfig>
    
    /**
     * Apply a configuration to a function definition.
     * This method should update the description and parameter information
     * in the function definition based on the configuration.
     *
     * @param definition The function definition to update
     * @param config The configuration to apply
     * @return The updated function definition
     */
    fun applyConfiguration(definition: FunctionDefinition, config: FunctionConfig): FunctionDefinition
    
    /**
     * Validate that the configuration is valid.
     * This method should check that the configuration is well-formed
     * and safe to use (no injection attacks, etc.)
     *
     * @param config The configuration to validate
     * @return True if the configuration is valid, false otherwise
     */
    fun validateConfiguration(config: FunctionConfig): Boolean
}