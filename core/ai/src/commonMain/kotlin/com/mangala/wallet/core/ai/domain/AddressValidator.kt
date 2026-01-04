package com.mangala.wallet.core.ai.domain

interface AddressValidator {
    fun validateAddress(address: String, networkName: String): AddressValidationResult
    suspend fun checkAccountExists(address: String, networkName: String): AccountExistenceResult
    
    fun canValidate(networkName: String): Boolean
    fun requiresAccountExistenceCheck(networkName: String): Boolean
}

data class AddressValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null,
    val formattedAddress: String? = null
)

data class AccountExistenceResult(
    val exists: Boolean,
    val errorMessage: String? = null
)