package com.mangala.wallet.features.chains.evmcompatible.core

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.utils.EIP55

class AddressValidator {

    open class AddressValidationException(msg: String) : Exception(msg)
    class InvalidAddressLength(msg: String) : AddressValidationException(msg)
    class InvalidAddressHex(msg: String) : AddressValidationException(msg)
    class InvalidAddressChecksum(msg: String) : AddressValidationException(msg)

    companion object {
        private const val ADDRESS_LENGTH_IN_HEX = 40

        @Throws(AddressValidationException::class)
        fun validate(address: String) {
            val cleanAddress = address.stripHexPrefix()

            check(cleanAddress.length == ADDRESS_LENGTH_IN_HEX) {
                throw InvalidAddressLength("address: $address")
            }

            try {
                BigInteger.parseString(cleanAddress, 16)
            } catch (ex: NumberFormatException) {
                throw InvalidAddressHex("address: $address")
            }

            if (isMixedCase(cleanAddress)) {
                val checksumAddress = EIP55.format(cleanAddress).stripHexPrefix()
                check(checksumAddress == cleanAddress) {
                    throw InvalidAddressChecksum("address: $address")
                }
            }
        }

        fun isAddressValid(address: String): Boolean {
            return try {
                validate(address)
                true
            } catch (ex: AddressValidationException) {
                false
            }
        }

        private fun isMixedCase(address: String): Boolean {
            var containsUpperCase = false
            var containsLowerCase = false

            address.forEach {
                when {
                    it.isUpperCase() -> containsUpperCase = true
                    it.isLowerCase() -> containsLowerCase = true
                }
            }
            return containsLowerCase && containsUpperCase
        }
    }

}
