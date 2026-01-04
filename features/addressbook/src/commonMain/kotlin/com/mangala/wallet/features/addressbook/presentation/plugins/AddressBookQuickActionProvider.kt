package com.mangala.wallet.features.addressbook.presentation.plugins

import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.core.ai.domain.model.action.QuickActionProvider
import com.mangala.wallet.core.ai.domain.model.action.QuickActionType

class AddressBookQuickActionProvider : QuickActionProvider {
    
    override fun getQuickActionsForFunction(
        functionName: String, 
        context: Map<String, Any>
    ): List<QuickAction> {
        return when (functionName) {
            "create_contact", "add_contact" -> createContactQuickActions(context)
            "edit_contact", "update_contact" -> editContactQuickActions(context)
            "import_contact" -> importContactQuickActions(context)
            else -> emptyList()
        }
    }
    
    private fun createContactQuickActions(context: Map<String, Any>): List<QuickAction> {
        val contactId = context["contactId"] as? String ?: context["id"] as? String
        val contactName = context["contactName"] as? String ?: context["name"] as? String
        
        return if (contactId != null) {
            listOf(
                QuickAction(
                    id = "view_contact_${contactId}",
                    label = "View Contact",
                    icon = "person",
                    actionType = QuickActionType.Navigate("contact_details"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                ),
                QuickAction(
                    id = "edit_contact_${contactId}",
                    label = "Edit Contact",
                    icon = "edit",
                    actionType = QuickActionType.Navigate("edit_contact"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                ),
                QuickAction(
                    id = "send_crypto_${contactId}",
                    label = "Send Crypto",
                    icon = "send",
                    actionType = QuickActionType.Navigate("send_crypto"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                )
            )
        } else {
            emptyList()
        }
    }
    
    private fun editContactQuickActions(context: Map<String, Any>): List<QuickAction> {
        val contactId = context["contactId"] as? String ?: context["id"] as? String
        val contactName = context["contactName"] as? String ?: context["name"] as? String
        
        return if (contactId != null) {
            listOf(
                QuickAction(
                    id = "view_contact_${contactId}",
                    label = "View Contact",
                    icon = "person",
                    actionType = QuickActionType.Navigate("contact_details"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                ),
                QuickAction(
                    id = "send_crypto_${contactId}",
                    label = "Send Crypto",
                    icon = "send",
                    actionType = QuickActionType.Navigate("send_crypto"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                )
            )
        } else {
            emptyList()
        }
    }
    
    private fun importContactQuickActions(context: Map<String, Any>): List<QuickAction> {
        val contactId = context["contactId"] as? String ?: context["id"] as? String
        val contactName = context["contactName"] as? String ?: context["name"] as? String
        
        return if (contactId != null) {
            listOf(
                QuickAction(
                    id = "view_contact_${contactId}",
                    label = "View Contact",
                    icon = "person",
                    actionType = QuickActionType.Navigate("contact_details"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                ),
                QuickAction(
                    id = "edit_contact_${contactId}",
                    label = "Edit Contact", 
                    icon = "edit",
                    actionType = QuickActionType.Navigate("edit_contact"),
                    metadata = mapOf("contactId" to contactId, "contactName" to contactName.orEmpty())
                )
            )
        } else {
            emptyList()
        }
    }
}