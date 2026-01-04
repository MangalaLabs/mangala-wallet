package com.mangala.wallet.features.transactionhistory.presentation.antelope

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryHeaderItem
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryItemAntelope
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionSummary
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class TransactionHistoryAntelopeScreen(private val accountName: String) :
    BaseScreen<TransactionHistoryAntelopeScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.TRANSACTION_HISTORY
    override val screenClassName: String = TransactionHistoryAntelopeScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TransactionHistoryAntelopeScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                accountName
            )
        }
    )

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: TransactionHistoryAntelopeScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val listActions = screenModel.listActions.collectAsLazyPagingItems()

        LaunchedEffect(
            key1 = screenModel.startDateFilter.value,
            key2 = screenModel.endDateFilter.value
        ) {
            screenModel.refreshListActions()
        }

        BottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet
            )
        ) {
            TransactionHistoryScreen(
                listActions = listActions,
                onBackClicked = {
                    navigator.pop()
                },
                onFilterClicked = {
//                    bottomSheetNavigator.show(
//                        TransactionHistoryFilterAntelopeBottomSheetScreen(
//                            startDateFilter = screenModel.startDateFilter.value,
//                            endDateFilter = screenModel.endDateFilter.value,
//                            onConfirm = { startDateFilter, endDateFilter ->
//                                screenModel.onStartDateFilterSelected(
//                                    startDateFilter
//                                )
//                                screenModel.onEndDateFilterSelected(
//                                    endDateFilter
//                                )
//                                println("startDateFilter: $startDateFilter, endDateFilter: $endDateFilter")
//                            }
//                        )
//                    )
                },
                isRefreshing = screenModel.isRefreshing.value,
                onPullToRefresh = {
                    screenModel.pullToRefresh()
                }
            )
        }
    }

    @Composable
    fun TransactionHistoryScreen(
        listActions: LazyPagingItems<TransactionHistoryItemAntelope>,
        onBackClicked: () -> Unit,
        onFilterClicked: () -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {
        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
        ) {
            MangalaWalletTopBarCenteredTitle(
                title = MR.strings.title_transaction_history_transaction.desc().localized(),
                onBackClicked = onBackClicked,
                trailingButton = {
//                    MangalaWalletIconButton(
//                        icon = MangalaWalletPack.Filter,
//                        onClick = {
//                            onFilterClicked()
//                        }
//                    )
                },
            )
            TransactionHistoryList(listActions, isRefreshing, onPullToRefresh)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun TransactionHistoryList(
        listActions: LazyPagingItems<TransactionHistoryItemAntelope>,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {

        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = {
                onPullToRefresh().run {
                    listActions.refresh()
                }
            }
        )

        Box(
            modifier = Modifier.pullRefresh(
                pullRefreshState,
                enabled = !isRefreshing
            ).fillMaxSize()
        ) {
            if (listActions.loadState.refresh != LoadStateLoading && listActions.loadState.append != LoadStateLoading && listActions.itemCount == 0) {
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
                    items(
                        count = listActions.itemCount,
                        key = { index ->
                            when (val item = listActions[index]) {
                                is TransactionHistoryItemAntelope.TransactionItem -> item.listActionDataUiModel.actionDataUiModel.trxId
                                    ?: item.listActionDataUiModel.actionDataUiModel.actionTraces.hashCode()

                                is TransactionHistoryItemAntelope.HeaderItem -> item.hashCode()
                                    .toString()

                                else -> "unknown_$index"
                            }
                        }
                    ) { index ->
                        val previousItem = if (index > 0) listActions[index - 1] else null
                        val nextItem =
                            if (index < listActions.itemCount - 1) listActions[index + 1] else null

                        val currentItem = listActions[index]

                        val roundedCornersShape = when {
                            (previousItem == null || previousItem is TransactionHistoryItemAntelope.HeaderItem) &&
                                    (nextItem == null || nextItem is TransactionHistoryItemAntelope.HeaderItem) -> RoundedCornerShape(
                                CornerRadius.Small
                            )

                            previousItem == null || previousItem is TransactionHistoryItemAntelope.HeaderItem -> RoundedCornerShape(
                                topStart = CornerRadius.Small,
                                topEnd = CornerRadius.Small
                            )

                            nextItem == null || nextItem is TransactionHistoryItemAntelope.HeaderItem -> RoundedCornerShape(
                                bottomStart = CornerRadius.Small,
                                bottomEnd = CornerRadius.Small
                            )

                            else -> RoundedCornerShape(0.dp)
                        }

                        when (currentItem) {
                            is TransactionHistoryItemAntelope.TransactionItem -> {
                                TransactionSummary(
                                    currentItem.listActionDataUiModel,
                                    roundedCornersShape
                                )
                            }

                            is TransactionHistoryItemAntelope.HeaderItem -> {
                                TransactionHistoryHeaderItem(currentItem)
                            }

                            else -> {}
                        }
                    }

                    if (listActions.loadState.append == LoadStateLoading) {
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
    fun TransactionHistoryEmptyState() {
        Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
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
                    color = MaterialTheme.mangalaColors.textPrimary
                )
            }
        }
    }
}
