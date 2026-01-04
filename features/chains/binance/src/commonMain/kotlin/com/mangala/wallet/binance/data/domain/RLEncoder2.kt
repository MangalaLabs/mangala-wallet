package com.mangala.wallet.binance.data.domain


import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.binance.data.model.RawTransaction
import okio.Buffer

class RLEncoder2 {
    companion object {
        private const val ADDRESS_LENGTH = 20

        fun encode(rawTransaction: RawTransaction): ByteArray {
            val buffer = Buffer()

            // Encode chainId as a 4-byte unsigned integer in big-endian byte order
            buffer.write(encodeInteger(rawTransaction.chainId))

            // Encode sender and recipient as 20-byte addresses (padded with leading zeros if necessary)
            buffer.write(encodeAddress(rawTransaction.sender))
            buffer.write(encodeAddress(rawTransaction.recipient))

            // Encode nonce, gasPrice, and gasLimit as big-endian integers with variable length encoding
            buffer.write(encodeBigInteger(rawTransaction.nonce))
            buffer.write(encodeBigInteger(rawTransaction.gasPrice))
            buffer.write(encodeBigInteger(rawTransaction.gasLimit))

            // Encode value as a big-endian integer in wei (i.e., 18 decimal places)
            buffer.write(encodeBigInteger(rawTransaction.value.toBigInteger()))

            // Encode data as a variable-length byte array
            buffer.write(encodeByteArray(rawTransaction.data))

            return buffer.readByteArray()
        }

        private fun encodeInteger(value: Int): ByteArray {
            return if (value == 0) {
                byteArrayOf(0x80.toByte())
            } else {
                encodeByteArray(BigInteger.parseString(value.toLong().toString()).toByteArray())
            }
        }

        private fun encodeBigInteger(value: BigInteger): ByteArray {
            return if (value == BigInteger.ZERO) {
                byteArrayOf(0x80.toByte())
            } else {
                encodeByteArray(value.toByteArray())
            }
        }

        private fun encodeAddress(address: String): ByteArray {
            return encodeByteArray(address.hexStringToByteArray())
        }

        private fun encodeByteArray(bytes: ByteArray): ByteArray {
            return when {
                bytes.size == 1 && bytes[0] in 0x00..0x7f -> {
                    // If the binary data is a single-byte value between 0x00 and 0x7f (inclusive),
                    // the RLP encoding is simply that byte.
                    bytes
                }
                bytes.size < 56 -> {
                    // If the binary data is a string (i.e., byte array) with length less than 56,
                    // the RLP encoding consists of a single byte indicating the length of the string,
                    // followed by the string itself.
                    byteArrayOf((0x80 or bytes.size).toByte()) + bytes
                }
                else -> {
                    // If the binary data is larger than a single byte, the RLP encoding consists
                    // of a single byte indicating the length of the binary data (in bytes),
                    // followed by the binary data itself.
                    val lengthBytes = encodeByteArray(BigInteger.parseString(bytes.size.toLong().toString()).toByteArray())
                    byteArrayOf((0xb7 + lengthBytes.size).toByte()) + lengthBytes + bytes
                }
            }
        }

        private fun String.hexStringToByteArray(): ByteArray {
            return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
        }
    }
}