package com.mangala.wallet.features.portfolio.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.chart.Chart
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.formattedBalance
import com.mangala.wallet.model.token.domain.formattedValue
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Ram
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Search
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionHistory
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.BasicTextFieldWithHint
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.VisibilityToggleIconButton
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.imageloader.MultiImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.onClickIfNotLoading
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class PortfolioScreen(
    private val accountId: String,
    private val address: String,
    private val networkType: NetworkType,
    private val initialAccountName: String
) : BaseScreen<PortfolioScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.PORTFOLIO
    override val screenClassName: String = PortfolioScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): PortfolioScreenModel = getScreenModel(parameters = {
        parametersOf(
            accountId,
            networkType.name,
            address,
            initialAccountName
        )
    })

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: PortfolioScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current

        val uiState by screenModel.uiState.collectAsStateMultiplatform()

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            )
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            WalletDetailScreen(
                uiState = uiState,
                onBackClicked = {
                    navigator.pop()
                },
                onClickBalanceVisible = {
                    screenModel.toggleBalanceVisible(it)
                },
                onToggleHideZeroBalances = {
                    screenModel.toggleHideZeroBalances(it)
                },
                onChangeTokenQuery = {
                    screenModel.onChangeTokenQuery(it)
                },
                onClickSend = {
//                    val screen = ScreenRegistry.get(
//                        SharedScreen.SelectRecipientTypeScreen(
//                            accountId = accountId,
//                            networkType = networkType.name
//                        )
//                    )
//                    bottomSheetNavigator.show(screen)
                    globalNavigator.push(ScreenRegistry.get(SharedScreen.ContactListScreen))
                },
                onClickReceive = {
                    val receiveTokenScreen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                            accountId,
                            address = null,
                            networkType,
                            initialBlockchainUid = null
                        )
                    )
                    navigator.push(receiveTokenScreen)
                },
                onClickSwap = {
                    val swapScreen = ScreenRegistry.get(
                        SharedScreen.SwapTokenScreen
                    )
                    navigator.push(swapScreen)
                },
                onClickTransactionHistory = {
                    val screen = ScreenRegistry.get(
                        when (networkType) {
                            NetworkType.ANTELOPE -> SharedScreen.TransactionHistoryAntelopeScreen(
                                address
                            )

                            NetworkType.EVM -> SharedScreen.TransactionHistoryScreen(accountId)

                            NetworkType.BITCOIN -> SharedScreen.TransactionHistoryBitcoinScreen(address, (uiState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel?.networkSelected?.blockChainUid.orEmpty())

                            else -> throw UnsupportedOperationException("Unsupported network type: $networkType")
                        }
                    )
                    navigator.push(screen)
                },
                isRefreshing = screenModel.isLoading.value,
                onPullToRefresh = {
                    screenModel.pullToRefresh()
                }
            )
        }
    }


    @Composable
    fun WalletDetailScreen(
        uiState: PortfolioScreenUiState,
        onBackClicked: () -> Unit,
        onClickBalanceVisible: (Boolean) -> Unit,
        onToggleHideZeroBalances: (Boolean) -> Unit,
        onChangeTokenQuery: (String) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: () -> Unit,
        onClickSwap: () -> Unit,
        onClickTransactionHistory: () -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit

    ) {
        WalletDetailScreenSuccessState(
            uiState,
            onBackClicked,
            onClickBalanceVisible = onClickBalanceVisible,
            onToggleHideZeroBalances = onToggleHideZeroBalances,
            onChangeTokenQuery = onChangeTokenQuery,
            onClickSend = onClickSend,
            onClickReceive = onClickReceive,
            onClickSwap = onClickSwap,
            onClickTransactionHistory = onClickTransactionHistory,
            isRefreshing = isRefreshing,
            onPullToRefresh = onPullToRefresh
        )
    }

    @OptIn(
        ExperimentalMaterialApi::class,
    )
    @Composable
    private fun WalletDetailScreenSuccessState(
        uiState: PortfolioScreenUiState,
        onBackClicked: () -> Unit,
        onClickBalanceVisible: (Boolean) -> Unit,
        onToggleHideZeroBalances: (Boolean) -> Unit,
        onChangeTokenQuery: (String) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: () -> Unit,
        onClickSwap: () -> Unit,
        onClickTransactionHistory: () -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        val isLoading =
            (uiState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel?.isLoading ?: false
        val uiModel = (uiState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel

        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = {
                onPullToRefresh()
            }
        )

        val focusRequester = remember { FocusRequester() }
        var queryTextFieldExpanded by remember { mutableStateOf(false) }

        MaxSizeBox(
            Modifier.clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {
                keyboardController?.hide()
                focusManager.clearFocus()
                queryTextFieldExpanded = false
            }
        ) {
            MaxSizeColumn(Modifier
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .statusBarsPadding()
            ) {
                PortfolioAppBar(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.mangalaColors.bgInnerCard)
                        .padding(Dimensions.Padding.default),
                    onBackClicked,
                    uiModel,
                    onClickTransactionHistory,
                    isLoading
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.mangalaColors.bgInnerCard)
                        .pullRefresh(pullRefreshState, enabled = !isRefreshing),
                    contentPadding = PaddingValues(bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding())
                ) {
                    item(key = "account_overview") {
                        AccountOverviewItem(
                            uiState = uiState,
                            onClickBalanceVisible = onClickBalanceVisible,
                            onClickSend = onClickSend,
                            onClickReceive = onClickReceive,
                            onClickSwap = onClickSwap,
                            isLoading = isLoading
                        )
                    }
                    item(key = "account_overview_list_spacer") {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = Dimensions.Padding.half)
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Spacing.XTINY)
                                    .background(MaterialTheme.mangalaColors.border)
                            )
                        }
                    }
                    stickyHeader(key = "balances_header") {
                        BalancesHeaderItem(
                            uiState = uiState,
                            onToggleHideZeroBalance = onToggleHideZeroBalances,
                            onChangeTokenQuery = onChangeTokenQuery,
                            isLoading = isLoading,
                            focusRequester,
                            queryTextFieldExpanded,
                            onClickSearchButton = {
                                queryTextFieldExpanded = true
                            }
                        )
                    }

                    when (uiModel) {
                        is PortfolioScreenUiModel.Evm -> {
                            item(key = "evm_token_items") {
                                EvmTokenItems(uiModel, isLoading)
                            }
                        }

                        is PortfolioScreenUiModel.Antelope -> {
                            item(key = "antelope_token_items") {
                                AntelopeTokenItems(uiModel, isLoading)
                            }
                        }

                        is PortfolioScreenUiModel.Bitcoin -> {
                            item(key = "bitcoin_token_items") {
                                BitcoinTokenItems(uiModel, isLoading)
                            }
                        }

                        null -> {}
                    }
                }
            }

            MaxWidthBox(
                modifier = Modifier.pullRefresh(pullRefreshState),
                contentAlignment = Alignment.Center
            ) {
                PullRefreshIndicator(
                    isRefreshing,
                    pullRefreshState,
                )
            }
        }
    }

    @Composable
    private fun PortfolioAppBar(
        modifier: Modifier,
        onBackClicked: () -> Unit,
        uiModel: PortfolioScreenUiModel?,
        onClickTransactionHistory: () -> Unit,
        isLoading: Boolean
    ) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    modifier = Modifier.size(Dimensions.IconButtonSize),
                    onClick = onBackClicked
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.ArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.mangalaColors.iconPrimary
                    )
                }
                Spacer(Modifier.width(Spacing.TINY))
                val accountName = when (uiModel) {
                    is PortfolioScreenUiModel.Antelope -> {
                        uiModel.accountName
                    }

                    is PortfolioScreenUiModel.Evm -> {
                        uiModel.accountName
                    }

                    else -> ""
                }

                Text(
                    text = accountName ?: "",
                    fontSize = FontType.REGULAR,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.mangalaColors.textPrimary
                )
            }
            IconButton(
                modifier = Modifier.size(Dimensions.TopBarTrailingIconSize),
                onClick = onClickTransactionHistory,
                enabled = isLoading.not()
            ) {
                Icon(
                    imageVector = MangalaWalletPack.TransactionHistory,
                    contentDescription = null,
                    tint = MaterialTheme.mangalaColors.iconPrimary,
                )
            }
        }
    }

    @Composable
    fun AntelopeTokenItems(
        uiModel: PortfolioScreenUiModel.Antelope?,
        isLoading: Boolean,
    ) {
        if (isLoading) {
            repeat(SKELETON_ITEMS_COUNT) {
                TokenItemAntelope(
                    imageSource = null,
                    contractSymbol = "",
                    contractName = "",
                    tokenFormattedBalance = "",
                    tokenFormattedValue = "",
                    balanceVisible = false,
                    isLoading = isLoading,
                    sparklineData = null,
                    priceChangePercentage24h = null
                )
            }
        } else {
            val assets = uiModel?.filteredTokenBalances
            assets?.forEach { token ->
                when (token) {
                    is AntelopeAssetsUiModel.RamBalanceUiModel -> {
                        uiModel.account?.let {
                            TokenItemAntelope(
                                imageSource = ImageSource.Vector(MangalaWalletPack.Ram),
                                contractSymbol = token.symbol,
                                contractName = token.name,
                                tokenFormattedBalance = it.ramBalanceFormatted,
                                tokenFormattedValue = token.formattedPrice,
                                isLoading = isLoading,
                                balanceVisible = uiModel.isBalanceVisible,
                                sparklineData = token.sparkline,
                                priceChangePercentage24h = token.priceChangePercentage24h
                            )
                        }
                    }

                    is AntelopeAssetsUiModel.TokenBalanceUiModel -> {
                        TokenItemAntelope(
                            imageSource = token.iconResource,
                            contractSymbol = token.symbol,
                            contractName = token.name,
                            tokenFormattedBalance = "${token.balance?.formatCompact()} ${token.symbol}",
                            tokenFormattedValue = token.formattedPrice,
                            isLoading = isLoading,
                            balanceVisible = uiModel.isBalanceVisible,
                            sparklineData = token.sparkline,
                            priceChangePercentage24h = token.priceChangePercentage24h
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun EvmTokenItems(uiModel: PortfolioScreenUiModel.Evm?, isLoading: Boolean) {
        if (isLoading) {
            repeat(SKELETON_ITEMS_COUNT) {
                TokenItemEvm(
                    token = null,
                    balanceVisible = false,
                    currencySymbol = "",
                    isLoading
                )
            }
        } else {
            uiModel?.filteredTokenBalances?.let {
                Column {
                    uiModel.filteredTokenBalances.forEach { tokenBalanceModel ->
                        TokenItemEvm(
                            token = tokenBalanceModel,
                            balanceVisible = uiModel.isBalanceVisible,
                            currencySymbol = uiModel.currencySymbol,
                            isLoading
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun BitcoinTokenItems(uiModel: PortfolioScreenUiModel.Bitcoin?, isLoading: Boolean) {
        if (isLoading) {
            repeat(SKELETON_ITEMS_COUNT) {
                TokenItemBitcoin(
                    token = null,
                    balanceVisible = false,
                    currencySymbol = "",
                    isLoading
                )
            }
        } else {
            uiModel?.filteredTokenBalances?.let {
                Column {
                    uiModel.filteredTokenBalances.forEach { tokenBalanceModel ->
                        TokenItemBitcoin(
                            token = tokenBalanceModel,
                            balanceVisible = uiModel.isBalanceVisible,
                            currencySymbol = uiModel.currencySymbol,
                            isLoading
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountOverviewItem(
        uiState: PortfolioScreenUiState,
        onClickBalanceVisible: (Boolean) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: () -> Unit,
        onClickSwap: () -> Unit,
        isLoading: Boolean
    ) {
        val uiModel = (uiState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel

        Column(Modifier.padding(Dimensions.Padding.default)) {
            TitleRow(uiModel, isLoading, onClickBalanceVisible)
            Spacer(modifier = Modifier.height(Spacing.TINY))
            TotalBalanceText(uiModel, isLoading)
            if (networkType != NetworkType.ANTELOPE) {
                // Hide PnL for networks other than Antelope for now, as we haven't found a way to calc historical balance for an account
                Spacer(modifier = Modifier.height(Spacing.SMALL))
                PnlRow(uiModel, isLoading)
            }
            Spacer(modifier = Modifier.height(Spacing.BASE))
            ActionButtons(
                isLoading = isLoading,
                onClickSend = { onClickIfNotLoading(isLoading, onClickSend) },
                onClickReceive = { onClickIfNotLoading(isLoading, onClickReceive) },
                onClickSwap = {
                    onClickIfNotLoading(isLoading) {
                        when (uiModel) {
                            is PortfolioScreenUiModel.Evm -> onClickSwap()
                            is PortfolioScreenUiModel.Antelope -> Unit
                            else -> Unit
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun TitleRow(
        uiModel: PortfolioScreenUiModel?,
        isLoading: Boolean,
        onClickBalanceVisible: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = MR.strings.title_wallet_details_total_amount.desc().localized(),
                style = TextStyle(fontSize = FontType.REGULAR, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.mangalaColors.textPrimary
            )

            val isVisible: Boolean = when (uiModel) {
                is PortfolioScreenUiModel.Evm -> {
                    uiModel.isBalanceVisible
                }

                is PortfolioScreenUiModel.Antelope -> {
                    uiModel.isBalanceVisible
                }

                is PortfolioScreenUiModel.Bitcoin -> {
                    uiModel.isBalanceVisible
                }

                null -> {
                    false
                }
            }

            VisibilityToggleIconButton(
                isVisible = isVisible,
                onClickBalanceVisible = { onClickBalanceVisible(isVisible.not()) },
                iconButtonModifier = Modifier.size(Dimensions.ButtonIconSize),
                modifier = Modifier.size(Dimensions.ButtonIconSize),
                enabled = isLoading.not()
            )
        }
    }

    @Composable
    private fun TotalBalanceText(
        uiModel: PortfolioScreenUiModel?,
        isLoading: Boolean
    ) {
        val balanceText: String?
        val coreBalanceSymbol: String

        when (uiModel) {
            is PortfolioScreenUiModel.Evm -> {
                balanceText =
                    if (uiModel.isBalanceVisible) uiModel.totalValueFormatted else HIDDEN_BALANCE_STRING
                coreBalanceSymbol = ""
            }

            is PortfolioScreenUiModel.Antelope -> {
                balanceText = if (uiModel.isBalanceVisible) {
                    uiModel.account?.totalValueFormatted
                } else {
                    HIDDEN_BALANCE_STRING
                }
                coreBalanceSymbol = uiModel.account?.coreBalanceSymbol.orEmpty()
            }

            is PortfolioScreenUiModel.Bitcoin -> {
                balanceText = if (uiModel.isBalanceVisible) {
                    uiModel.totalValueFormatted
                } else {
                    HIDDEN_BALANCE_STRING
                }
                coreBalanceSymbol = ""
            }

            null -> {
                balanceText = ""
                coreBalanceSymbol = ""
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = balanceText
                    ?: HIDDEN_BALANCE_STRING, // When balanceText is null, show placeholder under skeleton
                style = TextStyle(fontSize = FontType.TITLE_2_36, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading || balanceText == null)
            )

            Spacer(Modifier.width(Spacing.TINY))

            TextDescription2(
                text = coreBalanceSymbol,
                fontSize = FontType.REGULAR,
                color = MaterialTheme.mangalaColors.textSecondary,
            )
        }
    }

    @Composable
    private fun PnlRow(
        uiModel: PortfolioScreenUiModel?,
        isLoading: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = MR.strings.title_wallet_details_pnl.desc().localized(),
                style = TextStyle(fontSize = FontType.SMALL, fontWeight = FontWeight.Normal),
                color = MaterialTheme.mangalaColors.textSecondary
            )
//            Spacer(modifier = Modifier.width(Spacing.XTINY))
//            Icon(
//                imageVector = MangalaWalletPack.Exclamation,
//                contentDescription = null,
//                modifier = Modifier.size(Dimensions.IconSizeNextToText),
//                tint = Colors.gray
//            )
        }
        Spacer(modifier = Modifier.height(Spacing.TINY))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.mangalaWalletPlaceholder(isLoading)
        ) {
            val (pnlText, pnlColor) = when (uiModel) {
                is PortfolioScreenUiModel.Evm -> {
                    val pnlText = when {
                        uiModel.isBalanceVisible -> uiModel.formattedPnl
                        else -> HIDDEN_BALANCE_STRING
                    }
                    val pnlColor = uiModel.pnlColor

                    pnlText to pnlColor
                }

                is PortfolioScreenUiModel.Antelope -> {
                    val assets = uiModel.account
                    val pnlText = when {
                        uiModel.isBalanceVisible -> assets?.formattedPnl ?: ""
                        else -> HIDDEN_BALANCE_STRING
                    }
                    val pnlColor = assets?.nativeCoinPnlColor ?: MaterialTheme.mangalaColors.textSecondary

                    pnlText to pnlColor
                }

                is PortfolioScreenUiModel.Bitcoin -> {
                    val pnlText = when {
                        uiModel.isBalanceVisible -> uiModel.formattedPnl
                        else -> HIDDEN_BALANCE_STRING
                    }
                    val pnlColor = uiModel.pnlColor

                    pnlText to pnlColor
                }

                null -> {
                    val pnlText = HIDDEN_BALANCE_STRING
                    val pnlColor = MaterialTheme.mangalaColors.textPrimary

                    pnlText to pnlColor
                }

            }
            Text(
                text = pnlText,
                style = TextStyle(
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Medium,
                    color = pnlColor
                ),
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
//            Spacer(modifier = Modifier.width(Spacing.XTINY))
//            Icon(
//                imageVector = MangalaWalletPack.ArrowRight,
//                contentDescription = null,
//                modifier = Modifier.size(Dimensions.IconSizeNextToText),
//                tint = Colors.gray
//            )
        }
    }

    @Composable
    private fun ActionButtons(
        isLoading: Boolean,
        onClickSend: () -> Unit,
        onClickReceive: () -> Unit,
        onClickSwap: (() -> Unit)?,
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MangalaOutlinedButtonNew(
                label = MR.strings.all_send.desc().localized(),
                onClick = onClickSend,
                enabled = isLoading.not(),
                size = MangalaButtonSize.Medium,
                modifier = Modifier
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(Spacing.SMALL))
            MangalaOutlinedButtonNew(
                label = MR.strings.all_receive.desc().localized(),
                onClick = onClickReceive,
                enabled = isLoading.not(),
                size = MangalaButtonSize.Medium,
                modifier = Modifier
                    .weight(1f)
            )
//            Temporary disable swap feature
//            onClickSwap?.let {
//                Spacer(modifier = Modifier.width(Spacing.SMALL))
//                OutlinedButton(
//                    modifier = Modifier.weight(1f).fillMaxWidth()
//                        .height(Dimensions.Height.xxxLarge),
//                    onClick = onClickSwap,
//                    colors = ButtonDefaults.buttonColors(
//                        backgroundColor = backgroundColor,
//                        contentColor = contentColor
//                    ),
//                    border = BorderStroke(1.dp, borderColor),
//                    shape = RoundedCornerShape(CornerRadius.Tiny),
//                    enabled = isLoading.not()
//                ) {
//                    Text(text = MR.strings.button_wallet_details_swap.desc().localized())
//                }
//            }
        }
    }

    @Composable
    private fun BalancesHeaderItem(
        uiState: PortfolioScreenUiState,
        onToggleHideZeroBalance: (Boolean) -> Unit,
        onChangeTokenQuery: (String) -> Unit,
        isLoading: Boolean,
        focusRequester: FocusRequester,
        queryTextFieldExpanded: Boolean,
        onClickSearchButton: () -> Unit
    ) {
        val uiModel = (uiState as? PortfolioScreenUiState.Data)?.portfolioScreenUiModel

        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.mangalaColors.bgInnerCard)
                .padding(
                    top = Dimensions.Padding.default,
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.half // Reduce padding so can add padding in hide zero balances button for bigger touch area
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = MR.strings.title_wallet_details_balances.desc().localized(),
                    style = TextStyle(
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier.weight(2f)
                )

                val searchBarActivatedModifier = Modifier
                    .border(
                        1.dp,
                        MaterialTheme.mangalaColors.border,
                        RoundedCornerShape(Dimensions.Height.ultraLarge)
                    )
                    .padding(
                        top = Dimensions.Padding.quarter,
                        bottom = Dimensions.Padding.quarter,
                        start = Dimensions.Padding.half
                    )
                val searchBarModifier =
                    if (queryTextFieldExpanded) searchBarActivatedModifier else Modifier.padding(
                        vertical = Dimensions.Padding.quarter
                    )

                Row(
                    Modifier
                        .then(searchBarModifier)
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier.size(Dimensions.ButtonIconSize),
                        onClick = {
                            onClickSearchButton()
                        },
                        enabled = isLoading.not()
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Search,
                            contentDescription = null,
                            tint = MaterialTheme.mangalaColors.iconPrimary,
                        )
                    }
                    LaunchedEffect(queryTextFieldExpanded) {
                        if (queryTextFieldExpanded) {
                            focusRequester.requestFocus()
                        }
                    }
                    AnimatedVisibility(queryTextFieldExpanded) {
                        Row {
                            Spacer(Modifier.width(Spacing.XTINY))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                                    .mangalaWalletPlaceholder(isLoading)
                            ) {
                                val tokenQuery = uiModel?.tokenQuery

                                BasicTextFieldWithHint(
                                    value = tokenQuery,
                                    hint = MR.strings.message_wallet_details_search_tokens_hint.desc()
                                        .localized(),
                                    onValueChange = onChangeTokenQuery,
                                    hintColor = MaterialTheme.mangalaColors.textSecondary,
                                    textColor = MaterialTheme.mangalaColors.textPrimary,
                                    boxModifier = Modifier.wrapContentWidth().weight(1f),
                                    textFieldModifier = Modifier.focusRequester(focusRequester)
                                )

                                if (tokenQuery?.isNotEmpty() == true) {
                                    IconButton(
                                        modifier = Modifier.size(Dimensions.ButtonIconSize)
                                            .padding(start = Dimensions.Padding.half),
                                        onClick = {
                                            onChangeTokenQuery("")
                                        }) {
                                        Icon(
                                            imageVector = MangalaWalletPack.Clear,
                                            contentDescription = null,
                                            tint = MaterialTheme.mangalaColors.iconSecondary,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            val hideZeroBalances = uiModel?.hideZeroBalances ?: false
            val enabled = isLoading.not()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = enabled) {
                        onToggleHideZeroBalance(!hideZeroBalances) // Toggle and pass the updated value
                    }
                    .padding(vertical = Dimensions.Padding.half)
            ) {
                CircleCheckBox(hideZeroBalances, enabled) {
                    onToggleHideZeroBalance(!hideZeroBalances) // Toggle and pass the updated value
                }
                Spacer(Modifier.width(Spacing.XTINY))
                Text(
                    text = MR.strings.message_wallet_details_hide_zero_balances.desc().localized(),
                    style = TextStyle(fontSize = FontType.MICRO_10, fontWeight = FontWeight.Normal),
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            }
        }
    }

    @Composable
    private fun CircleCheckBox(isChecked: Boolean, enabled: Boolean, onClick: () -> Unit) {
        if (isChecked)
            Icon(
                imageVector = MangalaWalletPack.Check,
                contentDescription = null,
                modifier = Modifier
                    .size(Dimensions.IconSize)
                    .clip(CircleShape)
                    .clickable(enabled = enabled, onClick = onClick)
                    .background(MaterialTheme.mangalaColors.bg)
                    .padding(Dimensions.Padding.quarter),
                tint = MaterialTheme.mangalaColors.iconSecondary
            )
        else
            Box(
                modifier = Modifier
                    .size(Dimensions.IconSize)
                    .clip(CircleShape)
                    .clickable(enabled = enabled, onClick = onClick)
                    .border(
                        1.dp,
                        MaterialTheme.mangalaColors.border,
                        CircleShape
                    )
                    .padding(Dimensions.Padding.quarter),
            )
    }

    @Composable
    fun TokenItemAntelope(
        imageSource: ImageSource?,
        contractSymbol: String,
        contractName: String,
        tokenFormattedBalance: String,
        tokenFormattedValue: String?,
        balanceVisible: Boolean,
        isLoading: Boolean,
        sparklineData: List<Double>?,
        priceChangePercentage24h: BigDecimal?,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                vertical = Dimensions.Padding.medium,
                horizontal = Dimensions.Padding.default
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    imageSource == ImageSource.Resource(resource = null) -> {
                        Spacer(Modifier.size(Dimensions.IconNormalSize))
                    }
                    imageSource != null -> {
                        MultiImage(
                            imageSource,
                            modifier = Modifier
                                .mangalaWalletPlaceholder(isLoading)
                                .size(Dimensions.IconNormalSize)
                                .clip(CircleShape)
                        )
                    }
                }
                Spacer(Modifier.width(Spacing.TINY))
                Column {
                    Text(
                        text = contractSymbol,
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(
                                Dimensions.Height.xxxxLarge,
                                Dimensions.Width.medium
                            )
                        )
                    )
                    Spacer(Modifier.height(Spacing.XTINY))
                    Text(
                        text = contractName,
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(
                                Dimensions.Height.xxxxLarge,
                                Dimensions.Width.medium
                            )
                        )
                    )
                }
            }

            if (sparklineData.isNullOrEmpty() && isLoading.not()) {
                Spacer(
                    Modifier.size(Dimensions.chartSizeItem).weight(1f)
                        .padding(horizontal = Dimensions.Padding.default)
                )
            } else {
                Chart(
                    modifier = Modifier.size(Dimensions.chartSizeItem).weight(1f)
                        .padding(horizontal = Dimensions.Padding.default),
                    chartModifier = Modifier.fillMaxWidth().height(20.dp),
                    items = sparklineData,
                    priceChangePercentage = priceChangePercentage24h,
                    showPriceChangePercentage = false,
                    isLoading = isLoading
                )
            }

            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    text = if (balanceVisible) tokenFormattedBalance else HIDDEN_BALANCE_STRING,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.mangalaWalletPlaceholder(
                        isLoading,
                        modifier = Modifier.size(
                            Dimensions.Height.xxxxxxLarge,
                            Dimensions.Width.medium
                        )
                    )
                )
                Spacer(Modifier.height(Spacing.TINY))
                Text(
                    text = if (balanceVisible && tokenFormattedValue != null) tokenFormattedValue else HIDDEN_BALANCE_STRING,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.mangalaWalletPlaceholder(
                        isLoading || tokenFormattedValue == null,
                        modifier = Modifier.size(
                            Dimensions.Height.xxxLargePlus,
                            Dimensions.Width.mediumLarge
                        )
                    )
                )
            }
        }
    }

    @Composable
    fun TokenItemEvm(
        token: TokenBalanceModel?,
        balanceVisible: Boolean,
        currencySymbol: String,
        isLoading: Boolean
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                vertical = Dimensions.Padding.small,
                horizontal = Dimensions.Padding.default
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (token != null) {
                    LocalImage(
                        modifier = Modifier.size(Dimensions.IconNormalSize).clip(CircleShape)
                            .mangalaWalletPlaceholder(
                                isLoading,
                                modifier = Modifier.size(Dimensions.IconNormalSize)
                            ),
                        token.localImage,
                        isLoading = isLoading,
                        placeholderModifier = Modifier.size(Dimensions.IconNormalSize)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        token?.contractSymbol.orEmpty(),
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(53.dp, 20.dp)
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        token?.contractName.orEmpty(),
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(53.dp, 20.dp)
                        )
                    )
                }
            }

            if (token?.sparklineIn7d?.price.isNullOrEmpty() && isLoading.not()) {
                Spacer(
                    Modifier.width(70.dp).weight(1f)
                        .padding(horizontal = Dimensions.Padding.default)
                )
            } else {
                Chart(
                    modifier = Modifier.size(Dimensions.chartSizeItem).weight(1f)
                        .padding(horizontal = Dimensions.Padding.default),
                    chartModifier = Modifier.fillMaxWidth().height(20.dp),
                    token,
                    isLoading = isLoading
                )
            }

            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    if (balanceVisible) token?.formattedBalance()
                        .orEmpty() else HIDDEN_BALANCE_STRING,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.mangalaWalletPlaceholder(
                        isLoading,
                        modifier = Modifier.size(79.dp, 20.dp)
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (balanceVisible) token?.formattedValue(currencySymbol)
                        .orEmpty() else HIDDEN_BALANCE_STRING,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.mangalaWalletPlaceholder(
                        isLoading,
                        modifier = Modifier.size(46.dp, 18.dp)
                    )
                )
            }
        }
    }
    
    @Composable
    fun TokenItemBitcoin(
        token: TokenBalanceModel?,
        balanceVisible: Boolean,
        currencySymbol: String,
        isLoading: Boolean
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                vertical = Dimensions.Padding.small,
                horizontal = Dimensions.Padding.default
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (token != null) {
                    LocalImage(
                        modifier = Modifier.size(Dimensions.IconNormalSize).clip(CircleShape),
                        token.localImage,
                        isLoading = isLoading,
                        placeholderModifier = Modifier.size(Dimensions.IconNormalSize)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        token?.contractSymbol.orEmpty(),
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(53.dp, 20.dp)
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        token?.contractName.orEmpty(),
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(53.dp, 20.dp)
                        )
                    )
                }
            }

            if (token?.sparklineIn7d?.price.isNullOrEmpty() && isLoading.not()) {
                Spacer(
                    Modifier.width(70.dp).weight(1f)
                        .padding(horizontal = Dimensions.Padding.default)
                )
            } else {
                Chart(
                    modifier = Modifier.size(Dimensions.chartSizeItem).weight(1f)
                        .padding(horizontal = Dimensions.Padding.default),
                    chartModifier = Modifier.fillMaxWidth().height(20.dp),
                    token,
                    isLoading = isLoading
                )
            }

            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text(
                    if (balanceVisible) token?.formattedBalance()
                        .orEmpty() else HIDDEN_BALANCE_STRING,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.mangalaWalletPlaceholder(
                        isLoading,
                        modifier = Modifier.size(79.dp, 20.dp)
                    )
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (balanceVisible) token?.formattedValue(currencySymbol)
                        .orEmpty() else HIDDEN_BALANCE_STRING,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.mangalaWalletPlaceholder(
                        isLoading,
                        modifier = Modifier.size(46.dp, 18.dp)
                    )
                )
            }
        }
    }
}

private const val SKELETON_ITEMS_COUNT = 6
