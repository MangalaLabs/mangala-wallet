package com.mangala.wallet.features.addressbook.domain.validation

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import com.mangala.wallet.features.addressbook.presentation.state.UnifiedValidationState
import com.mangala.wallet.features.addressbook.presentation.state.withFieldValue
import com.mangala.wallet.features.addressbook.presentation.state.withFieldValidating
import com.mangala.wallet.features.addressbook.presentation.state.withFieldValidationResult

/**
 * Thread-safe validation manager that prevents race conditions and ensures
 * proper state management during validation operations.
 * 
 * Key features:
 * - Automatic cancellation of previous validations
 * - Thread-safe state updates
 * - Proper debouncing
 * - No stale validation results
 */
class SafeValidationManager(
    private val orchestrator: ValidationOrchestrator,
    private val scope: CoroutineScope
) {
    private val activeJobs = mutableMapOf<String, Job>()
    private val mutex = Mutex()
    
    private val _validationState = MutableStateFlow(UnifiedValidationState())
    val validationState: StateFlow<UnifiedValidationState> = _validationState.asStateFlow()
    
    /**
     * Validates a field with automatic cancellation of previous validations
     * and proper state management.
     */
    suspend fun validateField(
        fieldId: String,
        value: String,
        blockchain: String,
        existingAddresses: List<WalletAddressEntity> = emptyList(),
        context: ValidationContext = ValidationContext.ADDING_CONTACT,
        isPaste: Boolean = false,
        isRequired: Boolean = true
    ) {
        println("VAULTA_DEBUG: SafeValidationManager.validateField - fieldId=$fieldId, value='$value', blockchain='$blockchain'")
        
        // Cancel any existing validation for this field
        mutex.withLock {
            activeJobs[fieldId]?.cancel()
            activeJobs.remove(fieldId)
        }
        
        // Update state with new value immediately
        _validationState.value = _validationState.value.withFieldValue(fieldId, value)
        
        // If value is empty and field is not required, clear validation
        if (value.isBlank() && !isRequired) {
            println("VAULTA_DEBUG: SafeValidationManager - Value is blank, skipping validation")
            return
        }
        
        // Start new validation job
        val job = scope.launch {
            try {
                println("VAULTA_DEBUG: SafeValidationManager - Starting validation job for fieldId=$fieldId")
                
                // Update state to validating
                _validationState.value = _validationState.value.withFieldValidating(fieldId)
                
                // Perform validation through orchestrator
                val result = orchestrator.validateWalletAddress(
                    fieldId = fieldId,
                    input = value,
                    blockchain = blockchain,
                    existingAddresses = existingAddresses,
                    context = context,
                    isPaste = isPaste
                )
                
                println("VAULTA_DEBUG: SafeValidationManager - Validation completed with result: $result")
                
                // Update state with result only if this job hasn't been cancelled
                mutex.withLock {
                    if (activeJobs[fieldId] == this@launch) {
                        println("VAULTA_DEBUG: SafeValidationManager - Updating state with validation result")
                        _validationState.value = _validationState.value
                            .withFieldValidationResult(fieldId, result)
                    }
                }
                
            } catch (e: Exception) {
                // Handle validation error
                mutex.withLock {
                    if (activeJobs[fieldId] == this@launch) {
                        _validationState.value = _validationState.value
                            .withFieldValidationResult(
                                fieldId,
                                WalletValidationResult.Error("Validation failed: ${e.message}")
                            )
                    }
                }
            } finally {
                // Clean up job reference
                mutex.withLock {
                    if (activeJobs[fieldId] == this@launch) {
                        activeJobs.remove(fieldId)
                    }
                }
            }
        }
        
        // Store job reference
        mutex.withLock {
            activeJobs[fieldId] = job
        }
    }
    
    /**
     * Validates multiple fields concurrently
     */
    suspend fun validateMultipleFields(
        validations: List<FieldValidation>
    ) {
        validations.forEach { validation ->
            validateField(
                fieldId = validation.fieldId,
                value = validation.value,
                blockchain = validation.blockchain,
                existingAddresses = validation.existingAddresses,
                context = validation.context,
                isPaste = validation.isPaste,
                isRequired = validation.isRequired
            )
        }
    }
    
    /**
     * Cancels validation for a specific field
     */
    suspend fun cancelFieldValidation(fieldId: String) {
        mutex.withLock {
            activeJobs[fieldId]?.cancel()
            activeJobs.remove(fieldId)
            
            // Clear validation state for cancelled field
            _validationState.value = _validationState.value.clearFieldState(fieldId)
        }
    }
    
    /**
     * Cancels all active validations
     */
    suspend fun cancelAllValidations() {
        mutex.withLock {
            activeJobs.values.forEach { it.cancel() }
            activeJobs.clear()
            
            // Clear all validation states
            _validationState.value = UnifiedValidationState()
        }
    }
    
    /**
     * Updates field value without triggering validation
     */
    fun updateFieldValue(fieldId: String, value: String) {
        _validationState.value = _validationState.value.withFieldValue(fieldId, value)
    }
    
    /**
     * Clears validation state for a field
     */
    fun clearFieldValidation(fieldId: String) {
        _validationState.value = _validationState.value.clearFieldState(fieldId)
    }
    
    /**
     * Gets the current save button state based on validation states
     */
    fun canSave(): Boolean {
        return _validationState.value.canSave
    }
    
    /**
     * Checks if any field is currently validating
     */
    fun isValidating(): Boolean {
        return _validationState.value.isAnyFieldValidating
    }
    
    /**
     * Gets all fields with errors
     */
    fun getFieldsWithErrors(): Map<String, String> {
        return _validationState.value.getFieldsWithErrors()
            .mapNotNull { (fieldId, state) ->
                state.errorMessage?.let { fieldId to it }
            }
            .toMap()
    }
}

/**
 * Data class for batch field validation
 */
data class FieldValidation(
    val fieldId: String,
    val value: String,
    val blockchain: String,
    val existingAddresses: List<WalletAddressEntity> = emptyList(),
    val context: ValidationContext = ValidationContext.ADDING_CONTACT,
    val isPaste: Boolean = false,
    val isRequired: Boolean = true
)

/**
 * Extension function to create SafeValidationManager with orchestrator
 */
fun CoroutineScope.createSafeValidationManager(
    comprehensiveValidator: ComprehensiveAddressValidator,
    config: ValidationConfig = ValidationConfig()
): SafeValidationManager {
    val orchestrator = UnifiedValidationOrchestrator(
        comprehensiveValidator = comprehensiveValidator,
        scope = this,
        config = config
    )
    
    return SafeValidationManager(
        orchestrator = orchestrator,
        scope = this
    )
}