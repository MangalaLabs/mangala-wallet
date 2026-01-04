package com.mangala.wallet.core.ai.domain.model.function

/**
 * Represents the result of a function execution
 */
sealed class FunctionResult {
    /**
     * Represents a successful function execution
     *
     * @property data The data returned by the function
     * @property uiHint Optional hint for UI rendering
     */
    data class Success(
        val data: Map<String, Any?>,
        val uiHint: UiHint? = null
    ) : FunctionResult()

    /**
     * Represents a failed function execution
     *
     * @property code The error code
     * @property message The error message
     */
    data class Error(val code: String, val message: String) : FunctionResult()
    
    /**
     * Represents a hint for UI rendering
     *
     * @property type The type of UI element (e.g., "list", "detail", "confirmation")
     * @property renderer The specific renderer to use (e.g., "contact_list", "transaction_list")
     * @property metadata Additional metadata for the renderer
     */
    data class UiHint(
        val type: String,
        val renderer: String,
        val metadata: Map<String, Any?> = emptyMap()
    )
}