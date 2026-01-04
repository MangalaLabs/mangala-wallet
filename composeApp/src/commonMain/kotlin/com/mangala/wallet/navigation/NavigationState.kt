package com.mangala.wallet.navigation

import cafe.adriel.voyager.core.screen.Screen

sealed class NavigationState {
    object Loading : NavigationState()
    data class Ready(val screen: Screen) : NavigationState()
}