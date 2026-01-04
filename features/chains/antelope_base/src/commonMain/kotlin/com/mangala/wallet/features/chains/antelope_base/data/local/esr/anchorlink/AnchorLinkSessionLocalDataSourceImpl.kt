package com.mangala.wallet.features.chains.antelope_base.data.local.esr.anchorlink

import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeAnchorLinkSessionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AnchorLinkSessionLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AnchorLinkSessionLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun insertAnchorLinkSession(anchorLinkSession: AntelopeAnchorLinkSessionEntity) = withContext(ioDispatcher) {
        dbQuery.insertAnchorLinkSession(
            account_name = anchorLinkSession.account_name,
            session_url = anchorLinkSession.session_url,
            session_name = anchorLinkSession.session_name,
            session_receive_public_key = anchorLinkSession.session_receive_public_key,
            session_request_public_key = anchorLinkSession.session_request_public_key,
            blockchain_uid = anchorLinkSession.blockchain_uid
        )
    }

    override suspend fun getAnchorLinkSessionsByBlockchainUid(blockchainUid: String): List<AntelopeAnchorLinkSessionEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAnchorLinkSessionsByBlockchainUid(blockchainUid).executeAsList()
    }

    override suspend fun getAnchorLinkSessionByUrl(sessionUrl: String): AntelopeAnchorLinkSessionEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAnchorLinkSessionByUrl(sessionUrl).executeAsOneOrNull()
    }

    override suspend fun getAnchorLinkSessionsByAccountName(
        accountName: String,
        blockchainUid: String
    ): List<AntelopeAnchorLinkSessionEntity>? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAnchorLinkSession(
            account_name = accountName,
            blockchain_uid = blockchainUid
        ).executeAsList()
    }

    override suspend fun deleteAnchorLinkSession(accountName: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.deleteAnchorLinkSession(accountName, blockchainUid)
    }
}