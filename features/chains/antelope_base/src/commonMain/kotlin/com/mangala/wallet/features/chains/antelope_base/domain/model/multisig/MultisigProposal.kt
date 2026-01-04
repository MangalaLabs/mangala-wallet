package com.mangala.wallet.features.chains.antelope_base.domain.model.multisig

class MultisigProposal(
    val id: Long,
    val proposalName: String,
    val expirationTimestamp: Long,
    val proposerName: String,
    val proposerPermissionName: String,
    val actions: List<MultisigAction>,
    val approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>
)