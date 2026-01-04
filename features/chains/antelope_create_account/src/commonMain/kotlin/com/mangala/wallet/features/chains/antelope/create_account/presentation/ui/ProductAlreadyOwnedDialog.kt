package com.mangala.wallet.features.chains.antelope.create_account.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun ProductAlreadyOwnedDialog(
    accountName: String,
    onDismiss: () -> Unit,
    onConfirmCreate: () -> Unit
) {
    val mangalaColors = MaterialTheme.mangalaColors
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = CornerRadius.BottomSheet),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            Column(
                Modifier
                    .padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextDescription2(
                    "It seems like you’ve redeemed or made a purchase on this device (or another), but haven’t successfully created an account.",
                    color = MaterialTheme.mangalaColors.textSecondary
                )

                Text(
                    buildAnnotatedString {
                        append(
                            "Would you like to create new account "
                        )
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Medium,
                                color = mangalaColors.textPrimary
                            )
                        ) {
                            append(accountName)
                        }
                        append("?")
                    },
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontSize = FontType.SMALL,
                    color = MaterialTheme.mangalaColors.textSecondary
                )

                TextDescription2(
                    "If you create this account successfully, you won’t be able to retry creating the failed accounts before this",
                    color = MaterialTheme.mangalaColors.textSecondary,
                )

                MangalaGradientButton(
                    label = "Create account $accountName",
                    onClick = onConfirmCreate
                )

                MangalaOutlinedButtonNew(
                    onClick = onDismiss,
                    label = MR.strings.all_cancel.desc().localized()
                )
            }
        }
    }
}