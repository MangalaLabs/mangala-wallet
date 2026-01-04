package com.mangala.wallet.features.addressbook.presentation.message

import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MessageSendingStatus
import com.mangala.wallet.features.addressbook.domain.model.ContactAddress
import com.mangala.wallet.features.addressbook.domain.model.ContactInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.putJsonArray

data class ContactResultsMessage(
    override val id: String,
    override val timestamp: Instant = Clock.System.now(),
    override val senderId: String,
    override val isFromUser: Boolean = false,
    override val parentMessageId: String? = null,
    val query: String,
    val contacts: List<ContactInfo>,
    val totalCount: Int,
    val displayText: String? = null
) : Message {
    override val sendingStatus: MessageSendingStatus = MessageSendingStatus.SENT

    override fun getContentType(): String = CONTENT_TYPE

    override fun serializeJson(): String {
        val jsonObject = buildJsonObject {
            put("id", JsonPrimitive(id))
            put("timestamp", JsonPrimitive(timestamp.toEpochMilliseconds()))
            put("senderId", JsonPrimitive(senderId))
            put("isFromUser", JsonPrimitive(isFromUser))
            parentMessageId?.let { put("parentMessageId", JsonPrimitive(it)) }
            put("query", JsonPrimitive(query))
            putJsonArray("contacts") {
                contacts.forEach { contact ->
                    add(buildJsonObject {
                        put("id", JsonPrimitive(contact.id))
                        put("name", JsonPrimitive(contact.name))
                        contact.notes?.let { put("notes", JsonPrimitive(it)) }
                        putJsonArray("addresses") {
                            contact.addresses.forEach { address ->
                                add(buildJsonObject {
                                    put("address", JsonPrimitive(address.address))
                                    put("network", JsonPrimitive(address.network))
                                })
                            }
                        }
                    })
                }
            }
            put("totalCount", JsonPrimitive(totalCount))
            displayText?.let { put("displayText", JsonPrimitive(it)) }
        }
        return Json.encodeToString(jsonObject)
    }

    override fun deserializeJson(json: String): Message {
        val jsonObject = Json.parseToJsonElement(json).jsonObject

        val contacts = jsonObject["contacts"]?.jsonArray?.map { contactElement ->
            val contactObj = contactElement.jsonObject
            ContactInfo(
                id = contactObj["id"]?.jsonPrimitive?.content ?: "",
                name = contactObj["name"]?.jsonPrimitive?.content ?: "",
                notes = contactObj["notes"]?.jsonPrimitive?.content,
                addresses = contactObj["addresses"]?.jsonArray?.map { addressElement ->
                    val addressObj = addressElement.jsonObject
                    ContactAddress(
                        address = addressObj["address"]?.jsonPrimitive?.content ?: "",
                        network = addressObj["network"]?.jsonPrimitive?.content ?: ""
                    )
                } ?: emptyList()
            )
        } ?: emptyList()

        return ContactResultsMessage(
            id = jsonObject["id"]?.jsonPrimitive?.content ?: "",
            timestamp = jsonObject["timestamp"]?.jsonPrimitive?.long?.let {
                Instant.fromEpochMilliseconds(it)
            } ?: Clock.System.now(),
            senderId = jsonObject["senderId"]?.jsonPrimitive?.content ?: "",
            isFromUser = jsonObject["isFromUser"]?.jsonPrimitive?.boolean ?: false,
            parentMessageId = jsonObject["parentMessageId"]?.jsonPrimitive?.content,
            query = jsonObject["query"]?.jsonPrimitive?.content ?: "",
            contacts = contacts,
            totalCount = jsonObject["totalCount"]?.jsonPrimitive?.int ?: 0,
            displayText = jsonObject["displayText"]?.jsonPrimitive?.content
        )
    }

    companion object {
        const val CONTENT_TYPE = "addressbook_contact_results"
    }
}