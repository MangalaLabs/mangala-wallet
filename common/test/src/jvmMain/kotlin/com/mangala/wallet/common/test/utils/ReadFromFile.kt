package com.mangala.wallet.common.test.utils

import java.io.InputStreamReader

actual class SharedFileReader {
    actual fun loadJsonFile(fileName: String): String? {
        return javaClass.classLoader?.getResourceAsStream(fileName).use { stream ->
            InputStreamReader(stream!!).use { reader ->
                reader.readText()
            }
        }
    }
}