package com.mangala.wallet.features.conversationui.domain.validation.validators

import com.mangala.wallet.core.ai.domain.AccountExistenceResult
import com.mangala.wallet.core.ai.domain.AddressValidationResult
import com.mangala.wallet.core.ai.domain.AddressValidator
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.utils.BuildEnvironmentProvider

class BlockchainAddressValidator(
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : AddressValidator {

    private val validationPatterns = mapOf(
        "ethereum" to "^0x[a-fA-F0-9]{40}$",
        "solana" to "^[1-9A-HJ-NP-Za-km-z]{32,44}$",
        "binance_smart_chain" to "^0x[a-fA-F0-9]{40}$",
        "polygon" to "^0x[a-fA-F0-9]{40}$",
        "avalanche" to "^0x[a-fA-F0-9]{40}$",
        "arbitrum" to "^0x[a-fA-F0-9]{40}$",
        "optimism" to "^0x[a-fA-F0-9]{40}$",
        "fantom" to "^0x[a-fA-F0-9]{40}$",
        "gnosis" to "^0x[a-fA-F0-9]{40}$",
        "litecoin" to "^[LM3][a-km-zA-HJ-NP-Z1-9]{26,33}$",
        "dash" to "^X[1-9A-HJ-NP-Za-km-z]{33}$",
        "zcash" to "^(t1|t3|zc|zs)[a-zA-Z0-9]{33,34}$",
        "ecash" to "^(q|p)[a-z0-9]{41}$",
    )

    private val networkToBlockchainMap = mapOf(
        "ethereum" to "ethereum",
        "eth" to "ethereum",
        "solana" to "solana",
        "sol" to "solana",
        "binance" to "binance_smart_chain",
        "bsc" to "binance_smart_chain",
        "bnb" to "binance_smart_chain",
        "polygon" to "polygon",
        "matic" to "polygon",
        "avalanche" to "avalanche",
        "avax" to "avalanche",
        "arbitrum" to "arbitrum",
        "optimism" to "optimism",
        "fantom" to "fantom",
        "ftm" to "fantom",
        "gnosis" to "gnosis",
        "xdai" to "gnosis",
        "litecoin" to "litecoin",
        "ltc" to "litecoin",
        "dash" to "dash",
        "zcash" to "zcash",
        "zec" to "zcash",
        "ecash" to "ecash",
        "xec" to "ecash",
    )

    override fun validateAddress(address: String, networkName: String): AddressValidationResult {
        return validateForNetwork(networkName, address)
    }

    override suspend fun checkAccountExists(address: String, networkName: String): AccountExistenceResult {
        return AccountExistenceResult(
            exists = true,
            errorMessage = null
        )
    }

    override fun requiresAccountExistenceCheck(networkName: String): Boolean {
        return false
    }

    override fun canValidate(networkName: String): Boolean {
        return BlockchainNetworkData
            .getAllBlockchainNetworkSupported(includeDebugNetworks = buildEnvironmentProvider.isDevelopmentEnvironment())
            .any { it.name.equals(networkName, ignoreCase = true) }
    }

    private fun validateForNetwork(networkName: String, address: String): AddressValidationResult {
        val normalizedNetwork = networkName.lowercase()
        val blockchainName = networkToBlockchainMap[normalizedNetwork]
            ?: return AddressValidationResult(
                isValid = false,
                errorMessage = "Unknown network: $networkName"
            )

        val pattern = validationPatterns[blockchainName]
            ?: return AddressValidationResult(
                isValid = false,
                errorMessage = "No validation pattern for network: $networkName"
            )

        return try {
            val regex = pattern.toRegex()
            val isValid = address.matches(regex)

            if (isValid) {
                AddressValidationResult(
                    isValid = true,
                    formattedAddress = address.trim()
                )
            } else {
                AddressValidationResult(
                    isValid = false,
                    errorMessage = "Invalid $networkName address format"
                )
            }
        } catch (e: Exception) {
            AddressValidationResult(
                isValid = false,
                errorMessage = "Validation error: ${e.message}"
            )
        }
    }
}