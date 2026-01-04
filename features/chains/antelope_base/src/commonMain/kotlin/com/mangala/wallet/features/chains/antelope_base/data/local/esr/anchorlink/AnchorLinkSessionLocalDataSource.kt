package com.mangala.wallet.features.chains.antelope_base.data.local.esr.anchorlink

import com.mangala.wallet.features.chains.antelopebase.AntelopeAnchorLinkSessionEntity

interface AnchorLinkSessionLocalDataSource {
    suspend fun insertAnchorLinkSession(anchorLinkSession: AntelopeAnchorLinkSessionEntity)
    suspend fun getAnchorLinkSessionByUrl(sessionUrl: String): AntelopeAnchorLinkSessionEntity?
    suspend fun getAnchorLinkSessionsByBlockchainUid(blockchainUid: String): List<AntelopeAnchorLinkSessionEntity>
    suspend fun getAnchorLinkSessionsByAccountName(
        accountName: String,
        blockchainUid: String
    ): List<AntelopeAnchorLinkSessionEntity>?

    suspend fun deleteAnchorLinkSession(accountName: String, blockchainUid: String)
}