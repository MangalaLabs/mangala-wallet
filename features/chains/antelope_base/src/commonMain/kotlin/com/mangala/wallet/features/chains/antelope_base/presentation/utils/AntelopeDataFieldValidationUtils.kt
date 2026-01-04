package com.mangala.wallet.features.chains.antelope_base.presentation.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeDataFieldValidator.AssetValidator.InvalidAssetException
import com.memtrip.eos.abi.reader.bytereader.DefaultByteReader
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.abi.writer.bytewriter.NameWriter
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes.Companion.EXTENDED_TYPE_DELIMITER
import com.memtrip.eos.core.crypto.EosPublicKey
import com.memtrip.eos.core.crypto.EosSignature
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.core.toByteArray

object AntelopeDataFieldValidationUtils {
    fun isValidInput(
        value: String,
        type: AntelopePrimitiveDataTypes,
        validPrecision: Int? = null,
        strictValidationForName: Boolean = false,
        strictPrecisionCheck: Boolean = false
    ): Boolean {
        val validator = getValidator(
            type,
            validPrecision,
            strictValidationForName,
            strictPrecisionCheck
        )

        return validator.isValid(value)
    }

    fun getValidator(
        type: AntelopePrimitiveDataTypes,
        validPrecision: Int? = null,
        strictValidationForName: Boolean,
        strictPrecisionCheck: Boolean = false
    ): AntelopeDataFieldValidator {
        return when (type) {
            AntelopePrimitiveDataTypes.BOOL -> AntelopeDataFieldValidator.BoolValidator
            AntelopePrimitiveDataTypes.INT8 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.fromInt(-128),
                maxValue = BigInteger.fromInt(127)
            )

            AntelopePrimitiveDataTypes.UINT8 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.ZERO,
                maxValue = BigInteger.fromInt(255)
            )

            AntelopePrimitiveDataTypes.INT16 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.fromInt(-32768),
                maxValue = BigInteger.fromInt(32767)
            )

            AntelopePrimitiveDataTypes.UINT16 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.ZERO,
                maxValue = BigInteger.fromInt(65535)
            )

            AntelopePrimitiveDataTypes.INT32,
            AntelopePrimitiveDataTypes.VARINT32 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.fromInt(-2147483648),
                maxValue = BigInteger.fromInt(2147483647)
            )

            AntelopePrimitiveDataTypes.UINT32,
            AntelopePrimitiveDataTypes.VARUINT32,
            AntelopePrimitiveDataTypes.TIME_POINT_SEC,
            AntelopePrimitiveDataTypes.BLOCK_TIMESTAMP_TYPE -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.ZERO,
                maxValue = BigInteger.fromLong(4294967295)
            )
            // https://github.com/wharfkit/antelope/blob/master/src/chain/time.ts#L118
            AntelopePrimitiveDataTypes.INT64,
            AntelopePrimitiveDataTypes.TIME_POINT -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.parseString("-9223372036854775808"),
                maxValue = BigInteger.parseString("9223372036854775807")
            )

            AntelopePrimitiveDataTypes.UINT64 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.ZERO,
                maxValue = BigInteger.parseString("18446744073709551615")
            )

            AntelopePrimitiveDataTypes.INT128 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.parseString("-170141183460469231731687303715884105728"),
                maxValue = BigInteger.parseString("170141183460469231731687303715884105727")
            )

            AntelopePrimitiveDataTypes.UINT128 -> AntelopeDataFieldValidator.IntegerNumberValidator(
                minValue = BigInteger.ZERO,
                maxValue = BigInteger.parseString("340282366920938463463374607431768211455")
            )

            AntelopePrimitiveDataTypes.FLOAT32 -> AntelopeDataFieldValidator.DecimalNumberValidator(
                minValue = BigDecimal.fromDouble(Float.MIN_VALUE.toDouble()),
                maxValue = BigDecimal.fromDouble(Float.MAX_VALUE.toDouble())
            )

            AntelopePrimitiveDataTypes.FLOAT64 -> AntelopeDataFieldValidator.DecimalNumberValidator(
                minValue = BigDecimal.fromDouble(Double.MIN_VALUE),
                maxValue = BigDecimal.fromDouble(Double.MAX_VALUE)
            )

            AntelopePrimitiveDataTypes.FLOAT128 -> AntelopeDataFieldValidator.HexadecimalStringValidator(
                bytesLength = 16
            )
            AntelopePrimitiveDataTypes.NAME -> AntelopeDataFieldValidator.NameValidator(strictValidationForName)
            AntelopePrimitiveDataTypes.BYTES -> AntelopeDataFieldValidator.HexadecimalStringValidator(
                null
            )

            AntelopePrimitiveDataTypes.STRING -> AntelopeDataFieldValidator.StringValidator
            AntelopePrimitiveDataTypes.CHECKSUM160 -> AntelopeDataFieldValidator.HexadecimalStringValidator(
                bytesLength = 20
            )

            AntelopePrimitiveDataTypes.CHECKSUM256 -> AntelopeDataFieldValidator.HexadecimalStringValidator(
                bytesLength = 32
            )

            AntelopePrimitiveDataTypes.CHECKSUM512 -> AntelopeDataFieldValidator.HexadecimalStringValidator(
                bytesLength = 64
            )

            AntelopePrimitiveDataTypes.PUBLIC_KEY -> AntelopeDataFieldValidator.PublicKeyValidator
            AntelopePrimitiveDataTypes.SIGNATURE -> AntelopeDataFieldValidator.SignatureValidator
            AntelopePrimitiveDataTypes.SYMBOL -> AntelopeDataFieldValidator.SymbolValidator
            AntelopePrimitiveDataTypes.SYMBOL_CODE -> AntelopeDataFieldValidator.SymbolCodeValidator
            AntelopePrimitiveDataTypes.EXTENDED_SYMBOL -> AntelopeDataFieldValidator.ExtendedSymbolValidator
            AntelopePrimitiveDataTypes.EXTENDED_ASSET -> AntelopeDataFieldValidator.ExtendedAssetValidator(validPrecision)
            AntelopePrimitiveDataTypes.ASSET -> {
                // It is recommended to compare the value with the precision returned from get_currency_stats
                // to ensure that the user is not entering any excess precision
                AntelopeDataFieldValidator.AssetValidator(
                    validPrecision,
                    strictPrecisionCheck
                )
            }
        }
    }
}

sealed interface AntelopeDataFieldValidator {
    fun isValid(value: String): Boolean
    fun getValidationResult(value: String): Result<Unit>

    data object BoolValidator : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return value == "true" || value == "false"
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid boolean value"))
            }
        }
    }

    data object StringValidator : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return try {
                value.toByteArray(Charsets.UTF_8)
                true
            } catch (e: Exception) {
                false
            }
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid string value"))
            }
        }
    }

    data class IntegerNumberValidator(val minValue: BigInteger, val maxValue: BigInteger) :
        AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            if (value == "-" && minValue < BigInteger.ZERO) return true

            try {
                val number = BigInteger.parseString(value)

                return !(number < minValue || number > maxValue)
            } catch (e: Exception) {
                return false
            }
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid integer value"))
            }
        }
    }

    data class DecimalNumberValidator(val minValue: BigDecimal, val maxValue: BigDecimal) :
        AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            val number = BigDecimal.parseString(value)

            return !(number < minValue || number > maxValue)
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid decimal value"))
            }
        }
    }

    data class HexadecimalStringValidator(
        val bytesLength: Int? // null for unconstrained byte length
    ) : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            if (value.length % 2 != 0) return false

            if (bytesLength != null && value.length / 2 != bytesLength) return false

            return value.lowercase().all { it in '0'..'9' || it in 'a'..'f' }
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid hexadecimal value"))
            }
        }
    }

    data class NameValidator(val strictLength: Boolean = false) : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return getValidationResult(value).isSuccess
        }

        override fun getValidationResult(value: String): Result<Unit> {
            if (strictLength && value.length > MAX_NAME_LENGTH_STRICT) {
                return Result.failure(InvalidNameException.InvalidNameLength)
            } else if (!strictLength && value.length > MAX_NAME_LENGTH) {
                return Result.failure(InvalidNameException.InvalidNameLength)
            }

            val hasValidCharacter = value.lowercase().all { it in 'a'..'z' || it in '1'..'5' || it == '.' }

            if (strictLength) {
                return if (hasValidCharacter) {
                    Result.success(Unit)
                } else {
                    Result.failure(InvalidNameException.InvalidCharacterInName)
                }
            } else {
                return if (hasValidCharacter) {
                    if (value.length == 13) {
                        // Additional check to ensure that serialized value matches input value

                        val byteWriter = DefaultByteWriter()
                        val byteReader = DefaultByteReader()
                        val nameWriter = NameWriter()

                        nameWriter.put(value, byteWriter)
                        byteReader.load(byteWriter.toBytes())
                        val parsedValue = byteReader.getName(byteReader, null)

                        if (parsedValue == value) {
                            Result.success(Unit)
                        } else {
                            Result.failure(InvalidNameException.NameDoesNotMatchSerializedName(parsedValue))
                        }
                    } else {
                        Result.success(Unit)
                    }
                } else {
                    Result.failure(InvalidNameException.InvalidCharacterInName)
                }
            }

        }

        sealed class InvalidNameException : Throwable() {
            data object InvalidNameLength : InvalidNameException()
            data object InvalidCharacterInName: InvalidNameException()
            data class NameDoesNotMatchSerializedName(val serializedName: String) : InvalidNameException()
        }

        companion object {
            const val MAX_NAME_LENGTH = 13
            const val MAX_NAME_LENGTH_STRICT = 12
        }
    }

    data object PublicKeyValidator : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return try {
                EosPublicKey(value)
                true
            } catch (e: Exception) {
                false
            }
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid public key value"))
            }
        }
    }

    // https://github.com/wharfkit/antelope/blob/a22e2829638c82dfa463d82ced066820b182d5db/src/chain/asset.ts#L128
    data object SymbolValidator : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return getValidationResult(value).isSuccess
        }

        override fun getValidationResult(value: String): Result<Unit> {
            val parts = mutableListOf<String>()
            parts.addAll(value.split(","))

            if (parts.size != 2 && value != "0,") return Result.failure(
                InvalidSymbolException.InvalidSymbolFormat
            )

            if (value == "0,") parts.add("")

            var precisionException: InvalidSymbolException? = null
            var symbolException: InvalidSymbolException? = null

            val precision = parts.getOrNull(0)?.toIntOrNull()
            if (precision == null) {
                precisionException = InvalidSymbolException.MissingSymbolPrecision
            }

            val symbolName = parts.getOrNull(1)
            if (symbolName.isNullOrBlank()) {
                symbolException = InvalidSymbolException.MissingSymbolName
            }

            if (precisionException != null || symbolException != null) {
                return Result.failure(
                    DataFieldException.TwoFieldsException(
                        precisionException,
                        symbolException
                    )
                )
            }

            val isValidPrecision = precision in 0..MAX_PRECISION
            if (isValidPrecision.not()) {
                precisionException = InvalidSymbolException.InvalidSymbolPrecision
            }

            val isValidSymbolCode = SymbolCodeValidator.isValid(symbolName.orEmpty())
            if (isValidSymbolCode.not()) {
                symbolException = InvalidSymbolException.InvalidSymbolValue
            }

            val isValid = isValidPrecision && isValidSymbolCode

            return if (isValid) {
                Result.success(Unit)
            } else {
                Result.failure(
                    DataFieldException.TwoFieldsException(
                        precisionException,
                        symbolException
                    )
                )
            }
        }

        sealed class InvalidSymbolException : Throwable() {
            data object MissingSymbolPrecision : InvalidSymbolException()
            data object MissingSymbolName : InvalidSymbolException()
            data object InvalidSymbolFormat : InvalidSymbolException()
            data object InvalidSymbolPrecision : InvalidSymbolException()
            data object InvalidSymbolValue : InvalidSymbolException()
        }

        private const val MAX_PRECISION = 18
    }

    data class AssetValidator(
        val validPrecision: Int? = null, // Check if precision matches required precision if this value is not null
        val strictPrecisionCheck: Boolean = false
    ) : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return getValidationResult(value).isSuccess
        }

        override fun getValidationResult(value: String): Result<Unit> {
            val parts = mutableListOf<String>()
            parts.addAll(value.split(" "))

            if (parts.size != 2) return Result.failure(InvalidAssetException.InvalidAssetFormat)

            // TODO: Get the decimal separator from the locale and replace it instead of hardcoding .

            val valueString = parts.getOrNull(0)
            val symbol = parts.getOrNull(1)

            var valueException: InvalidAssetException? = null
            var symbolException: InvalidAssetException? = null

            if (symbol.isNullOrBlank()) {
                symbolException = InvalidAssetException.MissingAssetSymbol
            }

            if (valueString.isNullOrBlank()) {
                valueException = InvalidAssetException.MissingAssetValue
            }

            if (valueException != null || symbolException != null) {
                return Result.failure(
                    DataFieldException.TwoFieldsException(
                        valueException,
                        symbolException
                    )
                )
            }

            val value = valueString?.replace(".", "")?.toLongOrNull() // By converting to long, validates if amount is an Int64 in the process

            if (value == null) {
                valueException = InvalidAssetException.InvalidValueFormat
            }

            symbolException = if (SymbolCodeValidator.isValid(symbol.orEmpty()).not()) {
                InvalidAssetException.InvalidSymbol
            } else {
                null
            }

            if (valueException != null || symbolException != null) {
                return Result.failure(
                    DataFieldException.TwoFieldsException(
                        valueException,
                        symbolException
                    )
                )
            }

            if (validPrecision != null) {
                val actualPrecision = parts[0].split('.').getOrNull(1)?.length

                val isValidPrecision = if (strictPrecisionCheck) {
                    actualPrecision == validPrecision
                } else {
                    (actualPrecision ?: 0) <= validPrecision
                }

                if (isValidPrecision.not()) {
                    valueException = InvalidAssetException.InvalidValuePrecision
                }
            }

            return if (valueException == null) {
                Result.success(Unit)
            } else {
                Result.failure(
                    DataFieldException.TwoFieldsException(
                        valueException,
                        symbolException
                    )
                )
            }
        }

        sealed class InvalidAssetException : Throwable() {
            data object InvalidAssetFormat : InvalidAssetException()
            data object MissingAssetSymbol : InvalidAssetException()
            data object MissingAssetValue : InvalidAssetException()
            data object InvalidValueFormat : InvalidAssetException()
            data object InvalidValuePrecision : InvalidAssetException()
            data object InvalidSymbol : InvalidAssetException()
        }
    }

    data object SignatureValidator : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            if (value.startsWith("SIG_").not()) return false

            return try {
                EosSignature.fromString(value)
                true
            } catch (e: Exception) {
                false
            }
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid signature value"))
            }
        }
    }

    data object ExtendedSymbolValidator: AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return getValidationResult(value).isSuccess
        }

        override fun getValidationResult(value: String): Result<Unit> {
            val elements = value.split(EXTENDED_TYPE_DELIMITER)

            val symbol = elements.getOrNull(0)
            val contractName = elements.getOrNull(1)

            val symbolException = symbol.validateSymbol()
            val contractNameException = contractName.validateName()

            if (symbolException == null && contractNameException == null) {
                return Result.success(Unit)
            }

            return Result.failure(
                DataFieldException.ThreeFieldsException(
                    symbolException,
                    contractNameException
                )
            )
        }

        private fun String?.validateName(): Throwable? {
            if (isNullOrBlank()) {
                return InvalidExtendedSymbolException.MissingContractName
            }

            return NameValidator(strictLength = true).getValidationResult(orEmpty()).exceptionOrNull()
        }

        private fun String?.validateSymbol(): DataFieldException.TwoFieldsException? {
            if (isNullOrBlank()) {
                return DataFieldException.TwoFieldsException(
                    SymbolValidator.InvalidSymbolException.MissingSymbolPrecision,
                    SymbolValidator.InvalidSymbolException.MissingSymbolName
                )
            }

            val symbolValidationResult = SymbolValidator.getValidationResult(orEmpty()).exceptionOrNull() ?: return null

            return symbolValidationResult as? (DataFieldException.TwoFieldsException)
                ?: DataFieldException.TwoFieldsException(
                    symbolValidationResult,
                    null
                )
        }

        sealed class InvalidExtendedSymbolException : Throwable() {
            data object MissingContractName : InvalidExtendedSymbolException()
        }
    }

    data class ExtendedAssetValidator(val validPrecision: Int? = null): AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return ExtendedSymbolValidator.getValidationResult(value).isSuccess
        }

        override fun getValidationResult(value: String): Result<Unit> {
            val elements = value.split(EXTENDED_TYPE_DELIMITER)

            val asset = elements.getOrNull(0)
            val contractName = elements.getOrNull(1)

            val assetException = asset.validateAsset()
            val contractNameException = contractName.validateName()

            if (assetException == null && contractNameException == null) {
                return Result.success(Unit)
            }

            return Result.failure(
                DataFieldException.ThreeFieldsException(
                    assetException,
                    contractNameException
                )
            )
        }

        private fun String?.validateName(): Throwable? {
            if (isNullOrBlank()) {
                return InvalidExtendedAssetException.MissingContractName
            }

            return NameValidator(strictLength = true).getValidationResult(orEmpty()).exceptionOrNull()
        }

        private fun String?.validateAsset(): DataFieldException.TwoFieldsException? {
            if (isNullOrBlank()) {
                return DataFieldException.TwoFieldsException(
                    InvalidAssetException.MissingAssetValue,
                    InvalidAssetException.MissingAssetSymbol
                )
            }

            val assetValidationResult = AssetValidator(validPrecision).getValidationResult(this).exceptionOrNull() ?: return null

            return assetValidationResult as? (DataFieldException.TwoFieldsException)
                ?: DataFieldException.TwoFieldsException(
                    assetValidationResult,
                    null
                )
        }

        sealed class InvalidExtendedAssetException : Throwable() {
            data object MissingContractName : InvalidExtendedAssetException()
        }
    }

    data object SymbolCodeValidator : AntelopeDataFieldValidator {
        override fun isValid(value: String): Boolean {
            return value.length <= 7 && value.all { it in 'A'..'Z' }
        }

        override fun getValidationResult(value: String): Result<Unit> {
            return if (isValid(value)) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalArgumentException("Invalid symbol code value"))
            }
        }
    }

    sealed class DataFieldException(message: String) : Throwable(message) {
        data class TwoFieldsException(
            val field1Exception: Throwable?,
            val field2Exception: Throwable?
        ) : Throwable()

        data class ThreeFieldsException(
            val compoundFieldException: TwoFieldsException?,
            val additionalFieldException: Throwable?
        ) : Throwable()
    }
}