package com.mangala.wallet.core.ai.domain.model.factory

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.Message

interface MessageFactory {
    fun getSupportedFunctions(): Set<String>
    
    fun createMessageFromFunctionResult(
        functionName: String,
        result: FunctionResult,
        messageId: String,
        senderId: String
    ): Message?
}