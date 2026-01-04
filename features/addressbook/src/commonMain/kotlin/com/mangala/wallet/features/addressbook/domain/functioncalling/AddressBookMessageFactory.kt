package com.mangala.wallet.features.addressbook.domain.functioncalling

import com.mangala.wallet.core.ai.domain.model.factory.MessageFactory
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.features.addressbook.domain.model.ContactAddress
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.presentation.message.ContactResultsMessage
import com.mangala.wallet.features.addressbook.presentation.message.ContactCreatedMessage

interface AddressBookMessageFactory : MessageFactory

class AddressBookMessageFactoryImpl : AddressBookMessageFactory {
    
    override fun getSupportedFunctions(): Set<String> {
        return setOf("find_contact", "list_contacts", "add_contact", "edit_contact", "delete_contact")
    }
    override fun createMessageFromFunctionResult(
        functionName: String,
        result: FunctionResult,
        messageId: String,
        senderId: String
    ): Message? {
        return when (functionName) {
            "find_contact" -> createContactResultsMessage(result, messageId, senderId)
            "add_contact" -> createContactCreatedMessage(result, messageId, senderId)
            else -> null
        }
    }
    
    private fun createContactResultsMessage(
        result: FunctionResult,
        messageId: String,
        senderId: String
    ): ContactResultsMessage? {
        if (result !is FunctionResult.Success) return null
        
        val contactsData = result.data["contacts"] as? List<Map<String, Any?>> ?: return null
        val count = result.data["count"] as? Int ?: 0
        val query = result.uiHint?.metadata?.get("query") as? String ?: ""
        
        return ContactResultsMessage(
            id = messageId,
            senderId = senderId,
            query = query,
            contacts = contactsData.mapNotNull { contact ->
                val id = contact["id"] as? String ?: return@mapNotNull null
                val name = contact["name"] as? String ?: return@mapNotNull null
                ContactInfo(
                    id = id,
                    name = name,
                    notes = contact["notes"] as? String,
                    addresses = listOf(
                        ContactAddress(
                            address = contact.get("address") as? String ?: return@mapNotNull null,
                            network = contact.get("blockchain") as? String ?: return@mapNotNull null
                        )
                    )
                )
            },
            totalCount = count
        )
    }
    
    private fun createContactCreatedMessage(
        result: FunctionResult,
        messageId: String,
        senderId: String
    ): ContactCreatedMessage? {
        if (result !is FunctionResult.Success) {
            return null
        }
        
        val contactId = result.data["contactId"] as? String ?: return null
        val contactName = result.data["contactName"] as? String ?: return null
        val message = result.data["message"] as? String ?: "Contact created successfully"
        val functionName = result.uiHint?.metadata?.get("functionName") as? String ?: "add_contact"

        return ContactCreatedMessage(
            id = messageId,
            senderId = senderId,
            contactId = contactId,
            contactName = contactName,
            successMessage = message,
            functionName = functionName,
            triggerQuickActions = true
        )
    }
}