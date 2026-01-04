package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ActionAbi
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.formatDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

sealed class ProposalDetailScreenUiState {
    object Loading : ProposalDetailScreenUiState()
    data class Success(val data: String) : ProposalDetailScreenUiState()
    data class LoadDataSuccess(val data: ProposalDetail) : ProposalDetailScreenUiState()
    data class Error(val message: String) : ProposalDetailScreenUiState()
}

data class Approval(
    val actor: String,
    val permission: String,
    val status: String,
    val weight: Long = 0
)

data class ProposalDetail(
    val approvalStatus: String = "",
    val expirationDate: Instant = Instant.DISTANT_PAST,
    val actionProposalDetails: List<ActionProposalDetail> = emptyList(),
    val approvals: List<Approval> = emptyList(),
    val accountsImported: List<String> = emptyList(),
    val permissionsImported: List<String> = emptyList(),
    val accountExecuted: String = "",
    val permissionExecuted: String = "",
    val actionAbi: List<ActionAbi> = emptyList(),
    val showDialog: Boolean = false,
    val isApproved: Boolean = false,
    val isRequestedApproval: Boolean = false,
    val approvedCount: Int = 0,
    val requestedApprovalCount: Int = 0,
    val state: State = State.Pending,
) {
    val isExecutable: Boolean
        get() = state == State.Executable
    val totalApprovals: Int
        get() = approvals.size

    val expirationDateFormatted = expirationDate
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .formatDateTime(TimeZone.currentSystemDefault(), FormatStyle.FULL)
}

data class ActionProposalDetail(
    val actor: String = "",
    val permission: String = "",
    val action: String = "",
    val data: String = "",
    val dataDecoded: List<AntelopeActionAbi> = emptyList(),
    val authorizations: List<MultisigActionAuthorization> = emptyList(),
)

data class AuthorizationDetail(
    val actor: String,
    val permission: String,
    val action: String,
    val data: String,
)

enum class State {
    Pending, Executable, Expired
}