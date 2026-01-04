package com.mangala.wallet.features.addressbook.domain.functioncalling

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.FunctionParameter
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.ai.domain.model.function.ParameterType
import com.mangala.wallet.core.security.models.SecurityLevel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData

class AddressBookFunctions() : FunctionPlugin {
    private val MODULE_ID = "addressbook"
    
    override fun registerTo(registry: FunctionRegistry) {
        registry.registerFunction(createAddContactFunction())
        registry.registerFunction(createFindContactFunction())
    }
    
    private fun createAddContactFunction(): FunctionDefinition {
        return FunctionDefinition(
            name = "add_contact",
            description = "Add a new contact to the address book",
            parameters = mapOf(
                "name" to FunctionParameter(
                    name = "name",
                    type = ParameterType.STRING,
                    description = "Contact name",
                    required = true
                ),
                "blockchain_address_or_account_name" to FunctionParameter(
                    name = "blockchain_address_or_account_name",
                    type = ParameterType.STRING,
                    description = "Blockchain address or blockchain account name for the contact",
                    required = true
                ),
                "blockchain_network" to FunctionParameter(
                    name = "blockchain_network",
                    type = ParameterType.STRING,
                    description = "The blockchain network corresponding to the address or account name",
                    required = true,
                    enumValues = BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true).map { it.name }
                ),
                "notes" to FunctionParameter(
                    name = "notes",
                    type = ParameterType.STRING,
                    description = "Optional notes about the contact",
                    required = false
                )
            ),
            requiredParameters = listOf("name", "blockchain_address_or_account_name", "blockchain_network"),
            moduleId = MODULE_ID,
            securityLevel = SecurityLevel.RequireConfirmation,
        )
    }
    
    private fun createFindContactFunction(): FunctionDefinition {
        return FunctionDefinition(
            name = "find_contact",
            description = "Search for contacts in the address book",
            parameters = mapOf(
                "query" to FunctionParameter(
                    name = "query",
                    type = ParameterType.STRING,
                    description = "Search query to match against contact names or addresses",
                    required = true
                )
            ),
            requiredParameters = listOf("query"),
            moduleId = MODULE_ID,
            securityLevel = SecurityLevel.None,
        )
    }
}