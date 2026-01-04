package com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink

import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.anchorlink.AnchorLinkRepository
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AnchorLinkSessionManager(
    private val anchorLinkRepository: AnchorLinkRepository,
    private val decodeAnchorLinkSealedMessageUseCase: DecodeAnchorLinkSealedMessageUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) {

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _signRequest = MutableSharedFlow<AnchorLinkSignRequest?>(0)
    val signRequest = _signRequest.asSharedFlow()

    private var sessions = mutableMapOf<String, AnchorLinkSession>()
    private val antelopeBlockchainNetwork = BlockchainNetworkData.getAllBlockchainNetworkSupported(buildEnvironmentProvider.isDevelopmentEnvironment())
        .filter { it.blockchainType.networkType == NetworkType.ANTELOPE }

    fun initialize() {
        coroutineScope.launch {
            antelopeBlockchainNetwork.forEach { blockchainNetwork ->
                val sessions =
                    anchorLinkRepository.getSessionsByBlockchainUid(blockchainNetwork.blockchainType.uid)
                println("Anchor Link sessions $sessions")

                sessions.forEach { session ->
                    println("Anchor Link Session Manager: add connection ${session.accountName} url ${session.url}")
                    addConnection(session)
                }
            }
        }
    }

    fun addConnection(session: AnchorLinkSession) {
        if (session.url in sessions) {
            return
        }

        sessions[session.url] = session
        coroutineScope.launch {
            val connectionResult = anchorLinkRepository.connectSession(
                session = session,
                onRead = { onRead(session, it) },
                onClose = {
                    sessions.remove(session.url)
                }
            )

            connectionResult.onFailure { error ->
                println("Anchor Link Session Manager: Failed to connect to ${session.url}: ${error.message}")
                error.printStackTrace()
                sessions.remove(session.url)
            }
        }
    }

    private fun onRead(session: AnchorLinkSession, data: ByteArray) {
        val esrUri = decodeAnchorLinkSealedMessageUseCase(session, data)
        println("Anchor Link Session Manager: Received data: $esrUri")

        coroutineScope.launch {
            _signRequest.emit(AnchorLinkSignRequest("", esrUri))
        }
    }
}