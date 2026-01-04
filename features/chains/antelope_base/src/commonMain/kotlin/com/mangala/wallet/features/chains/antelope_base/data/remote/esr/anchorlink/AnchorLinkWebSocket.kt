package com.mangala.wallet.features.chains.antelope_base.data.remote.esr.anchorlink

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText

class AnchorLinkWebSocket(private val client: HttpClient) {

    suspend fun start(url: String, onRead: (ByteArray) -> Unit, onClose: () -> Unit) {
        try {
            client.webSocket(urlString = url) {
                try {
                    while (true) {
                        val frame = incoming.receive()

                        when (frame) {
                            is Frame.Text -> {
                                println(frame.readText())
                            }

                            is Frame.Binary -> {
                                val bytes = frame.readBytes()
                                onRead(bytes)
                            }

                            is Frame.Close -> {
                                println("Connection closed")
                                break
                            }

                            else -> {

                            }
                        }
                        println(frame)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    onClose()
                }
            }
        } catch (e: Exception) {
            // Handle connection errors (UnknownHostException, network errors, etc.)
            println("WebSocket connection failed: ${e.message}")
            e.printStackTrace()
            onClose()
        }
    }
}