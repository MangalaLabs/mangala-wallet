package com.mangala.wallet.features.transactionhistory.presentation.evm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.transactionhistory.presentation.evm.filter.TransactionHistoryFilterBottomSheetScreen
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Document
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Filter
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxReceive
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxSend
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxSwap
import com.mangala.wallet.features.transactionhistory.presentation.TransactionHistoryItem
import com.mangala.wallet.features.transactionhistory.presentation.TransactionUi
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import org.koin.core.parameter.parametersOf

class TransactionHistoryScreen(private val accountId: String) :
    BaseScreen<TransactionHistoryScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.TRANSACTION_HISTORY
    override val screenClassName: String = TransactionHistoryScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TransactionHistoryScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                accountId
            )
        }
    )

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: TransactionHistoryScreenModel) {
        val parentNavigator = LocalNavigator.currentOrThrow

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet
            )
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            TransactionHistoryScreen(
                screenModel.list,
                isRefreshing = screenModel.isRefreshing.value,
                onPullToRefresh = {
                    screenModel.pullToRefresh()
                },
                onBackClicked = {
                    parentNavigator.pop()
                },
                onFilterClicked = {
                    bottomSheetNavigator.show(
                        TransactionHistoryFilterBottomSheetScreen(
                            screenModel.transactionTypeFilter.value,
                            screenModel.transactionStatusFilter.value,
                            screenModel.startDateFilter.value,
                            screenModel.endDateFilter.value,
                            onConfirm = { typeFilter, statusFilter, startDateFilter, endDateFilter ->
                                screenModel.onTransactionTypeFilterSelected(typeFilter)
                                screenModel.onTransactionStatusFilterSelected(statusFilter)
                                screenModel.onStartDateFilterSelected(startDateFilter)
                                screenModel.onEndDateFilterSelected(endDateFilter)
                            }
                        )
                    )
                },
                onClickTransaction = {
                    parentNavigator.push(
                        ScreenRegistry.get(
                            SharedScreen.TransactionInfoScreen(
                                accountId = accountId,
                                txHash = it.txHash
                            )
                        )
                    )
                }
            )
        }
    }

    @Composable
    fun TransactionHistoryScreen(
        pager: Flow<PagingData<TransactionHistoryItem>>,
        onBackClicked: () -> Unit,
        onFilterClicked: () -> Unit,
        onClickTransaction: (TransactionUi) -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {
        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_transaction_history.desc().localized(),
                onBackClicked = onBackClicked,
                trailingButton = {
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Filter,
                        onClick = {
                            onFilterClicked()
                        }
                    )
                }
            )
            TransactionHistoryList(pager, onClickTransaction, isRefreshing, onPullToRefresh)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun TransactionHistoryList(
        pager: Flow<PagingData<TransactionHistoryItem>>,
        onClickTransaction: (TransactionUi) -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {
        val items: LazyPagingItems<TransactionHistoryItem> = pager.collectAsLazyPagingItems()

        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = {
                onPullToRefresh().run {
                    items.refresh()
                }
            }
        )

        Box(
            modifier = Modifier.pullRefresh(
                pullRefreshState,
                enabled = !isRefreshing
            ).fillMaxSize()
        ) {
            if (items.loadState.refresh != LoadStateLoading && items.loadState.append != LoadStateLoading && items.itemCount == 0) {
                TransactionHistoryEmptyState()
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = Dimensions.Padding.default,
                        end = Dimensions.Padding.default,
                        bottom = Dimensions.Padding.large
                    ),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    if (items.loadState.refresh == LoadStateLoading) {
                        item {
                            Text(MR.strings.all_loading_normal.desc().localized()) // temp message, no need for localization
                        }
                        Napier.d(
                            message = "items.loadState.refresh in lazycolumn  = ${items.loadState.refresh}",
                            tag = "refresh"
                        )
                    }

                    items(
                        count = items.itemCount,
                        key = {
                            when (val item = items[it]) {
                                is TransactionHistoryItem.TransactionItem -> item.transaction.txHash
                                is TransactionHistoryItem.HeaderItem -> item.hashCode()
                                else -> it.hashCode()
                            }
                        }
                    ) { index ->
                        val previousItem = if (index > 0) items[index - 1] else null
                        val nextItem = if (index < items.itemCount - 1) items[index + 1] else null

                        val currentItem = items[index]

                        val roundedCornersShape = when {
                            (previousItem == null || previousItem is TransactionHistoryItem.HeaderItem) &&
                                    (nextItem == null || nextItem is TransactionHistoryItem.HeaderItem) -> RoundedCornerShape(
                                CornerRadius.Small
                            )

                            previousItem == null || previousItem is TransactionHistoryItem.HeaderItem -> RoundedCornerShape(
                                topStart = CornerRadius.Small,
                                topEnd = CornerRadius.Small
                            )

                            nextItem == null || nextItem is TransactionHistoryItem.HeaderItem -> RoundedCornerShape(
                                bottomStart = CornerRadius.Small,
                                bottomEnd = CornerRadius.Small
                            )

                            else -> RoundedCornerShape(0.dp)
                        }

                        when (currentItem) {
                            is TransactionHistoryItem.TransactionItem -> {
                                TransactionHistoryItem(
                                    currentItem.transaction,
                                    roundedCornersShape,
                                    onClick = {
                                        onClickTransaction(it)
                                    },
                                )
                            }

                            is TransactionHistoryItem.HeaderItem -> {
                                TransactionHistoryHeaderItem(currentItem)
                            }

                            else -> {}
                        }
                    }

                    if (items.loadState.append == LoadStateLoading) {
                        item {
                            Text(MR.strings.all_loading_normal.desc().localized()) // temp message, no need for localization
                        }
                    }
                }
                PullRefreshIndicator(
                    isRefreshing,
                    pullRefreshState,
                    Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    @Composable
    fun TransactionHistoryEmptyState(
    ) {
        val state = rememberScrollState()
        Box(Modifier.fillMaxSize().verticalScroll(state)) {
            MaxSizeColumn(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().align(Alignment.Center)
            ) {
                Image(
                    painterResource(MR.images.transaction_history_empty),
                    null,
                    modifier = Modifier.size(213.dp, 178.dp)
                )
                Spacer(Modifier.height(Spacing.SMALL))
                TextDescription2(
                    text = MR.strings.title_transaction_history_no_transactions.desc().localized(),
                    color = Color.Black
                )
            }
        }
    }

    @Composable
    fun TransactionHistoryItem(
        item: TransactionUi,
        roundedCornerShape: Shape,
        onClick: (TransactionUi) -> Unit
    ) {
        with(item) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(roundedCornerShape)
                    .clickable { onClick(item) }
                    .background(Color(0xFFFAFAFA))
                    .padding(Dimensions.Padding.default),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    val (icon, title, subtitle) = when (transactionType) {
                        TransactionType.SEND -> {
                            Triple(
                                MangalaWalletPack.TxSend,
                                MR.strings.all_send.desc().localized(),
                                MR.strings.message_transaction_history_to.format(formattedAddress)
                                    .localized()
                            )
                        }

                        TransactionType.RECEIVE -> {
                            Triple(
                                MangalaWalletPack.TxReceive,
                                MR.strings.all_receive.desc().localized(),
                                MR.strings.message_transaction_history_from.format(formattedAddress)
                                    .localized()
                            )
                        }

                        TransactionType.SWAP -> {
                            Triple(
                                MangalaWalletPack.TxSwap,
                                MR.strings.all_swap.desc().localized(),
                                MR.strings.message_transaction_history_from.format(formattedAddress)
                                    .localized()
                            )
                        }

                        TransactionType.CONTRACT_CALL -> {
                            Triple(
                                MangalaWalletPack.Document,
                                MR.strings.all_smart_contract_call.desc().localized(),
                                MR.strings.message_transaction_history_to.format(formattedAddress)
                                    .localized()
                            )
                        }

                        TransactionType.CONTRACT_DEPLOYMENT -> {
                            Triple(
                                MangalaWalletPack.Document,
                                MR.strings.all_contract_deployment.desc().localized(),
                                MR.strings.message_transaction_history_from.format(formattedAddress)
                                    .localized()
                            )
                        }
                    }
                    Icon(icon, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(Spacing.TINY))
                    Column {
                        TextDescription2(
                            title,
                            color = Colors.main1Text,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(Spacing.TINY))
                        TextTiny(subtitle, color = Colors.caption)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    TextDescription2(
                        amount,
                        fontWeight = FontWeight.Medium,
                        color = when (transactionType) {
                            TransactionType.SEND, TransactionType.CONTRACT_DEPLOYMENT, TransactionType.CONTRACT_CALL -> Colors.coral
                            else -> Colors.green
                        }
                    )
                    Spacer(Modifier.height(Spacing.TINY))
                    TextTiny(item.formattedDate, color = Colors.caption)
                }
            }
        }
    }

    @Composable
    fun TransactionHistoryHeaderItem(item: TransactionHistoryItem.HeaderItem) {
        MaxWidthColumn(
            Modifier.padding(
                top = Dimensions.Padding.large,
                bottom = Dimensions.Padding.half
            )
        ) {
            val text = when (item) {
                is TransactionHistoryItem.HeaderItem.Date -> {
                    item.date
                }

                is TransactionHistoryItem.HeaderItem.Today -> {
                    MR.strings.message_transaction_history_date_today.desc().localized()
                }

                else -> {
                    MR.strings.message_transaction_history_date_yesterday.desc().localized()
                }
            }
            TextDescription2(text, color = Colors.main1Text, fontWeight = FontWeight.Medium)
        }
    }
}