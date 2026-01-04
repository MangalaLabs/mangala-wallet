/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.websocket

import com.mangala.wallet.walletconnect.utils.WCLogger
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

internal class KtorSocket(
    private val serverUrl: String,
    private val client: HttpClient = httpClient(),
) {
    internal val receive = MutableSharedFlow<String>(replay = 1)
    internal val status = MutableStateFlow<Status>(Status.Idle)
    private val sendFlow = MutableSharedFlow<String>(replay = 1)
    private var sendRoutine: Job? = null
    private var rvRoutine: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    sealed class Status {
        object Idle : Status()
        object Connected : Status()
        object Closed : Status()
        data class Error(val error: Throwable) : Status()
    }

    suspend fun connect() {
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            status.value = Status.Error(error = throwable)
        }
        scope.launch(exceptionHandler) {
            client.webSocket(
                serverUrl.replace("https://", "wss://").replace("http://", "ws://"),
            ) {
                rvRoutine = launch { receiveMessage() }
                sendRoutine = launch { sendMessage() }
                status.value = Status.Connected
                WCLogger.log("websocket connected:$serverUrl")
                sendRoutine?.join()
                rvRoutine?.cancelAndJoin()
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.receiveMessage() {
        for (frame in incoming) {
            when (frame.frameType) {
                FrameType.CLOSE -> status.value = Status.Closed
                FrameType.PING -> send(Frame.Pong(data = byteArrayOf(1)))
                else -> {
                    if (frame is Frame.Text) {
                        receive.emit(frame.readText())
                    }
                }
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendMessage() {
        sendFlow.collect {
            send(it)
        }
    }


    fun close() {
        status.value = Status.Closed
        rvRoutine?.cancel()
        WCLogger.log("websocket closed:$serverUrl")
    }

    fun send(message: String) {
        sendFlow.tryEmit(message)
    }
}
