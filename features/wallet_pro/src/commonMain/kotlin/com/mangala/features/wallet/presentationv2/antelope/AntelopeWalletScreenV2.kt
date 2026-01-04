package com.mangala.features.wallet.presentationv2.antelope

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.SharedScreen
import com.mangala.features.wallet.presentationv2.antelope.components.*
import com.mangala.features.wallet.presentationv2.core.base.BaseWalletScreenV2
import com.mangala.features.wallet.presentationv2.core.common.components.WalletHeaderV2
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.component.MangalaBrandText
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics

import kotlinx.coroutines.delay

class AntelopeWalletScreenV2 : BaseWalletScreenV2<AntelopeWalletScreenModel>() {
    
    override val networkType = NetworkType.ANTELOPE
    override val screenName = MangalaAnalytics.Screens.WALLET
    override val screenClassName = AntelopeWalletScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): AntelopeWalletScreenModel {
        return getScreenModel()
    }
    
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: AntelopeWalletScreenModel) {
        val globalNavigator = LocalGlobalNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val isRefreshing by screenModel.isRefreshing.collectAsStateMultiplatform()

        val hasWallet = uiState.hasWallet

        if (hasWallet) {
            var contentVisible by remember { mutableStateOf(false) }
            var showAddAccountBottomSheet by remember { mutableStateOf(false) }
            var showAccountSwitchBottomSheet by remember { mutableStateOf(false) }
            var showNetworkSelectionBottomSheet by remember { mutableStateOf(false) }
            var showFilterBottomSheet by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(100)
                contentVisible = true
            }

            val pullRefreshState = rememberPullRefreshState(
                refreshing = isRefreshing,
                onRefresh = { screenModel.onRefresh() }
            )

            OnboardingGradientBackground(
                circleBackgroundEnabled = true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .statusBarsPadding()
                            .pointerInput(uiState.isSearchActive) {
                                if (uiState.isSearchActive) {
                                    detectTapGestures(
                                        onTap = { screenModel.onSearchToggled() }
                                    )
                                }
                            }
                    ) {
                        WalletHeaderV2(
                            selectedNetwork = uiState.selectedNetwork,
                            isConnected = uiState.isConnected,
                            notificationCount = uiState.notificationCount,
                            isDevelopmentEnvironment = uiState.isDevelopmentEnvironment,
                            onNotificationClick = { screenModel.onToggleAccountMode() },
                            onAddAccountClick = { showAddAccountBottomSheet = true },
                            onToggleAccountMode = { screenModel.onToggleAccountMode() },
                            onNetworkDropdownClick = {
                                showNetworkSelectionBottomSheet = true
                            }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = WalletThemeV2.Dimensions.paddingMedium),
                            verticalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingMedium)
                        ) {
                            AdaptivePortfolioHeader(
                                isSingleAccountMode = uiState.isSingleAccountMode,
                                balanceDecimals = uiState.selectedCoreBalanceUnit.decimal,
                                totalPortfolioBalance = uiState.accountValueInSelectedUnit,
                                totalPortfolioUsd = uiState.portfolioTotalUsd,
                                totalAccounts = uiState.portfolioAccounts.size,
                                accounts = uiState.portfolioAccounts,
                                activeAccountName = uiState.accountName,
                                totalPnlAmount = uiState.portfolioPnlAmount,
                                totalPnlAmountFormatted = uiState.portfolioPnlAmountFormatted,
                                totalPnlPercentage = uiState.portfolioPnlPercentage,
                                pnlColor = uiState.portfolioPnlColor,
                                isBalanceHidden = uiState.isPortfolioBalanceHidden,
                                fiatSymbol = uiState.fiatSymbol,
                                selectedCurrency = uiState.selectedCoreBalanceUnit,
                                coinGeckoExchangeRateData = uiState.coinGeckoExchangeRate?.data,
                                onToggleHideBalance = { screenModel.onTogglePortfolioHideBalance() },
                                onCurrencyClick = { screenModel.onPortfolioCurrencyClick() },
                                onCopyAccountName = { screenModel.onCopyAddress() },
                                onAccountClick = { showAccountSwitchBottomSheet = true },
                                onSelectCurrency = { screenModel.onSelectCurrency(it) }
                            )

                            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingXSmall))

                            AnimatedVisibility(
                                visible = contentVisible,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        WalletThemeV2.Animation.durationLong,
                                        delayMillis = WalletThemeV2.Animation.delayStep3
                                    )
                                ) +
                                        slideInVertically(
                                            initialOffsetY = { it / 6 },
                                            animationSpec = tween(
                                                WalletThemeV2.Animation.durationLong,
                                                delayMillis = WalletThemeV2.Animation.delayStep3
                                            )
                                        )
                            ) {
                                AntelopeQuickActionsRow(
                                    onSendClick = {
                                        val screen =
                                            ScreenRegistry.get(SharedScreen.ContactListScreen)
                                        navigator.push(screen)
                                    },
                                    onReceiveClick = {
                                        val screen = ScreenRegistry.get(
                                            SharedScreen.ReceiveTokenScreen(
                                                accountId = uiState.accountName,
                                                address = null,
                                                networkType = NetworkType.ANTELOPE,
                                                initialBlockchainUid = null
                                            )
                                        )
                                        navigator.push(screen)
                                    },
                                    onMyQRClick = {
                                        val networkType =
                                            uiState.selectedNetwork?.blockchainType?.networkType

                                        networkType?.let {
                                            this@AntelopeWalletScreenV2.scanQRCode.scanQRCode(
                                                scanQRCodeListener = object : ScanQRCodeListener {
                                                    override fun onScanQRCodeResult(result: String) {
                                                        onHandleQrCodeResult(
                                                            screenModel.onScanQrCodeResult(result),
                                                            navigator,
                                                            globalNavigator,
                                                            screenModel
                                                        )
                                                    }
                                                },
                                                currentAccountId = screenModel.getCurrentAccountId(),
                                                networkType = it,
                                                initialBlockchainUid = null,
                                            )
                                        }
                                    },
                                    onHistoryClick = {
                                        val screen = ScreenRegistry.get(
                                            SharedScreen.TransactionHistoryAntelopeScreen(
                                                accountName = uiState.accountName
                                            )
                                        )
                                        navigator.push(screen)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingSmall))

                            AnimatedVisibility(
                                visible = contentVisible,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        WalletThemeV2.Animation.durationLong,
                                        delayMillis = WalletThemeV2.Animation.delayStep5
                                    )
                                )
                            ) {
                                AggregatedTokenListSection(
                                    tokens = uiState.filteredAggregatedTokens,
                                    activeAccountTokens = uiState.filteredActiveAccountTokens,
                                    activeAccountName = uiState.accountName,
                                    isAggregatedView = uiState.isAggregatedTokenView,
                                    isBalanceHidden = uiState.isPortfolioBalanceHidden,
                                    isSearchActive = uiState.isSearchActive,
                                    searchQuery = uiState.searchQuery,
                                    selectedCurrency = uiState.selectedCoreBalanceUnit,
                                    exchangeRateData = uiState.coinGeckoExchangeRate?.data,
                                    onToggleView = { screenModel.onToggleTokenView() },
                                    onTokenClick = { token -> /* TODO: Navigate to token detail */ },
                                    onSearchClick = { screenModel.onSearchToggled() },
                                    onSearchQueryChanged = { query ->
                                        screenModel.onSearchQueryChanged(
                                            query
                                        )
                                    },
                                    onFilterClick = { showFilterBottomSheet = true }
                                )
                            }

                            // Bottom spacing
                            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.paddingXLarge))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = WalletThemeV2.Colors.secondaryBackground,
                        contentColor = WalletThemeV2.Colors.accentBlue
                    )
                }
            }

            if (showAddAccountBottomSheet) {
                AddAccountBottomSheet(
                    onCreateNewAccount = {
                        val screen = ScreenRegistry.get(SharedScreen.Step2SelectAccountNameScreenV2)
                        navigator.push(screen)
                    },
                    onImportPrivateKey = {
                        val screen = ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                        navigator.push(screen)
                    },
                    onDismiss = {
                        showAddAccountBottomSheet = false
                    }
                )
            }

            if (showAccountSwitchBottomSheet) {
                AccountSwitchBottomSheet(
                    accounts = uiState.portfolioAccounts,
                    activeAccountName = uiState.accountName,
                    selectedCurrency = uiState.selectedCoreBalanceUnit,
                    exchangeRateData = uiState.coinGeckoExchangeRate?.data,
                    onAccountSelected = { accountName ->
                        screenModel.onAccountSelected(accountName)
                        showAccountSwitchBottomSheet = false
                    },
                    onDismiss = {
                        showAccountSwitchBottomSheet = false
                    }
                )
            }

            if (showNetworkSelectionBottomSheet) {
                AntelopeNetworkSelectionBottomSheet(
                    availableNetworks = screenModel.getAvailableNetworks(),
                    selectedNetwork = uiState.selectedNetwork,
                    onNetworkSelected = { network ->
                        screenModel.onNetworkSelected(network)
                        showNetworkSelectionBottomSheet = false
                    },
                    onDismiss = {
                        showNetworkSelectionBottomSheet = false
                    }
                )
            }

            if (showFilterBottomSheet) {
                FilterBottomSheet(
                    currentOptions = uiState.filterOptions,
                    onFilterOptionsChanged = { filterOptions ->
                        screenModel.onFilterOptionsChanged(filterOptions)
                        showFilterBottomSheet = false
                    },
                    onDismiss = {
                        showFilterBottomSheet = false
                    }
                )
            }
        } else {
            NoWalletImportedState(navigator)
        }
    }
    
    override fun onNavigateToSend() {
    }
    
    override fun onNavigateToReceive() {
    }
    
    override fun onNavigateToHistory() {
    }
    
    override val isBottomBarVisible = true

    override fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: AntelopeWalletScreenModel
    ) {
        when (result) {
            QrCodeData.Login -> {
                // TODO Not handled scan QR code Login
            }

            is QrCodeData.Payment -> {
                val uiState = screenModel.uiState.value

                val step2Screen = ScreenRegistry.get(
                    SharedScreen.Step2SelectNetwork(
                        accountId = screenModel.getCurrentAccountId(),
                        networkType = uiState.selectedNetwork?.blockchainType?.networkType?.name.orEmpty(),
                        address = result.address
                    )
                )

                if (result.chain != null) {
                    val step3Screen = ScreenRegistry.get(
                        SharedScreen.Step3SelectAmountScreen(
                            accountId = screenModel.getCurrentAccountId(),
                            address = result.address,
                            blockchainUid = result.chain?.uid,
                            amount = result.amount,
                            contactId = null
                        )
                    )
                    globalNavigator.push(listOf(step2Screen, step3Screen))
                    return
                }

                globalNavigator.push(step2Screen)
            }

            is QrCodeData.AntelopeCreateAccountForFriend -> {
                val createAccountForFriendRequest =
                    (result.request as? CreateAccountForFriendRequest) ?: return
                val (accountName, activePublicKey, ownerPublicKey, blockchainUid) = createAccountForFriendRequest

                val screen = ScreenRegistry.get(
                    SharedScreen.CreateAccountForFriendScreen(
                        accountName,
                        activePublicKey,
                        ownerPublicKey,
                        blockchainUid
                    )
                )
                navigator.push(screen)
            }

            QrCodeData.WalletConnect -> {
                // TODO Not implemented scan QR code WalletConnect
            }

            null -> {
                // TODO Not handled scan invalid QR code
            }

            is QrCodeData.NotSignedTransaction -> {
                // TODO
            }

            is QrCodeData.SignedTransaction -> {
                // TODO
            }

            is QrCodeData.SyncAccount -> {
                // TODO
            }

            is QrCodeData.Esr -> {
                val screen = ScreenRegistry.get(SharedScreen.EsrScreen(result.esrUri))
                navigator.push(screen)
            }

            is QrCodeData.ImportAccount -> {
                val screen =
                    ScreenRegistry.get(SharedScreen.AntelopeImportAccountScreen(result.privateKey))
                navigator.push(screen)
            }

            is QrCodeData.AntelopeKeyCert -> {
                val screen =
                    ScreenRegistry.get(SharedScreen.ImportAccountByKeyCertScreen(result.keyCert))
                navigator.push(screen)
            }
        }
    }
}

@Composable
private fun NoWalletImportedState(navigator: Navigator) {
    OnboardingGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.1f))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp), // Apply padding inside each page
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LocalImage(
                    imageResource = MR.images.character,
                    modifier = Modifier.size(200.dp) // Slightly larger for better visual impact
                )
            }

            Spacer(modifier = Modifier.height(48.dp)) // Optimized spacing between image and text

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                MangalaBrandText(
                    fullText = "Welcome to Mangala",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 39.2.sp,
                    letterSpacing = (-0.28).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "To get started, create a new wallet or import an existing one.",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFD1D1D1),
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.17).sp,
                    lineHeight = 23.8.sp,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))

            Spacer(modifier = Modifier.height(32.dp)) // Space between content and buttons

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp) // Add horizontal padding for buttons
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Tighter button spacing
            ) {
                MangalaGradientButton(
                    label = "Create a new wallet",
                    onClick = {
                        val antelopeCreateAccountScreen =
                            ScreenRegistry.get(SharedScreen.AntelopeCreateAccountV2Screen)
                        navigator.push(antelopeCreateAccountScreen)
                    },
                    buttonStyle = MangalaButtonStyle.GRADIENT,
                    modifier = Modifier.fillMaxWidth()
                )
                MangalaGradientButton(
                    label = "I already have a wallet",
                    onClick = {
                        val importPrivateKeyScreen =
                            ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                        navigator.push(importPrivateKeyScreen)
                    },
                    buttonStyle = MangalaButtonStyle.TRANSPARENT,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}