package com.mangala.wallet.core.ai.domain.model.function.definition

import com.mangala.wallet.core.ai.domain.model.function.FunctionParameter
import com.mangala.wallet.core.security.models.SecurityLevel

/**
 * Defines a function that can be registered and executed
 *
 * @property name The name of the function
 * @property description A description of what the function does
 * @property parameters Map of parameter name to parameter definition
 * @property requiredParameters List of required parameter names
 * @property moduleId Identifier for the module that owns this function
 */
data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: Map<String, FunctionParameter>,
    val requiredParameters: List<String>,
    val moduleId: String,
    val securityLevel: SecurityLevel
)