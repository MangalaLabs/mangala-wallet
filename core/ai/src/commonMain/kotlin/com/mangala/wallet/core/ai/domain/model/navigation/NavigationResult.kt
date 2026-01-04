package com.mangala.wallet.core.ai.domain.model.navigation

import cafe.adriel.voyager.core.screen.Screen

sealed class NavigationResult {
    data class EmitNavigationEvent(val event: Any) : NavigationResult()
    data class NavigateToScreen(val screenProvider: () -> Screen) : NavigationResult()
    object Handled : NavigationResult()
    object NotHandled : NavigationResult()
}