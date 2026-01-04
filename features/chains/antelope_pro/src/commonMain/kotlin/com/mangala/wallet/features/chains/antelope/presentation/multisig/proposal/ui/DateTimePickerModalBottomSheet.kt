package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.formatDate
import com.mangala.wallet.utils.formatTime
import com.mangala.wallet.utils.getShortMonthName
import dev.darkokoa.datetimewheelpicker.WheelDatePicker
import dev.darkokoa.datetimewheelpicker.WheelTimePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import dev.darkokoa.datetimewheelpicker.core.format.DateOrder
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.core.format.dateFormatter
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DateTimePickerModalBottomSheet(
    initialTimestamp: Long,
    minSelectableLocalDate: LocalDate = LocalDate(1970, 1, 1),
    minSelectableLocalTime: LocalTime = LocalTime(0, 0),
    onSelectExpirationDate: (Long) -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        onDismissRequest = onDismissRequest,
        containerColor = ColorsNew.white,
        dragHandle = {
            DragHandle(
                color = ColorsNew.stroke,
                width = 32.dp,
                height = 4.dp,
                shape = RoundedCornerShape(CornerRadius.Medium)
            )
        },
    ) {
        MaxWidthColumn(
            Modifier.padding(Dimensions.Padding.default),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var isSelectingDate by remember { mutableStateOf(false) }
            var selectedLocalDate by remember(initialTimestamp) {
                mutableStateOf(
                    Instant.fromEpochMilliseconds(initialTimestamp)
                        .toLocalDateTime(
                            TimeZone.currentSystemDefault()
                        ).date
                )
            }
            var selectedLocalTime by remember(initialTimestamp) {
                mutableStateOf(
                    Instant.fromEpochMilliseconds(initialTimestamp)
                        .toLocalDateTime(
                            TimeZone.currentSystemDefault()
                        ).time
                )
            }
            val selectedLocalDateFormatted by remember(selectedLocalDate) {
                mutableStateOf(selectedLocalDate.formatDate(style = FormatStyle.MEDIUM))
            }
            val selectedLocalTimeFormatted by remember(selectedLocalTime) {
                mutableStateOf(selectedLocalTime.formatTime())
            }

            Text("Choose expired date", style = MangalaTypography.Size14Medium())
            VerticalSpacer(Spacing.BASE)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL),
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                DateTimeCategoryChip(
                    text = selectedLocalDateFormatted,
                    isSelected = isSelectingDate,
                    onClick = {
                        isSelectingDate = true
                    }
                )
                DateTimeCategoryChip(
                    text = selectedLocalTimeFormatted,
                    isSelected = !isSelectingDate,
                    onClick = {
                        isSelectingDate = false
                    }
                )
            }
            VerticalSpacer(Spacing.XXMEDIUM)
            if (isSelectingDate) {
                WheelDatePicker(
                    startDate = selectedLocalDate,
                    minDate = minSelectableLocalDate,
                    dateFormatter = remember(Locale.current) {
                        dateFormatter(
                            DateOrder.match(
                                Locale.current
                            ),
                            monthDisplayStyle = MonthDisplayStyle.FULL,
                            formatMonth = { month, monthDisplayStyle ->
                                month.number.getShortMonthName()
                            }
                        )
                    },
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = ColorsNew.primary_100,
                        shape = RoundedCornerShape(CornerRadius.Tiny),
                        border = null
                    ),
                    rowCount = 3,
                ) {
                    selectedLocalDate = it
                }
            } else {
                WheelTimePicker(
                    startTime = selectedLocalTime,
                    minTime = minSelectableLocalTime,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = ColorsNew.primary_100,
                        shape = RoundedCornerShape(CornerRadius.Tiny),
                        border = null
                    ),
                    rowCount = 3
                ) { snappedTime ->
                    selectedLocalTime = snappedTime
                }
            }
            VerticalSpacer(Spacing.XBASE)
            MangalaGradientButton(
                label = MR.strings.all_confirm.desc().localized(),
                onClick = {
                    onSelectExpirationDate(
                        LocalDateTime(
                            selectedLocalDate,
                            selectedLocalTime
                        ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                    )
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DateTimeCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        remember(isSelected) { if (isSelected) ColorsNew.primary_500 else ColorsNew.primary_100 }
    val textColor =
        remember(isSelected) { if (isSelected) ColorsNew.white else ColorsNew.primary_400 }

    Box(
        Modifier.background(backgroundColor, shape = RoundedCornerShape(CornerRadius.Medium))
            .clickable(onClick = onClick).padding(
                horizontal = Dimensions.Padding.small,
                vertical = Dimensions.Padding.quarter
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MangalaTypography.Size13Medium()
        )
    }
}