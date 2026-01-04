package com.mangala.wallet.features.chains.antelope_base.domain.model.proposal

import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Instant

data class ProposalData(
    val proposalName: String,
    val proposer: String,
    val requestedApprovals: List<String>?,
    val blockchainType: BlockchainType,
    val createdAt: Instant,
    val expiresAt: Instant,
    val actionsName: List<String>,
    val isDraft: Boolean
) {
    enum class Type {
        PROPOSAL,
        APPROVAL
    }
}
