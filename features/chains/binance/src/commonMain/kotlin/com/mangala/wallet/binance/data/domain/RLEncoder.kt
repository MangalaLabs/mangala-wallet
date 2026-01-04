package com.mangala.wallet.binance.data.domain

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.binance.data.model.RawTransaction
import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString

class RLEncoder {
    companion object {
        private const val ADDRESS_LENGTH = 20
        private const val WORD_SIZE = 32
        private const val WORD_SIZE_HEX = 64

        fun encode(rawTransaction: RawTransaction): ByteArray {
            val buffer = Buffer()

            // Encode chainId as a 4-byte unsigned integer in big-endian byte order
            buffer.writeInt(rawTransaction.chainId)

            // Encode sender and recipient as 20-byte addresses (padded with leading zeros if necessary)
            buffer.writeFixedLengthAddress(rawTransaction.sender)
            buffer.writeFixedLengthAddress(rawTransaction.recipient)

            // Encode nonce, gasPrice, and gasLimit as big-endian integers with variable length encoding
            buffer.writeVariableLengthInteger(rawTransaction.nonce)
            buffer.writeVariableLengthInteger(rawTransaction.gasPrice)
            buffer.writeVariableLengthInteger(rawTransaction.gasLimit)

            // Encode value as a big-endian integer in wei (i.e., 18 decimal places)
            buffer.writeVariableLengthInteger(rawTransaction.value.toBigInteger())

            // Encode data as a variable-length byte array
            buffer.writeVariableLengthByteArray(rawTransaction.data)

            return buffer.readByteArray()
        }

        private fun Buffer.writeFixedLengthAddress(address: String) {
            val paddedAddress = address.removePrefix("0x").padStart(ADDRESS_LENGTH * 2, '0')
            write(paddedAddress.hexStringToByteArray())
        }

        private fun Buffer.writeVariableLengthInteger(value: BigInteger) {
            if (value == BigInteger.ZERO) {
                writeByte(0x80)
                return
            }

            val bytes = value.toByteArray()
            val length = bytes.size

            if (bytes[0].toInt() and 0xFF > 0x7F) {
                writeByte(0x00)
                write(bytes)
            } else {
                write(bytes)
            }
        }

        private fun Buffer.writeVariableLengthByteArray(bytes: ByteArray) {
            if (bytes.isEmpty()) {
                writeByte(0x80)
                return
            }

            writeVariableLengthInteger(BigInteger.parseString(bytes.size.toString()))
            write(bytes)
        }

        fun String.hexStringToByteArray(): ByteArray {
            return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        }
    }
}