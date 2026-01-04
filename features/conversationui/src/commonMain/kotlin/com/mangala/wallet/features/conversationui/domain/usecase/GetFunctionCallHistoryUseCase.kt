package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource

/**
 * Use case to retrieve function call history for a user.
 * Useful for showing transaction history or debugging.
 */
class GetFunctionCallHistoryUseCase(
    private val repository: ChatHistoryLocalDataSource
) {
    /**
     * Gets recent function calls for a user with optional filters.
     * 
     * @param userId The user ID to get function calls for
     * @param functionName Optional filter by function name (e.g., "transfer", "approve")
     * @param executionStatus Optional filter by execution status
     * @param limit Maximum number of results to return (default: 20)
     * @return List of function call messages sorted by most recent first
     */
    suspend operator fun invoke(
        userId: String,
        functionName: String? = null,
        executionStatus: ExecutionStatus? = null,
        limit: Int = 20
    ): List<FunctionCallConfirmationRequiredMessage> {
        return repository.getRecentFunctionCalls(
            userId = userId,
            functionName = functionName,
            executionStatus = executionStatus,
            limit = limit
        )
    }
    
    /**
     * Gets all executed function calls for a user.
     * Useful for showing transaction history.
     */
    suspend fun getExecutedCalls(
        userId: String,
        limit: Int = 50
    ): List<FunctionCallConfirmationRequiredMessage> {
        return invoke(
            userId = userId,
            executionStatus = ExecutionStatus.EXECUTED,
            limit = limit
        )
    }
    
    /**
     * Gets all pending function calls for a user.
     * Useful for showing unconfirmed transactions.
     */
    suspend fun getPendingCalls(
        userId: String
    ): List<FunctionCallConfirmationRequiredMessage> {
        return invoke(
            userId = userId,
            executionStatus = ExecutionStatus.PENDING,
            limit = 100 // Higher limit for pending calls
        )
    }
    
    /**
     * Gets all failed function calls for a user.
     * Useful for debugging or retry functionality.
     */
    suspend fun getFailedCalls(
        userId: String,
        limit: Int = 20
    ): List<FunctionCallConfirmationRequiredMessage> {
        return invoke(
            userId = userId,
            executionStatus = ExecutionStatus.FAILED,
            limit = limit
        )
    }
    
    /**
     * Gets function calls by type (e.g., all transfers).
     */
    suspend fun getCallsByType(
        userId: String,
        functionName: String,
        limit: Int = 20
    ): List<FunctionCallConfirmationRequiredMessage> {
        return invoke(
            userId = userId,
            functionName = functionName,
            limit = limit
        )
    }
}