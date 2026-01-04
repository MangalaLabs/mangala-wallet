/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.core

import com.mangala.wallet.walletconnect.entity.*
import com.mangala.wallet.walletconnect.entity.JsonRpcRequest
import com.mangala.wallet.walletconnect.entity.SocketMessage
import com.mangala.wallet.walletconnect.utils.*
import com.mangala.wallet.walletconnect.utils.JSON
import com.mangala.wallet.walletconnect.utils.WCCipher
import com.mangala.wallet.walletconnect.utils.encodeJson
import com.mangala.wallet.walletconnect.websocket.KtorSocket
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

internal class WCSession(
    val config: WCSessionConfig,
    private val clientId: String,
    private var remotePeerId: String? = null // only server needs remote peerId
) {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val key: String by lazy {
        config.key
    }

    private lateinit var socket: KtorSocket
    private var isSocketConnected: Boolean = false
    private var isSessionEnd = false
    private var statusJob: Job? = null

    init {
        resetSocket()
    }

    private fun resetSocket() {
        statusJob?.cancel()
        socket = KtorSocket(serverUrl = config.bridge)
        statusJob = scope.launch {
            socket.status.collect {
                isSocketConnected = it is KtorSocket.Status.Connected
            }
        }
    }

    val message: Flow<WCMethod> by lazy {
        socket.receive.mapNotNull {
            WCLogger.log("onReceive raw:$it")
            handleMessage(JSON.decodeFromString(SocketMessage.serializer(), it))
        }
    }

    suspend fun connectSocket(
        handShakeTopic: String? = null
    ) {
        if (socket.status.value !is KtorSocket.Status.Idle) resetSocket()
        socket.connect()
        while (socket.status.value is KtorSocket.Status.Idle) {
            delay(100)
        }
        val status = socket.status.value
        if (status is KtorSocket.Status.Connected) {
            isSocketConnected = true
            handShakeTopic?.let {
                subMessage(topic = it)
            }
            remotePeerId?.let {
                subMessage(topic = it)
            }
            subMessage(topic = clientId)
        } else {
            isSocketConnected = false
            throw if (status is KtorSocket.Status.Error) status.error else Error("WebSocket closed:${config.bridge}")
        }
    }

    private fun subMessage(
        topic: String
    ) {
        socket.send(
            SocketMessage(
                topic = topic,
                type = SocketMessage.Type.Sub,
                payload = ""
            ).encodeJson()
        )
    }

    fun closeSocket() {
        socket.close()
        isSessionEnd = true
        statusJob?.cancel()
    }

    suspend fun send(
        method: WCMethod,
        ignoreResponse: Boolean = false,
    ): WCMethod {
        if (isSessionEnd) {
            throw Error("Session is closed:$config")
        }
        if (!isSocketConnected) {
            connectSocket()
        }
        val payload = when (method) {
            is WCMethod.CustomRequest -> {
                JsonRpcRequest(
                    id = method.requestId,
                    method = method.method,
                    params = method.params,
                    jsonrpc = JSONRPC_VERSION
                ).encodeJson()
            }

            is WCMethod.Request -> {
                JsonRpcRequest(
                    id = method.requestId,
                    method = method.type.value,
                    params = method.params.map {
                        when (it) {
                            is WCMethod.Request.Params.Request -> JSON.encodeToJsonElement(it)
                            is WCMethod.Request.Params.Update -> JSON.encodeToJsonElement(it)
                        }
                    },
                    jsonrpc = JSONRPC_VERSION
                ).encodeJson()
            }

            is WCMethod.Response -> {
                when (method.result) {
                    is JsonElement -> {
                        JsonRpcResponse(
                            id = method.requestId,
                            result = method.result,
                            jsonrpc = JSONRPC_VERSION
                        ).encodeJson()
                    }
                    else -> {
                        JsonRpcResponse(
                            id = method.requestId,
                            result = method.result.toString(),
                            jsonrpc = JSONRPC_VERSION
                        ).encodeJson()
                    }
                }
            }

            is WCMethod.Error -> {
                JsonRpcErrorResponse(
                    id = method.id,
                    jsonrpc = JSONRPC_VERSION,
                    error = JsonRpcError(
                        code = method.code,
                        message = method.error
                    )
                ).encodeJson()
            }
        }
        WCLogger.log("send:$payload")
        val encryptedPayload = WCCipher.encrypt(payload = payload, key = key)
        val result = message.filter {
            it.requestId == method.requestId
        }
        socket.send(
            SocketMessage(
                topic = remotePeerId ?: config.topic,
                type = SocketMessage.Type.Pub,
                payload = encryptedPayload.encodeJson()
            ).encodeJson()
                .also {
                    WCLogger.log("send encrypted:$it")
                }
        )

        return if (ignoreResponse) method else result.first()
    }

    private fun handleMessage(socketMessage: SocketMessage): WCMethod? {
        if (socketMessage.type != SocketMessage.Type.Pub) return null
        return decrypt(socketMessage.payload)
    }


    private fun decrypt(payload: String): WCMethod? {
        var requestId: Long? = null
        return try {
            val decrypted = WCCipher.decrypt(payload = payload, key = key)
                .decodeJson<JsonElement>()
            WCLogger.log("onReceived decrypted:$decrypted")
            val error = decrypted.jsonObject["error"]
            if (error != null) {
                return JSON.decodeFromJsonElement<JsonRpcErrorResponse>(decrypted).let {
                    WCMethod.Error(
                        id = it.id,
                        error = it.error.message,
                        code = it.error.code
                    )
                }
            }
            val result = decrypted.jsonObject["result"]
            requestId = decrypted.jsonObject["id"]?.jsonPrimitive?.content?.toLong() ?: return null
            if (result != null) {
                val requestResponse = try {
                    JSON.decodeFromJsonElement(WCMethod.Response.RequestResponse.serializer(), result).also {
                        remotePeerId = it.peerId
                        remotePeerId?.let { topic ->
                            subMessage(topic)
                        }
                    }
                } catch (e: Exception) {
                    null
                }
                WCMethod.Response(
                    id = requestId,
                    result = requestResponse ?: result
                )
            } else {
                val request = JSON.decodeFromJsonElement<JsonRpcRequest<JsonArray>>(decrypted)
                when (request.method) {
                    WCMethodType.SESSION_REQUEST.value -> {
                        WCMethod.Request(
                            id = request.id,
                            type = WCMethodType.SESSION_REQUEST,
                            params = request.params.encodeJson().decodeJson<List<WCMethod.Request.Params.Request>>()
                                .also {
                                    remotePeerId = it.first().peerId
                                    remotePeerId?.let { topic ->
                                        subMessage(topic)
                                    }
                                }
                        )
                    }

                    WCMethodType.SESSION_UPDATE.value -> {
                        WCMethod.Request(
                            id = request.id,
                            type = WCMethodType.SESSION_UPDATE,
                            params = request.params.encodeJson().decodeJson<List<WCMethod.Request.Params.Update>>()
                        )
                    }

                    else -> {
                        WCMethod.CustomRequest(
                            id = request.id,
                            method = request.method.orEmpty(),
                            params = request.params
                        )
                    }
                }
            }

        } catch (e: Throwable) {
            WCMethod.Error(
                id = requestId ?: return null,
                error = e.toString(),
                code = -1,
            )
        }
    }
}
