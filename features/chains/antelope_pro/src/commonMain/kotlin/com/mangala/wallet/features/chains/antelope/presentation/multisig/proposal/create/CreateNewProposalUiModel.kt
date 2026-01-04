package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create

import androidx.compose.runtime.Stable
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.formatDate
import com.mangala.wallet.utils.formatDateTime
import com.mangala.wallet.utils.formatTime
import com.mangala.wallet.utils.localDateNow
import com.mangala.wallet.utils.localTimeNow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Stable
data class CreateNewProposalUiModel(
    val proposalName: String,
    val proposalNameErrorMessage: WrappedStringResource? = null,
    val isCheckingProposalNameExistence: Boolean = false,
    val expirationTimestamp: Long,
    val minSelectableDate: Long,
    val proposerName: String,
    val proposerPermissionName: String,
    val actions: List<MultisigAction>,
    val approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>,
    val promptConfirmTransaction: Boolean = false,
    val error: WrappedStringResource? = null,
    val resourceRequiredBreakdown: FeeBreakdown? = null,
    val resourceRequiredTotal: String? = null,
    val isLoading: Boolean = false,
    val transactionReadyToSubmit: Boolean = false,
    val txHash: String? = null,
    val blockExplorerUrl: String? = null,
    val isLoadedFromDraft: Boolean,
    val isDraftExists: Boolean
) {
    val expirationInLocalDateTime = Instant
        .fromEpochMilliseconds(expirationTimestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val expirationDateTimeFormatted = expirationInLocalDateTime
        .formatDateTime(TimeZone.currentSystemDefault(), dateStyle = FormatStyle.LONG, timeStyle = FormatStyle.FULL)
    val formattedSelectedAccountAndPermission =
        if (proposerName.isNotBlank() && proposerPermissionName.isNotBlank()) "$proposerName@$proposerPermissionName" else null
    val buttonText = when {
        proposalName.isBlank() -> "Please enter proposal name first"
        proposerName.isBlank() || proposerPermissionName.isBlank() -> "Please select proposer account and permission"
        actions.isEmpty() -> "Please add at least one action"
        approvers.isEmpty() -> "Please add at least one approver"
        else -> "Submit"
    }
    val minSelectableLocalDateTime = Instant
        .fromEpochMilliseconds(minSelectableDate)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val minSelectableLocalDate = minSelectableLocalDateTime.date
    val minSelectableLocalTime = if (minSelectableLocalDate == localDateNow()) {
        localTimeNow()
    } else {
        LocalTime(0, 0, 0, 0)
    }
    val isButtonEnabled =
        !isLoading
            && proposalName.isNotBlank()
            && proposalNameErrorMessage == null
            && proposerName.isNotBlank()
            && proposerPermissionName.isNotBlank()
            && actions.isNotEmpty()
            && approvers.isNotEmpty()

    val saveDraftTitle = if (isDraftExists) {
        MR.strings.title_multisig_proposal_overwrite_draft_confirmation
    } else if (isLoadedFromDraft) {
        MR.strings.title_multisig_proposal_update_draft_confirmation
    } else {
        MR.strings.title_multisig_proposal_save_draft_confirmation
    }
    val saveDraftText = if (isDraftExists) {
        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_overwrite_draft_confirmation_description, proposalName)
    } else if (isLoadedFromDraft) {
        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_update_draft_confirmation_description, proposalName)
    } else {
        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_save_draft_confirmation_description)
    }
    val saveDraftPositiveButtonText = if (isDraftExists) {
        MR.strings.button_multisig_proposal_overwrite_draft
    } else if (isLoadedFromDraft) {
        MR.strings.all_save
    } else {
        MR.strings.all_save
    }
    val saveDraftNegativeButtonText = if (isDraftExists) {
        MR.strings.all_discard
    } else if (isLoadedFromDraft) {
        MR.strings.button_multisig_proposal_save_draft_negative
    } else {
        MR.strings.button_multisig_proposal_save_draft_negative
    }
}
