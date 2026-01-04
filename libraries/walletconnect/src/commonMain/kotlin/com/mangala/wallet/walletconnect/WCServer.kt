/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect

import com.mangala.wallet.walletconnect.core.WCCollectionsManager
import com.mangala.wallet.walletconnect.core.WCSession
import com.mangala.wallet.walletconnect.entity.*
import com.mangala.wallet.walletconnect.utils.JSON
import com.mangala.wallet.walletconnect.utils.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.coroutines.CoroutineContext

class WCServer(
    store: WCConnectionPersistStore,
    private val coroutineContext: CoroutineContext = Dispatchers.Default
) {
    private val manager by lazy {
        WCCollectionsManager(
            store = store,
            coroutineContext = coroutineContext
        )
    }

    val connections = manager.connections

    fun getConnection(connectionId: String) = manager.getConnection(connectionId)

    val request by lazy {
        manager.message.filter {
            it.method is WCMethod.CustomRequest
        }
    }

    class PairRequest(
        val requestId: Long,
        val peerId: String,
        val peerMeta: WCPeerMeta?,
        val chainId: Long,
        private val approveCallback: suspend (accounts: List<String>, chainId: Long) -> Result<WCConnection>,
        private val reject: suspend () -> Unit
    ) {
        suspend fun approve(
            account: List<String>,
            chainId: Long
        ) = approveCallback(account, chainId)

        suspend fun reject() = reject.invoke()
    }

    suspend fun pair(uri: String, clientMeta: WCPeerMeta): Result<PairRequest> = withContext(coroutineContext) {
        runCatching {
            val config = WCSessionConfig.fromUri(uri) ?: throw IllegalArgumentException("Invalid uri:$uri")
            val clientId = UUID.randomUUID()
            val session = WCSession(
                config = config,
                clientId = clientId
            )
            session.connectSocket(
                handShakeTopic = config.topic,
            )

            val request = session.message.first {
                it is WCMethod.Request && it.type == WCMethodType.SESSION_REQUEST
            } as WCMethod.Request
            val peerMeta = (request.params.first() as WCMethod.Request.Params.Request).peerMeta
            val peerId = (request.params.first() as WCMethod.Request.Params.Request).peerId
            PairRequest(
                requestId = request.id,
                peerId = peerId,
                peerMeta = peerMeta,
                chainId = (request.params.first() as WCMethod.Request.Params.Request).chainId ?: -1,
                approveCallback = { accounts, chainId ->
                    runCatching {
                        session.send(
                            method = WCMethod.Response(
                                id = request.id,
                                result = JSON.encodeToJsonElement(WCMethod.Response.RequestResponse(
                                    peerId = clientId,
                                    peerMeta = clientMeta,
                                    approved = true,
                                    chainId = chainId,
                                    accounts = accounts
                                ))
                            ),
                            ignoreResponse = true
                        )
                        WCConnection(
                            id = UUID.randomUUID(),
                            config = config,
                            clientId = clientId,
                            peerMeta = peerMeta,
                            accounts = accounts,
                            chainId = chainId,
                            peerId = peerId,
                            clientMeta = clientMeta
                        ).also {
                            manager.storeCollection(
                                wcCollection = it,
                                session = session
                            )
                        }
                    }
                },
                reject = {
                    session.send(
                        method = WCMethod.Error(
                            id = request.id,
                            code = -32000,
                            error = "Session Rejected"
                        ),
                        ignoreResponse = true
                    )
                }
            )
        }
    }

    suspend fun disconnect(connectionId: String) = manager.disconnect(connectionId)

    suspend fun response(
        connectionId: String,
        requestId: Long,
        response: JsonElement
    ) {
        manager.getSession(connectionId)?.send(
            method = WCMethod.Response(
                id = requestId,
                result = response
            )
        )
    }

    suspend fun errorResponse(
        connectionId: String,
        requestId: Long,
        errorCode: Long,
        errorMessage: String,
    ) {
        manager.getSession(connectionId)?.send(
            method = WCMethod.Error(
                id = requestId,
                code = errorCode,
                error = errorMessage
            )
        )
    }
}
