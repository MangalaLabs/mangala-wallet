package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource
import com.mangala.wallet.features.conversationui.domain.util.FunctionCallHasher

/**
 * Result of function call validation
 */
sealed class FunctionCallValidationResult {
    /**
     * Function call can proceed - no previous execution found
     */
    object CanProceed : FunctionCallValidationResult()
    
    /**
     * Function call was already executed successfully
     */
    data class AlreadyExecuted(
        val previousExecution: FunctionCallConfirmationRequiredMessage,
        val transactionHash: String?
    ) : FunctionCallValidationResult()
    
    /**
     * Function call is currently pending/in progress
     */
    data class InProgress(
        val pendingExecution: FunctionCallConfirmationRequiredMessage
    ) : FunctionCallValidationResult()
    
    /**
     * Function call previously failed - user can retry
     */
    data class PreviouslyFailed(
        val failedExecution: FunctionCallConfirmationRequiredMessage,
        val errorMessage: String?
    ) : FunctionCallValidationResult()
    
    /**
     * Function call was cancelled by user
     */
    data class Cancelled(
        val cancelledExecution: FunctionCallConfirmationRequiredMessage
    ) : FunctionCallValidationResult()
}

/**
 * Use case to validate if a function call can be executed.
 * This is the main entry point for checking function call execution status.
 */
class ValidateFunctionCallExecutionUseCase(
    private val repository: ChatHistoryLocalDataSource,
    private val hasher: FunctionCallHasher = FunctionCallHasher
) {
    /**
     * Validates if a function call can be executed by checking for previous executions.
     * 
     * @param sessionId The conversation session ID
     * @param functionCall The function call to validate
     * @param walletAddress The wallet address for context
     * @param networkId Optional network ID for multi-chain support
     * @return Validation result indicating if the function can proceed or what previous state exists
     */
    suspend operator fun invoke(
        sessionId: String,
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null
    ): FunctionCallValidationResult {
        val functionHash = hasher.generateHash(functionCall, walletAddress, networkId)
        val existingExecution = repository.getFunctionCallByHash(sessionId, functionHash)
        
        return when (existingExecution?.executionStatus) {
            null -> FunctionCallValidationResult.CanProceed
            
            ExecutionStatus.EXECUTED -> FunctionCallValidationResult.AlreadyExecuted(
                previousExecution = existingExecution,
                transactionHash = existingExecution.transactionHash
            )
            
            ExecutionStatus.PENDING, ExecutionStatus.CONFIRMED -> FunctionCallValidationResult.InProgress(
                pendingExecution = existingExecution
            )
            
            ExecutionStatus.FAILED -> FunctionCallValidationResult.PreviouslyFailed(
                failedExecution = existingExecution,
                errorMessage = existingExecution.executionMetadata?.errorMessage
            )
            
            ExecutionStatus.CANCELLED -> FunctionCallValidationResult.Cancelled(
                cancelledExecution = existingExecution
            )
            
            ExecutionStatus.EXPIRED -> {
                // Expired calls can be retried
                FunctionCallValidationResult.CanProceed
            }
        }
    }
    
    /**
     * Validates with time window - allows re-execution after a certain period.
     */
    suspend fun validateWithTimeWindow(
        sessionId: String,
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null,
        windowSizeMinutes: Int = 60
    ): FunctionCallValidationResult {
        val functionHash = hasher.generateHashWithTimeWindow(
            functionCall, 
            walletAddress, 
            networkId, 
            windowSizeMinutes
        )
        val existingExecution = repository.getFunctionCallByHash(sessionId, functionHash)
        
        return when (existingExecution?.executionStatus) {
            null -> FunctionCallValidationResult.CanProceed
            
            ExecutionStatus.EXECUTED -> FunctionCallValidationResult.AlreadyExecuted(
                previousExecution = existingExecution,
                transactionHash = existingExecution.transactionHash
            )
            
            ExecutionStatus.PENDING, ExecutionStatus.CONFIRMED -> FunctionCallValidationResult.InProgress(
                pendingExecution = existingExecution
            )
            
            ExecutionStatus.FAILED -> FunctionCallValidationResult.PreviouslyFailed(
                failedExecution = existingExecution,
                errorMessage = existingExecution.executionMetadata?.errorMessage
            )
            
            ExecutionStatus.CANCELLED -> FunctionCallValidationResult.Cancelled(
                cancelledExecution = existingExecution
            )
            
            ExecutionStatus.EXPIRED -> FunctionCallValidationResult.CanProceed
        }
    }
    
    /**
     * Simple check if a function call can proceed (convenience method)
     */
    suspend fun canProceed(
        sessionId: String,
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null
    ): Boolean {
        return invoke(sessionId, functionCall, walletAddress, networkId) is FunctionCallValidationResult.CanProceed
    }
}