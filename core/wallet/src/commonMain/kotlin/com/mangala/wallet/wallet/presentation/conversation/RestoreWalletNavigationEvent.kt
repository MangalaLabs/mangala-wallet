package com.mangala.wallet.wallet.presentation.conversation

import com.mangala.wallet.core.ai.domain.model.navigation.NavigationEvent
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.ui.SharedScreen

sealed class RestoreWalletNavigationEvent : NavigationEvent {

    data object NavigateToRestoreWallet : RestoreWalletNavigationEvent() {
        override val screen: Screen = ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
        override val args: Map<String, Any> = mapOf()
    }
}