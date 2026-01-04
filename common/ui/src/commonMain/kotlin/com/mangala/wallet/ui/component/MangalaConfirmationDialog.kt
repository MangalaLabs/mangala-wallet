package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun MangalaConfirmationDialog(
    title: String,
    description: String,
    negativeButtonText: String,
    positiveButtonText: String,
    onDismiss: () -> Unit,
    onNegativeButton: () -> Unit,
    onPositiveAction: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        content = {
            Column(
                Modifier
                    .background(
                        MaterialTheme.mangalaColors.bgInnerCard,
                        shape = RoundedCornerShape(CornerRadius.BottomSheet)
                    )
                    .padding(Dimensions.Padding.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )
                VerticalSpacer(Spacing.TINY)
                Text(
                    text = description,
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                    textAlign = TextAlign.Center
                )
                VerticalSpacer(Spacing.BASE)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MangalaOutlinedButtonNew(
                        onClick = {
                            onNegativeButton()
                            onDismiss()
                        },
                        label = negativeButtonText,
                        modifier = Modifier.weight(1f),
                        size = MangalaButtonSize.Medium
                    )

                    MangalaGradientButton(
                        label = positiveButtonText,
                        onClick = {
                            onPositiveAction()
                            onDismiss()
                        },
                        size = MangalaButtonSize.Medium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}