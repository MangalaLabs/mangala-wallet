package com.mangala.wallet.utils.ext

private fun toHexString2(input: ByteArray, offset: Int, length: Int, withPrefix: Boolean): String {
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