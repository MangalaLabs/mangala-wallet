package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.formatDateTime
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ActionDataFieldUiModel(
    val field: AntelopeActionAbi,
    val errorMessage: InputFieldError? = null,
    val symbolDecimals: Int? = null,
    val symbolInfoLoading: Boolean = false,
    val symbolNameInputDisabled: Boolean = false,
) {
    val valueAsInstant by lazy {
        try {
            val basePrimitiveType = if (field.isPrimitive) {
                AntelopePrimitiveDataTypes.fromValue(field.baseType)
            } else {
                return@lazy null
            }

            return@lazy when (basePrimitiveType) {
                AntelopePrimitiveDataTypes.TIME_POINT -> {
                    Instant.fromEpochMilliseconds(field.value.toLong() / 1000)
                }
                AntelopePrimitiveDataTypes.TIME_POINT_SEC -> {
                    Instant.fromEpochSeconds(field.value.toLong())
                }
                AntelopePrimitiveDataTypes.BLOCK_TIMESTAMP_TYPE -> {
                    Instant.fromEpochMilliseconds(field.value.toLong() * 500 + 946684800000)
                }
                else -> {
                    return@lazy null
                }
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
    val valueAsFormattedLocalDateTime by lazy {
        try {
            val instant = valueAsInstant ?: return@lazy null

            instant
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .formatDateTime(TimeZone.currentSystemDefault(), dateStyle = FormatStyle.LONG, timeStyle = FormatStyle.FULL)
        } catch (e: NumberFormatException) {
            null
        }
    }
}

sealed interface InputFieldError {
    data class SingleInput(val errorMessage: WrappedStringResource?) : InputFieldError
    data class TwoFieldsInput(
        val firstFieldErrorMessage: WrappedStringResource?,
        val secondFieldErrorMessage: WrappedStringResource?
    ) : InputFieldError
    data class ThreeFieldsInput(
        val firstFieldErrorMessage: WrappedStringResource?,
        val secondFieldErrorMessage: WrappedStringResource?,
        val thirdFieldErrorMessage: WrappedStringResource?
    ) : InputFieldError
}