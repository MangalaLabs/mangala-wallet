package com.mangala.wallet.features.conversationui.domain.service

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.features.conversationui.presentation.FeeCalculation
import com.mangala.wallet.features.conversationui.presentation.components.transaction.ProgressStep

/**
 * Interface for broadcasting transactions across different blockchain networks
 */
interface TransactionBroadcaster {
    /**
     * Broadcast a transaction to the blockchain
     */
    suspend fun broadcast(
        transaction: Any,
        fees: FeeCalculation
    ): FunctionResult
    
    /**
     * Broadcast a transaction with progress tracking
     */
    suspend fun broadcastWithProgress(
        transaction: Any,
        fees: FeeCalculation,
        onProgress: (ProgressStep) -> Unit
    ): FunctionResult
    
    /**
     * Broadcast a Bitcoin transaction
     */
    suspend fun broadcastBitcoinTransaction(
        accountId: String,
        recipientAddress: String,
        amount: String,
        fees: FeeCalculation
    ): FunctionResult
    
    /**
     * Broadcast an Ethereum/EVM transaction
     */
    suspend fun broadcastEthereumTransaction(
        accountId: String,
        recipientAddress: String,
        amount: String,
        tokenContract: String?,
        fees: FeeCalculation
    ): FunctionResult
    
    /**
     * Broadcast an EOS/Antelope transaction
     */
    suspend fun broadcastEosTransaction(
        accountId: String,
        fromAccount: String,
        toAccount: String,
        amount: String,
        memo: String,
        tokenContract: String,
        fees: FeeCalculation
    ): FunctionResult
}