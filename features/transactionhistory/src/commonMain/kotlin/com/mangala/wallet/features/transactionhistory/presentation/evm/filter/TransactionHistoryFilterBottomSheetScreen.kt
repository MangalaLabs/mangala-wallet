package com.mangala.wallet.features.transactionhistory.presentation.evm.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Document
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Swap
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxReceive
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxSend
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaDatePickerDialog
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.Toast
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formatDate
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class TransactionHistoryFilterBottomSheetScreen(
    private val typeFilter: TransactionType?,
    private val statusFilter: TransactionStatus?,
    private val startDateFilter: Instant?,
    private val endDateFilter: Instant?,
    @Transient val onConfirm: (
        typeFilter: TransactionType?,
        statusFilter: TransactionStatus?,
        startDateFilter: Instant?,
        endDateFilter: Instant?,
    ) -> Unit
) : BaseScreen<TransactionHistoryFilterBottomSheetScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.TRANSACTION_HISTORY_FILTER
    override val screenClassName: String = TransactionHistoryFilterBottomSheetScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TransactionHistoryFilterBottomSheetScreenModel {
        return getScreenModel(parameters = {
            parametersOf(
                typeFilter,
                statusFilter,
                startDateFilter,
                endDateFilter
            )
        })
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun ScreenContent(screenModel: TransactionHistoryFilterBottomSheetScreenModel) {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val uiModel = screenModel.uiState.collectAsStateMultiplatform().value

        val datePickerState = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = uiModel.initialDatePickerTime,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return screenModel.endDate.value?.let { utcTimeMillis <= it } ?: true
                }
            }
        )
        val datePickerStateEnd = rememberDatePickerState(
            initialDisplayMode = DisplayMode.Picker,
            initialSelectedDateMillis = uiModel.initialDatePickerTimeEnd,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return screenModel.startDate.value?.let { utcTimeMillis >= it } ?: true
                }
            }
        )

        TransactionHistoryFilterBottomSheetScreen(
            uiModel = uiModel,
            onSelectTransactionType = {
                screenModel.onTypeSelected(it)
            },
            onSelectTransactionStatus = {
                screenModel.onStatusSelected(it)
            },
            onClose = {
                bottomSheetNavigator.hide()
            },
            onClickStartDate = {
                screenModel.onClickPickStartDate()
            },
            onClickEndDate = {
                screenModel.onClickPickEndDate()
            },
            onConfirm = {
                onConfirm(
                    screenModel.uiState.value.selectedType,
                    screenModel.uiState.value.selectedStatus,
                    screenModel.uiState.value.selectedStartDate,
                    screenModel.uiState.value.selectedEndDate,
                )
                bottomSheetNavigator.hide()
            },
            onReset = {
                screenModel.onResetFilters()
            }
        )

        if (uiModel.isDatePickerDialogVisible) {
            val datePickerStateToUse =
                if (uiModel.isPickingStartDate == true) datePickerState else datePickerStateEnd

            MangalaDatePickerDialog(
                datePickerState = datePickerStateToUse,
                onDismissRequest = screenModel::onDismissDatePickerDialog,
                onConfirmClicked = {
                    when (datePickerStateToUse) {
                        datePickerState -> {
                            datePickerState.selectedDateMillis?.let {
                                screenModel.updateStartDate(it)
                            }
                        }

                        datePickerStateEnd -> {
                            datePickerStateEnd.selectedDateMillis?.let {
                                screenModel.updateEndDate(it)
                            }
                        }
                    }
                    val selectedDateMillis = datePickerStateToUse.selectedDateMillis
                    selectedDateMillis?.let(screenModel::onConfirmPickDate)
                },
                onDismissClicked = screenModel::onDismissDatePickerDialog
            )
        }
    }

    @Composable
    fun TransactionHistoryFilterBottomSheetScreen(
        uiModel: TransactionHistoryFilterBottomSheetScreenUiModel,
        onSelectTransactionType: (TransactionType?) -> Unit,
        onSelectTransactionStatus: (TransactionStatus?) -> Unit,
        onClickStartDate: () -> Unit,
        onClickEndDate: () -> Unit,
        onClose: () -> Unit,
        onConfirm: () -> Unit,
        onReset: () -> Unit
    ) {
        Column(
            Modifier
                .padding(horizontal = Dimensions.Padding.default)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MaxWidthBox(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimensions.Padding.small)
            ) {
                TextTopBar(
                    MR.strings.title_filter_transactions.desc().localized(),
                    modifier = Modifier.align(Alignment.Center)
                )
                MangalaWalletIconButton(
                    icon = MangalaWalletPack.Clear,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .background(Colors.cloudGray, CircleShape)
                        .padding(6.dp),
                    tint = Color(0xFF85858B)
                ) {
                    onClose()
                }
            }
            VerticalSpacer(Spacing.BASE)
            TypeFilterGroup(
                uiModel.selectedType,
                onSelect = onSelectTransactionType
            )
            VerticalSpacer(Spacing.BASE)
            StatusFilterGroup(
                uiModel.selectedStatus,
                onSelect = onSelectTransactionStatus
            )
            VerticalSpacer(Spacing.BASE)
            DateFilterGroup(
                startDateFilter = uiModel.selectedStartDate,
                endDateFilter = uiModel.selectedEndDate,
                onClickStartDate = onClickStartDate,
                onClickEndDate = onClickEndDate
            )
            VerticalSpacer(Spacing.BASE)
            TextTiny(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                color = Colors.gray
            )
            VerticalSpacer(68.dp)
            MaxWidthRow(horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL)) {
                OutlinedButton(
                    modifier = Modifier.weight(1f).height(44.dp),
                    onClick = {
                        onReset()
                    },
                    border = BorderStroke(1.dp, Colors.main1Text),
                    shape = RoundedCornerShape(CornerRadius.Small)
                ) {
                    TextNormal(
                        MR.strings.button_filter_transactions_reset.desc().localized(),
                        color = Colors.main1Text
                    )
                }
                ButtonNormal(
                    text = MR.strings.button_filter_transactions_confirm.desc().localized(),
                    buttonModifier = Modifier.weight(1f).height(44.dp),
                    shape = RoundedCornerShape(CornerRadius.Small),
                    elevation = null,
                    onClick = {
                        onConfirm()
                    }
                )
            }
            VerticalSpacer(Spacing.XSMALL)
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun TypeFilterGroup(
        typeFilter: TransactionType?,
        onSelect: (TransactionType?) -> Unit
    ) {
        TextDescription2(MR.strings.message_filter_transactions_type.desc().localized())
        VerticalSpacer(Spacing.XTINY)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
            verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)
        ) {
            FilterItemAll(isSelected = typeFilter == null) {
                onSelect(null)
            }
            TypeFilterItem(
                onClick = {
                    onSelect(TransactionType.SWAP)
                },
                icon = MangalaWalletPack.Swap,
                text = MR.strings.all_swap.desc().localized(),
                isSelected = typeFilter == TransactionType.SWAP
            )
            TypeFilterItem(
                onClick = {
                    onSelect(TransactionType.RECEIVE)
                },
                icon = MangalaWalletPack.TxReceive,
                text = MR.strings.all_receive.desc().localized(),
                isSelected = typeFilter == TransactionType.RECEIVE
            )
            TypeFilterItem(
                onClick = {
                    onSelect(TransactionType.SEND)
                },
                icon = MangalaWalletPack.TxSend,
                text = MR.strings.all_send.desc().localized(),
                isSelected = typeFilter == TransactionType.SEND
            )
            TypeFilterItem(
                onClick = {
                    onSelect(TransactionType.CONTRACT_CALL)
                },
                icon = MangalaWalletPack.Document,
                text = MR.strings.all_smart_contract_call.desc().localized(),
                isSelected = typeFilter == TransactionType.CONTRACT_CALL
            )
            TypeFilterItem(
                onClick = {
                    onSelect(TransactionType.CONTRACT_DEPLOYMENT)
                },
                icon = MangalaWalletPack.Document,
                text = MR.strings.all_contract_deployment.desc().localized(),
                isSelected = typeFilter == TransactionType.CONTRACT_DEPLOYMENT
            )
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun StatusFilterGroup(
        statusFilter: TransactionStatus?,
        onSelect: (TransactionStatus?) -> Unit
    ) {
        TextDescription2(MR.strings.all_status.desc().localized())
        VerticalSpacer(Spacing.XTINY)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)) {
            FilterItemAll(isSelected = statusFilter == null) {
                onSelect(null)
            }
            FilterItem(
                onClick = {
                    onSelect(TransactionStatus.SUCCESS)
                },
                isSelected = statusFilter == TransactionStatus.SUCCESS
            ) {
                TextDescription2(
                    MR.strings.all_transaction_completed.desc().localized(),
                    color = Colors.green,
                    modifier = it
                )
            }
            FilterItem(
                onClick = {
                    onSelect(TransactionStatus.PENDING)
                },
                isSelected = statusFilter == TransactionStatus.PENDING
            ) {
                TextDescription2(
                    MR.strings.all_transaction_pending.desc().localized(),
                    color = Colors.orangeRed,
                    modifier = it
                )
            }
            FilterItem(
                onClick = {
                    onSelect(TransactionStatus.FAILED)
                },
                isSelected = statusFilter == TransactionStatus.FAILED
            ) {
                TextDescription2(
                    MR.strings.all_transaction_failed.desc().localized(),
                    color = Colors.coral,
                    modifier = it
                )
            }
        }
    }

    @Composable
    fun DateFilterGroup(
        startDateFilter: Instant?,
        endDateFilter: Instant?,
        onClickStartDate: () -> Unit,
        onClickEndDate: () -> Unit
    ) {
        TextDescription2(MR.strings.all_date.desc().localized())
        VerticalSpacer(Spacing.XTINY)
        MaxWidthRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val timeZone = TimeZone.currentSystemDefault()
            // TODO: Localization: RTL people may want to see the date in the opposite order
            DateFilterItem(
                value = startDateFilter?.toLocalDateTime(timeZone)?.formatDate(timeZone),
                hint = "Start date" // TODO: Localize after confirmation from Son
            ) {
                onClickStartDate()
            }
            Box(Modifier.padding(horizontal = Dimensions.Padding.small)) {
                TextDescription2(
                    MR.strings.message_filter_transactions_date_to.desc().localized(),
                    color = Colors.darkGray,
                )
            }
            DateFilterItem(
                value = endDateFilter?.toLocalDateTime(timeZone)?.formatDate(timeZone),
                hint = "End date" // TODO: Localize after confirmation from Son
            ) {
                onClickEndDate()
            }
        }
    }

    @Composable
    private fun FilterItemAll(isSelected: Boolean, onClick: () -> Unit) {
        FilterItem(onClick = onClick, isSelected = isSelected) {
            TextDescription2(
                MR.strings.message_filter_transactions_all.desc().localized(),
                color = Colors.darkGray,
                modifier = it
            )
        }
    }

    @Composable
    fun TypeFilterItem(isSelected: Boolean, onClick: () -> Unit, icon: ImageVector, text: String) {
        FilterItem(isSelected = isSelected, onClick = onClick) {
            Row(
                modifier = it,
                horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Colors.caption,
                    modifier = Modifier.size(18.dp)
                )
                TextDescription2(text, color = Colors.gray)
            }
        }
    }

    @Composable
    private fun RowScope.DateFilterItem(
        value: String?,
        hint: String,
        onClick: () -> Unit
    ) {
        FilterItem(
            modifier = Modifier.Companion.weight(1f),
            onClick = onClick,
            contentPadding = PaddingValues(
                vertical = Dimensions.Padding.half,
                horizontal = Dimensions.Padding.small
            ),
            isSelected = false
        ) {
            TextDescription2(
                value ?: hint,
                color = Colors.gray,
            )
        }
    }

    @Composable
    fun FilterItem(
        modifier: Modifier = Modifier,
        contentPadding: PaddingValues = PaddingValues(Dimensions.Padding.half),
        isSelected: Boolean,
        onClick: () -> Unit,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        Box(
            Modifier
                .then(modifier)
                .defaultMinSize(minWidth = 60.dp, minHeight = 36.dp)
                .clip(RoundedCornerShape(CornerRadius.Small))
                .then(if (isSelected) Modifier.background(Colors.cloudGray) else Modifier)
                .border(1.dp, Color(0xFF85858B), RoundedCornerShape(CornerRadius.Small))
                .clickable(onClick = onClick)
                .padding(contentPadding)
        ) {
            content(Modifier.align(Alignment.Center))
        }
    }
}