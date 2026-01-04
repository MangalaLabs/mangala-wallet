package com.mangala.wallet.features.chains.evmcompatible.core

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.experimental.xor
import kotlin.math.min

fun String.removeLeadingZeros(): String {
    return this.trimStart('0')
}

fun ByteArray?.toRawHexString(): String {
    return this?.joinToString(separator = "") {
        it.toInt().and(0xff).toString(16).padStart(2, '0')
    } ?: ""
}

fun ByteArray?.toHexString(): String {
    val rawHex = this?.toRawHexString() ?: return ""
    return "0x$rawHex"
}

@Throws(NumberFormatException::class)
fun String.hexStringToByteArray(): ByteArray {
    return this.getByteArray()
}

@Throws(NumberFormatException::class)
fun String.hexStringToByteArrayOrNull(): ByteArray? {
    return try {
        this.getByteArray()
    } catch (error: Throwable) {
        null
    }
}

private fun String.getByteArray(): ByteArray {
    var hexWithoutPrefix = this.stripHexPrefix()
    if (hexWithoutPrefix.length % 2 == 1) {
        hexWithoutPrefix = "0$hexWithoutPrefix"
    }
    return hexWithoutPrefix.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun String.stripHexPrefix(): String {
    return if (this.startsWith("0x", true)) {
        this.substring(2)
    } else {
        this
    }
}

fun Long.toHexString(): String {
    return "0x${this.toString(16)}"
}

fun Int.toHexString(): String {
    return "0x${this.toString(16)}"
}

fun String.hexStringToLongOrNull(): Long? {
    return try {
        this.stripHexPrefix().toLongOrNull(16)
    } catch (e: Exception) {
        null
    }
}

fun String.hexStringToIntOrNull(): Int? {
    return this.stripHexPrefix().toIntOrNull(16)
}

fun BigInteger.toHexString(): String {
    return "0x${this.toString(16)}"
}

fun String.hexStringToBigIntegerOrNull(): BigInteger? {
    return this.stripHexPrefix().toBigInteger(16)
}

// Converts positive long values to a byte array without leading zero byte (for sign bit)
fun Long.toByteArray(): ByteArray {
    var array = this.toBigInteger().toByteArray()
    if (array.isEmpty()) return array
    if (array[0].toInt() == 0) {
        val tmp = array.copyOfRange(1, array.size)
        array = tmp
    }
    return array
}

fun BigInteger.toBytes(numBytes: Int): ByteArray {
    val bytes = ByteArray(numBytes)
    val biBytes = this.toByteArray()
    val start = if (biBytes.size == numBytes + 1) 1 else 0
    val length = min(biBytes.size, numBytes)
    biBytes.copyInto(bytes, numBytes - length, start, length)
    return bytes
}

//fun Short.toBytes(): ByteArray {
//    return ByteBuffer.allocate(2).putShort(this).array()
//}
//
//fun ByteArray.toShort(): Short {
//    val bb = ByteBuffer.wrap(this)
//    bb.order(ByteOrder.BIG_ENDIAN)
//
//    return bb.short
//}
//
//fun ByteArray?.toInt(): Int {
//    val b: BigInteger = this?.toBigInteger() ?: return 0
//    return if (this == null || this.isEmpty()) 0 else BigInteger.fromByteArray(this, Sign.POSITIVE).toInt()
//}

fun ByteArray.toInt(): Int {
    var result = 0
    for (i in this.indices) {
        result = result shl 8
        result = result or (this[i].toInt() and 0xFF)
    }
    return result
}

//fun ByteArray?.toInt2(): Int {
//    return if (this == null || this.isEmpty()) 0 else BigInteger.fromByteArray(this, Sign.POSITIVE).signum()
//}

//
//fun ByteArray?.toLong(): Long {
//    return if (this == null || this.isEmpty()) 0 else BigInteger(1, this).toLong()
//}
//
fun ByteArray?.toBigInteger(): BigInteger {
    return if (this == null || this.isEmpty()) BigInteger.ZERO else BigInteger.fromByteArray(this, Sign.POSITIVE)
}
//
//fun RLPElement?.toInt(): Int {
//    val rlpData = this?.rlpData
//    return if (this == null || rlpData == null || rlpData.isEmpty()) 0 else BigInteger(1, rlpData).toInt()
//}
//
//fun RLPElement?.toLong(): Long {
//    val rlpData = this?.rlpData
//    return if (this == null || rlpData == null || rlpData.isEmpty()) 0 else BigInteger(1, rlpData).toLong()
//}
//
//fun RLPElement?.toBigInteger(): BigInteger {
//    val rlpData = this?.rlpData
//    return if (this == null || rlpData == null || rlpData.isEmpty()) BigInteger.ZERO else BigInteger(1, rlpData)
//}

//fun RLPElement?.asString(): String {
//    val rlpData = this?.rlpData
//    return if (this == null || rlpData == null || rlpData.isEmpty()) "" else String(rlpData)
//}

fun ByteArray.xor(other: ByteArray): ByteArray {
    val out = ByteArray(this.size)
    for (i in this.indices) {
        out[i] = (this[i] xor (other[i % other.size]))
    }
    return out
}

fun Int.toBytesNoLeadZeroes(): ByteArray {
    var value = this

    if (value == 0) return byteArrayOf()

    var length = 0

    var tmpVal = value
    while (tmpVal != 0) {
        tmpVal = tmpVal.ushr(8)
        ++length
    }

    val result = ByteArray(length)

    var index = result.size - 1
    while (value != 0) {

        result[index] = (value and 0xFF).toByte()
        value = value.ushr(8)
        index -= 1
    }

    return result
}

fun BigDecimal.removeTrailingZeroes(): BigDecimal {
    if (this == BigDecimal.ZERO) return BigDecimal.ZERO
    var significand = this.significand
    var divisionResult = BigInteger.QuotientAndRemainder(this.significand, BigInteger.ZERO)
    do {
        divisionResult = divisionResult.quotient.divrem(BigInteger.TEN)
        if (divisionResult.remainder == BigInteger.ZERO) {
            significand = divisionResult.quotient
        }
    } while (divisionResult.remainder == BigInteger.ZERO)
    return BigDecimal.fromBigIntegerWithExponent(significand, this.exponent)
}

fun BigDecimal.getScale(): Long {
    return this.decimalMode?.scale ?: -1
}

fun String.amountToPush(): String{
    val value = BigDecimal.parseString(this)
    val decimal = 18
    val bigInteger = value.moveDecimalPoint(decimal).toBigInteger()
    val result = "0x${bigInteger.toString(16)}"
    return result
}

fun BigInteger.amountToPush(): String{
    val result = "0x${this.toString(16)}"
    return result
}

fun String.amountToBigInt(): BigInteger {
    val value = BigDecimal.parseString(this)
    val decimal = 18
    return value.moveDecimalPoint(decimal).toBigInteger()
}

fun String.amountToBigInt(decimal: Long): BigInteger {
    return this.amountToBigDecimal(decimal).toBigInteger()
}

fun String.amountToBigDecimal(decimal: Long): BigDecimal {
    val value = BigDecimal.parseString(this)
    return value.moveDecimalPoint(decimal)
}

