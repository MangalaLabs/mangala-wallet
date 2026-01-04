package com.mangala.wallet.features.chains.antelope_base.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.mangala.antelope.base.domain.model.FeeBreakdown
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

@Composable
fun AntelopeResourceProviderFeeDialog(
    feeBreakdown: FeeBreakdown?,
    resourceRequiredTotal: String?,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(size = CornerRadius.BottomSheet),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            )
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp, top = 32.dp, end = 20.dp, bottom = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val resources = mutableListOf<String>()

                if (feeBreakdown != null) {
                    val cpu = BalanceFormatter.deserializeOrNull(feeBreakdown.cpu)
                    val net = BalanceFormatter.deserializeOrNull(feeBreakdown.net)
                    val ram = BalanceFormatter.deserializeOrNull(feeBreakdown.ram)

                    if (cpu != null && cpu.amount != 0.0) {
                        resources.add("CPU")
                    }

                    if (net != null && net.amount != 0.0) {
                        resources.add("NET")
                    }

                    if (ram != null && ram.amount != 0.0) {
                        resources.add("RAM")
                    }
                }

                val resourcesText = when {
                    resources.isEmpty() -> ""
                    resources.size == 1 -> "(${resources[0]})"
                    else -> "(" + resources.dropLast(1)
                        .joinToString(", ") + " " + MR.strings.message_power_up_antelope_resource_dialog_spoken_words.desc()
                        .localized() + " " + resources.last() + ")"
                }

                TextTopBar(
                    text = MR.strings.title_power_up_antelope_resource_dialog.desc().localized(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))
                TextDescription2(
                    text = MR.strings.message_power_up_antelope_resource_dialog_question.desc()
                        .localized(), textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))
                TextDescription2(
                    text = MR.strings.message_power_up_antelope_resource_dialog_description.format(
                        resourcesText
                    ).localized(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.mangalaColors.textSecondary,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))
                Text(
                    buildAnnotatedString {
                        append(
                            MR.strings.all_total.desc()
                                .localized() + " "
                        )
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.mangalaColors.textPrimary
                            )
                        ) {
                            append("$resourceRequiredTotal")
                        }
                    },
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    fontSize = FontType.SMALL,
                    color = MaterialTheme.mangalaColors.textSecondary
                )
                Spacer(modifier = Modifier.height(Spacing.BASE))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MangalaOutlinedButtonNew(
                        label = MR.strings.button_power_up_antelope_resource_dialog_decline.desc()
                            .localized(),
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    )


                    Spacer(modifier = Modifier.width(7.dp))
                    MangalaGradientButton(
                        label = MR.strings.button_power_up_antelope_resource_dialog_accept.desc()
                            .localized(),
                        onClick = {
                            onClick()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
