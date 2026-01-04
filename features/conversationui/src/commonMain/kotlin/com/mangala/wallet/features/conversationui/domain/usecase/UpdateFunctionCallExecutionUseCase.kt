package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.message.ExecutionMetadata
import com.mangala.wallet.core.ai.domain.model.message.ExecutionStatus
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource
import kotlinx.datetime.Clock

/**
 * Use case to update the execution status of a function call.
 * This is called after a function is executed, failed, or cancelled.
 */
class UpdateFunctionCallExecutionUseCase(
    private val repository: ChatHistoryLocalDataSource
) {
    /**
     * Updates the execution status of a function call message.
     * 
     * @param messageId The ID of the function call message to update
     * @param status The new execution status
     * @param transactionHash Optional blockchain transaction hash
     * @param metadata Optional execution metadata (gas used, errors, etc.)
     */
    suspend operator fun invoke(
        messageId: String,
        status: ExecutionStatus,
        transactionHash: String? = null,
        metadata: ExecutionMetadata? = null
    ) {
        repository.updateFunctionCallExecutionStatus(
            messageId = messageId,
            executionStatus = status,
            transactionHash = transactionHash,
            executionMetadata = metadata
        )
    }
    
    /**
     * Marks a function call as confirmed by the user.
     * This is called when the user clicks "Confirm" in the UI.
     */
    suspend fun markAsConfirmed(messageId: String) {
        invoke(
            messageId = messageId,
            status = ExecutionStatus.CONFIRMED
        )
    }
    
    /**
     * Marks a function call as successfully executed.
     * 
     * @param messageId The ID of the function call message
     * @param transactionHash The blockchain transaction hash
     * @param walletAddress The wallet address that executed the transaction
     * @param networkId The network ID where the transaction was executed
     * @param gasUsed Optional gas used for the transaction
     * @param blockNumber Optional block number where the transaction was included
     */
    suspend fun markAsExecuted(
        messageId: String,
        transactionHash: String,
        walletAddress: String,
        networkId: String,
        gasUsed: String? = null,
        blockNumber: Long? = null
    ) {
        invoke(
            messageId = messageId,
            status = ExecutionStatus.EXECUTED,
            transactionHash = transactionHash,
            metadata = ExecutionMetadata(
                walletAddress = walletAddress,
                networkId = networkId,
                gasUsed = gasUsed,
                errorMessage = null,
                blockNumber = blockNumber
            )
        )
    }
    
    /**
     * Marks a function call as failed.
     * 
     * @param messageId The ID of the function call message
     * @param errorMessage The error message describing what went wrong
     * @param walletAddress The wallet address that attempted the transaction
     * @param networkId The network ID where the transaction was attempted
     */
    suspend fun markAsFailed(
        messageId: String,
        errorMessage: String,
        walletAddress: String,
        networkId: String? = null
    ) {
        invoke(
            messageId = messageId,
            status = ExecutionStatus.FAILED,
            metadata = ExecutionMetadata(
                walletAddress = walletAddress,
                networkId = networkId,
                gasUsed = null,
                errorMessage = errorMessage,
                blockNumber = null
            )
        )
    }
    
    /**
     * Marks a function call as cancelled by the user.
     */
    suspend fun markAsCancelled(messageId: String) {
        invoke(
            messageId = messageId,
            status = ExecutionStatus.CANCELLED
        )
    }
    
    /**
     * Marks a function call as expired.
     * This can be used for time-sensitive operations.
     */
    suspend fun markAsExpired(messageId: String) {
        invoke(
            messageId = messageId,
            status = ExecutionStatus.EXPIRED
        )
    }
}