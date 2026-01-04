package com.mangala.wallet.features.addressbook.presentation.contact.edit

import com.mangala.wallet.features.addressbook.presentation.contact.validation.ValidationLoadingState

/**
 * Detailed validation state for a field
 * Provides all information needed for UI display
 */
data class FieldValidationState(
    val fieldId: String,
    val state: ValidationLoadingState = ValidationLoadingState.IDLE,
    val errorMessage: String? = null,
    val warningMessage: String? = null,
    val suggestion: String? = null,
    val lastValidatedValue: String? = null,
    val isModified: Boolean = false
) {
    /**
     * Check if field has any issues
     */
    val hasIssues: Boolean
        get() = state == ValidationLoadingState.INVALID || 
                state == ValidationLoadingState.WARNING
    
    /**
     * Check if field is in a good state
     */
    val isValid: Boolean
        get() = state == ValidationLoadingState.VALID
    
    /**
     * Check if field is currently being validated
     */
    val isValidating: Boolean
        get() = state == ValidationLoadingState.VALIDATING
    
    /**
     * Get the appropriate message to display
     */
    val displayMessage: String?
        get() = when (state) {
            ValidationLoadingState.INVALID -> errorMessage
            ValidationLoadingState.WARNING -> warningMessage
            else -> null
        }
    
    /**
     * Get the appropriate color for the field border
     * Returns a descriptive string that UI can map to actual colors
     */
    val borderColorHint: String
        get() = when (state) {
            ValidationLoadingState.IDLE -> "default"
            ValidationLoadingState.TYPING -> "primary"
            ValidationLoadingState.VALIDATING -> "primary"
            ValidationLoadingState.VALID -> "success"
            ValidationLoadingState.INVALID -> "error"
            ValidationLoadingState.WARNING -> "warning"
        }
    
    /**
     * Check if should show trailing icon
     */
    val shouldShowIcon: Boolean
        get() = state != ValidationLoadingState.IDLE && 
                state != ValidationLoadingState.TYPING
}

/**
 * Extension functions for easy state updates
 */
fun FieldValidationState.toTyping(): FieldValidationState {
    return copy(
        state = ValidationLoadingState.TYPING,
        isModified = true
    )
}

fun FieldValidationState.toValidating(): FieldValidationState {
    return copy(
        state = ValidationLoadingState.VALIDATING,
        errorMessage = null,
        warningMessage = null
    )
}

fun FieldValidationState.toValid(cleanValue: String? = null): FieldValidationState {
    return copy(
        state = ValidationLoadingState.VALID,
        errorMessage = null,
        warningMessage = null,
        lastValidatedValue = cleanValue ?: lastValidatedValue
    )
}

fun FieldValidationState.toInvalid(error: String): FieldValidationState {
    return copy(
        state = ValidationLoadingState.INVALID,
        errorMessage = error,
        warningMessage = null
    )
}

fun FieldValidationState.toWarning(warning: String): FieldValidationState {
    return copy(
        state = ValidationLoadingState.WARNING,
        warningMessage = warning,
        errorMessage = null
    )
}

fun FieldValidationState.toIdle(): FieldValidationState {
    return copy(
        state = ValidationLoadingState.IDLE,
        errorMessage = null,
        warningMessage = null,
        suggestion = null
    )
}

fun FieldValidationState.withSuggestion(suggestion: String): FieldValidationState {
    return copy(suggestion = suggestion)
}