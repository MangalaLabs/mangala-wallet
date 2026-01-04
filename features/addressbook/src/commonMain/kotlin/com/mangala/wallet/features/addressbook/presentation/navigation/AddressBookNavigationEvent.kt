package com.mangala.wallet.features.addressbook.presentation.navigation

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationEvent
import com.mangala.wallet.ui.SharedScreen

sealed class AddressBookNavigationEvent : NavigationEvent {
    data class NavigateToContactDetails(
        val contactId: String
    ) : AddressBookNavigationEvent() {
        override val screen: Screen =
            ScreenRegistry.get(SharedScreen.ContactScreen(
                contactId = contactId, 
                prefilledName = "", 
                prefilledAddress = "", 
                prefilledBlockchain = "", 
                onBackClick = {}
            ))
        override val args: Map<String, Any> = mapOf("contactId" to contactId)

        override fun navigate(navigator: Navigator) {
            val screen = ScreenRegistry.get(
                SharedScreen.ContactScreen(
                    contactId = contactId,
                    prefilledName = "",
                    prefilledAddress = "",
                    prefilledBlockchain = "",
                    onBackClick = { navigator.pop() }
                )
            )

            navigator.push(screen)
        }
    }

    data class NavigateToEditContact(
        val contactId: String
    ) : AddressBookNavigationEvent() {
        override val screen: Screen = ScreenRegistry.get(
            SharedScreen.ContactScreen(
                contactId = contactId,
                prefilledName = "",
                prefilledAddress = "",
                prefilledBlockchain = "",
                onBackClick = {}
            )
        )
        override val args: Map<String, Any> = mapOf("contactId" to contactId)
    }

    data class NavigateToSendCrypto(
        val contactId: String
    ) : AddressBookNavigationEvent() {
        override val screen: Screen = ScreenRegistry.get(
            SharedScreen.Step2SelectNetwork(
                accountId = "", // This will need to be provided by the AI function call
                networkType = "",
                address = null
            )
        )
        override val args: Map<String, Any> = mapOf("contactId" to contactId)
    }

    data class ShowDeleteConfirmation(
        val contactId: String
    ) : AddressBookNavigationEvent() {
        // For delete confirmation, we navigate to contact details where delete can be triggered
        override val screen: Screen =
            ScreenRegistry.get(SharedScreen.ContactDetailScreen(contactId.toLong()))
        override val args: Map<String, Any> = mapOf("contactId" to contactId)
    }
}