package com.mangala.wallet.core.ai.domain.model.function

/**
 * Defines a parameter for a function
 *
 * @property name The name of the parameter
 * @property type The type of the parameter
 * @property description The description of the parameter
 * @property required Whether the parameter is required
 * @property enumValues Possible values for enum-like parameters
 * @property properties Nested properties for object parameters
 */
data class FunctionParameter(
    val name: String,
    val type: ParameterType,
    val description: String,
    val required: Boolean = false,
    val enumValues: List<String>? = null,
    val properties: Map<String, FunctionParameter>? = null
)