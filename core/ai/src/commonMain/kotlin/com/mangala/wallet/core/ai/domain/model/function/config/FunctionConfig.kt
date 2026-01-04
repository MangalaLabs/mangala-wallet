package com.mangala.wallet.core.ai.domain.model.function.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Configuration for a function that can be loaded from external sources.
 * This allows function descriptions and parameter information to be updated
 * without code changes.
 */
@Serializable
data class FunctionConfig(
    /**
     * The name of the function. This must match the name in the function definition.
     */
    val name: String,
    
    /**
     * Description of what the function does. This can be updated without code changes.
     */
    val description: String,
    
    /**
     * Parameters for the function. The keys must match the parameters in the function definition.
     */
    val parameters: Map<String, ParameterConfig> = emptyMap(),
    
    /**
     * The list of required parameter names. If specified, this overrides the
     * required parameters in the function definition.
     */
    @SerialName("required_parameters")
    val requiredParameters: List<String>? = null,
    
    /**
     * Whether the function requires user confirmation before execution.
     * If null, the value from the function definition is used.
     */
    @SerialName("securityLevel")
    val securityLevel: String? = null,
    
    /**
     * Version of this configuration. Can be used to ensure compatibility.
     */
    val version: String = "1.0"
)

/**
 * Configuration for a function parameter that can be loaded from external sources.
 */
@Serializable
data class ParameterConfig(
    /**
     * Description of what the parameter does. This can be updated without code changes.
     */
    val description: String,
    
    /**
     * Whether the parameter is required. If null, the value from the function definition is used.
     */
    val required: Boolean? = null,
    
    /**
     * Possible values for enum-like parameters. If null, the values from the function definition are used.
     */
    @SerialName("enum_values")
    val enumValues: List<String>? = null,
    
    /**
     * Nested properties for object parameters. If null, the properties from the function definition are used.
     */
    val properties: Map<String, ParameterConfig>? = null
)