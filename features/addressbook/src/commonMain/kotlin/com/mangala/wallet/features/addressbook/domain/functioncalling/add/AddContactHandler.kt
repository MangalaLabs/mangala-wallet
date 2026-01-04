package com.mangala.wallet.features.addressbook.domain.functioncalling.add

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.addressbook.domain.usecase.contact.CreateContactWithAddressUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType

class AddContactHandler(
    private val createContactWithAddressUseCase: CreateContactWithAddressUseCase
) : FunctionHandler {
    override val functionName: String = "add_contact"

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            val name = parameters["name"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Contact name is required")

            val blockchainAddress = parameters["blockchain_address_or_account_name"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Blockchain address is required")

            val blockchainNetwork = parameters["blockchain_network"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Blockchain network is required")

            val blockchainType = BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true)
                .find { it.name == blockchainNetwork }?.blockchainType
                ?: return FunctionResult.Error("INVALID_PARAMETER", "Invalid blockchain network")

            val result = createContactWithAddressUseCase(
                contactName = name,
                address = blockchainAddress,
                blockchainTypeId = blockchainType.uid,
                isPrimary = true
            )

            result.fold(
                onSuccess = { contactId ->
                    FunctionResult.Success(
                        data = mapOf(
                            "result" to "success",
                            "message" to "Contact created successfully",
                            "contactId" to contactId,
                            "contactName" to name,
                            "blockchainNetwork" to blockchainNetwork,
                            "address" to blockchainAddress
                        ),
                        uiHint = FunctionResult.UiHint(
                            type = "success",
                            renderer = "contact_created_success",
                            metadata = mapOf(
                                "functionName" to "add_contact",
                                "contact_id" to contactId,
                                "contactName" to name
                            )
                        )
                    )
                },
                onFailure = { exception ->
                    FunctionResult.Error("EXECUTION_ERROR", exception.message ?: "Failed to create contact")
                }
            )
        } catch (e: Exception) {
            FunctionResult.Error("EXECUTION_ERROR", e.message ?: "Failed to create contact")
        }
    }
}