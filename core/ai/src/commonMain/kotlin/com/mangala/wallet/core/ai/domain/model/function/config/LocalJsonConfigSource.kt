package com.mangala.wallet.core.ai.domain.model.function.config

import com.mangala.wallet.core.ai.domain.model.function.FunctionParameter
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.security.models.SecurityLevel
import dev.icerock.moko.resources.FileResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Implementation of [FunctionConfigSource] that loads function configurations from a local JSON file.
 * This is the initial implementation for the function configuration system.
 */
class LocalJsonConfigSource(
    private val jsonFileResource: FileResource,
    private val json: Json,
    private val resourceReader: ResourceReader
) : FunctionConfigSource {
    
    private var configurations: List<FunctionConfig>? = null
    
    /**
     * Load function configurations from the JSON file resource.
     *
     * @return Flow of [FunctionConfig] objects
     */
    override suspend fun loadConfigurations(): Flow<FunctionConfig> = flow {
        val jsonContent = resourceReader.readResourceAsString(jsonFileResource)
        val response = json.decodeFromString<FunctionConfigsResponse>(jsonContent)
        
        val configs = response.tools
            .filter { it.type == "function" }
            .map { toolConfig ->
                val paramConfigs = toolConfig.parameters.properties.mapValues { (_, paramSchema) ->
                    ParameterConfig(
                        description = paramSchema.description,
                        required = toolConfig.parameters.required.contains(paramSchema.type),
                        enumValues = paramSchema.enum,
                        properties = paramSchema.properties?.mapValues { (_, nestedSchema) ->
                            ParameterConfig(
                                description = nestedSchema.description,
                                required = nestedSchema.required?.contains(nestedSchema.type) ?: false,
                                enumValues = nestedSchema.enum
                            )
                        }
                    )
                }

                FunctionConfig(
                    name = toolConfig.name,
                    description = toolConfig.description,
                    parameters = paramConfigs,
                    requiredParameters = toolConfig.parameters.required,
                    securityLevel = toolConfig.securityLevel,
                    version = "1.0"
                )
            }
        
        configurations = configs
        configs.forEach { emit(it) }
    }
    
    /**
     * Get a configuration by function name.
     *
     * @param functionName The name of the function to get the configuration for
     * @return The function configuration, or null if not found
     */
    override suspend fun getConfigurationByName(functionName: String): FunctionConfig? {
        if (configurations == null) {
            // Load configurations if not already loaded
            val configs = mutableListOf<FunctionConfig>()
            loadConfigurations().collect { configs.add(it) }
            configurations = configs
        }
        
        return configurations?.find { it.name == functionName }
    }
    
    /**
     * Get all configurations.
     *
     * @return List of all function configurations
     */
    override suspend fun getAllConfigurations(): List<FunctionConfig> {
        if (configurations == null) {
            // Load configurations if not already loaded
            val configs = mutableListOf<FunctionConfig>()
            loadConfigurations().collect { configs.add(it) }
            configurations = configs
        }
        
        return configurations ?: emptyList()
    }
    
    /**
     * Apply a configuration to a function definition.
     * This method updates the description and parameter information
     * in the function definition based on the configuration.
     *
     * @param definition The function definition to update
     * @param config The configuration to apply
     * @return The updated function definition
     */
    override fun applyConfiguration(definition: FunctionDefinition, config: FunctionConfig): FunctionDefinition {
        // Get the parameters from the definition
        val updatedParameters = definition.parameters.mapValues { (name, param) ->
            val paramConfig = config.parameters[name]
            if (paramConfig != null) {
                // Update the parameter with the configuration
                updateParameter(param, paramConfig)
            } else {
                // If there's no configuration for this parameter, keep it as is
                param
            }
        }
        
        // Use the required parameters from the configuration if provided,
        // otherwise keep the original required parameters
        val updatedRequiredParams = config.requiredParameters ?: definition.requiredParameters

        val updatedSecurityLevel = if (config.securityLevel != null) {
            SecurityLevel.parseSecurityLevel(config.securityLevel)
        } else {
            definition.securityLevel
        }

        // Return the updated function definition
        return definition.copy(
            description = config.description,
            parameters = updatedParameters,
            requiredParameters = updatedRequiredParams,
            securityLevel = updatedSecurityLevel
        )
    }
    
    /**
     * Update a function parameter with a parameter configuration.
     */
    private fun updateParameter(parameter: FunctionParameter, config: ParameterConfig): FunctionParameter {
        // Update nested properties if they exist
        val updatedProperties = if (parameter.properties != null && config.properties != null) {
            parameter.properties.mapValues { (name, prop) ->
                val propConfig = config.properties[name]
                if (propConfig != null) {
                    updateParameter(prop, propConfig)
                } else {
                    prop
                }
            }
        } else {
            parameter.properties
        }
        
        // Return the updated parameter
        return parameter.copy(
            description = config.description,
            required = config.required ?: parameter.required,
            enumValues = config.enumValues ?: parameter.enumValues,
            properties = updatedProperties
        )
    }
    
    /**
     * Validate that the configuration is valid.
     * This method checks that the configuration is well-formed
     * and safe to use (no injection attacks, etc.)
     *
     * @param config The configuration to validate
     * @return True if the configuration is valid, false otherwise
     */
    override fun validateConfiguration(config: FunctionConfig): Boolean {
        // Check for potential injection attacks in the description
        if (containsInjectionPatterns(config.description)) {
            return false
        }
        
        // Check parameter descriptions for injection attacks
        for ((_, paramConfig) in config.parameters) {
            if (containsInjectionPatterns(paramConfig.description)) {
                return false
            }
            
            // Check nested property descriptions for injection attacks
            paramConfig.properties?.forEach { (_, propConfig) ->
                if (containsInjectionPatterns(propConfig.description)) {
                    return false
                }
            }
        }
        
        return true
    }
    
    /**
     * Check if a string contains potential injection patterns.
     * This is a simple implementation that checks for common injection patterns.
     * In a production environment, this should be expanded with a more comprehensive check.
     */
    private fun containsInjectionPatterns(text: String): Boolean {
        val injectionPatterns = listOf(
            "{{", "}}", // Handlebars/Mustache injection
            "${", "}", // String interpolation
            "<script", "</script>", // Script tags
            "javascript:", // JavaScript protocol
            "data:", // Data URI scheme
            "onerror=", "onload=", // Event handlers
            "\\", // Escape character
            "eval\\(", "Function\\(" // JavaScript eval
        )
        
        return injectionPatterns.any { text.contains(it, ignoreCase = true) }
    }
}