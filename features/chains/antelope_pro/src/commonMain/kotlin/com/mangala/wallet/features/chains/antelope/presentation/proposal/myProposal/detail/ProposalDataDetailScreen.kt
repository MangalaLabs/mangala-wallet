package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDownExpand
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ActionProposalDetail
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ProposalDataDetailScreen(details: List<ActionProposalDetail>) {
    var dropdownOpen by remember { mutableStateOf(false) }
    val dropdownIconRotationState by animateFloatAsState(
        targetValue = if (dropdownOpen) 180f else 0f
    )
    LaunchedEffect(details) {
        if (details.isNotEmpty()) {
            dropdownOpen = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = Dimensions.Padding.default,
                top = Dimensions.Padding.small,
                end = Dimensions.Padding.default,
                bottom = Dimensions.Padding.small
            )
            .background(
                color = MaterialTheme.mangalaColors.bgInnerCard,
                shape = RoundedCornerShape(size = CornerRadius.Small)
            ).animateContentSize()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.default)
        ) {
            Text(
                text = MR.strings.label_proposals_detail_detail_proposal.desc().localized(),
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MR.strings.label_proposals_detail_action_detail.desc().localized(),
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    modifier = Modifier.size(Spacing.BASE),
                    onClick = { dropdownOpen = !dropdownOpen }
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.ArrowDownExpand,
                        contentDescription = null,
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(Spacing.MEDIUM)
                            .rotate(dropdownIconRotationState)
                    )
                }
            }
            if (dropdownOpen) {
                details.forEachIndexed { index, detail ->
                    ActionDetailScreen(detail, index == details.lastIndex)
                }
            }

        }
    }
}