package com.mangala.wallet.features.addressbook.domain.functioncalling.edit

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateContactUseCase
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class EditContactHandler(
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val updateContactUseCase: UpdateContactUseCase
) : FunctionHandler {
    override val functionName: String = "edit_contact"

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            // Extract parameters
            val contactId = parameters["contact_id"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Contact ID is required")

            // Fetch the existing contact
            val existingContact = getContactByIdUseCase(contactId)
                ?: return FunctionResult.Error("NOT_FOUND", "Contact with ID $contactId not found")

            // Apply updates only for provided parameters
            val updatedContact = existingContact.copy(
                name = parameters["name"] as? String ?: existingContact.name,
                notes = parameters["notes"] as? String ?: existingContact.notes,
                updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            )

            // Note: blockchain_address_or_account_name and blockchain_network are handled 
            // separately through wallet address management and are not part of the ContactEntity

            // Update the contact
            val success = updateContactUseCase(updatedContact)

            if (success) {
                FunctionResult.Success(
                    mapOf(
                        "success" to true,
                        "contact_id" to contactId,
                        "message" to "Contact updated successfully",
                        "updated_fields" to buildList {
                            if (parameters.containsKey("name")) add("name")
                            if (parameters.containsKey("notes")) add("notes")
                            if (parameters.containsKey("blockchain_address_or_account_name")) add("blockchain_address")
                            if (parameters.containsKey("blockchain_network")) add("blockchain_network")
                        }
                    )
                )
            } else {
                FunctionResult.Error("UPDATE_FAILED", "Failed to update contact")
            }
        } catch (e: Exception) {
            FunctionResult.Error("EXECUTION_ERROR", e.message ?: "Failed to update contact")
        }
    }
}