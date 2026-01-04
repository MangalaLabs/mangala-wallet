package com.mangala.wallet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MaxWidthBox
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

//@Composable
//expect fun MangalaWalletDialog(
//    title: String,
//    message: String,
//    positiveButtonText: String,
//    negativeButtonText: String,
//    onPositiveClick: () -> Unit,
//    onNegativeClick: () -> Unit
//)

@Composable
fun MangalaCommonDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveClick: () -> Unit,
    onNegativeClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onNegativeClick,
        buttons = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onPositiveClick)
                        .align(Alignment.End) // Align box content to the right
                ) {
                    TextDialogButton(
                        text = positiveButtonText,
                        isPositiveAction = false,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                top = 0.dp,
                                bottom = 8.dp // Increased bottom padding
                            )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNegativeClick)
                        .align(Alignment.End) // Align box content to the right
                ) {
                    TextDialogButton(
                        text = negativeButtonText,
                        isPositiveAction = true,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                top = 8.dp,
                                bottom = 24.dp // Increased bottom padding
                            )
                    )
                }
            }

        },
        title = { TextTitle4(text = title, color = Colors.main1Text) },
        text = { TextDescription1(text = message, color = Colors.main1Text) },
    )
}

@Composable
fun MangalaCommonDialog(
    title: String,
    message: String,
    actionButtonText: String,
    onClickActionButton: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onClickActionButton,
        buttons = {
            MaxWidthBox(
                contentAlignment = Alignment.CenterEnd
            ) {
                TextDialogButton(
                    text = actionButtonText,
                    isPositiveAction = true,
                    modifier = Modifier
                        .clickable(onClick = onClickActionButton)
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            top = 8.dp,
                            bottom = 24.dp // Increased bottom padding
                        )
                )
            }
        },
        title = { TextTitle4(text = title, color = Colors.main1Text) },
        text = { TextDescription1(text = message, color = Colors.main1Text) },
    )
}

@Composable
fun MangalaCommonDialogDelete(
    onNegativeClick: () -> Unit,
    onPositiveClick: () -> Unit
) {
    MangalaCommonDialog(
        title = MR.strings.title_delete_wallet.desc().localized(),
        message = MR.strings.message_delete_wallet.desc().localized(),
        positiveButtonText = MR.strings.reset_wallet.desc().localized().uppercase(),
        negativeButtonText = MR.strings.all_cancel.desc().localized().uppercase(),
        onNegativeClick = onNegativeClick,
        onPositiveClick = onPositiveClick
    )
}
