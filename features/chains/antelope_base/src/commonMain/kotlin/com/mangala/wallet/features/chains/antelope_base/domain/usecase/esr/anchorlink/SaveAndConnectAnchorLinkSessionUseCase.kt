package com.mangala.wallet.features.chains.antelope_base.domain.usecase.esr.anchorlink

import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.anchorlink.AnchorLinkSession
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.anchorlink.AnchorLinkRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class SaveAndConnectAnchorLinkSessionUseCase(
    private val anchorLinkRepository: AnchorLinkRepository,
    private val anchorLinkSessionManager: AnchorLinkSessionManager
) {

    suspend operator fun invoke(session: AnchorLinkSession, blockchainType: BlockchainType) {
        anchorLinkRepository.addSession(session, blockchainType.uid)
        anchorLinkSessionManager.addConnection(session)
    }
}