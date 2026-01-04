package com.mangala.wallet.features.send_base.conversation

import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerPlugin
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerRegistry
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.send.AntelopeSendCryptoUseCase

class WalletFunctionHandlers(
    private val antelopeSendCryptoUseCase: AntelopeSendCryptoUseCase
): FunctionHandlerPlugin {
    override fun getFunctionHandlers(): List<FunctionHandler> {
        return listOf(
            SendTransactionFunctionHandler(antelopeSendCryptoUseCase),
        )
    }

    override fun registerTo(registry: FunctionHandlerRegistry) {
        registry.registerPlugin(this)
    }
}