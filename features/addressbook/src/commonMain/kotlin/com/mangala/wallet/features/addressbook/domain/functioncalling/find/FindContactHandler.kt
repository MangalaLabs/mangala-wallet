package com.mangala.wallet.features.addressbook.domain.functioncalling.find

import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandler
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.FilterCriteria
import com.mangala.wallet.features.addressbook.domain.usecase.contact.SearchContactsUseCase
import kotlinx.coroutines.flow.first

class FindContactHandler(
    private val filterContactsUseCase: FilterContactsUseCase,
) : FunctionHandler {
    override val functionName: String = "find_contact"

    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        return try {
            val query = parameters["query"] as? String
                ?: return FunctionResult.Error("MISSING_PARAMETER", "Search query is required")

            filterContactsUseCase(
                filterCriteria = FilterCriteria(query),
                page = 0,
                pageSize = 50
            ).fold(
                onSuccess = {
                    val results = it.map {
                        mapOf(
                            "id" to it.contactId,
                            "name" to it.contactName,
                            "blockchain" to it.blockchainName,
                            "address" to it.walletAddress
                        )
                    }

                    FunctionResult.Success(
                        data = mapOf(
                            "contacts" to results,
                            "count" to results.size
                        ),
                        uiHint = FunctionResult.UiHint(
                            type = "list",
                            renderer = "contact_list",
                            metadata = mapOf("query" to query)
                        )
                    )
                },
                onFailure = {
                    FunctionResult.Error("EXECUTION_ERROR", it.cause?.message ?: "Failed to search contacts")
                }
            )
        } catch (e: Exception) {
            FunctionResult.Error("EXECUTION_ERROR", e.message ?: "Failed to search contacts")
        }
    }
}