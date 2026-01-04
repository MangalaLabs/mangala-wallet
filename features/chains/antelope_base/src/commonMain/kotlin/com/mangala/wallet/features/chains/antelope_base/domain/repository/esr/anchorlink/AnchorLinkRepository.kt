package com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.anchorlink

import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession

interface AnchorLinkRepository {
    suspend fun addSession(session: AnchorLinkSession, blockchainUid: String)
    suspend fun getSessions(accountName: String, blockchainUid: String): List<AnchorLinkSession>
    suspend fun getSession(sessionUrl: String): AnchorLinkSession?
    suspend fun getSessionsByBlockchainUid(sessionUrl: String): List<AnchorLinkSession>
    suspend fun connectSession(session: AnchorLinkSession, onRead: (ByteArray) -> Unit, onClose: () -> Unit): Result<Unit>
}