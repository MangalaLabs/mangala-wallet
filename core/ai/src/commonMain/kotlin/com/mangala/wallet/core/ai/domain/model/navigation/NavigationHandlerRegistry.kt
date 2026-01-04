package com.mangala.wallet.core.ai.domain.model.navigation

class NavigationHandlerRegistry(handlers: List<NavigationHandler>) {
    private val handlers = handlers.toMutableList()
    
    fun registerHandler(handler: NavigationHandler) {
        handlers.add(handler)
    }
    
    fun handleNavigation(destination: String, context: Map<String, Any>): NavigationResult? {
        return handlers.firstOrNull { it.canHandle(destination, context) }
            ?.handleNavigation(destination, context)
    }
    
    fun getRegisteredHandlers(): List<NavigationHandler> = handlers.toList()
    
    fun getSupportedDestinations(): Set<String> {
        return handlers.flatMap { it.getSupportedDestinations() }.toSet()
    }
}