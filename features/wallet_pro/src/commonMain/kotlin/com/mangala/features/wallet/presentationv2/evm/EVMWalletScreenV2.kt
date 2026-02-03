package com.mangala.features.wallet.presentationv2.evm

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.features.wallet.presentationv2.core.base.BaseWalletScreenV2
import com.mangala.features.wallet.presentationv2.core.common.components.WalletHeaderV2
import com.mangala.features.wallet.presentationv2.evm.components.EVMAccountSwitchBottomSheet
import com.mangala.features.wallet.presentationv2.evm.components.EVMAddAccountBottomSheet
import com.mangala.features.wallet.presentationv2.evm.components.EVMPortfolioHeader
import com.mangala.features.wallet.presentationv2.evm.components.EVMQuickActionsRow
import com.mangala.features.wallet.presentationv2.evm.components.EVMTokenListSection
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.component.MangalaBrandText
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay

/**
 * EVM Wallet Screen V2
 * Follows the same pattern as AntelopeWalletScreenV2 but adapted for EVM networks
 * Key differences:
 * - Shows truncated wallet addresses (0x1234...abcd) instead of account names
 * - Uses local balance calculation (no server-side portfolio API for EVM yet)
 * - Supports HD wallet account switching
 */
class EVMWalletScreenV2 : BaseWalletScreenV2<EVMWalletScreenModel>() {

    override val networkType = NetworkType.EVM
    override val screenName = MangalaAnalytics.Screens.WALLET
    override val screenClassName = EVMWalletScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): EVMWalletScreenModel {
        return getScreenModel()
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: EVMWalletScreenModel) {
        val globalNavigator = LocalGlobalNavigator.current
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val isRefreshing by screenModel.isRefreshing.collectAsStateMultiplatform()

        val hasWallet = uiState.hasWallet

        if (hasWallet) {
            var contentVisible by remember { mutableStateOf(false) }
            var showAddAccountBottomSheet by remember { mutableStateOf(false) }
            var showAccountSwitchBottomSheet by remember { mutableStateOf(false) }

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
                    ) {
                        // Header with network selector
                        WalletHeaderV2(
                            selectedNetwork = uiState.selectedNetwork,
                            notificationCount = 0,
                            isDevelopmentEnvironment = uiState.isDevelopmentEnvironment,
                            onNotificationClick = { /* TODO */ },
                            onAddAccountClick = { showAddAccountBottomSheet = true },
                            onToggleAccountMode = { /* Not used for EVM */ },
                            onNetworkDropdownClick = { /* TODO: Network selection */ }
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = WalletThemeV2.Dimensions.paddingMedium),
                            verticalArrangement = Arrangement.spacedBy(WalletThemeV2.Dimensions.spacingMedium)
                        ) {
                            // Portfolio Header - shows truncated address for EVM
                            EVMPortfolioHeader(
                                isSingleAccountMode = uiState.isSingleAccountMode,
                                activeAccount = uiState.activeAccount,
                                totalPortfolioUsd = uiState.portfolioTotalUsd,
                                totalPnlAmountFormatted = uiState.portfolioPnlFormatted,
                                pnlColor = uiState.portfolioPnlColor,
                                isBalanceHidden = uiState.isPortfolioBalanceHidden,
                                fiatSymbol = uiState.fiatSymbol,
                                accounts = uiState.accounts,
                                onToggleHideBalance = { screenModel.onTogglePortfolioHideBalance() },
                                onCopyAddress = { screenModel.onCopyAddress() },
                                onAccountClick = { showAccountSwitchBottomSheet = true }
                            )

                            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingXSmall))

                            // Quick Action Buttons
                            AnimatedVisibility(
                                visible = contentVisible,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        WalletThemeV2.Animation.durationLong,
                                        delayMillis = WalletThemeV2.Animation.delayStep3
                                    )
                                ) + slideInVertically(
                                    initialOffsetY = { it / 6 },
                                    animationSpec = tween(
                                        WalletThemeV2.Animation.durationLong,
                                        delayMillis = WalletThemeV2.Animation.delayStep3
                                    )
                                )
                            ) {
                                EVMQuickActionsRow(
                                    onSendClick = {
                                        globalNavigator.push(ScreenRegistry.get(SharedScreen.ContactListScreen))
                                    },
                                    onReceiveClick = {
                                        val screen = ScreenRegistry.get(
                                            SharedScreen.ReceiveTokenScreen(
                                                accountId = screenModel.getCurrentAccountId(),
                                                address = screenModel.getCurrentAddress(),
                                                networkType = NetworkType.EVM,
                                                initialBlockchainUid = uiState.selectedNetwork?.blockChainUid
                                            )
                                        )
                                        globalNavigator.push(screen)
                                    },
                                    onHistoryClick = {
                                        // TODO: Navigate to EVM transaction history
                                    },
                                    onScanClick = {
                                        val networkType = uiState.selectedNetwork?.blockchainType?.networkType
                                        networkType?.let {
                                            MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.QR_SCANNER_OPENED)
                                            this@EVMWalletScreenV2.scanQRCode.scanQRCode(
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
                                                initialBlockchainUid = null
                                            )
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.spacingSmall))

                            // Token list section
                            AnimatedVisibility(
                                visible = contentVisible,
                                enter = fadeIn(
                                    animationSpec = tween(
                                        WalletThemeV2.Animation.durationLong,
                                        delayMillis = WalletThemeV2.Animation.delayStep5
                                    )
                                )
                            ) {
                                EVMTokenListSection(
                                    tokens = uiState.activeAccount?.balances,
                                    isBalanceHidden = uiState.isPortfolioBalanceHidden,
                                    isLoading = uiState.isLoadingWallets,
                                    currencySymbol = uiState.fiatSymbol,
                                    isSearchActive = uiState.isSearchActive,
                                    searchQuery = uiState.searchQuery,
                                    onSearchClick = { screenModel.onSearchToggled() },
                                    onSearchQueryChanged = { screenModel.onSearchQueryChanged(it) },
                                    onTokenClick = { token ->
                                        // TODO: Navigate to token detail
                                    }
                                )
                            }

                            // Bottom spacing for navigation bar
                            Spacer(modifier = Modifier.height(WalletThemeV2.Dimensions.paddingXLarge))
                        }
                    }

                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter),
                        backgroundColor = WalletThemeV2.Colors.secondaryBackground,
                        contentColor = WalletThemeV2.Colors.evmAccent
                    )
                }
            }

            // Bottom Sheets
            if (showAddAccountBottomSheet) {
                EVMAddAccountBottomSheet(
                    onCreateNewAccount = {
                        val screen = ScreenRegistry.get(SharedScreen.EvmCreateAccountScreen())
                        globalNavigator.push(screen)
                    },
                    onImportWallet = {
                        val screen = ScreenRegistry.get(SharedScreen.RestoreRecoveryPhraseScreen())
                        navigator.push(screen)
                    },
                    onDismiss = { showAddAccountBottomSheet = false }
                )
            }

            if (showAccountSwitchBottomSheet) {
                EVMAccountSwitchBottomSheet(
                    accounts = uiState.accounts,
                    activeAccountIndex = uiState.selectedAccountIndex,
                    onAccountSelected = { index ->
                        screenModel.onAccountSelected(index)
                        showAccountSwitchBottomSheet = false
                    },
                    onDismiss = { showAccountSwitchBottomSheet = false }
                )
            }
        } else {
            NoEVMWalletState(navigator, globalNavigator)
        }
    }

    override fun onNavigateToSend() {
        // Handled in ScreenContent
    }

    override fun onNavigateToReceive() {
        // Handled in ScreenContent
    }

    override fun onNavigateToHistory() {
        // Handled in ScreenContent
    }

    override val isBottomBarVisible = true

    override fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: EVMWalletScreenModel
    ) {
        when (result) {
            is QrCodeData.Payment -> {
                MangalaAnalytics.trackEvent(
                    MangalaAnalytics.EventName.QR_SCANNER_RESULT_PARSED,
                    mapOf(
                        MangalaAnalytics.EventParam.QR_RESULT_TYPE to MangalaAnalytics.EventParamValue.QR_RESULT_TYPE_PAYMENT
                    )
                )

                val step2Screen = ScreenRegistry.get(
                    SharedScreen.Step2SelectNetwork(
                        accountId = screenModel.getCurrentAccountId(),
                        networkType = NetworkType.EVM.name,
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

            QrCodeData.WalletConnect -> {
                // TODO: Implement WalletConnect support
            }

            QrCodeData.Login -> {
                // Not applicable for EVM
            }

            is QrCodeData.ImportAccount -> {
                // TODO: Handle import for EVM
            }

            else -> {
                // Handle other cases or show error
            }
        }
    }
}

/**
 * State shown when no EVM wallet is imported
 */
@Composable
private fun NoEVMWalletState(
    navigator: Navigator,
    globalNavigator: Navigator
) {
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
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                LocalImage(
                    imageResource = MR.images.character,
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

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
                    text = "To get started, create a new EVM wallet or import an existing one.",
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
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MangalaGradientButton(
                    label = "Create a new wallet",
                    onClick = {
                        val createWalletScreen = ScreenRegistry.get(SharedScreen.CreateWalletGuideScreen)
                        globalNavigator.push(createWalletScreen)
                    },
                    buttonStyle = MangalaButtonStyle.GRADIENT,
                    modifier = Modifier.fillMaxWidth()
                )

                MangalaGradientButton(
                    label = "I already have a wallet",
                    onClick = {
                        val importWalletScreen = ScreenRegistry.get(SharedScreen.RestoreRecoveryPhraseScreen())
                        navigator.push(importWalletScreen)
                    },
                    buttonStyle = MangalaButtonStyle.TRANSPARENT,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
