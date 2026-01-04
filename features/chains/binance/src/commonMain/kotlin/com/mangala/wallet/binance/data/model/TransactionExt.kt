package com.mangala.wallet.binance.data.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import okio.Buffer

fun Transaction.toSerializedByteArray(): ByteArray {
    val buffer = Buffer()

    // Write nonce
    buffer.writeVariableLengthInteger(nonce)

    // Write gasPrice
    buffer.writeVariableLengthInteger(gasPrice)

    // Write gasLimit
    buffer.writeVariableLengthInteger(gasLimit)

    // Write recipient
    buffer.writeFixedLengthAddress(recipient)

    // Write value
    buffer.writeVariableLengthInteger(value)

    // Write data
    buffer.writeVariableLengthByteArray(data)

    return buffer.readByteArray()
}

private fun Buffer.writeVariableLengthInteger(value: BigInteger) {
    if (value == BigInteger.ZERO) {
        writeByte(0x80)
        return
    }

    var bytes = value.toByteArray()
    while (bytes.size > 1 && bytes[0] == 0x00.toByte()) {
        bytes = bytes.sliceArray(1 until bytes.size)
    }

    write(bytes)
}

private const val ADDRESS_LENGTH = 20

private fun Buffer.writeFixedLengthAddress(address: String) {
    val paddedAddress = address.removePrefix("0x").padStart(ADDRESS_LENGTH * 2, '0')
    write(paddedAddress.hexStringToByteArray())
}

private fun Buffer.writeVariableLengthByteArray(bytes: ByteArray) {
    if (bytes.isEmpty()) {
        writeByte(0x80)
        return
    }

    writeVariableLengthInteger(BigInteger.parseString(bytes.size.toString()))
    write(bytes)
}

//fun String.hexStringToByteArray(): ByteArray {
//    val cleanString = if (startsWith("0x")) {
//        substring(2)
//    } else {
//        this
//    }
//    require(cleanString.length % 2 == 0) { "The input string must have an even number of characters" }
//    return cleanString.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
//}

fun String.hexStringToByteArray(): ByteArray {
    val cleanString = if (startsWith("0x")) {
        substring(2)
    } else {
        this
    }
    val paddedString = if (cleanString.length % 2 == 1) {
        "0$cleanString"
    } else {
        cleanString
    }
    return paddedString.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}


