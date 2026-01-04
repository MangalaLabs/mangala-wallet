package com.mangala.wallet.features.chains.evmcompatible.domain.utils

import com.mangala.wallet.core.ai.domain.AccountExistenceResult
import com.mangala.wallet.core.ai.domain.AddressValidationResult
import com.mangala.wallet.core.ai.domain.AddressValidator
import com.mangala.wallet.features.chains.evmcompatible.core.AddressValidator as EvmCoreAddressValidator
import com.mangala.wallet.features.chains.evmcompatible.core.stripHexPrefix
import com.mangala.wallet.features.chains.evmcompatible.utils.EIP55
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType

class EvmAddressValidator : AddressValidator {

    override fun validateAddress(address: String, networkName: String): AddressValidationResult {
        val isValidAddress = EvmCoreAddressValidator.isAddressValid(address)
        val blockchainNetworkData = getBlockchainNetworkData(networkName)

        if (!isValidAddress) {
            return AddressValidationResult(
                isValid = false,
                errorMessage = "Invalid EVM address format",
                formattedAddress = null
            )
        }

        if (blockchainNetworkData == null) {
            return AddressValidationResult(
                isValid = false,
                errorMessage = "Unsupported network: $networkName",
                formattedAddress = null
            )
        }

        return AddressValidationResult(
            isValid = true,
            errorMessage = null,
            formattedAddress = EIP55.format(address)
        )
    }

    override suspend fun checkAccountExists(address: String, networkName: String): AccountExistenceResult {
        val blockchainNetworkData = getBlockchainNetworkData(networkName)
            ?: return AccountExistenceResult(
                exists = false,
                errorMessage = "Unsupported network: $networkName"
            )

        return AccountExistenceResult(
            exists = true,
            errorMessage = null
        )
    }

    override fun requiresAccountExistenceCheck(networkName: String): Boolean {
        return false
    }

    private fun getBlockchainNetworkData(networkName: String) = BlockchainNetworkData
        .getAllBlockchainNetworkSupported(true)
        .firstOrNull { it.blockchainType.networkType == NetworkType.EVM && it.name.equals(networkName, ignoreCase = true) }

    override fun canValidate(networkName: String): Boolean {
        return BlockchainNetworkData
            .getAllBlockchainNetworkSupported(true)
            .any { it.blockchainType.networkType == NetworkType.EVM && it.name.equals(networkName, ignoreCase = true) }
    }
}