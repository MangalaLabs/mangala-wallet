package com.mangala.wallet.core.ai.domain.model.function.definition

/**
 * Plugin interface for modules to register their functions with the registry
 */
interface FunctionPlugin {
    fun registerTo(registry: FunctionRegistry)
}