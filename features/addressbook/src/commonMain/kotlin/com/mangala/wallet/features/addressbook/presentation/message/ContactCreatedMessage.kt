package com.mangala.wallet.features.addressbook.presentation.message

import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

data class ContactCreatedMessage(
    override val id: String,
    override val timestamp: Instant = Clock.System.now(),
    override val senderId: String,
    override val isFromUser: Boolean = false,
    override val parentMessageId: String? = null,
    val contactId: String,
    val contactName: String,
    val successMessage: String,
    val functionName: String,
    val triggerQuickActions: Boolean = true
) : Message {
    override fun getContentType(): String = CONTENT_TYPE

    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT

    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("contactId", JsonPrimitive(contactId))
            put("contactName", JsonPrimitive(contactName))
            put("successMessage", JsonPrimitive(successMessage))
            put("functionName", JsonPrimitive(functionName))
            put("triggerQuickActions", JsonPrimitive(triggerQuickActions))
        }
        return jsonObject.toString()
    }

    override fun deserializeJson(json: String): Message {
        TODO("Not yet implemented")
    }

    companion object {
        const val CONTENT_TYPE = "addressbook_contact_created"
    }
}