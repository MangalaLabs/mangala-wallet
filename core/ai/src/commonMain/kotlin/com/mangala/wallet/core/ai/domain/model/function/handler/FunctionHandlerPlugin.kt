package com.mangala.wallet.core.ai.domain.model.function.handler

interface FunctionHandlerPlugin {
    fun getFunctionHandlers(): List<FunctionHandler>
    fun registerTo(registry: FunctionHandlerRegistry)
}