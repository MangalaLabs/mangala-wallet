package com.mangala.wallet.core.ai.domain.model.action

interface ActionHandlerRegistry {
    fun registerHandler(handler: ActionHandler)
    fun handleAction(action: String, context: Map<String, Any>): ActionResult?
}

class DefaultActionHandlerRegistry(handlers: List<ActionHandler>) : ActionHandlerRegistry {
    private val handlers = handlers.toMutableList()
    
    override fun registerHandler(handler: ActionHandler) {
        handlers.add(handler)
    }
    
    override fun handleAction(action: String, context: Map<String, Any>): ActionResult? {
        return handlers.asSequence()
            .filter { handler -> handler.canHandle(action, context) }
            .mapNotNull { handler ->
                try {
                    handler.handleAction(action, context)
                } catch (e: Exception) {
                    ActionResult.ShowToast("Error handling action: ${e.message}", isError = true)
                }
            }
            .firstOrNull()
    }
}