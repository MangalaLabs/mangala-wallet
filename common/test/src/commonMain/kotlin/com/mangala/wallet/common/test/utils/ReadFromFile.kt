package com.mangala.wallet.common.test.utils

expect class SharedFileReader() {
    fun loadJsonFile(fileName: String): String?
}