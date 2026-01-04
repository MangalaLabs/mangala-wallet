package com.mangala.wallet.features.chains.antelope.create_account.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.CreateAccountWithInAppPurchaseUseCase
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaWrappedTextButton
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTopBar
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun RetryCreateAccountErrorDialog(
    error: CreateAccountWithInAppPurchaseUseCase.CreateAccountError?,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
    onContactSupport: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = CornerRadius.BottomSheet)
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextTopBar(
                    text = "Error creating account",
                    fontWeight = FontWeight.Bold,
                    color = Colors.darkDarkGray
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))
                TextDescription2(
                    text = error.mapToErrorMessageStringResource().desc().localized(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = Colors.darkDarkGray
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    when (error) {
                        CreateAccountWithInAppPurchaseUseCase.CreateAccountError.NetworkError -> {
                            ButtonNormal(
                                text = MR.strings.button_wallet_main_retry_create.desc()
                                    .localized(),
                                onClick = onRetry,
                                buttonModifier = Modifier.fillMaxWidth()
                            )
                            MangalaWrappedTextButton(
                                text = MR.strings.all_ok.desc().localized(),
                                onClick = onContactSupport,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseAlreadyConsumed, CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchaseCancelled, CreateAccountWithInAppPurchaseUseCase.CreateAccountError.PurchasePending -> {
                            ButtonNormal(
                                text = MR.strings.all_ok.desc().localized(),
                                onClick = onDismiss,
                                buttonModifier = Modifier.fillMaxWidth()
                            )
                            MangalaWrappedTextButton(
                                text = MR.strings.all_contact_support.desc().localized(),
                                onClick = onContactSupport,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        CreateAccountWithInAppPurchaseUseCase.CreateAccountError.AntelopeNodeError,
                        CreateAccountWithInAppPurchaseUseCase.CreateAccountError.UnknownError, null -> {
                            ButtonNormal(
                                text = MR.strings.all_ok.desc().localized(),
                                onClick = onDismiss,
                                buttonModifier = Modifier.fillMaxWidth()
                            )
                            MangalaWrappedTextButton(
                                text = MR.strings.all_contact_support.desc().localized(),
                                onClick = onContactSupport,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}