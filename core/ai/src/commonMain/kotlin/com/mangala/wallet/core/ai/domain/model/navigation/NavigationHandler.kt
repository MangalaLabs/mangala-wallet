package com.mangala.wallet.core.ai.domain.model.navigation

interface NavigationHandler {
    fun getSupportedDestinations(): Set<String>
    fun canHandle(destination: String, context: Map<String, Any>): Boolean
    fun handleNavigation(destination: String, context: Map<String, Any>): NavigationResult
}