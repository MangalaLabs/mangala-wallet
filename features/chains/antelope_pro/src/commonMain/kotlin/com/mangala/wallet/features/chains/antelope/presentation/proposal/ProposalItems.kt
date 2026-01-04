package com.mangala.wallet.features.chains.antelope.presentation.proposal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Dimensions.ButtonIconSize
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.FontSizeNew
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextIconAvatar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProposalSelectAccountDropdown(
    selectedAccountName: String,
    availableAccountNames: List<String>,
    onSelectAccount: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val borderColor = MaterialTheme.mangalaColors.border

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = it }
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(CornerRadius.Medium)
                )
                .padding(
                    horizontal = Dimensions.Padding.half,
                    vertical = Dimensions.Padding.quarter
                )
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProposerAvatar(
                submitter = selectedAccountName
            )

            Spacer(modifier = Modifier.width(Spacing.XTINY))

            Text(
                text = selectedAccountName,
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textPrimary,
            )

            Spacer(modifier = Modifier.width(Spacing.TINY))

            Icon(
                imageVector = MangalaWalletPack.Dropdown,
                contentDescription = null
            )
        }

        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            },
            containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            shape = RoundedCornerShape(CornerRadius.Medium),
            matchTextFieldWidth = false,
        ) {
            availableAccountNames.forEachIndexed { index, option ->
                val onClickModifier = remember(option) {
                    Modifier.clickable {
                        expanded.value = false
                        onSelectAccount(option)
                    }
                }

                Row(
                    modifier = onClickModifier
                        .fillMaxWidth()
                        .drawWithCache {
                            onDrawWithContent {
                                if (index > 0) drawLine(
                                    color = borderColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 0.5f
                                )
                                drawContent()
                            }
                        }
                        .padding(
                            horizontal = Dimensions.Padding.small,
                            vertical = Dimensions.Padding.half
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProposerAvatar(
                        submitter = option
                    )

                    Spacer(modifier = Modifier.width(Spacing.XTINY))

                    Text(
                        text = option,
                        style = MangalaTypography.Size14Regular(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProposalFilterStateChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    TextDescription2(
        text = title,
        fontSize = FontSizeNew.SMALL_BODY,
        color = if (isSelected) MaterialTheme.mangalaColors.textOnBadge else MaterialTheme.mangalaColors.textSecondary,
        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(CornerRadius.Medium))
            .clickable(onClick = onClick)
            .background(color = if (isSelected) MaterialTheme.mangalaColors.bgBadge else MaterialTheme.mangalaColors.bgAlpha)
            .padding(
                vertical = Dimensions.Padding.quarter,
                horizontal = Dimensions.Padding.small
            )
    )
}

@Composable
internal fun ProposalCard(
    proposal: ProposalUiModel,
    shouldShowSubmitterInfo: Boolean = true,
    onClick: () -> Unit
) {
    MaxWidthColumn(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(CornerRadius.Small))
            .clickable(onClick = onClick)
            .background(color = MaterialTheme.mangalaColors.bgInnerCard)
            .padding(
                horizontal = Dimensions.Padding.default,
                vertical = Dimensions.Padding.small
            ),
    ) {
        CustomText(
            text = proposal.proposalName.ifBlank { MR.strings.label_my_proposal_card_unnamed.desc().localized() },
            fontSize = FontType.REGULAR,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.XSMALL))

        ProposalDetails(
            proposal = proposal,
            shouldShowSubmitterInfo = shouldShowSubmitterInfo
        )

        Spacer(modifier = Modifier.height(Spacing.XMEDIUM))

        ItemState(proposal = proposal)
    }
}

@Composable
private fun ProposalDetails(
    proposal: ProposalUiModel,
    shouldShowSubmitterInfo: Boolean,
) {
    MaxWidthRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        CustomText(text = proposal.action)
        if (shouldShowSubmitterInfo) {
            Spacer(modifier = Modifier.width(Spacing.TINY))

            ProposerAvatar(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                submitter = proposal.submitter
            )

            Spacer(modifier = Modifier.width(Spacing.XTINY))

            CustomText(text = proposal.submitter, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ProposerAvatar(
    modifier: Modifier = Modifier,
    submitter: String,
) {
    val submitterAvatarLetter =
        remember(submitter) { submitter.trim().firstOrNull()?.uppercaseChar() }

    TextIconAvatar(
        modifier = modifier
            .size(ButtonIconSize)
            .background(
                color = getAvatarColor(submitterAvatarLetter),
                shape = CircleShape
            ),
        text = submitterAvatarLetter.toString(),
        color = ColorsNew.white,
        style = MangalaTypography.Size14SemiBold().copy(textAlign = TextAlign.Center)
    )
}

@Composable
private fun ItemState(proposal: ProposalUiModel) {
    val (stateText, stateColorText, stateColorBackground) = remember(proposal.state) {
        getStateDetails(
            proposal.state
        )
    }

    MaxWidthRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        StateBadge(
            text = stateText.desc().localized(),
            colorBackground = stateColorText,
            colorText = stateColorBackground
        )
//        TODO: temporary disable this due to we can't get information about proposal expired date
//        CustomText(
//            text = proposal.formattedProposalDate,
//            color = if (proposal.dateState == ProposalUiModel.DateState.Expired) ColorsNew.error_600 else ColorsNew.primary_950
//        )
    }
}

@Composable
private fun StateBadge(text: String, colorBackground: Color, colorText: Color) {
    CustomText(
        modifier = Modifier
            .background(
                color = colorBackground,
                shape = RoundedCornerShape(CornerRadius.Medium)
            )
            .padding(
                horizontal = Dimensions.Padding.half,
                vertical = Dimensions.Padding.quarter
            ),
        text = text,
        color = colorText
    )
}

@Composable
private fun CustomText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = FontType.SMALL,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = MaterialTheme.mangalaColors.textSecondary
) {
    TextDescription2(
        modifier = modifier,
        text = text,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color
    )
}

fun getAvatarColor(firstChar: Char?): Color {
    return when (firstChar?.lowercaseChar()) {
        'a' -> ColorsNew.avatarA
        'b' -> ColorsNew.avatarB
        'c' -> ColorsNew.avatarC
        'd' -> ColorsNew.avatarD
        'e' -> ColorsNew.avatarE
        'f' -> ColorsNew.avatarF
        'g' -> ColorsNew.avatarG
        'h' -> ColorsNew.avatarH
        'i' -> ColorsNew.avatarI
        'j' -> ColorsNew.avatarJ
        'k' -> ColorsNew.avatarK
        'l' -> ColorsNew.avatarL
        'm' -> ColorsNew.avatarM
        'n' -> ColorsNew.avatarN
        'o' -> ColorsNew.avatarO
        'p' -> ColorsNew.avatarP
        'q' -> ColorsNew.avatarQ
        'r' -> ColorsNew.avatarR
        's' -> ColorsNew.avatarS
        't' -> ColorsNew.avatarT
        'u' -> ColorsNew.avatarU
        'v' -> ColorsNew.avatarV
        'w' -> ColorsNew.avatarW
        'x' -> ColorsNew.avatarX
        'y' -> ColorsNew.avatarY
        'z' -> ColorsNew.avatarZ
        else -> ColorsNew.avatarA // Default to Colors.avatarA as fallback
    }
}

private fun getStateDetails(state: ProposalUiModel.State): Triple<StringResource, Color, Color> {
    return when (state) {
        ProposalUiModel.State.Pending -> Triple(
            MR.strings.label_proposal_item_pending,
            ColorsNew.warning_100,
            ColorsNew.warning_500
        )

        ProposalUiModel.State.Executable -> Triple(
            MR.strings.label_proposal_item_executable,
            ColorsNew.success_100,
            ColorsNew.success_600
        )

        ProposalUiModel.State.Draft -> Triple(
            MR.strings.label_proposal_item_draft,
            ColorsNew.error_100,
            ColorsNew.error_500
        )
    }
}