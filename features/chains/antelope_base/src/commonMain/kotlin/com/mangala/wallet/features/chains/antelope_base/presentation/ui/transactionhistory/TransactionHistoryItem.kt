package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mangala.antelope.base.api.model.Act
import com.mangala.antelope.base.api.model.ActionTrace
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.RepeatCircle
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.utils.ext.jsonPrimitiveOrNull
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TransactionSummary(
    uiModel: ListActionDataUiModel,
    roundedCornerShape: Shape,
    modifier: Modifier = Modifier
) {
    var isActionsListExpanded by remember { mutableStateOf(false) }
    val dropdownIconRotationState by animateFloatAsState(
        targetValue = if (isActionsListExpanded) 180f else 0f
    )

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(roundedCornerShape)
                .clickable { isActionsListExpanded = !isActionsListExpanded }
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(
                    horizontal = Dimensions.Padding.small,
                    vertical = Dimensions.Padding.half
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                HeaderRow(
                    headerText = {
                        TextDescription2(
                            text = "Transaction: ${uiModel.formattedTxId}",
                            color = MaterialTheme.mangalaColors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    },
                    iconTint = MaterialTheme.mangalaColors.textPrimary,
                    trailingIcon = {
                        Icon(
                            imageVector = MangalaWalletPack.Dropdown,
                            contentDescription = null,
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.rotate(dropdownIconRotationState)
                        )
                    },
                    leadingIcon = MangalaWalletPack.RepeatCircle, // TODO: Determine leading icon
                )

                Spacer(modifier = Modifier.height(8.dp))

                TransactionSummaryHeaders(
                    uiModel.summaryHeaders
                )

                TransactionTime(
                    blockTime = uiModel.blockTime,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        AnimatedVisibility(
            isActionsListExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            MaxWidthColumn {
                AdditionalActionTraces(
                    uiModel.actionDataUiModel.actionTraces,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))
    }
}

@Composable
fun AdditionalActionTraces(
    traces: List<ActionTrace>,
    modifier: Modifier
) {
    traces.forEachIndexed { index, trace ->
        Spacer(modifier = Modifier.height(1.dp))

        trace.act?.let {
            ActionTraceItem(
                modifier = modifier,
                act = it,
                roundedCornerShape = roundedCornerItemShape(traces, index)
            )
        }
    }
}

@Composable
fun ActionTraceItem(
    modifier: Modifier = Modifier,
    act: Act,
    roundedCornerShape: Shape,
    endCornerIcon: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(roundedCornerShape)
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .padding(horizontal = Dimensions.Padding.small, vertical = Dimensions.Padding.half),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            HeaderRow(
                headerText = {
                    TextDescription2(
                        text = "Action: ${act.actionId}",
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                },
                iconTint = MaterialTheme.mangalaColors.textPrimary,
                trailingIcon = endCornerIcon
            )

            Spacer(modifier = Modifier.height(8.dp))

            ConditionalRows(
                act = act
            )
        }
    }
}


@Composable
fun HeaderRow(
    headerText: @Composable () -> Unit,
    iconTint: Color,
    trailingIcon: @Composable () -> Unit = {},
    leadingIcon: ImageVector? = null
) {
    MaxWidthRow(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            leadingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "Transaction Icon",
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            headerText()
        }

        trailingIcon()
    }
}

@Composable
fun ConditionalRows(
    act: Act
) {
    Column {
        act.data?.entries?.forEach { (key, value) ->
            TransactionRow(
                label = key,
                value = value.jsonPrimitiveOrNull?.content ?: value.toString()
            )
        }
    }
}

@Composable
fun TransactionRow(
    label: String,
    value: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.Top) {
            TransactionSummaryHeaderText(text = "$label: ", color = MaterialTheme.mangalaColors.textPrimary)
            TransactionSummaryHeaderText(value)
        }
    }
}

@Composable
fun TransactionTime(blockTime: String?, modifier: Modifier) {
    TextTiny(
        text = blockTime?.formatHourAndMinute() ?: "00:00",
        color = MaterialTheme.mangalaColors.textSecondary,
        modifier = modifier
    )
}

private fun String.formatHourAndMinute(): String {
    val isoString = if (this.endsWith("Z") || this.contains("+")) this else "${this}Z"
    val dateTime = Instant.parse(isoString)
    val localDateTime = dateTime.toLocalDateTime(TimeZone.currentSystemDefault())

    val hour = localDateTime.hour
    val minute = localDateTime.minute
    val formattedHour = if (hour < 10) "0$hour" else hour.toString()
    val formattedMinute = if (minute < 10) "0$minute" else minute.toString()

    return "$formattedHour:$formattedMinute"
}
