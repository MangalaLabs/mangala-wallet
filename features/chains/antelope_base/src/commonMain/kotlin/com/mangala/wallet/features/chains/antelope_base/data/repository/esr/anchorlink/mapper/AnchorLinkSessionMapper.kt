package com.mangala.wallet.features.chains.antelope_base.data.repository.esr.anchorlink.mapper

import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.features.chains.antelopebase.AntelopeAnchorLinkSessionEntity

fun AntelopeAnchorLinkSessionEntity.toAnchorLinkSession(): AnchorLinkSession {
    return AnchorLinkSession(
        accountName = account_name,
        receiveKey = session_receive_public_key,
        requestKey = session_request_public_key,
        url = session_url,
        name = session_name
    )
}

fun AnchorLinkSession.toAntelopeAnchorLinkSessionEntity(blockchainUid: String): AntelopeAnchorLinkSessionEntity {
    return AntelopeAnchorLinkSessionEntity(
        account_name = accountName,
        session_receive_public_key = receiveKey,
        session_request_public_key = requestKey,
        session_url = url,
        session_name = name,
        blockchain_uid = blockchainUid
    )
}