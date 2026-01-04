package com.mangala.wallet.features.chains.antelope_base.domain.utils

import com.mangala.wallet.core.ai.domain.AccountExistenceResult
import com.mangala.wallet.core.ai.domain.AddressValidationResult
import com.mangala.wallet.core.ai.domain.AddressValidator
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CheckAccountNotExistsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.ValidateAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType

class AntelopeAccountValidator(
    private val validateAccountUseCase: ValidateAccountUseCase,
    private val checkAccountNotExistsUseCase: CheckAccountNotExistsUseCase
): AddressValidator {

    override fun validateAddress(address: String, networkName: String): AddressValidationResult {
        val isValidAddress = validateAccountUseCase.validateAccountName(address)
        val blockchainNetworkData = getBlockchainNetworkData(networkName)

        if (isValidAddress.not()) {
            return AddressValidationResult(
                isValid = false,
                errorMessage = "Invalid account name format",
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
            formattedAddress = address.lowercase()
        )
    }

    override suspend fun checkAccountExists(address: String, networkName: String): AccountExistenceResult {
        val blockchainNetworkData = getBlockchainNetworkData(networkName)
            ?: return AccountExistenceResult(
                exists = false,
                errorMessage = "Unsupported network: $networkName"
            )

        val accountExists = checkAccountNotExistsUseCase(
            blockchainType = blockchainNetworkData.blockchainType,
            accountName = address
        ).not()

        return AccountExistenceResult(
            exists = accountExists,
            errorMessage = if (accountExists) null else "Account does not exist on ${blockchainNetworkData.name}"
        )
    }

    override fun requiresAccountExistenceCheck(networkName: String): Boolean {
        return canValidate(networkName)
    }

    private fun getBlockchainNetworkData(networkName: String) = BlockchainNetworkData
        .getAllBlockchainNetworkSupported(true)
        .firstOrNull() { it.blockchainType.networkType == NetworkType.ANTELOPE && it.name.equals(networkName, ignoreCase = true) }

    override fun canValidate(networkName: String): Boolean {
        return BlockchainNetworkData
            .getAllBlockchainNetworkSupported(true)
            .any { it.blockchainType.networkType == NetworkType.ANTELOPE && it.name.equals(networkName, ignoreCase = true) }
    }
}