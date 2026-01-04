package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.account.domain.eos.AccountNameType

class ValidateAccountUseCase {

    /**
     * Validates the account name regardless of type
     */
    fun validateAccountName(accountName: String): Boolean {
        return validateAccountName(accountName, AccountNameType.Premium).isValid || validateAccountName(accountName, AccountNameType.Standard).isValid
    }

    fun validateAccountName(
        accountName: String,
        accountType: AccountNameType,
        checkForSuffix: Boolean = false
    ): AccountCharacterValidationResult {
        val trimmedAccountName = accountName.trim()
        val startsAndEndsCorrectly = validateStartsAndEndsCorrectly(trimmedAccountName)

        return if (accountType == AccountNameType.Premium) {
            validatePremiumAccountName(trimmedAccountName, startsAndEndsCorrectly, checkForSuffix)
        } else {
            validateStandardAccountName(trimmedAccountName, startsAndEndsCorrectly)
        }
    }

    private fun validatePremiumAccountName(
        trimmedAccountName: String,
        startsAndEndsCorrectly: Boolean,
        checkForSuffix: Boolean
    ): AccountCharacterValidationResult.PremiumAccount {
        val isValidLength = validatePremiumAccountNameLength(trimmedAccountName)
        val containsOnlyValidCharacters = hasValidPremiumAccountCharacters(trimmedAccountName)

        return AccountCharacterValidationResult.PremiumAccount(
            containsOnlyValidCharacters = containsOnlyValidCharacters,
            isValidLength = isValidLength,
            startsAndEndsCorrectly = startsAndEndsCorrectly,
            forceCheckSuffix = checkForSuffix,
            hasSuffix = AntelopeAccount.isPremiumAccountName(trimmedAccountName)
        )
    }

    private fun validateStandardAccountName(
        trimmedAccountName: String,
        startsAndEndsCorrectly: Boolean
    ): AccountCharacterValidationResult.StandardAccount {
        val containsOnlyValidCharacters = hasValidStandardAccountCharacters(trimmedAccountName)
        val isValidLength = validateStandardAccountNameLength(trimmedAccountName)

        return AccountCharacterValidationResult.StandardAccount(
            containsOnlyValidCharacters = containsOnlyValidCharacters,
            isValidLength = isValidLength,
            startsAndEndsCorrectly = startsAndEndsCorrectly,
        )
    }

    private fun validatePremiumAccountNameLength(trimmedAccountName: String): Boolean {
        return trimmedAccountName.length <= AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME
    }

    private fun validateStandardAccountNameLength(trimmedAccountName: String) =
        trimmedAccountName.length == AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME

    private fun validateStartsAndEndsCorrectly(trimmedAccountName: String): Boolean {
        val doesNotStartWithNumber = if (trimmedAccountName.isEmpty()) {
            true
        } else {
            trimmedAccountName.first() !in '0'..'9'
        }

        return (!trimmedAccountName.startsWith('.')
            && !trimmedAccountName.endsWith('.')
            && doesNotStartWithNumber)
    }

    private fun hasValidPremiumAccountCharacters(accountName: String): Boolean {
        val pattern = Regex("^[a-z1-5.]*$")
        return pattern.matches(accountName)
    }

    private fun hasValidStandardAccountCharacters(accountName: String): Boolean {
        val pattern = Regex("^[a-z1-5]*$")
        return pattern.matches(accountName)
    }
}

sealed interface AccountCharacterValidationResult {
    val containsOnlyValidCharacters: Boolean
    val isValidLength: Boolean
    val startsAndEndsCorrectly: Boolean
    val isValid: Boolean

    data class StandardAccount(
        override val containsOnlyValidCharacters: Boolean,
        override val isValidLength: Boolean,
        override val startsAndEndsCorrectly: Boolean,
    ) : AccountCharacterValidationResult {
        override val isValid: Boolean =
            containsOnlyValidCharacters && isValidLength && startsAndEndsCorrectly
    }

    data class PremiumAccount(
        override val containsOnlyValidCharacters: Boolean,
        override val isValidLength: Boolean,
        override val startsAndEndsCorrectly: Boolean,
        val hasSuffix: Boolean,
        val forceCheckSuffix: Boolean
    ) : AccountCharacterValidationResult {
        override val isValid: Boolean =
            containsOnlyValidCharacters && isValidLength && startsAndEndsCorrectly && (!forceCheckSuffix || hasSuffix)
    }
}