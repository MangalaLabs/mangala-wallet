package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create

import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeDataFieldValidator
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource

fun String.validateNameGetErrorMessage(strictLengthValidation: Boolean = false): WrappedStringResource? {
    return if (this.isNotBlank()) {
        val validationResult = AntelopeDataFieldValidator.NameValidator().getValidationResult(
            this,
        )
        val exception = validationResult.exceptionOrNull()

        return exception?.getNameErrorMessage(strictLengthValidation)
    } else null
}

fun Throwable.getNameErrorMessage(strictLengthValidation: Boolean = false): WrappedStringResource {
    return when (this) {
        is AntelopeDataFieldValidator.NameValidator.InvalidNameException -> {
            when (this) {
                AntelopeDataFieldValidator.NameValidator.InvalidNameException.InvalidCharacterInName -> WrappedStringResource.StringRes(
                    MR.strings.message_multisig_proposal_name_invalid_character_error
                )

                AntelopeDataFieldValidator.NameValidator.InvalidNameException.InvalidNameLength -> WrappedStringResource.StringRes(
                    MR.strings.message_multisig_proposal_name_length_error,
                    if (strictLengthValidation) AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT else AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH
                )

                is AntelopeDataFieldValidator.NameValidator.InvalidNameException.NameDoesNotMatchSerializedName -> WrappedStringResource.StringRes(
                    MR.strings.message_multisig_proposal_name_serialization_does_not_match_error,
                    this.serializedName
                )
            }
        }
        else -> WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_permission_name_error)
    }
}