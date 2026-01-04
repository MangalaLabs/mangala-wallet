package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ColumnScope.TransactionSummaryHeaders(headers: List<ActionDataSummaryHeaderUiModel>) {
    headers.forEachIndexed { index, header ->
        when (header) {
            is ActionDataSummaryHeaderUiModel.RamBuy -> RamBuyTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.RamSell -> RamSellTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.RamTransfer -> RamTransferTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.ResourceProviderFee -> ResourceProviderFeeTransactionHeader(
                header
            )

            is ActionDataSummaryHeaderUiModel.TokenTransfer -> TokenTransferTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.CreateAccount -> CreateAccountTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.ContractCall -> ContractCallTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.LinkAuth -> LinkAuthTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.UpdateAuth -> UpdateAuthTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.RentViaRex -> RentViaRexTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.PowerUp -> PowerupTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.DelegateBandwidth -> DelegateBandwidthTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.MsigPropose -> MsigProposeTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.MsigApproveToggle -> MsigApproveTransactionHeader(
                header
            )

            is ActionDataSummaryHeaderUiModel.MsigCancel -> MsigCancelTransactionHeader(header)
            is ActionDataSummaryHeaderUiModel.MsigExecute -> MsigExecuteTransactionHeader(header)
        }

        if (index < headers.size - 1) {
            VerticalSpacer(Spacing.TINY)
        }
    }
}

@Composable
fun RamBuyTransactionHeader(header: ActionDataSummaryHeaderUiModel.RamBuy) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                when (header.buyRamType) {
                    // TODO: Localization
                    ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_SELF -> {
                        append("You")
                        withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                            append(" bought ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Colors.mediumGreen,
                                fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                            )
                        ) {
                            append(header.ramBytesBoughtFormatted ?: header.totalCostFormatted)
                        }
                        append(" of RAM ${if (header.ramBytesBought != null) "for " else ""}")
                        if (header.ramBytesBought != null) {
                            withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                                append(header.totalCostFormatted)
                            }
                        }
                    }

                    ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BUY_FOR_OTHERS -> {
                        append("You")
                        withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                            append(" bought ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Colors.mediumGreen,
                                fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                            )
                        ) {
                            append(header.ramBytesBoughtFormatted ?: header.totalCostFormatted)
                        }
                        append(" of RAM for ")
                        withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                            append(header.recipientAccount)
                        }
                        if (header.ramBytesBought != null) {
                            append(" for ")
                            withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                                append(header.totalCostFormatted)
                            }
                        }
                    }

                    ActionDataSummaryHeaderUiModel.RamBuy.BuyRamType.BOUGHT_BY_OTHERS -> {
                        withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                            append(header.payerAccount)
                        }
                        append(" bought you ")
                        withStyle(
                            SpanStyle(
                                color = Colors.mediumGreen,
                                fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                            )
                        ) {
                            append(header.ramBytesBoughtFormatted)
                        }
                        append(" of RAM ${if (header.ramBytesBought != null) "for " else ""}")
                        if (header.ramBytesBought != null) {
                            withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                                append(header.totalCostFormatted)
                            }
                        }
                    }
                }
            }
        )

        header.pricePerKbFormatted?.let {
            TransactionSummaryHeaderText(
                text = buildAnnotatedString {
                    append("RAM price: ")
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(it)
                    }
                }
            )
        }

        header.newRamBalanceFormatted?.let {
            TransactionSummaryHeaderText(
                text = "RAM balance: $it"
            )
        }

        TransactionSummaryHeaderText(
            text = "RAM fee: ${header.ramFeeFormatted}"
        )
    }
}

@Composable
fun RamSellTransactionHeader(header: ActionDataSummaryHeaderUiModel.RamSell) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" sold ")
                }
                withStyle(
                    SpanStyle(
                        color = Colors.brightRed,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                ) {
                    append(header.ramBytesSoldFormatted ?: header.totalReceivedFormatted)
                }
                append(" of RAM ")
                if (header.ramBytesSold != null) {
                    append("for ")
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.totalReceivedFormatted)
                    }
                }
            }
        )

        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("RAM price: ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.pricePerKbFormatted)
                }
            }
        )

        header.newRamBalanceFormatted?.let {
            TransactionSummaryHeaderText(
                text = "RAM balance: $it"
            )
        }

        TransactionSummaryHeaderText(
            text = "RAM fee: ${header.ramFeeFormatted}"
        )
    }
}

@Composable
fun RamTransferTransactionHeader(header: ActionDataSummaryHeaderUiModel.RamTransfer) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" ${if (header.isOutgoingTransaction) "sent" else "received"} ")
                }
                withStyle(
                    SpanStyle(
                        color = if (header.isOutgoingTransaction) Colors.brightRed else Colors.mediumGreen,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                ) {
                    append(header.ramBytesFormatted)
                }
                append(" of RAM")
                append(if (header.isOutgoingTransaction) " to " else " from ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(if (header.isOutgoingTransaction) header.recipientAccount else header.senderAccount)
                }
            }
        )

        if (header.memo.isNotEmpty()) {
            TransactionSummaryHeaderText(
                text = "Memo: ${header.memo}"
            )
        }
    }
}

@Composable
fun ResourceProviderFeeTransactionHeader(header: ActionDataSummaryHeaderUiModel.ResourceProviderFee) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.resourceProviderAccount)
                }
                append(" provided you the resources needed for this transaction for ")
                withStyle(
                    SpanStyle(
                        color = Colors.brightRed,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                ) {
                    append(header.amountPaidFormatted)
                }
            }
        )

        if (header.memo.isNotEmpty()) {
            TransactionSummaryHeaderText(
                text = "Memo: ${header.memo}"
            )
        }
    }
}

@Composable
fun TokenTransferTransactionHeader(header: ActionDataSummaryHeaderUiModel.TokenTransfer) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" ${if (header.isOutgoingTransaction) "sent" else "received"} ")
                }
                withStyle(
                    SpanStyle(
                        color = if (header.isOutgoingTransaction) Colors.brightRed else Colors.mediumGreen,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                ) {
                    append(header.quantityFormatted)
                }
                append(if (header.isOutgoingTransaction) " to " else " from ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(if (header.isOutgoingTransaction) header.recipientAccount else header.senderAccount)
                }
            }
        )

        if (header.memo.isNotEmpty()) {
            TransactionSummaryHeaderText(
                text = "Memo: ${header.memo}"
            )
        }
    }
}

@Composable
fun CreateAccountTransactionHeader(header: ActionDataSummaryHeaderUiModel.CreateAccount) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" created ")
                }
                append("account ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.newAccountName)
                }
            }
        )
    }
}

@Composable
fun ContractCallTransactionHeader(header: ActionDataSummaryHeaderUiModel.ContractCall) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append(if (header.isFromSelf) "You" else header.from)
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" called ")
                }
                append("the action ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.actionId)
                }
            }
        )
    }
}

@Composable
fun LinkAuthTransactionHeader(header: ActionDataSummaryHeaderUiModel.LinkAuth) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" linked ")
                }
                append("the authorization ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.auth)
                }
                append(" to ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.actionId)
                }
            }
        )
    }
}

@Composable
fun UpdateAuthTransactionHeader(header: ActionDataSummaryHeaderUiModel.UpdateAuth) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" updated ")
                }
                append("the authorization ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.permissionName)
                }
            }
        )
    }
}

@Composable
fun RentViaRexTransactionHeader(header: ActionDataSummaryHeaderUiModel.RentViaRex) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" rented ")
                }
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(if (header.resourceType == ActionDataSummaryHeaderUiModel.RentViaRex.ResourceType.RENT_CPU) "CPU" else "NET")
                }
                append(" with ")
                withStyle(
                    SpanStyle(
                        color = Colors.brightRed,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                ) {
                    append(header.amountFormatted)
                }
            }
        )
    }
}

@Composable
fun PowerupTransactionHeader(header: ActionDataSummaryHeaderUiModel.PowerUp) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                if (header.powerUpType == ActionDataSummaryHeaderUiModel.ResourceRentType.RENTED_FOR_BY_OTHERS) {
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.from)
                    }
                } else {
                    append("You")
                }
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" powered up ${if (header.powerUpType == ActionDataSummaryHeaderUiModel.ResourceRentType.RENT_FOR_OTHERS) header.receiver else ""} ")
                }
                append("with ")
                withStyle(
                    SpanStyle(
                        color = Colors.brightRed,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                ) {
                    append(header.amountFormatted)
                }
            }
        )
    }
}

@Composable
fun DelegateBandwidthTransactionHeader(header: ActionDataSummaryHeaderUiModel.DelegateBandwidth) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                if (header.delegateType == ActionDataSummaryHeaderUiModel.ResourceRentType.RENTED_FOR_BY_OTHERS) {
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.from)
                    }
                } else {
                    append("You")
                }
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" staked ")
                }
                withStyle(
                    SpanStyle(
                        color = Colors.brightRed,
                        fontFamily = getSfProFamilyFont(weight = FontWeight.W600)
                    )
                        ) {
                    append(header.totalAmountFormatted)
                }
                append(" for ${header.resources}")
                if (header.delegateType == ActionDataSummaryHeaderUiModel.ResourceRentType.RENT_FOR_OTHERS) {
                    append(" for ")
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.receiver)
                    }
                }
            }
        )
    }
}

@Composable
fun MsigProposeTransactionHeader(header: ActionDataSummaryHeaderUiModel.MsigPropose) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                if (header.isProposalFromCurrentAccount) {
                    append("You")
                } else {
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.proposer)
                    }
                }
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" proposed ")
                }
                append("multisig transaction ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.proposalName)
                }
            }
        )

        TransactionSummaryHeaderText(
            text = "Requested approvals: ${header.formattedRequestedPermissions}"
        )

        TransactionSummaryHeaderText(
            text = "Actions: ${header.formattedActions}"
        )
    }
}

@Composable
fun MsigApproveTransactionHeader(header: ActionDataSummaryHeaderUiModel.MsigApproveToggle) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" ${if (header.isApprove) "approved" else "unapproved"} ")
                }
                if (header.isMsigProposedBySelf) {
                    append("your")
                } else {
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.proposer)
                    }
                    append("'s")
                }
                append(" multisig proposal ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.proposalName)
                }
                append(" with permission ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.formattedApprovedPermission)
                }
            }
        )
    }
}

@Composable
fun MsigCancelTransactionHeader(header: ActionDataSummaryHeaderUiModel.MsigCancel) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" canceled ")
                }
                if (header.isMsigProposedBySelf) {
                    append("your")
                } else {
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.proposer)
                    }
                    append("'s")
                }
                append(" multisig proposal ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.proposalName)
                }
            }
        )
    }
}

@Composable
fun MsigExecuteTransactionHeader(header: ActionDataSummaryHeaderUiModel.MsigExecute) {
    MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
        TransactionSummaryHeaderText(
            text = buildAnnotatedString {
                append("You")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(" executed ")
                }
                if (header.isMsigProposedBySelf) {
                    append("your")
                } else {
                    withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                        append(header.proposer)
                    }
                    append("'s")
                }
                append(" multisig proposal ")
                withStyle(SpanStyle(color = MaterialTheme.mangalaColors.textPrimary)) {
                    append(header.proposalName)
                }
            },
        )
    }
}

@Composable
fun TransactionSummaryHeaderText(text: String, color: Color = MaterialTheme.mangalaColors.textSecondary) {
    TextDescription2(
        text = text,
        color = color,
        fontWeight = FontWeight.W500
    )
}

@Composable
fun TransactionSummaryHeaderText(text: AnnotatedString, color: Color = MaterialTheme.mangalaColors.textSecondary) {
    TextDescription2(
        text = text,
        color = color,
        fontWeight = FontWeight.W500
    )
}