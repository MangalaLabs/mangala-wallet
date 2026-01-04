package com.mangala.wallet.features.chains.antelope_base.domain.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

fun String.base64uToByteArray(): ByteArray {
    return this.base64uToBase64().base64StringToByteArray()
}

fun String.base64uToBase64(): String {
    var base64 = this.replace('-', '+').replace('_', '/')

    val paddingNeeded = (4 - base64.length % 4) % 4
    base64 += "=".repeat(paddingNeeded)

    return base64
}

@OptIn(ExperimentalEncodingApi::class)
fun String.base64StringToByteArray(): ByteArray {
    return Base64.Default.decode(this)
}

fun encodeBase64u(data: ByteArray, urlSafe: Boolean = true): String {
    val byteLength = data.size
    val byteRemainder = byteLength % 3
    val mainLength = byteLength - byteRemainder
    val charset = baseCharset + if (urlSafe) "-_" else "+/"
    val parts = mutableListOf<String>()

    var a: Int
    var b: Int
    var c: Int
    var d: Int
    var chunk: Int

    // Main loop deals with bytes in chunks of 3
    for (i in 0 until mainLength step 3) {
        // Combine the three bytes into a single integer
        chunk = (data[i].toInt() and 0xFF shl 16) or
                (data[i + 1].toInt() and 0xFF shl 8) or
                (data[i + 2].toInt() and 0xFF)

        // Use bitmasks to extract 6-bit segments from the triplet
        a = (chunk and 16515072) shr 18 // 16515072 = (2^6 - 1) << 18
        b = (chunk and 258048) shr 12 // 258048   = (2^6 - 1) << 12
        c = (chunk and 4032) shr 6 // 4032     = (2^6 - 1) << 6
        d = chunk and 63 // 63       =  2^6 - 1

        // Convert the raw binary segments to the appropriate ASCII encoding
        parts.add("${charset[a]}${charset[b]}${charset[c]}${charset[d]}")
    }

    // Deal with the remaining bytes
    if (byteRemainder == 1) {
        chunk = data[mainLength].toInt() and 0xFF

        a = (chunk and 252) shr 2 // 252 = (2^6 - 1) << 2

        // Set the 4 least significant bits to zero
        b = (chunk and 3) shl 4 // 3   = 2^2 - 1

        parts.add("${charset[a]}${charset[b]}")
    } else if (byteRemainder == 2) {
        chunk = ((data[mainLength].toInt() and 0xFF) shl 8) or
                (data[mainLength + 1].toInt() and 0xFF)

        a = (chunk and 64512) shr 10 // 64512 = (2^6 - 1) << 10
        b = (chunk and 1008) shr 4 // 1008  = (2^6 - 1) << 4

        // Set the 2 least significant bits to zero
        c = (chunk and 15) shl 2 // 15    = 2^4 - 1

        parts.add("${charset[a]}${charset[b]}${charset[c]}")
    }

    return parts.joinToString("")
}

private const val baseCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
