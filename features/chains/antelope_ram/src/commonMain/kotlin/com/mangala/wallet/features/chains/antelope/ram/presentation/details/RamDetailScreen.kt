package com.mangala.wallet.features.chains.antelope.ram.presentation.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.antelope.base.model.SamplingInterval
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryHeaderItem
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionHistoryItemAntelope
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory.TransactionSummary
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Elevation
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowUp
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxSwap
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.component.VisibilityToggleIconButton
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.navigation.navigationResult
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class RamDetailScreen(
    private val accountName: String
) : BaseScreen<RamDetailScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_RAM_DETAILS
    override val screenClassName: String = RamDetailScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): RamDetailScreenModel = getScreenModel(parameters = {
        parametersOf(
            accountName
        )
    })

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: RamDetailScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        RamDetailScreenSuccessState(
            screenModel = screenModel,
            navigator = navigator,
            onBackClicked = { navigator.pop() },
            isRefreshing = screenModel.isLoading.value,
            onPullToRefresh = { screenModel.pullToRefresh() },
            onClickBalanceVisible = { screenModel.saveRamVisibleStatus(it) }
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun RamDetailScreenSuccessState(
        screenModel: RamDetailScreenModel,
        onBackClicked: () -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
        onClickBalanceVisible: (Boolean) -> Unit,
        navigator: Navigator
    ) {
        val uiModel by screenModel.uiState.collectAsStateMultiplatform()

        val navigationResult = navigator.navigationResult
        val shouldRefresh =
            navigationResult.getResult<Boolean>(SharedScreen.BuySellRamScreen::class.simpleName.toString())

        LaunchedEffect(shouldRefresh.value) {
            if (shouldRefresh.value == true) {
                screenModel.pullToRefresh()
            }
        }

        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = {
                onPullToRefresh()
            }
        )

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            )
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current
            MaxSizeBox(
                modifier = Modifier
                    .background(MaterialTheme.mangalaColors.bg)
                    .pullRefresh(pullRefreshState, enabled = !isRefreshing)
            ) {
                MaxSizeColumn(
                    modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
                ) {
                    MaxWidthColumn(Modifier.weight(1f)) {
                        MangalaWalletTopBarCenteredTitle(
                            title = MR.strings.title_ram_detail.desc().localized(),
                            onBackClicked = onBackClicked,
                            modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
                                .windowInsetsPadding(WindowInsets.statusBars)
                        )

                        LazyColumn(
                            modifier = Modifier.background(MaterialTheme.mangalaColors.bg)
                                .weight(1f),
                            contentPadding = PaddingValues(
                                bottom = Dimensions.Padding.half
                            )
                        ) {
                            item {
                                RamDetailOverviewItem(
                                    uiModel = uiModel,
                                    onClickBalanceVisible = { onClickBalanceVisible(it) },
                                    onClickButtonBuy = {
                                        val screen =
                                            ScreenRegistry.get(
                                                SharedScreen.BuySellRamScreen(
                                                    accountName = accountName,
                                                    isBuyRam = true
                                                )
                                            )
                                        navigator.push(screen)
                                    },
                                    onClickButtonSell = {
                                        val screen =
                                            ScreenRegistry.get(
                                                SharedScreen.BuySellRamScreen(
                                                    accountName = accountName,
                                                    isBuyRam = false
                                                )
                                            )
                                        navigator.push(screen)
                                    },
                                    onClickButtonTransfer = {
                                        val screen = ScreenRegistry.get(
                                            SharedScreen.RamTransferScreen(
                                                accountName = accountName
                                            )
                                        )
                                        navigator.push(screen)
                                    },
                                    onClickButtonGiftRam = {
                                        val screen = ScreenRegistry.get(
                                            SharedScreen.GiftRamScreen(
                                                accountName = accountName
                                            )
                                        )
                                        navigator.push(screen)
                                    },
                                    onClickButtonWRAM = {
                                    },
                                    onChangeRamUnit = { screenModel.onRamUnitChange(it) },
                                    isDevelopmentEnvironment = screenModel.isDevelopmentEnvironment()
                                )
                            }
                            item {
                                VerticalSpacer(
                                    Spacing.BASE,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.mangalaColors.bgInnerCard)
                                )
                                Spacer(Modifier.height(Dimensions.Padding.quarter))
                                VerticalSpacer(Spacing.SMALL)
                            }
                            if (uiModel.allRamActionsSorted == null) {
                                item {
                                    Column(
                                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                                    ) {
                                        repeat(10) {
                                            RamItemLoading()
                                        }
                                    }
                                }
                            } else {
                                val items = uiModel.allRamActionsSorted ?: emptyList()
                                items(
                                    count = items.size,
                                    key = { index ->
                                        when (val item = items[index]) {
                                            is TransactionHistoryItemAntelope.TransactionItem -> {
                                                item.listActionDataUiModel.actionDataUiModel.trxId
                                                    ?: item.listActionDataUiModel.actionDataUiModel.actionTraces.hashCode()
                                            }

                                            is TransactionHistoryItemAntelope.HeaderItem -> {
                                                item.hashCode().toString()
                                            }

                                            else -> "unknown_$index"
                                        }
                                    }
                                ) { index ->
                                    val previousItem = if (index > 0) items[index - 1] else null
                                    val nextItem =
                                        if (index < items.size - 1) items[index + 1] else null

                                    val currentItem = items[index]

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
                                                roundedCornersShape,
                                                Modifier.padding(horizontal = Dimensions.Padding.default)
                                            )
                                        }

                                        is TransactionHistoryItemAntelope.HeaderItem -> {
                                            TransactionHistoryHeaderItem(
                                                currentItem,
                                                paddingHorizontal = Dimensions.Padding.default
                                            )
                                        }

                                        else -> {}
                                    }
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = Elevation.Medium,
                        color = MaterialTheme.mangalaColors.bgInnerCard
                    ) {
                        ContentSheetBottomCollapsed(
                            false, // TODO: Infer from uiModel instead
                            isClickIcon = {
                                val screen = ScreenRegistry.get(
                                    SharedScreen.ChartRamScreen(
                                        isLoading = false, // TODO: Infer from uiModel instead
                                        ramCurrency = uiModel.nativeCoinSymbol,
                                        ramPrice = uiModel.ramPrice.toString(),
                                        pnlPercent = uiModel.pnlStringPercent.orEmpty(),
                                        pnlColor = uiModel.pnlColor
                                    )
                                )
                                bottomSheetNavigator.show(screen)
                            },
                            uiModel = uiModel,
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        )
                    }
                }


                PullRefreshIndicator(
                    isRefreshing,
                    pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                        .padding(vertical = Dimensions.paddingRefreshingOffsetDefaultTop)
                )
            }
        }
    }

    @Composable
    fun RamDetailOverviewItem(
        uiModel: RamDetailUiModel?,
        onClickBalanceVisible: (Boolean) -> Unit,
        onClickButtonBuy: () -> Unit,
        onClickButtonSell: () -> Unit,
        onClickButtonTransfer: () -> Unit,
        onClickButtonGiftRam: () -> Unit,
        onClickButtonWRAM: () -> Unit,
        onChangeRamUnit: (RamDetailUiModel.RamUnit) -> Unit,
        isDevelopmentEnvironment: Boolean
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(horizontal = Dimensions.Padding.default)
        ) {
            Spacer(modifier = Modifier.height(Spacing.SMALL))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextDescription2(
                    text = MR.strings.title_wallet_main_total_amount.desc().localized(),
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textSecondary
                )
                Spacer(modifier = Modifier.width(Spacing.XTINY))
                VisibilityToggleIconButton(
                    uiModel?.showBalance == true,
                    onClickBalanceVisible = { onClickBalanceVisible((uiModel?.showBalance == true).not()) },
                    iconButtonModifier = Modifier.size(Dimensions.IconButtonSizeSmall),
                    modifier = Modifier.size(Dimensions.IconButtonSizeSmall),
                    enabled = uiModel?.showBalance != null
                )
            }
            Spacer(modifier = Modifier.height(Spacing.TINY))

            var isExpanded by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.Center
            ) {

                TextTitle4(
                    text = if (uiModel?.showBalance == true && uiModel.totalRam != null) uiModel.totalRam else HIDDEN_BALANCE_STRING,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier.mangalaWalletPlaceholder(uiModel?.totalRam == null)
                )
                Spacer(modifier = Modifier.width(Spacing.XTINY))
                TextDescription2(
                    text = uiModel?.ramUnitFormatted.orEmpty(),
                    fontSize = FontType.TINY,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    fontWeight = FontWeight.Normal
                )
                DropDownMenu(
                    isExpanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                    nativeCoinSymbol = uiModel?.nativeCoinSymbol.orEmpty(),
                    onChange = onChangeRamUnit,
                    isEnabled = uiModel?.totalRam != null
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.Padding.half))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                TextDescription2(
                    text = MR.strings.profit_and_loss.desc().localized(),
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textSecondary
                )
                Spacer(modifier = Modifier.width(Spacing.XTINY))
                TextDescription2(
                    text = uiModel?.pnlValueString ?: "placeholder",
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Normal,
                    color = uiModel?.pnlColor ?: MaterialTheme.mangalaColors.textSecondary,
                    modifier = Modifier.mangalaWalletPlaceholder(uiModel?.pnlValueString == null)
                )
            }
            Spacer(modifier = Modifier.height(Dimensions.Padding.default))
            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    ItemTotalRamDetail(
                        stringTitle = MR.strings.total_buy.desc().localized(),
                        stringEOS = uiModel?.totalBuyFormatted ?: "placeholder",
                        shape = RoundedCornerShape(topStart = Dimensions.Padding.small),
                        modifier = Modifier.weight(1f),
                        uiModel = uiModel,
                        isLoading = uiModel?.totalBuyFormatted == null
                    )
                    ItemTotalRamDetail(
                        stringTitle = MR.strings.total_sell.desc().localized(),
                        stringEOS = uiModel?.totalSellFormatted ?: "placeholder",
                        shape = RoundedCornerShape(topEnd = Dimensions.Padding.small),
                        modifier = Modifier.weight(1f),
                        uiModel = uiModel,
                        isLoading = uiModel?.totalSellFormatted == null
                    )
                }
                Row {
                    ItemTotalRamDetail(
                        stringTitle = MR.strings.total_fee.desc().localized(),
                        stringEOS = uiModel?.totalFeeFormatted ?: "placeholder",
                        shape = RoundedCornerShape(topStart = 0f),
                        modifier = Modifier.weight(1f),
                        uiModel = uiModel,
                        isLoading = uiModel?.totalFeeFormatted == null
                    )
                    ItemTotalRamDetail(
                        stringTitle = MR.strings.total_profit.desc().localized(),
                        stringEOS = uiModel?.profitFormatted ?: "placeholder",
                        shape = RoundedCornerShape(topStart = 0f),
                        modifier = Modifier.weight(1f),
                        uiModel = uiModel,
                        isLoading = uiModel?.profitFormatted == null
                    )
                }

                ItemTotalRamDetail(
                    stringTitle = MR.strings.title_ramDetail_dca.desc().localized(),
                    stringEOS = uiModel?.dcaValueFormatted ?: "placeholder",
                    shape = RoundedCornerShape(
                        bottomStart = Dimensions.Padding.small,
                        bottomEnd = Dimensions.Padding.small
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    uiModel = uiModel,
                    isLoading = uiModel?.dcaValueFormatted == null
                )
            }
            Spacer(modifier = Modifier.height(Spacing.SMALL))
            ButtonRowBuySell(
                isEnabled = uiModel?.account != null,
                onClickButtonBuy = {
                    onClickButtonBuy()
                },
                onClickButtonSell = {
                    onClickButtonSell()
                },
                onClickButtonTransfer = {
                    onClickButtonTransfer()
                },
                onClickButtonGiftRam = {
                    onClickButtonGiftRam()
                },
                onClickButtonWRAM = {
                    onClickButtonWRAM()
                },
            )
            //TODO: Leonard remove after have ui for gift ram
            if (isDevelopmentEnvironment) {
                println("=== Show gift ram button ===")
                VerticalSpacer(Spacing.TINY)
                MangalaGradientButton(
                    label = MR.strings.gift_ram.desc().localized(),
                    onClick = { onClickButtonGiftRam() },
                    size = MangalaButtonSize.Medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ItemTotalRamDetail(
    stringTitle: String,
    stringEOS: String,
    shape: RoundedCornerShape,
    modifier: Modifier,
    uiModel: RamDetailUiModel?,
    isLoading: Boolean
) {
    Column(
        modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.mangalaColors.border, shape = shape)
            .background(color = MaterialTheme.mangalaColors.bgInnerCard)
            .padding(horizontal = Dimensions.Padding.default, vertical = Dimensions.Padding.half)
    ) {
        TextDescription2(
            text = stringTitle,
            fontSize = FontType.TINY,
            color = MaterialTheme.mangalaColors.textSecondary,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.width(Spacing.XTINY))
        TextDescription2(
            text = if (uiModel?.showBalance == true) stringEOS else HIDDEN_BALANCE_STRING,
            fontSize = FontType.SMALL,
            color = MaterialTheme.mangalaColors.textPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.mangalaWalletPlaceholder(isLoading)
        )
    }
}

@Composable
fun RamItemLoading() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(vertical = Dimensions.Padding.small)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MangalaWalletPack.TxSwap,
                    contentDescription = null,
                    tint = Color(0xFF383838),
                    modifier = Modifier.mangalaWalletPlaceholder(true)
                )
                Spacer(Modifier.width(8.dp))
                TextDescription2(
                    text = MR.strings.buy_ram.desc().localized(),
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier.mangalaWalletPlaceholder(true)
                )
            }
            Spacer(Modifier.height(8.dp))
            TextDescription2(
                text = "16:00",
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.mangalaWalletPlaceholder(true)
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            TextDescription2(
                text = "+ 1000 kb",
                fontSize = FontType.SMALL,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.mangalaWalletPlaceholder(true)
            )
            Spacer(Modifier.height(8.dp))
            TextDescription2(
                text = "~100 EOS",
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.mangalaWalletPlaceholder(true)
            )
        }
    }
}

@Composable
fun ButtonRowTime(
    selectedInterval: SamplingInterval?,
    onClick: (samplingInterval: SamplingInterval) -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = Dimensions.Padding.default),
        horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY)
    ) {
        SamplingInterval.entries.forEachIndexed { index, interval ->
            TextButton(
                onClick = {
                    onClick(interval)
                },
                shape = RoundedCornerShape(CornerRadius.XTiny),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (selectedInterval == interval) MaterialTheme.mangalaColors.bgBadge else MaterialTheme.mangalaColors.bg,
                    disabledBackgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
                    disabledContentColor = MaterialTheme.mangalaColors.textSecondary,
                    contentColor = if (selectedInterval == interval) MaterialTheme.mangalaColors.textOnBadge else MaterialTheme.mangalaColors.textPrimary
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selectedInterval == interval) MaterialTheme.mangalaColors.bgBadge else MaterialTheme.mangalaColors.border
                ),
                modifier = Modifier.height(Dimensions.IconNormalSize).weight(1f),
                enabled = selectedInterval != null
            ) {
                Text(
                    text = interval.value,
                    fontSize = FontType.MICRO_10,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
fun ButtonRowBuySell(
    onClickButtonBuy: () -> Unit,
    onClickButtonSell: () -> Unit,
    onClickButtonTransfer: () -> Unit,
    onClickButtonGiftRam: () -> Unit,
    onClickButtonWRAM: () -> Unit,
    isEnabled: Boolean
) {
    MaxWidthRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
    ) {
        MangalaOutlinedButtonNew(
            label = MR.strings.buy_ram.desc().localized(),
            onClick = onClickButtonBuy,
            enabled = isEnabled,
            modifier = Modifier.weight(1f),
            size = MangalaButtonSize.Medium
        )
        MangalaOutlinedButtonNew(
            label = MR.strings.sell_ram.desc().localized(),
            onClick = onClickButtonSell,
            enabled = isEnabled,
            modifier = Modifier.weight(1f),
            size = MangalaButtonSize.Medium
        )
        MangalaOutlinedButtonNew(
            label = MR.strings.transfer_ram.desc().localized(),
            onClick = onClickButtonTransfer,
            enabled = isEnabled,
            modifier = Modifier.weight(1f),
            size = MangalaButtonSize.Medium
        )
    }
}


@Composable
fun ContentSheetBottomCollapsed(
    isLoading: Boolean,
    isClickIcon: () -> Unit,
    uiModel: RamDetailUiModel?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .padding(
                horizontal = Dimensions.Padding.default,
                vertical = Dimensions.Padding.small
            )
            .fillMaxWidth()
            .height(Dimensions.contentSheetBottomCollapsedHeight)
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TextDescription2(
                text = MR.strings.current_ram_prices.desc().localized(),
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
            Spacer(modifier = Modifier.height(Spacing.XTINY))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    Spacing.XTINY
                )
            ) {
                if (uiModel != null) {
                    TextDescription2(
                        text = uiModel.ramPriceFormatted,
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(isLoading)
                    )
                    TextDescription2(
                        text = uiModel.priceChangePercentageString.orEmpty(),
                        fontSize = FontType.TINY,
                        fontWeight = FontWeight.Normal,
                        color = uiModel.pnlColor,
                        modifier = Modifier.mangalaWalletPlaceholder(isLoading)
                    )
                }
            }
        }
        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically)
                .mangalaWalletPlaceholder(isLoading),
            onClick = { isClickIcon() }
        ) {
            Icon(
                modifier = Modifier.size(Dimensions.ButtonRamDetailIconSize),
                imageVector = MangalaWalletPack.ArrowUp,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconSecondary
            )
        }
    }
}

@Composable
fun DropDownMenu(
    isExpanded: Boolean,
    nativeCoinSymbol: String,
    onChange: (RamDetailUiModel.RamUnit) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    isEnabled: Boolean
) {

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            modifier = Modifier.padding(1.dp)
                .width(Dimensions.IconButtonSizeSmall)
                .height(Dimensions.IconButtonSizeSmall),
            onClick = { onExpandedChange(!isExpanded) },
            enabled = isEnabled
        ) {
            Icon(
                modifier = Modifier,
                imageVector = MangalaWalletPack.Dropdown,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconSecondary
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(MaterialTheme.mangalaColors.bgInnerCard)
        ) {
            RamDetailRamTypeDropDownItem(
                text = RamDetailUiModel.RamUnit.BYTES.unitString,
                onClick = {
                    onChange(RamDetailUiModel.RamUnit.BYTES)
                    onExpandedChange(false)
                }
            )
            RamDetailRamTypeDropDownItem(
                text = RamDetailUiModel.RamUnit.KB.unitString,
                onClick = {
                    onChange(RamDetailUiModel.RamUnit.KB)
                    onExpandedChange(false)
                }
            )
            RamDetailRamTypeDropDownItem(
                text = RamDetailUiModel.RamUnit.MB.unitString,
                onClick = {
                    onChange(RamDetailUiModel.RamUnit.MB)
                    onExpandedChange(false)
                }
            )
            RamDetailRamTypeDropDownItem(
                text = RamDetailUiModel.RamUnit.GB.unitString,
                onClick = {
                    onChange(RamDetailUiModel.RamUnit.GB)
                    onExpandedChange(false)
                }
            )
            RamDetailRamTypeDropDownItem(
                text = nativeCoinSymbol,
                onClick = {
                    onChange(RamDetailUiModel.RamUnit.NATIVE_COIN)
                    onExpandedChange(false)
                }
            )
        }
    }
}

@Composable
fun RamDetailRamTypeDropDownItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        onClick = onClick
    ) {
        TextDescription2(
            text = text,
            fontSize = FontType.SMALL,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.mangalaColors.textPrimary
        )
    }
}