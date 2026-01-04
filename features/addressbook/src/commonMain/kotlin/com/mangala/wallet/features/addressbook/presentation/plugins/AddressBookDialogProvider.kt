package com.mangala.wallet.features.addressbook.presentation.plugins

import androidx.compose.runtime.Composable
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProvider
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import com.mangala.wallet.features.addressbook.domain.model.ContactAction
import com.mangala.wallet.features.addressbook.presentation.components.ContactActionDialog

class AddressBookDialogProvider : DialogProvider {
    override fun getSupportedDialogTypes(): Set<String> = setOf("contact_action_dialog")
    
    override fun canProvideDialog(type: String, context: Map<String, Any>): Boolean {
        return type in getSupportedDialogTypes()
    }
    
    @Composable
    override fun ProvideDialog(
        type: String,
        context: Map<String, Any>,
        onAction: (String, Map<String, Any>) -> Unit,
        onDismiss: () -> Unit
    ) {
        when (type) {
            "contact_action_dialog" -> {
                val contact = context["contact"] as ContactInfo
                ContactActionDialog(
                    contact = contact,
                    onAction = { contactAction ->
                        onAction(contactAction.actionName, mapOf("contact" to contact))
                    },
                    onDismiss = onDismiss
                )
            }
        }
    }
}