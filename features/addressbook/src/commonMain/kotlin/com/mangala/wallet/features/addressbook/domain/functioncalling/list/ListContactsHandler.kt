package com.mangala.wallet.features.addressbook.domain.functioncalling.list

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesForContactUseCase
import kotlinx.coroutines.flow.first

class ListContactsHandler(
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val getWalletAddressesForContactUseCase: GetWalletAddressesForContactUseCase
) : FunctionHandler {
    override val functionName: String = "list_contacts"

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            val contacts = getAllContactsUseCase.observeContacts().first()

            val results = contacts.map { contact ->
                // Fetch wallet addresses for each contact
                val walletAddresses = getWalletAddressesForContactUseCase(
                    contactId = contact.id,
                    limit = 100, // Get all addresses for the contact
                    offset = 0
                )

                val addresses = walletAddresses.map { walletAddress ->
                    mapOf(
                        "id" to walletAddress.id,
                        "address" to walletAddress.address,
                        "blockchainId" to walletAddress.blockchainNetworkId,
                        "isDefault" to walletAddress.isDefault
                    )
                }

                mapOf(
                    "id" to contact.id,
                    "name" to contact.name,
                    "notes" to contact.notes,
                    "addresses" to addresses
                )
            }

            FunctionResult.Success(
                mapOf(
                    "contacts" to results,
                    "count" to results.size
                )
            )
        } catch (e: Exception) {
            FunctionResult.Error("EXECUTION_ERROR", e.message ?: "Failed to list contacts")
        }
    }
}