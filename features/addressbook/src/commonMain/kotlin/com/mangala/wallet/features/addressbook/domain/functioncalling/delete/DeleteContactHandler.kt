package com.mangala.wallet.features.addressbook.domain.functioncalling.delete

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactByIdUseCase

class DeleteContactHandler(
    private val getContactByIdUseCase: GetContactByIdUseCase,
    private val deleteContactUseCase: DeleteContactUseCase
) : FunctionHandler {
    override val functionName: String = "delete_contact"

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            // Extract parameters
            val contactId = parameters["contact_id"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Contact ID is required")

            // Verify the contact exists
            val existingContact = getContactByIdUseCase(contactId)
                ?: return FunctionResult.Error("NOT_FOUND", "Contact with ID $contactId not found")

            // Delete the contact
            val success = deleteContactUseCase(contactId)

            if (success) {
                FunctionResult.Success(
                    mapOf(
                        "success" to true,
                        "contact_id" to contactId,
                        "message" to "Contact '${existingContact.name}' deleted successfully"
                    )
                )
            } else {
                FunctionResult.Error("DELETE_FAILED", "Failed to delete contact")
            }
        } catch (e: Exception) {
            FunctionResult.Error("EXECUTION_ERROR", e.message ?: "Failed to delete contact")
        }
    }
}