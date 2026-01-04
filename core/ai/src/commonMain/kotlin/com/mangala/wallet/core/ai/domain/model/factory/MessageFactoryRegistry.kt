package com.mangala.wallet.core.ai.domain.model.factory

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.Message

interface MessageFactoryRegistry {
    fun registerFactory(factory: MessageFactory)
    fun createMessageFromFunctionResult(
        functionName: String,
        result: FunctionResult,
        messageId: String,
        senderId: String
    ): Message?
}

class DefaultMessageFactoryRegistry(factories: List<MessageFactory>) : MessageFactoryRegistry {
    private val factories = factories.toMutableList()
    
    override fun registerFactory(factory: MessageFactory) {
        factories.add(factory)
    }
    
    override fun createMessageFromFunctionResult(
        functionName: String,
        result: FunctionResult,
        messageId: String,
        senderId: String
    ): Message? {
        return factories.asSequence()
            .filter { factory -> functionName in factory.getSupportedFunctions() }
            .mapNotNull { factory ->
                factory.createMessageFromFunctionResult(
                    functionName = functionName,
                    result = result,
                    messageId = messageId,
                    senderId = senderId
                )
            }
            .firstOrNull()
    }
}