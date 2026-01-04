package com.mangala.wallet.features.chains.antelope_base.data.repository.proposal.mapper

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigProposal
import com.mangala.wallet.features.chains.antelopebase.SelectMsigProposalDraftByAccountName
import kotlinx.serialization.json.Json

fun SelectMsigProposalDraftByAccountName.toMultisigProposal(json: Json) = MultisigProposal(
    id = id,
    proposalName = proposal_name,
    expirationTimestamp = expires_at,
    proposerName = account_name,
    proposerPermissionName = draft_proposer_permission_name,
    actions = json.decodeFromString(draft_actions_detail_json),
    approvers = json.decodeFromString(draft_approvers_detail_json)
)