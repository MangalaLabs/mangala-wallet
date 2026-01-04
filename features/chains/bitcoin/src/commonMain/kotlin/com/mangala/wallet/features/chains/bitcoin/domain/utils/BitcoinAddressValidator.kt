package com.mangala.wallet.features.chains.bitcoin.domain.utils

import com.mangala.wallet.core.ai.domain.AccountExistenceResult
import com.mangala.wallet.core.ai.domain.AddressValidationResult
import com.mangala.wallet.core.ai.domain.AddressValidator
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import fr.acinq.bitcoin.Bitcoin.addressToPublicKeyScript
import fr.acinq.bitcoin.utils.Either

class BitcoinAddressValidator : AddressValidator {
    
    override fun validateAddress(address: String, networkName: String): AddressValidationResult {
        return try {
            val blockchainType = getBlockchainNetworkData(networkName)?.blockchainType
                ?: return AddressValidationResult(
                    isValid = false,
                    errorMessage = "Unsupported network: $networkName"
                )
            
            val chainHash = getChainHash(blockchainType)
            val result = addressToPublicKeyScript(chainHash, address)

            return when (result) {
                is Either.Left -> AddressValidationResult(
                    isValid = false,
                    errorMessage = "Invalid Bitcoin address format"
                )
                else -> AddressValidationResult(
                    isValid = true,
                    formattedAddress = address
                )
            }
        } catch (e: Exception) {
            AddressValidationResult(
                isValid = false,
                errorMessage = "Address validation failed: ${e.message}"
            )
        }
    }
    
    override suspend fun checkAccountExists(address: String, networkName: String): AccountExistenceResult {
        return AccountExistenceResult(
            exists = true
        )
    }
    
    override fun canValidate(networkName: String): Boolean {
        return getBlockchainNetworkData(networkName) != null
    }

    private fun getBlockchainNetworkData(networkName: String) = BlockchainNetworkData
        .getAllBlockchainNetworkSupported(true)
        .firstOrNull { it.blockchainType.networkType == NetworkType.BITCOIN && it.name.equals(networkName, ignoreCase = true) }
    
    override fun requiresAccountExistenceCheck(networkName: String): Boolean {
        return false
    }
}