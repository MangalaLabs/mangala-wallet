package com.mangala.wallet.features.addressbook.presentation.plugins

import com.mangala.wallet.core.ai.domain.model.navigation.NavigationHandler
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationResult
import com.mangala.wallet.features.addressbook.presentation.navigation.AddressBookNavigationEvent

class AddressBookNavigationHandler : NavigationHandler {
    
    override fun getSupportedDestinations(): Set<String> = setOf(
        "contact_details",
        "edit_contact", 
        "send_crypto",
        "delete_contact_confirmation"
    )
    
    override fun canHandle(destination: String, context: Map<String, Any>): Boolean {
        return destination in getSupportedDestinations()
    }
    
    override fun handleNavigation(destination: String, context: Map<String, Any>): NavigationResult {
        val contactId = extractContactId(context)
        
        return when (destination) {
            "contact_details" -> {
                NavigationResult.EmitNavigationEvent(
                    AddressBookNavigationEvent.NavigateToContactDetails(contactId)
                )
            }
            "edit_contact" -> {
                NavigationResult.EmitNavigationEvent(
                    AddressBookNavigationEvent.NavigateToEditContact(contactId)
                )
            }
            "send_crypto" -> {
                NavigationResult.EmitNavigationEvent(
                    AddressBookNavigationEvent.NavigateToSendCrypto(contactId)
                )
            }
            "delete_contact_confirmation" -> {
                NavigationResult.EmitNavigationEvent(
                    AddressBookNavigationEvent.ShowDeleteConfirmation(contactId)
                )
            }
            else -> NavigationResult.NotHandled
        }
    }
    
    private fun extractContactId(context: Map<String, Any>): String {
        return context["contact_id"]?.let { contact ->
            when (contact) {
                is Map<*, *> -> contact["id"] as? String
                else -> contact.toString()
            }
        }.orEmpty()
    }
}