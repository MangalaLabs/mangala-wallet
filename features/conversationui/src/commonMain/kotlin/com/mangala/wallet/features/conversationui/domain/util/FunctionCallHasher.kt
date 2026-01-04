package com.mangala.wallet.features.conversationui.domain.util

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import kotlinx.datetime.Clock
import kotlin.jvm.JvmInline

object FunctionCallHasher {
    
    /**
     * Generates a unique hash for a function call based on its parameters
     * and execution context. This is used to prevent duplicate executions.
     */
    fun generateHash(
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null
    ): String {
        val data = buildString {
            append(functionCall.name)
            append("|")
            
            // Sort parameters for consistency
            functionCall.parameters.entries
                .sortedBy { it.key }
                .forEach { (key, value) ->
                    append("$key=$value|")
                }
            
            append(walletAddress.lowercase())
            networkId?.let { append("|${it.lowercase()}") }
        }
        
        // Use a simple hash for now, could be replaced with SHA256 if needed
        return data.hashCode().toUInt().toString(16)
    }
    
    /**
     * Generates a hash with a time window to allow re-execution after a period
     */
    fun generateHashWithTimeWindow(
        functionCall: FunctionCallRequest,
        walletAddress: String,
        networkId: String? = null,
        windowSizeMinutes: Int = 60
    ): String {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val timeWindow = currentTime / (windowSizeMinutes * 60 * 1000)
        
        val data = buildString {
            append(functionCall.name)
            append("|")
            
            // Sort parameters for consistency
            functionCall.parameters.entries
                .sortedBy { it.key }
                .forEach { (key, value) ->
                    append("$key=$value|")
                }
            
            append(walletAddress.lowercase())
            networkId?.let { append("|${it.lowercase()}") }
            append("|window:$timeWindow")
        }
        
        return data.hashCode().toUInt().toString(16)
    }
}

/**
 * Extension function for convenience
 */
fun FunctionCallRequest.generateHash(
    walletAddress: String,
    networkId: String? = null
): String = FunctionCallHasher.generateHash(this, walletAddress, networkId)