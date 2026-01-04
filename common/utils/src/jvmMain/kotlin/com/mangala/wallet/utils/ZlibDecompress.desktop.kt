package com.mangala.wallet.utils

import java.io.ByteArrayOutputStream
import java.util.zip.Inflater

actual fun ByteArray.decompressRawZlib(): ByteArray {
    val inflater = Inflater(true)
    val outputStream = ByteArrayOutputStream()

    return outputStream.use {
        val buffer = ByteArray(1024)

        inflater.setInput(this)

        var count = -1
        while (count != 0) {
            count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }

        inflater.end()
        outputStream.toByteArray()
    }
}