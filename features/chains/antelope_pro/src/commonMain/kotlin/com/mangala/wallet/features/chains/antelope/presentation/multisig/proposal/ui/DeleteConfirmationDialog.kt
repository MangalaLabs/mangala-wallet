package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc

@Composable
fun DeleteConfirmationDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
    onDeleteAction: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                Modifier.background(
                    MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(CornerRadius.BottomSheet)
                ).padding(Dimensions.Padding.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painterResource(MR.images.delete_confirmation),
                    contentDescription = null,
                    modifier = Modifier.size(width = 139.dp, height = 104.dp)
                )
                VerticalSpacer(Spacing.SMALL)
                Text(
                    text = title,
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary
                )
                VerticalSpacer(Spacing.TINY)
                Text(
                    text = description,
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.Center
                )
                VerticalSpacer(Spacing.BASE)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MangalaButton(
                        onClick = {
                            onDeleteAction()
                            onDismiss()
                        },
                        label = MR.strings.all_delete.desc().localized(),
                        modifier = Modifier.weight(1f),
                        backgroundColor = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                        contentColor = MaterialTheme.mangalaColors.buttonDestructiveContent,
                        size = MangalaButtonSize.Medium

                    )
                    MangalaOutlinedButtonNew(
                        onClick = {
                            onDismiss()
                        },
                        label = MR.strings.all_cancel.desc().localized(),
                        modifier = Modifier.weight(1f),
                        size = MangalaButtonSize.Medium
                    )
                }
            }
        }
    )
}