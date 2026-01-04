package com.mangala.wallet.features.chains.antelope.presentation.proposal

import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.formatDate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ProposalUiModel(
    val proposalName: String,
    val state: State,
    val action: String,
    val submitter: String,
    val expiredDate: Instant,
) {
    val formattedProposalExpiredDate = lazy {
        expiredDate
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .formatDate(
                timeZone = TimeZone.currentSystemDefault(),
                style = FormatStyle.MEDIUM
            )
    }

    val dateState = lazy {
        if ((expiredDate) < Clock.System.now())
            DateState.Expired
        else
            DateState.Open
    }

    enum class State {
        Pending, Executable, Draft
    }

    enum class DateState {
        Expired, Open
    }
}
