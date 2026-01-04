package com.mangala.wallet.features.addressbook.presentation.plugins

import com.mangala.wallet.core.ai.domain.model.action.ActionHandler
import com.mangala.wallet.core.ai.domain.model.action.ActionResult
import com.mangala.wallet.core.ai.domain.model.action.QuickAction
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo

class AddressBookActionHandler : ActionHandler {
    override fun getSupportedActions(): Set<String> = setOf(
        "show_contact_dialog",
        "show_quick_actions",
        "edit_contact",
        "delete_contact",
        "send_crypto",
        "view_contact_details",
        "navigate"
    )
    
    override fun canHandle(action: String, context: Map<String, Any>): Boolean {
        return action in getSupportedActions()
    }
    
    override fun handleAction(action: String, context: Map<String, Any>): ActionResult {
        return when (action) {
            "show_contact_dialog" -> {
                val contact = context["contact"] as? ContactInfo
                if (contact != null) {
                    ActionResult.ShowDialog("contact_action_dialog", context)
                } else {
                    ActionResult.ShowToast("Contact information not found", isError = true)
                }
            }
            "edit_contact" -> {
                ActionResult.Navigate("edit_contact", context)
            }
            "delete_contact" -> {
                ActionResult.Navigate("delete_contact_confirmation", context)
            }
            "send_crypto" -> {
                ActionResult.Navigate("send_crypto", context)
            }
            "view_contact_details" -> {
                val contactId = (context["contact"] as? ContactInfo)?.id
                ActionResult.Navigate("contact_details", mapOf("contact_id" to contactId.orEmpty()))
            }
            "show_quick_actions" -> {
                val messageId = context["messageId"] as? String
                val actions = context["actions"] as? List<QuickAction>
                val quickActionContext = context["context"] as? Map<String, Any>

                if (messageId != null && actions != null) {
                    val result = ActionResult.ShowQuickActions(
                        messageId = messageId,
                        actions = actions,
                        context = quickActionContext ?: emptyMap()
                    )
                    result
                } else {
                    ActionResult.NotHandled
                }
            }
            "navigate" -> {
                val destination = context["destination"] as? String
                if (destination != null) {
                    ActionResult.Navigate(destination, context)
                } else {
                    ActionResult.NotHandled
                }
            }
            else -> ActionResult.NotHandled
        }
    }
}