package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalDropdownMenu
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    title: String,
    dialogMessage: String,
    permissions: List<String>,
    permissionSelected: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onPermissionExecute: (String) -> Unit,
) {
    if (!showDialog) return
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.xDefault),
            shape = RoundedCornerShape(CornerRadius.Medium),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            )
        ) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.xDefault),
                verticalArrangement = Arrangement.spacedBy(Spacing.XXTINY, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )

                SelectAccountPermission(
                    proposers = permissions,
                    permissionExecute = permissionSelected,
                    { onPermissionExecute(it) }
                )

                Text(
                    text = dialogMessage,
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        Spacing.XTINY,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MangalaOutlinedButtonNew(
                        label = MR.strings.all_cancel.desc().localized(),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                    )

                    MangalaGradientButton(
                        label = MR.strings.button_proposal_details_confirm.desc().localized(),
                        onClick = { onConfirm() },
                        enabled = true,
                        modifier = Modifier
                            .weight(1f)
                    )

                }
            }
        }
    }
}

@Composable
fun SelectAccountPermission(
    proposers: List<String>,
    permissionExecute: String,
    onPermissionExecute: (String) -> Unit,
) {
    Column {
        ProposalDropdownMenu(
            label = MR.strings.title_multisig_account_and_permission_screen_permission.desc()
                .localized(),
            value = permissionExecute,
            items = proposers,
            onValueChange = onPermissionExecute,
            placeholder = MR.strings.title_multisig_account_and_permission_screen_permission.desc()
                .localized(),
            requiredInput = false
        )
    }
}