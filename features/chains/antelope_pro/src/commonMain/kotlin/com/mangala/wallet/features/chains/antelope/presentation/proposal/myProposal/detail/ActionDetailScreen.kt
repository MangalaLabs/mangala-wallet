package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDownExpand
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ActionProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTextInputField
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.MangalaTypography
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ActionDetailScreen(actionDetail: ActionProposalDetail, isLastItem: Boolean) {
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimensions.Padding.half),
            verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            ProposalTextInputField(
                label = MR.strings.label_multisig_action_action_name.desc()
                    .localized(),
                query = actionDetail.action,
                onQueryChange = {},
                placeholder = "",
                enabled = false
            )
            Spacer(modifier = Modifier.height(Spacing.SMALL))

            actionDetail.dataDecoded.forEach { actionAbi ->
                ActionDataField(actionAbi)
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))
            AuthorizationInfo(
                authorizations = actionDetail.authorizations
            )
        }
        if (!isLastItem) {
            Spacer(modifier = Modifier.height(Spacing.BASE))
            HorizontalLine()
        }
    }
}


@Composable
fun AuthorizationInfo(
    authorizations: List<MultisigActionAuthorization>
) {
    Column {
        MaxWidthRow {
            Text(
                modifier = Modifier.weight(1f),
                text = MR.strings.label_proposals_detail_authorization.desc().localized(),
                style = MangalaTypography.Size13Medium(),
                color = MaterialTheme.mangalaColors.textSecondary
            )
        }

        authorizations.forEach { authorization ->
            ProposalTextInputField(
                query = authorization.formatted,
                onQueryChange = {},
                placeholder = "",
                enabled = false
            )
        }
    }
}

@Composable
private fun ActionDataField(
    actionAbi: AntelopeActionAbi
) {
    Column(
        modifier = Modifier.padding(start = Spacing.XSMALL.times(actionAbi.level))
    ) {
        if (actionAbi.isPrimitive) {
            ProposalTextInputField(
                label = "${actionAbi.fieldName} (${actionAbi.fieldType})",
                query = actionAbi.mapValue[actionAbi.fieldName]?.joinToString(", ") {
                    it.value.toString() // TODO: Possible bug here as we haven't converted this into the proper data type to toString (e.g EosPublicKey might turn out to be a different value)
                } ?: "",
                onQueryChange = {},
                placeholder = MR.strings.hint_create_new_proposal_approver.desc().localized(),
                modifier = Modifier.fillMaxWidth(),
                requiredInput = false,
                enabled = false
            )
        } else if (actionAbi.isArrayPrimitive) {
            ActionDataFieldArrayPrimitive(actionAbi)
        } else if (actionAbi.isObject) {
            ActionDataFieldObject(actionAbi)
        } else if (actionAbi.isArrayObject) {
            ActionDataFieldArrayObject(actionAbi)
        }
    }
}

@Composable
fun ActionDataFieldArrayPrimitive(actionAbi: AntelopeActionAbi) {
    var dropdownOpen by remember { mutableStateOf(true) }
    val dropdownIconRotationState by animateFloatAsState(
        targetValue = if (dropdownOpen) 180f else 0f
    )
    Column {
        val arraySize = actionAbi.mapValue[actionAbi.fieldName]?.size ?: 0
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${actionAbi.fieldName} (${actionAbi.fieldType})",
                color = MaterialTheme.mangalaColors.textSecondary,
                style = MangalaTypography.Size13Medium()
            )
            if (arraySize > 1) {
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
            } else {
                dropdownOpen = true
            }
        }
        if (dropdownOpen) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.Top)
            ) {
                actionAbi.mapValue[actionAbi.fieldName]?.forEach {
                    ProposalTextInputField(
                        query = it.value.toString(),
                        onQueryChange = {},
                        placeholder = "",
                        enabled = false
                    )
                }
            }
        }
    }
}


@Composable
fun ActionDataFieldObject(
    actionAbi: AntelopeActionAbi
) {

    var dropdownOpen by remember { mutableStateOf(true) }
    val dropdownIconRotationState by animateFloatAsState(
        targetValue = if (dropdownOpen) 180f else 0f
    )
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${actionAbi.fieldName} (${actionAbi.fieldType})",
            color = MaterialTheme.mangalaColors.textSecondary,
            style = MangalaTypography.Size13Medium()
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
        actionAbi.subFields.forEach {
            ActionDataField(it)
        }
    }
}


@Composable
fun ActionDataFieldArrayObject(
    actionAbi: AntelopeActionAbi
) {
    var dropdownOpen by remember { mutableStateOf(true) }
    val dropdownIconRotationState by animateFloatAsState(
        targetValue = if (dropdownOpen) 180f else 0f
    )
    if (actionAbi.arraySize == 0) {
        MaxWidthRow {
            Text(
                text = "${actionAbi.fieldName} (${actionAbi.fieldType})",
                color = MaterialTheme.mangalaColors.textSecondary,
                style = MangalaTypography.Size13Medium()
            )

            Spacer(modifier = Modifier.size(Spacing.TINY))
        }
    } else {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${actionAbi.fieldName} (${actionAbi.fieldType})",
                color = MaterialTheme.mangalaColors.textSecondary,
                style = MangalaTypography.Size13Medium()
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
            val arraySize = actionAbi.arraySize
            val elementSize = arraySize / actionAbi.subFields.size
            val valueList = actionAbi.mapValue[actionAbi.fieldName] ?: emptyList()
            Column(modifier = Modifier.padding(start = Spacing.XSMALL.times(actionAbi.level))) {
                repeat(elementSize) { i ->
                    actionAbi.subFields.forEachIndexed { index, subField ->
                        ProposalTextInputField(
                            label = "${subField.fieldName} (${subField.fieldType})",
                            query = valueList.getOrNull(i * actionAbi.subFields.size + index)?.value.toString(),
                            onQueryChange = {},
                            placeholder = MR.strings.hint_create_new_proposal_approver.desc()
                                .localized(),
                            modifier = Modifier.fillMaxWidth(),
                            requiredInput = false,
                            enabled = false
                        )
                        Spacer(modifier = Modifier.size(Spacing.TINY))
                    }
                }
            }
        }
    }

}

@Composable
fun HorizontalLine(
    color: Color = MaterialTheme.mangalaColors.border,
    thickness: Dp = 1.dp,
) {
    Divider(
        color = color,
        thickness = thickness,
    )
}