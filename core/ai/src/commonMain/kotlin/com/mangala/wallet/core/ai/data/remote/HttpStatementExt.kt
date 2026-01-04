package com.mangala.wallet.core.ai.data.remote

import io.ktor.client.statement.HttpStatement
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString

inline fun <reified T> HttpStatement.asFlow(format: StringFormat): Flow<T> = callbackFlow {
    val content: ByteReadChannel = this@asFlow.body()

    while (!content.isClosedForRead) {
        val line = content.readUTF8Line()
        println("HttpStatement asFlow read $line")
        try {
            if (!line.isNullOrEmpty()) {
                val obj = if (line.startsWith("data:")) {
                    format.decodeFromString<T>(line.substringAfter("data:"))
                } else {
                    format.decodeFromString<T>(line)
                }
                trySendBlocking(obj)
            }

        } catch (e: Exception) {
            throw e
        }
    }
    awaitClose {

    }
}