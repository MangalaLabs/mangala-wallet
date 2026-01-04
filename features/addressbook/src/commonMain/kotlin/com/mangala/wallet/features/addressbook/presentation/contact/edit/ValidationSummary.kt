package com.mangala.wallet.features.addressbook.presentation.contact.edit

import com.mangala.wallet.features.addressbook.presentation.contact.validation.ValidationLoadingState

/**
 * Validation summary to display all errors in one place
 */
data class ValidationSummary(
    val errorCount: Int = 0,
    val warningCount: Int = 0,
    val errors: List<ValidationIssue> = emptyList(),
    val warnings: List<ValidationIssue> = emptyList(),
    val isExpanded: Boolean = true
) {
    val hasIssues: Boolean
        get() = errorCount > 0 || warningCount > 0
    
    val totalIssues: Int
        get() = errorCount + warningCount
    
    val summaryText: String
        get() = when {
            errorCount > 0 && warningCount > 0 -> 
                "$errorCount error${if (errorCount > 1) "s" else ""}, $warningCount warning${if (warningCount > 1) "s" else ""}"
            errorCount > 0 -> 
                "$errorCount error${if (errorCount > 1) "s" else ""}"
            warningCount > 0 -> 
                "$warningCount warning${if (warningCount > 1) "s" else ""}"
            else -> "No issues"
        }
}

/**
 * Individual validation issue
 */
data class ValidationIssue(
    val fieldId: String,
    val fieldName: String,
    val message: String,
    val type: IssueType,
    val suggestion: String? = null,
    val actions: List<ValidationAction> = emptyList()
) {
    /**
     * Get user-friendly field name
     */
    val displayFieldName: String
        get() = when {
            fieldId.startsWith("wallet_address_") -> {
                val index = fieldId.substringAfter("wallet_address_").toIntOrNull() ?: 0
                "Wallet Address ${index + 1}"
            }
            fieldId.startsWith("email_") -> {
                val index = fieldId.substringAfter("email_").toIntOrNull() ?: 0
                "Email ${index + 1}"
            }
            fieldId.startsWith("phone_") -> {
                val index = fieldId.substringAfter("phone_").toIntOrNull() ?: 0
                "Phone ${index + 1}"
            }
            fieldId == "name" -> "Contact Name"
            fieldId == "note" -> "Note"
            else -> fieldName
        }
}

/**
 * Type of validation issue
 */
enum class IssueType {
    ERROR,
    WARNING
}

/**
 * Action that can be taken to fix an issue
 */
data class ValidationAction(
    val label: String,
    val actionId: String
)

/**
 * Helper to build validation summary from state
 */
object ValidationSummaryBuilder {
    
    fun build(
        validationErrors: Map<String, String>,
        fieldValidationStates: Map<String, FieldValidationState>,
        exchangeWarnings: Map<String, String>,
        testnetWarnings: Map<String, String>
    ): ValidationSummary {
        val errors = mutableListOf<ValidationIssue>()
        val warnings = mutableListOf<ValidationIssue>()
        
        // Add validation errors
        validationErrors.forEach { (fieldId, error) ->
            errors.add(
                ValidationIssue(
                    fieldId = fieldId,
                    fieldName = fieldId,
                    message = error,
                    type = IssueType.ERROR,
                    suggestion = fieldValidationStates[fieldId]?.suggestion
                )
            )
        }
        
        // Add field validation errors/warnings
        fieldValidationStates.forEach { (fieldId, state) ->
            when (state.state) {
                ValidationLoadingState.INVALID -> {
                    if (!validationErrors.containsKey(fieldId)) {
                        state.errorMessage?.let { error ->
                            errors.add(
                                ValidationIssue(
                                    fieldId = fieldId,
                                    fieldName = fieldId,
                                    message = error,
                                    type = IssueType.ERROR,
                                    suggestion = state.suggestion
                                )
                            )
                        }
                    }
                }
                ValidationLoadingState.WARNING -> {
                    state.warningMessage?.let { warning ->
                        warnings.add(
                            ValidationIssue(
                                fieldId = fieldId,
                                fieldName = fieldId,
                                message = warning,
                                type = IssueType.WARNING,
                                suggestion = state.suggestion
                            )
                        )
                    }
                }
                else -> { /* No issues */ }
            }
        }
        
        // Add exchange warnings
        exchangeWarnings.forEach { (walletId, warning) ->
            val fieldId = "wallet_$walletId"
            warnings.add(
                ValidationIssue(
                    fieldId = fieldId,
                    fieldName = fieldId,
                    message = warning,
                    type = IssueType.WARNING,
                    actions = listOf(
                        ValidationAction("Learn more", "learn_exchange"),
                        ValidationAction("Continue anyway", "accept_exchange")
                    )
                )
            )
        }
        
        // Add testnet warnings
        testnetWarnings.forEach { (walletId, warning) ->
            val fieldId = "wallet_$walletId"
            warnings.add(
                ValidationIssue(
                    fieldId = fieldId,
                    fieldName = fieldId,
                    message = warning,
                    type = IssueType.WARNING,
                    actions = listOf(
                        ValidationAction("Switch to mainnet", "switch_mainnet")
                    )
                )
            )
        }
        
        return ValidationSummary(
            errorCount = errors.size,
            warningCount = warnings.size,
            errors = errors,
            warnings = warnings
        )
    }
}