package com.mangala.wallet.core.ai.domain.model.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

interface NavigationEvent {
    val screen: Screen
    val args: Map<String, Any>
    
    fun navigate(navigator: Navigator) {
        navigator.push(screen)
    }
}