package com.mangala.wallet.features.chains.antelope_base.data.repository.esr.anchorlink

import com.mangala.wallet.features.chains.antelope_base.data.local.esr.anchorlink.AnchorLinkSessionLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.anchorlink.AnchorLinkRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.esr.anchorlink.mapper.toAnchorLinkSession
import com.mangala.wallet.features.chains.antelope_base.data.repository.esr.anchorlink.mapper.toAntelopeAnchorLinkSessionEntity
import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.anchorlink.AnchorLinkRepository

class AnchorLinkRepositoryImpl(
    private val anchorLinkRemoteDataSource: AnchorLinkRemoteDataSource,
    private val anchorLinkLocalDataSource: AnchorLinkSessionLocalDataSource
) : AnchorLinkRepository {

    override suspend fun addSession(session: AnchorLinkSession, blockchainUid: String) {
        anchorLinkLocalDataSource.insertAnchorLinkSession(
            session.toAntelopeAnchorLinkSessionEntity(
                blockchainUid
            )
        )
    }

    override suspend fun getSessions(accountName: String, blockchainUid: String): List<AnchorLinkSession> {
        return anchorLinkLocalDataSource.getAnchorLinkSessionsByAccountName(
            accountName,
            blockchainUid
        )?.map {
            it.toAnchorLinkSession()
        } ?: emptyList()
    }

    override suspend fun getSession(sessionUrl: String): AnchorLinkSession? {
        return anchorLinkLocalDataSource.getAnchorLinkSessionByUrl(sessionUrl)
            ?.toAnchorLinkSession()
    }

    override suspend fun getSessionsByBlockchainUid(sessionUrl: String): List<AnchorLinkSession> {
        return anchorLinkLocalDataSource.getAnchorLinkSessionsByBlockchainUid(sessionUrl)
            .map { it.toAnchorLinkSession() }
    }

    override suspend fun connectSession(session: AnchorLinkSession, onRead: (ByteArray) -> Unit, onClose: () -> Unit): Result<Unit> {
        return anchorLinkRemoteDataSource.connect(
            url = session.url.replace("https", "wss"),
            onRead = onRead,
            onClose = onClose
        )
    }
}