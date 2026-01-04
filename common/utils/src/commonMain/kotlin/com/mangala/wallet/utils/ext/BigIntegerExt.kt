package com.mangala.wallet.utils.ext

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.getThousandSeparator

fun BigInteger.clearBit(n: Int): BigInteger {
    if (n < 0) throw ArithmeticException("Negative bit address")

    // Create a bitmask with only the nth bit set.
    val mask = BigInteger.ONE.shiftLeft(n)

    // Invert the mask to have all bits set except the nth bit.
    val invertedMask = mask.not()

    // Perform a bitwise AND with the inverted mask to clear the nth bit.
    return this.and(invertedMask)
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

fun BigInteger.getLowestSetBit(): Int {
    if (this == BigInteger.ZERO) return -1

    // Convert to binary string representation
    val binaryString = this.toString(2)

    // Find the index of the rightmost '1'
    val lowestSetBitIndex = binaryString.lastIndexOf('1')

    // Return the number of bits to the right of the rightmost '1'
    return if (lowestSetBitIndex != -1) binaryString.length - 1 - lowestSetBitIndex else -1
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

fun BigInteger.formatWithThousandSeparator(): String {
    val thousandSeparator = getThousandSeparator()
    val regex = "(\\d)(?=(\\d{3})+(?!\\d))".toRegex()
    val value = this.toString()
    return value.replace(regex, "$1$thousandSeparator")
}