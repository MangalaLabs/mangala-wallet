package com.mangala.wallet.core.ai.domain.model.function.config

import kotlinx.serialization.Serializable

/**
 * Response containing a list of function configurations.
 * This format matches the structure of the function_calls.json file.
 */
@Serializable
data class FunctionConfigsResponse(
    /**
     * List of function configurations.
     */
    val tools: List<ToolConfig> = emptyList()
)

/**
 * Representation of a tool in the configuration file.
 * In the current implementation, we only support function tools.
 */
@Serializable
data class ToolConfig(
    /**
     * Type of the tool. Currently only "function" is supported.
     */
    val type: String,
    
    /**
     * Name of the function.
     */
    val name: String,
    
    /**
     * Description of what the function does.
     */
    val description: String,
    
    /**
     * Parameters for the function.
     */
    val parameters: ParametersSchema,
    
    /**
     * Whether the function requires user confirmation before execution.
     */
    val securityLevel: String? = null
)

/**
 * Schema for function parameters.
 */
@Serializable
data class ParametersSchema(
    /**
     * Type of the parameters object. Always "object".
     */
    val type: String,
    
    /**
     * Properties of the parameters object.
     */
    val properties: Map<String, ParameterSchema>,
    
    /**
     * List of required parameter names.
     */
    val required: List<String> = emptyList()
)

/**
 * Schema for an individual parameter.
 */
@Serializable
data class ParameterSchema(
    /**
     * Type of the parameter.
     */
    val type: String,
    
    /**
     * Description of what the parameter does.
     */
    val description: String,
    
    /**
     * Possible values for enum-like parameters.
     */
    val enum: List<String>? = null,
    
    /**
     * Properties for object-type parameters.
     */
    val properties: Map<String, ParameterSchema>? = null,
    
    /**
     * Required properties for object-type parameters.
     */
    val required: List<String>? = null
)