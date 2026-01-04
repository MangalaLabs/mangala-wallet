package com.mangala.wallet.features.addressbook.domain.functioncalling.edit

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.UpdateContactUseCase
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

class EditContactNameHandler(
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val updateContactUseCase: UpdateContactUseCase
) : FunctionHandler {
    override val functionName: String = "edit_contact_name"

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            // Extract required parameters
            val contactId = parameters["contact_id"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Contact ID is required")
            
            val newName = parameters["new_name"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "New name is required")
            
            val oldName = parameters["old_name"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Old name is required for verification")

            // Validate new name is not empty
            if (newName.isBlank()) {
                return FunctionResult.Error("INVALID_PARAMETER", "New name cannot be empty")
            }

            // Fetch the existing contact
            val existingContact = getContactByIdUseCase(contactId)
                ?: return FunctionResult.Error("NOT_FOUND", "Contact with ID $contactId not found")

            // Verify the old name matches (optional verification step)
            if (existingContact.name != oldName) {
                return FunctionResult.Error("VERIFICATION_FAILED", 
                    "Current contact name '${existingContact.name}' does not match provided old name '$oldName'")
            }

            // Check if the new name is different from current name
            if (existingContact.name == newName) {
                return FunctionResult.Success(
                    mapOf(
                        "success" to true,
                        "contact_id" to contactId,
                        "message" to "Contact name is already '$newName'. No update needed.",
                        "old_name" to oldName,
                        "new_name" to newName,
                        "changed" to false
                    )
                )
            }

            // Update the contact with new name
            val updatedContact = existingContact.copy(
                name = newName,
                updatedAt = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            )

            // Update the contact
            val success = updateContactUseCase(updatedContact)

            if (success) {
                FunctionResult.Success(
                    mapOf(
                        "success" to true,
                        "contact_id" to contactId,
                        "message" to "Contact name updated successfully from '$oldName' to '$newName'",
                        "old_name" to oldName,
                        "new_name" to newName,
                        "changed" to true
                    )
                )
            } else {
                FunctionResult.Error("UPDATE_FAILED", "Failed to update contact name")
            }
        } catch (e: Exception) {
            FunctionResult.Error("EXECUTION_ERROR", e.message ?: "Failed to update contact name")
        }
    }
}