package com.mangala.wallet.utils

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.ionspin.kotlin.bignum.integer.toBigInteger
import kotlin.experimental.xor
import kotlin.math.min
import kotlin.random.Random

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

//fun toHexString2(input: ByteArray, offset: Int, length: Int, withPrefix: Boolean): String {
//    val stringBuilder = StringBuilder()
//    if (withPrefix) {
//        stringBuilder.append("0x")
//    }
//    for (i in offset until offset + length) {
//        stringBuilder.append(String.format("%02x", input[i].toInt() and 0xFF))
//    }
//
//    return stringBuilder.toString()
//}

fun toHexString2(input: ByteArray, offset: Int, length: Int, withPrefix: Boolean): String {
    val stringBuilder = StringBuilder()
    if (withPrefix) {
        stringBuilder.append("0x")
    }
    for (i in offset until offset + length) {
        val hex = input[i].toUByte().toString(16).padStart(2, '0')
        stringBuilder.append(hex)
    }

    return stringBuilder.toString()
}

fun ByteArray.toHexString2(): String {
    return toHexString2(this, 0, this.size, true)
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

fun String.getByteArray(): ByteArray {
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
    return this.stripHexPrefix().toLongOrNull(16)
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
    val value = BigDecimal.parseString(this)
    return value.moveDecimalPoint(decimal).toBigInteger()
}


fun BigInteger.testBit(n: Int): Boolean {
    val two = BigInteger.fromInt(2)
    // Calculate 2^n
    val powerOfTwo = two.pow(n)
    // Calculate 2^(n+1)
    val powerOfTwoPlusOne = two.pow(n + 1)

    // Calculate this mod 2^(n+1)
    val modResult = this.mod(powerOfTwoPlusOne)

    // If modResult >= 2^n, the bit is set
    return modResult >= powerOfTwo
}


fun BigInteger.shiftLeft(n: Int): BigInteger {
    // BigInteger representation of 2
    val two = BigInteger.fromInt(2)

    // Calculate 2^n
    val multiplier = two.pow(n)

    // Multiply this BigInteger by 2^n to achieve left shift
    return this.multiply(multiplier)
}

fun BigInteger.shiftRight(n: Int): BigInteger {
    // BigInteger representation of 2
    val two = BigInteger.fromInt(2)

    // Calculate 2^n
    val divisor = two.pow(n)

    // Divide this BigInteger by 2^n to achieve right shift
    return this.divide(divisor)
}

fun BigInteger.toInt(): Int {
    val bytes = this.toByteArray()
    if (bytes.isEmpty()) return 0 // Equivalent to check if BigInteger is zero

    // Ensure there are at least 4 bytes to avoid IndexOutOfBoundsException
    val safeBytes = bytes.copyOfRange(maxOf(bytes.size - 4, 0), bytes.size)
    var result = 0

    // Convert the last 4 bytes (or fewer if the number is smaller) to an Int
    for (byte in safeBytes) {
        result = (result shl 8) or (byte.toInt() and 0xFF)
    }

    return result
}

fun BigInteger.bitCount(): Int {
    var count = 0
    // Convert BigInteger to binary string representation.
    val binaryString = this.toString(2)
    // Count the number of '1's in the string.
    binaryString.forEach { char ->
        if (char == '1') count++
    }
    return count
}

fun BigInteger.modPow(exponent: BigInteger, modulus: BigInteger): BigInteger {
    // Basic validation
    require(modulus > BigInteger.ZERO) { "Modulus must be positive." }
    require(exponent >= BigInteger.ZERO) { "Exponent must be non-negative." }

    // Edge cases
    if (modulus == BigInteger.ONE) return BigInteger.ZERO
    if (exponent == BigInteger.ZERO) return BigInteger.ONE

    var result = BigInteger.ONE
    var base = this % modulus
    var exp = exponent

    while (exp > BigInteger.ZERO) {
        if (exp % BigInteger.TWO != BigInteger.ZERO) {
            result = (result * base) % modulus
        }
        exp /= BigInteger.TWO
        base = (base * base) % modulus
    }

    return result
}

fun randomBigInteger(mBits: Int): BigInteger {
    val byteArray = ByteArray((mBits + 7) / 8) // +7 to round up to the nearest byte
    Random.nextBytes(byteArray)
    // Ensure the BigInteger is within the required bit length
    val bitMask = (1 shl (mBits % 8)) - 1
    if (mBits % 8 > 0) {
        byteArray[0] = (byteArray[0].toInt() and bitMask).toByte()
    }
    return BigInteger.fromUByteArray(byteArray.toUByteArray(), com.ionspin.kotlin.bignum.integer.Sign.POSITIVE)
}

fun BigInteger.clearBit(n: Int): BigInteger {
    if (n < 0) throw ArithmeticException("Negative bit address")

    // Create a bitmask with only the nth bit set.
    val mask = BigInteger.ONE.shiftLeft(n)

    // Invert the mask to have all bits set except the nth bit.
    val invertedMask = mask.not()

    // Perform a bitwise AND with the inverted mask to clear the nth bit.
    return this.and(invertedMask)
}

fun BigInteger.getLowestSetBit(): Int {
    if (this == BigInteger.ZERO) return -1

    // Convert to binary string representation
    val binaryString = this.toString(2)

    // Find the index of the rightmost '1'
    val lowestSetBitIndex = binaryString.lastIndexOf('1')

    // Return the number of bits to the right of the rightmost '1'
    return if (lowestSetBitIndex != -1) binaryString.length - 1 - lowestSetBitIndex else -1
}

fun List<BigDecimal>.average(): BigDecimal {
    if (isEmpty()) throw ArithmeticException("Cannot calculate average of empty list")

    val sum = fold(BigDecimal.ZERO) { acc, value -> acc + value }
    return sum.divide(
        BigDecimal.fromInt(size),
        calculatingDecimalMode
    )
}

//fun BigInteger.clearBit(n: Int): BigInteger {
//    require(n >= 0) { "Bit position must be non-negative" }
//    val intNum = n ushr 5
//    val result = IntArray(maxOf(this.bitLength(), (n + 1 ushr 5) + 1))
//    for (i in result.indices) result[result.size - i - 1] = this.intValue(i)
//    result[result.size - intNum - 1] = result[result.size - intNum - 1] and (1 shl (n and 31)).inv()
//    return BigInteger.fromByteArray(result, Sign.POSITIVE)
//}