package com.mangala.wallet.features.addressbook.domain.validation

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.domain.util.BlockchainSymbolMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

/**
 * Centralized validation orchestrator that handles all wallet address validation
 * with proper cancellation, debouncing, and state management.
 */
interface ValidationOrchestrator {
    /**
     * Validates a wallet address with proper debouncing and cancellation
     */
    suspend fun validateWalletAddress(
        fieldId: String,
        input: String,
        blockchain: String,
        existingAddresses: List<WalletAddressEntity> = emptyList(),
        context: ValidationContext = ValidationContext.ADDING_CONTACT,
        isPaste: Boolean = false
    ): WalletValidationResult
    
    /**
     * Cancels validation for a specific field
     */
    fun cancelValidation(fieldId: String)
    
    /**
     * Cancels all active validations
     */
    fun cancelAllValidations()
    
    /**
     * Gets the current validation state for a field
     */
    fun getValidationState(fieldId: String): FieldValidationState?
    
    /**
     * Registers a listener for validation state changes
     */
    fun addValidationStateListener(listener: ValidationStateListener)
    
    /**
     * Removes a validation state listener
     */
    fun removeValidationStateListener(listener: ValidationStateListener)
}

/**
 * Validation state for a field
 */
data class FieldValidationState(
    val fieldId: String,
    val status: ValidationStatus,
    val result: WalletValidationResult? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class ValidationStatus {
        IDLE,
        VALIDATING,
        COMPLETED,
        CANCELLED
    }
}

/**
 * Listener for validation state changes
 */
interface ValidationStateListener {
    fun onValidationStateChanged(fieldId: String, state: FieldValidationState)
}

/**
 * Implementation of ValidationOrchestrator with proper async handling
 */
class UnifiedValidationOrchestrator(
    private val comprehensiveValidator: ComprehensiveAddressValidator,
    private val scope: CoroutineScope,
    private val config: ValidationConfig = ValidationConfig()
) : ValidationOrchestrator {
    
    private val activeJobs = mutableMapOf<String, Job>()
    private val validationStates = mutableMapOf<String, FieldValidationState>()
    private val listeners = mutableListOf<ValidationStateListener>()
    private val mutex = Mutex()
    
    override suspend fun validateWalletAddress(
        fieldId: String,
        input: String,
        blockchain: String,
        existingAddresses: List<WalletAddressEntity>,
        context: ValidationContext,
        isPaste: Boolean
    ): WalletValidationResult {
        // Cancel any existing validation for this field synchronously
        mutex.withLock {
            activeJobs[fieldId]?.cancel()
            activeJobs.remove(fieldId)
        }
        
        // If input is empty, return immediately
        if (input.isBlank()) {
            updateValidationState(fieldId, FieldValidationState(
                fieldId = fieldId,
                status = FieldValidationState.ValidationStatus.IDLE,
                result = null
            ))
            return WalletValidationResult.Success("")
        }
        
        // Calculate appropriate delay
        val delayMs = calculateDelay(input, blockchain, isPaste)
        
        
        // Update state to validating
        updateValidationState(fieldId, FieldValidationState(
            fieldId = fieldId,
            status = FieldValidationState.ValidationStatus.VALIDATING
        ))
        
        // Create a shared result holder to ensure we capture the result
        var validationResult: WalletValidationResult? = null
        
        // Start validation job with timeout
        val job = scope.launch {
            try {
                withTimeout(config.timeout) { // Use config timeout value
                    // Apply debounce delay
                    if (delayMs > 0) {
                        delay(delayMs)
                    }
                    
                    // Perform validation
                    val result = comprehensiveValidator.validateAddressOrDomain(
                        input = input,
                        selectedBlockchain = blockchain,
                        existingAddresses = existingAddresses,
                        context = context
                    )
                    
                    
                    // Store result in shared variable
                    validationResult = result
                    
                    // Update state with result
                    updateValidationState(fieldId, FieldValidationState(
                        fieldId = fieldId,
                        status = FieldValidationState.ValidationStatus.COMPLETED,
                        result = result
                    ))
                }
            } catch (e: TimeoutCancellationException) {
                // Handle timeout
                val timeoutResult = WalletValidationResult.Warning(
                    "⚠️ Validation timed out. Please verify the address manually.",
                    input
                )
                
                validationResult = timeoutResult
                
                updateValidationState(fieldId, FieldValidationState(
                    fieldId = fieldId,
                    status = FieldValidationState.ValidationStatus.COMPLETED,
                    result = timeoutResult
                ))
            } catch (e: Exception) {
                e.printStackTrace()
                
                // Handle validation error
                val errorResult = WalletValidationResult.Error(
                    "Validation failed: ${e.message ?: "Unknown error"}"
                )
                
                validationResult = errorResult
                
                updateValidationState(fieldId, FieldValidationState(
                    fieldId = fieldId,
                    status = FieldValidationState.ValidationStatus.COMPLETED,
                    result = errorResult
                ))
            }
        }
        
        // Store job for cancellation
        mutex.withLock {
            activeJobs[fieldId] = job
        }
        
        // Wait for validation to complete
        job.join()
        
        // Return the captured result or check the state map
        val result = validationResult ?: validationStates[fieldId]?.result
        if (result == null) {
        }
        return result ?: WalletValidationResult.Error("Validation failed")
    }
    
    override fun cancelValidation(fieldId: String) {
        scope.launch {
            mutex.withLock {
                activeJobs[fieldId]?.cancel()
                activeJobs.remove(fieldId)
                
                // Update state to cancelled if it was validating
                validationStates[fieldId]?.let { state ->
                    if (state.status == FieldValidationState.ValidationStatus.VALIDATING) {
                        updateValidationState(fieldId, state.copy(
                            status = FieldValidationState.ValidationStatus.CANCELLED
                        ))
                    }
                }
            }
        }
    }
    
    override fun cancelAllValidations() {
        scope.launch {
            mutex.withLock {
                activeJobs.values.forEach { it.cancel() }
                activeJobs.clear()
                
                // Update all validating states to cancelled
                validationStates.forEach { (fieldId, state) ->
                    if (state.status == FieldValidationState.ValidationStatus.VALIDATING) {
                        updateValidationState(fieldId, state.copy(
                            status = FieldValidationState.ValidationStatus.CANCELLED
                        ))
                    }
                }
            }
        }
    }
    
    override fun getValidationState(fieldId: String): FieldValidationState? {
        return validationStates[fieldId]
    }
    
    override fun addValidationStateListener(listener: ValidationStateListener) {
        listeners.add(listener)
    }
    
    override fun removeValidationStateListener(listener: ValidationStateListener) {
        listeners.remove(listener)
    }
    
    private fun updateValidationState(fieldId: String, state: FieldValidationState) {
        validationStates[fieldId] = state
        listeners.forEach { it.onValidationStateChanged(fieldId, state) }
    }
    
    private fun calculateDelay(input: String, blockchain: String, isPaste: Boolean): Long {
        return when {
            isPaste -> config.debounce.paste
            isAntelopeChain(blockchain) && input.length < 6 -> config.debounce.antelopeShort
            isAntelopeChain(blockchain) -> config.debounce.antelopeLong
            else -> config.debounce.standard
        }
    }
    
    private fun isAntelopeChain(blockchain: String): Boolean {
        val symbol = BlockchainSymbolMapper.normalizeToSymbol(blockchain)
        return symbol in setOf("VAULTA", "VAULTA_TESTNET", "TELOS", "TELOS_TESTNET", 
                               "WAX", "WAX_TESTNET", "FIO", "FIO_TESTNET")
    }
}

/**
 * Configuration for validation behavior
 */
data class ValidationConfig(
    val debounce: DebounceConfig = DebounceConfig(),
    val timeout: Long = 7_000L, // Reduced from 10 to 7 seconds
    val enableCaching: Boolean = true,
    val cacheExpiration: Long = 5 * 60 * 1000L // 5 minutes
)

/**
 * Debounce configuration for different input scenarios
 */
data class DebounceConfig(
    val standard: Long = 800L,       // Increased from 300L
    val paste: Long = 100L,          // Increased from 0L to avoid immediate validation
    val deletion: Long = 200L,       // Increased from 50L
    val antelopeShort: Long = 1000L, // Increased from 400L
    val antelopeLong: Long = 1500L   // Increased from 600L
)