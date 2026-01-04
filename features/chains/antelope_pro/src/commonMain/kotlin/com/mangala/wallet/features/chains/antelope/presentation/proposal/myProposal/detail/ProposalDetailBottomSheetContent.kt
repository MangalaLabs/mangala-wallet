package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Dimensions.ButtonIconSize
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.Approval
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_APPROVED_STATUS
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaTextAvatar
import com.mangala.wallet.ui.theme.MangalaTypography
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

@Composable
fun ProposalDetailBottomSheet(
    approvals: List<Approval>
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.5f)
            .padding(
                horizontal = Dimensions.Padding.default,
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
        ) {
            itemsIndexed(approvals) { index, approval ->
                ApprovalRow(approval = approval)
            }
        }
    }
}


@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier.padding(Spacing.TINY),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = Spacing.XBASE, height = Spacing.XTINY)
                .background(MaterialTheme.mangalaColors.border, shape = RoundedCornerShape(CornerRadius.Micro))
        )
        Text(
            text = MR.strings.label_proposal_details_only_list_approval.desc().localized(),
            color = MaterialTheme.mangalaColors.textPrimary,
            style = MangalaTypography.Size14Medium(),
            modifier = Modifier.padding(bottom = Dimensions.Padding.default)
        )
    }
}


@Composable
fun ApprovalRow(approval: Approval) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.mangalaColors.bgInnerCard,
                shape = RoundedCornerShape(size = Spacing.XSMALL)
            )
            .padding(
                start = Dimensions.Padding.default,
                top = Dimensions.Padding.small,
                end = Dimensions.Padding.default,
                bottom = Dimensions.Padding.small
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.SMALL, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MangalaTextAvatar(
                name = approval.actor,
                modifier = Modifier.size(ButtonIconSize)
            )

            Spacer(modifier = Modifier.width(Spacing.TINY))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = approval.actor,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    style = MangalaTypography.Size13Medium()
                )
                Text(
                    text = MR.strings.label_proposal_details_account_weight.format(approval.weight).localized(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    style = MangalaTypography.Size13Regular()
                )
            }

            val backgroundColor =
                if (approval.status == PROPOSAL_APPROVED_STATUS) ColorsNew.success_100 else ColorsNew.warning_100
            val textColor =
                if (approval.status == PROPOSAL_APPROVED_STATUS) ColorsNew.success_600 else ColorsNew.warning_600
            Box(
                modifier = Modifier
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(CornerRadius.Small)
                    )
                    .padding(
                        horizontal = Dimensions.Padding.small,
                        vertical = Dimensions.Padding.quarter
                    )
            ) {
                Text(
                    text = approval.status,
                    style = MangalaTypography.Size14Regular(),
                    color = textColor
                )
            }
        }
    }
}
