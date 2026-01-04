package com.mangala.wallet.features.send_base.conversation

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry

class WalletFunctions() : FunctionPlugin {

    companion object {
        const val MODULE_ID = "wallet"
    }

    override fun registerTo(registry: FunctionRegistry) {
        registry.registerFunction(sendTransactionFunction)
    }
}