package com.mangala.wallet.auth.navigation

/**
 * Interface for handling navigation from auth module to other parts of the app
 */
interface AuthNavigationHandler {
    fun navigateToConversationUi()
}

/**
 * Default implementation that does nothing - to be overridden by the app module
 */
class DefaultAuthNavigationHandler : AuthNavigationHandler {
    override fun navigateToConversationUi() {
        println("Navigation to ConversationUi requested - implement in app module")
    }
}