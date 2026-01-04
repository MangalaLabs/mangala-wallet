package com.mangala.wallet.features.chains.antelope.presentation.proposal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.State
import com.mangala.wallet.features.chains.antelope.presentation.proposal.Constants.COLLAPSED_THRESHOLD
import com.mangala.wallet.features.chains.antelope.presentation.proposal.Constants.HEADER_ANIMATION_DURATION_MS
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.datetime.Instant


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposalDetailHeader(
    proposalName: String,
    proposalDetail: ProposalDetail,
    onBackClicked: () -> Unit,
    title: String,
    headerBackgroundColor: Color,
    scrollBehavior: TopAppBarScrollBehavior
) {
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = remember { derivedStateOf { collapsedFraction > COLLAPSED_THRESHOLD } }

    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(proposalDetail) {
        if (proposalDetail.expirationDate != Instant.DISTANT_PAST) {
            isLoading.value = false
        }
    }

    val animatedHeaderColor by animateColorAsState(
        targetValue = if (isLoading.value) ColorsNew.warning_100 else headerBackgroundColor,
        animationSpec = tween(durationMillis = HEADER_ANIMATION_DURATION_MS)
    )

    LargeTopAppBar(
        title = {
            if (!isCollapsed.value) {
                ProposalInfo(
                    proposalName = proposalName,
                    approvedCount = proposalDetail.approvedCount,
                    state = proposalDetail.state,
                    expirationDateStr = proposalDetail.expirationDateFormatted,
                    expirationDate = proposalDetail.expirationDate,
                    totalApprovals = proposalDetail.totalApprovals
                )
            }
        },
        navigationIcon = {
            MangalaWalletTopBarCenteredTitle(
                title = title,
                onBackClicked = onBackClicked,
                modifier = Modifier.background(animatedHeaderColor)
                    .statusBarsPadding()
                    .defaultMinSize(minHeight = Spacing.LARGE),
                backIconTint = ColorsNew.black,
                textColor = ColorsNew.black,
            )
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = animatedHeaderColor,
            scrolledContainerColor = animatedHeaderColor
        )
    )
}


@Composable
fun ProposalInfo(
    proposalName: String,
    expirationDateStr: String,
    expirationDate: Instant,
    approvedCount: Int,
    totalApprovals: Int,
    state: State
) {
    Column {
        Text(
            proposalName,
            color = ColorsNew.black,
            style = MangalaTypography.Size17SemiBold()
        )
        Spacer(modifier = Modifier.height(Spacing.XSMALL))

        Text(
            expirationDateStr,
            color = ColorsNew.black,
            style = MangalaTypography.Size14Regular(),
            modifier = Modifier.mangalaWalletPlaceholder(expirationDate == Instant.DISTANT_PAST)
        )
        Spacer(modifier = Modifier.height(Spacing.XSMALL))
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                Spacing.TINY,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StateItem(state, expirationDate)
            Text(
                text = MR.strings.message_proposal_details_approved.format(
                    approvedCount,
                    totalApprovals
                ).localized(),
                color = ColorsNew.black,
                style = MangalaTypography.Size14Regular(),
                modifier = Modifier.mangalaWalletPlaceholder(expirationDate == Instant.DISTANT_PAST)
            )
        }
        Spacer(modifier = Modifier.height(Spacing.SMALL))
    }
}


@Composable
fun StateItem(state: State, expirationDate: Instant) {

    val (stateText, stateColorText, stateColorBackground) = remember(state) {
        getStateDetails(
            state
        )
    }
    StateBadge(
        stateText.desc().localized(),
        stateColorText,
        stateColorBackground,
        expirationDate
    )
}

@Composable
private fun StateBadge(
    text: String,
    colorBackground: Color,
    textColor: Color,
    expirationDate: Instant
) {
    Box(
        modifier = Modifier
            .mangalaWalletPlaceholder(expirationDate == Instant.DISTANT_PAST)
            .background(
                color = colorBackground,
                shape = RoundedCornerShape(Spacing.XSMALL)
            )
            .padding(horizontal = Dimensions.Padding.small, vertical = Dimensions.Padding.quarter)

    ) {
        Text(
            text = text,
            style = MangalaTypography.Size14Regular(),
            color = textColor
        )
    }
}

private fun getStateDetails(state: State): Triple<StringResource, Color, Color> {
    return when (state) {
        State.Pending -> Triple(
            MR.strings.label_proposal_item_pending,
            ColorsNew.white,
            ColorsNew.warning_500
        )

        State.Executable -> Triple(
            MR.strings.label_proposal_item_executable,
            ColorsNew.white,
            ColorsNew.success_600
        )

        State.Expired -> Triple(
            MR.strings.label_proposal_item_expired,
            ColorsNew.white,
            ColorsNew.error_600
        )
    }
}