package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource
import com.mangala.wallet.features.conversationui.domain.util.FunctionCallHasher

/**
 * Use case to check if a function call has already been executed.
 * This helps prevent duplicate executions when users reopen sessions.
 */
class CheckFunctionCallExecutionUseCase(
    private val repository: ChatHistoryLocalDataSource,
    private val hasher: FunctionCallHasher = FunctionCallHasher
) {
    /**
     * Checks if a function call has been executed by generating its hash
     * and looking for matching executed function calls in the session.
     * 
     * @param sessionId The conversation session ID
     * @param functionCall The function call to check
     * @param walletAddress The wallet address for context
     * @param networkId Optional network ID for multi-chain support
     * @return The executed function call message if found, null otherwise
     */
    suspend operator fun invoke(
        sessionId: String,
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null
    ): FunctionCallConfirmationRequiredMessage? {
        val hash = hasher.generateHash(functionCall, walletAddress, networkId)
        return repository.getFunctionCallByHash(sessionId, hash)?.takeIf {
            it.executionStatus == com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus.EXECUTED
        }
    }
    
    /**
     * Checks if a function call has been executed within a time window.
     * Useful for operations that can be repeated after a certain period.
     * 
     * @param sessionId The conversation session ID
     * @param functionCall The function call to check
     * @param walletAddress The wallet address for context
     * @param networkId Optional network ID for multi-chain support
     * @param windowSizeMinutes Time window in minutes (default: 60)
     * @return The executed function call message if found within the window, null otherwise
     */
    suspend fun checkWithTimeWindow(
        sessionId: String,
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null,
        windowSizeMinutes: Int = 60
    ): FunctionCallConfirmationRequiredMessage? {
        val hash = hasher.generateHashWithTimeWindow(
            functionCall, 
            walletAddress, 
            networkId, 
            windowSizeMinutes
        )
        return repository.getFunctionCallByHash(sessionId, hash)?.takeIf {
            it.executionStatus == com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus.EXECUTED
        }
    }
}