package com.mangala.wallet.features.chains.evmcompatible.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.jvm.JvmStatic
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString

/**
 *
 * Message codec functions.
 *
 *
 * Implementation as per https://github.com/ethereum/wiki/wiki/JSON-RPC#hex-value-encoding
 */
object Numeric {
    private const val HEX_PREFIX = "0x"
    private fun isValidHexQuantity(value: String?): Boolean {
        if (value == null) {
            return false
        }
        return if (value.length < 3) {
            false
        } else value.startsWith(HEX_PREFIX)
    }

    fun cleanHexPrefix(input: String): String {
        return if (containsHexPrefix(input)) {
            input.substring(2)
        } else {
            input
        }
    }

    fun prependHexPrefix(input: String): String {
        return if (!containsHexPrefix(input)) {
            HEX_PREFIX + input
        } else {
            input
        }
    }

    fun containsHexPrefix(input: String): Boolean {
        return input.length > 1 && input[0] == '0' && input[1] == 'x'
    }


    fun toHexStringWithPrefix(value: BigInteger): String {
        return HEX_PREFIX + value.toString(16)
    }

    fun toHexStringNoPrefix(value: BigInteger): String {
        return value.toString(16)
    }



//    fun hexStringToByteArray(input: String): ByteArray {
//        val cleanInput = cleanHexPrefix(input)
//        val len = cleanInput.length
//        if (len == 0) {
//            return byteArrayOf()
//        }
//        val data: ByteArray
//        val startIdx: Int
//        if (len % 2 != 0) {
//            data = ByteArray(len / 2 + 1)
//            data[0] = (cleanInput[0].digitToIntOrNull(16) ?: -1).toByte()
//            startIdx = 1
//        } else {
//            data = ByteArray(len / 2)
//            startIdx = 0
//        }
//        var i = startIdx
//        while (i < len) {
//            data[(i + 1) / 2] = ((cleanInput[i].digitToIntOrNull(16) ?: -1 shl 4)
//            + cleanInput[i + 1].digitToIntOrNull(16)!! ?: -1).toByte()
//            i += 2
//        }
//        return data
//    }


    fun hexStringToByteArray(input: String): ByteArray {
        val cleanInput = input.removePrefix("0x")
        return ByteArray(cleanInput.length / 2) { index ->
            cleanInput.substring(index * 2, index * 2 + 2).toUByte(16).toByte()
        }
    }


    fun asByte(m: Int, n: Int): Byte {
        return (m shl 4 or n).toByte()
    }

}