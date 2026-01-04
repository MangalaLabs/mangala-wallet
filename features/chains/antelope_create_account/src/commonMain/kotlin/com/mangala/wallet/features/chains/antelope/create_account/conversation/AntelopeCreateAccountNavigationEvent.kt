package com.mangala.wallet.features.chains.antelope.create_account.conversation

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationEvent
import com.mangala.wallet.ui.SharedScreen


sealed class AntelopeCreateAccountNavigationEvent: NavigationEvent {

    data object NavigateToCreateAccount : AntelopeCreateAccountNavigationEvent() {
        override val screen: Screen = ScreenRegistry.get(SharedScreen.Step2SelectAccountNameScreenV2)
        override val args: Map<String, Any> = mapOf()
    }
}