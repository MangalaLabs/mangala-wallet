package com.mangala.wallet.cryptography

private val ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray()
private val ENCODED_ZERO = ALPHABET[0]

/**
 * Encodes the given bytes as a base58 string (no checksum is appended).
 *
 * @param input the bytes to encode
 * @return the base58-encoded string
 */
fun ByteArray.base58(): String {
    var input = this
    if (input.isEmpty()) {
        return ""
    }
    // Count leading zeros.
    var zeros = 0
    while (zeros < input.size && input[zeros].toInt() == 0) {
        ++zeros
    }
    // Convert base-256 digits to base-58 digits (plus conversion to ASCII characters)
    input = input.copyOf(input.size) // since we modify it in-place
    val encoded = CharArray(input.size * 2) // upper bound
    var outputStart = encoded.size
    var inputStart = zeros
    while (inputStart < input.size) {
        encoded[--outputStart] = ALPHABET[divmod(input, inputStart, 256, 58).toInt()]
        if (input[inputStart].toInt() == 0) {
            ++inputStart // optimization - skip leading zeros
        }
    }
    // Preserve exactly as many leading encoded zeros in output as there were leading zeros in input.
    while (outputStart < encoded.size && encoded[outputStart] == ENCODED_ZERO) {
        ++outputStart
    }
    while (--zeros >= 0) {
        encoded[--outputStart] = ENCODED_ZERO
    }
    // Return encoded string (including encoded leading zeros).
    return encoded.concatToString(outputStart, outputStart + (encoded.size - outputStart))
}

/**
 * Divides a number, represented as an array of bytes each containing a single digit
 * in the specified base, by the given divisor. The given number is modified in-place
 * to contain the quotient, and the return value is the remainder.
 *
 * @param number the number to divide
 * @param firstDigit the index within the array of the first non-zero digit
 * (this is used for optimization by skipping the leading zeros)
 * @param base the base in which the number's digits are represented (up to 256)
 * @param divisor the number to divide by (up to 256)
 * @return the remainder of the division operation
 */
private fun divmod(number: ByteArray, firstDigit: Int, base: Int, divisor: Int): Byte {
    // this is just long division which accounts for the base of the input digits
    var remainder = 0
    for (i in firstDigit until number.size) {
        val digit = number[i].toInt() and 0xFF
        val temp = remainder * base + digit
        number[i] = (temp / divisor).toByte()
        remainder = temp % divisor
    }
    return remainder.toByte()
}