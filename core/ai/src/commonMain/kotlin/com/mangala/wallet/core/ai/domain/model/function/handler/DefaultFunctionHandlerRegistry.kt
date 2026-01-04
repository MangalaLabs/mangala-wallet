package com.mangala.wallet.core.ai.domain.model.function.handler

/**
 * Default implementation of [FunctionHandlerRegistry]
 *
 * @property plugins List of function handler plugins to register on initialization
 * @property handlers List of individual function handlers to register on initialization
 */
class DefaultFunctionHandlerRegistry(
    plugins: List<FunctionHandlerPlugin> = emptyList(),
) : FunctionHandlerRegistry {

    private val handlerMap = mutableMapOf<String, FunctionHandler>()

    init {
        // Register handlers from plugins
        plugins.forEach { plugin ->
            registerPlugin(plugin)
        }
    }

    override fun registerPlugin(plugin: FunctionHandlerPlugin) {
        plugin.getFunctionHandlers().forEach { handler ->
            registerHandler(handler)
        }
    }

    override fun registerHandler(handler: FunctionHandler) {
        handlerMap[handler.functionName] = handler
    }

    override fun getHandlers(): List<FunctionHandler> {
        return handlerMap.values.toList()
    }

    override fun getHandlerByName(functionName: String): FunctionHandler? {
        return handlerMap[functionName]
    }

    override fun hasHandler(functionName: String): Boolean {
        return handlerMap.containsKey(functionName)
    }
}