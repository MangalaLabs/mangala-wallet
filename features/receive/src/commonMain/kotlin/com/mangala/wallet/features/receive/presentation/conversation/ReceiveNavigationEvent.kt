package com.mangala.wallet.features.receive.presentation.conversation

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationEvent
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.SharedScreen

sealed class ReceiveNavigationEvent: NavigationEvent {

    data class NavigateToReceiveQr(
        private val accountId: String?,
        private val address: String? = null,
        private val networkType: NetworkType,
        private val initialBlockchainUid: String?
    ) : ReceiveNavigationEvent() {
        override val screen: Screen =
            ScreenRegistry.get(SharedScreen.ReceiveTokenScreen(accountId, address = address, networkType, initialBlockchainUid))
        override val args: Map<String, Any> = mapOf()
    }
}