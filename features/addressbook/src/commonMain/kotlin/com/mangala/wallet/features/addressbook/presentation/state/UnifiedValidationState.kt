package com.mangala.wallet.features.addressbook.presentation.state

import com.mangala.wallet.features.addressbook.domain.validation.WalletValidationResult

/**
 * Unified validation state that provides a single source of truth
 * for all validation states across the AddressBook feature.
 * 
 * This replaces the scattered validation state management with a
 * centralized, immutable state structure.
 */
data class UnifiedValidationState(
    val fields: Map<String, FieldValidationState> = emptyMap(),
    val isAnyFieldValidating: Boolean = false,
    val canSave: Boolean = true,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Updates the validation state for a specific field
     */
    fun updateFieldState(fieldId: String, state: FieldValidationState): UnifiedValidationState {
        val newFields = fields + (fieldId to state)
        return copy(
            fields = newFields,
            isAnyFieldValidating = newFields.any { it.value.isValidating },
            canSave = computeCanSave(newFields),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Updates multiple field states at once
     */
    fun updateMultipleFieldStates(updates: Map<String, FieldValidationState>): UnifiedValidationState {
        val newFields = fields + updates
        return copy(
            fields = newFields,
            isAnyFieldValidating = newFields.any { it.value.isValidating },
            canSave = computeCanSave(newFields),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Clears the validation state for a specific field
     */
    fun clearFieldState(fieldId: String): UnifiedValidationState {
        val newFields = fields - fieldId
        return copy(
            fields = newFields,
            isAnyFieldValidating = newFields.any { it.value.isValidating },
            canSave = computeCanSave(newFields),
            lastUpdated = System.currentTimeMillis()
        )
    }
    
    /**
     * Clears all validation states
     */
    fun clearAll(): UnifiedValidationState {
        return UnifiedValidationState()
    }
    
    /**
     * Gets the validation state for a specific field
     */
    fun getFieldState(fieldId: String): FieldValidationState? {
        return fields[fieldId]
    }
    
    /**
     * Checks if a specific field is valid
     */
    fun isFieldValid(fieldId: String): Boolean {
        return fields[fieldId]?.isValid ?: true
    }
    
    /**
     * Checks if a specific field has an error
     */
    fun hasFieldError(fieldId: String): Boolean {
        return fields[fieldId]?.hasError ?: false
    }
    
    /**
     * Gets all fields with errors
     */
    fun getFieldsWithErrors(): Map<String, FieldValidationState> {
        return fields.filter { it.value.hasError }
    }
    
    /**
     * Gets all fields with warnings
     */
    fun getFieldsWithWarnings(): Map<String, FieldValidationState> {
        return fields.filter { it.value.hasWarning }
    }
    
    /**
     * Computes whether the form can be saved based on all field states
     */
    private fun computeCanSave(fields: Map<String, FieldValidationState>): Boolean {
        // Cannot save if any field is validating
        if (fields.any { it.value.isValidating }) {
            return false
        }
        
        // Cannot save if any required field is empty or has error
        if (fields.any { it.value.isRequired && (it.value.isEmpty || it.value.hasError) }) {
            return false
        }
        
        // Cannot save if any field has a blocking error
        if (fields.any { it.value.hasBlockingError }) {
            return false
        }
        
        // Warnings don't prevent saving
        return true
    }
}

/**
 * Represents the validation state of a single field
 */
data class FieldValidationState(
    val fieldId: String,
    val value: String = "",
    val status: ValidationStatus = ValidationStatus.IDLE,
    val validationResult: WalletValidationResult? = null,
    val isRequired: Boolean = false,
    val lastValidated: Long? = null
) {
    val isEmpty: Boolean get() = value.isBlank()
    val isValidating: Boolean get() = status == ValidationStatus.VALIDATING
    val isValid: Boolean get() = status == ValidationStatus.VALID || 
                                 (status == ValidationStatus.WARNING && !hasBlockingError)
    
    val hasError: Boolean get() = status == ValidationStatus.ERROR || 
                                   (validationResult is WalletValidationResult.Error)
    
    val hasWarning: Boolean get() = status == ValidationStatus.WARNING || 
                                     (validationResult is WalletValidationResult.Warning)
    
    val hasBlockingError: Boolean get() = hasError && 
                                           validationResult?.let { 
                                               it is WalletValidationResult.Error 
                                           } ?: false
    
    val errorMessage: String? get() = when (validationResult) {
        is WalletValidationResult.Error -> validationResult.message
        is WalletValidationResult.Warning -> validationResult.message
        else -> null
    }
    
    /**
     * Updates the field with a new value and resets validation state
     */
    fun updateValue(newValue: String): FieldValidationState {
        return copy(
            value = newValue,
            status = if (newValue.isBlank()) ValidationStatus.IDLE else ValidationStatus.TYPING,
            validationResult = null
        )
    }
    
    /**
     * Updates the field to validating state
     */
    fun startValidating(): FieldValidationState {
        return copy(
            status = ValidationStatus.VALIDATING,
            validationResult = null
        )
    }
    
    /**
     * Updates the field with validation result
     */
    fun completeValidation(result: WalletValidationResult): FieldValidationState {
        val newStatus = when (result) {
            is WalletValidationResult.Success -> ValidationStatus.VALID
            is WalletValidationResult.Warning -> ValidationStatus.WARNING
            is WalletValidationResult.Error -> ValidationStatus.ERROR
        }
        
        return copy(
            status = newStatus,
            validationResult = result,
            lastValidated = System.currentTimeMillis()
        )
    }
    
    /**
     * Clears the validation state
     */
    fun clearValidation(): FieldValidationState {
        return copy(
            status = if (value.isBlank()) ValidationStatus.IDLE else ValidationStatus.TYPING,
            validationResult = null,
            lastValidated = null
        )
    }
}

/**
 * Validation status enum that replaces ValidationLoadingState
 */
enum class ValidationStatus {
    IDLE,      // No validation performed yet
    TYPING,    // User is typing, validation pending
    VALIDATING, // Validation in progress
    VALID,     // Validation passed
    WARNING,   // Validation passed with warnings
    ERROR      // Validation failed
}

/**
 * Extension functions for convenience
 */
fun UnifiedValidationState.withFieldValue(fieldId: String, value: String): UnifiedValidationState {
    val currentState = fields[fieldId] ?: FieldValidationState(fieldId = fieldId)
    return updateFieldState(fieldId, currentState.updateValue(value))
}

fun UnifiedValidationState.withFieldValidating(fieldId: String): UnifiedValidationState {
    val currentState = fields[fieldId] ?: FieldValidationState(fieldId = fieldId)
    return updateFieldState(fieldId, currentState.startValidating())
}

fun UnifiedValidationState.withFieldValidationResult(
    fieldId: String, 
    result: WalletValidationResult
): UnifiedValidationState {
    val currentState = fields[fieldId] ?: FieldValidationState(fieldId = fieldId)
    return updateFieldState(fieldId, currentState.completeValidation(result))
}