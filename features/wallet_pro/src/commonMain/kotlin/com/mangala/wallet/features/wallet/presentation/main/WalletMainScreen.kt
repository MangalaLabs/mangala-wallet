package com.mangala.wallet.features.wallet.presentation.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import com.mangala.features.wallet.presentation.AccountCardButton
import com.mangala.features.wallet.presentation.AddAccountCard
import com.mangala.features.wallet.presentation.AntelopeAccountsPagerDataState
import com.mangala.features.wallet.presentation.AntelopeAssetCard
import com.mangala.features.wallet.presentation.AntelopeAssetsUiModel
import com.mangala.features.wallet.presentation.AntelopeRamAssetCard
import com.mangala.features.wallet.presentation.AntelopeResources
import com.mangala.features.wallet.presentation.BaseWalletMainScreen
import com.mangala.features.wallet.presentation.BaseWalletMainScreenComposable
import com.mangala.features.wallet.presentation.BitcoinAccountsPagerDataState
import com.mangala.features.wallet.presentation.BitcoinAssetCard
import com.mangala.features.wallet.presentation.CreateWalletCard
import com.mangala.features.wallet.presentation.EvmAccountsPagerDataState
import com.mangala.features.wallet.presentation.EvmAssetCard
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeReceive
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeSend
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Portfolio
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.features.chains.antelope.presentation.esr.EsrScreen
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreenModel.Companion.KEY_PLACEHOLDER
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTitle2_36
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.component.VisibilityToggleIconButton
import com.mangala.wallet.ui.component.WalletMainScreenTopBar
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.isQrCodeScanningSupported
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.collectLatest

internal class WalletMainScreen : BaseWalletMainScreen<WalletMainScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.WALLET_MAIN
    override val screenClassName: String = WalletMainScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): WalletMainScreenModel = getScreenModel()

    override fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: WalletMainScreenModel
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
                    MangalaAnalytics.trackEvent(
                        MangalaAnalytics.EventName.QR_SCANNER_RESULT_PARSED,
                        mapOf(
                            MangalaAnalytics.EventParam.QR_RESULT_TYPE to MangalaAnalytics.EventParamValue.QR_RESULT_TYPE_PAYMENT
                        )
                    )

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
                MangalaAnalytics.trackEvent(
                    MangalaAnalytics.EventName.QR_SCANNER_RESULT_PARSED,
                    mapOf(
                        MangalaAnalytics.EventParam.QR_RESULT_TYPE to MangalaAnalytics.EventParamValue.QR_RESULT_TYPE_TRANSACTION
                    )
                )

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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: WalletMainScreenModel) {
        val globalNavigator = LocalGlobalNavigator.current

        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            ),
            sheetBackgroundColor = Color.Transparent
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            if (bottomNavigator.isVisible.not())
                LocalBottomNavigationVisibility.current.value = true

            LaunchedEffect(Unit) {
                // TODO: Move this to global screen
                screenModel.anchorLinkIncomingSignRequests.collectLatest {
                    if (it != null) {
                        navigator.push(EsrScreen(it.esrUri))
                    }
                }
            }

            WalletMainScreen(
                screenModel = screenModel,
                uiState = uiState.value,
                navigator = navigator,
                globalNavigator = globalNavigator,
                onClickImportWallet = {
                    when (it) {
                        NetworkType.ANTELOPE -> {
                            val antelopeImportAccountScreen =
                                ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                            globalNavigator.push(antelopeImportAccountScreen)
                        }

                        NetworkType.EVM, NetworkType.BITCOIN -> {
                            val importWalletGuideScreen =
                                ScreenRegistry.get(SharedScreen.ImportWalletGuideScreen())
                            navigator.push(importWalletGuideScreen)
                        }

                        else -> throw UnsupportedOperationException("Network $it does not handle import wallet")
                    }
                },
                onClickCreateWallet = {
                    val createWalletGuideScreen =
                        ScreenRegistry.get(SharedScreen.CreateWalletGuideScreen)
                    globalNavigator.push(createWalletGuideScreen)
                },
                onClickCreateAccount = {
                    when (it) {
                        NetworkType.ANTELOPE -> {
                            val antelopeCreateAccountScreen =
                                ScreenRegistry.get(SharedScreen.AntelopeCreateAccountV2Screen)
                            navigator.push(antelopeCreateAccountScreen)
                        }

                        NetworkType.EVM -> {
                            val evmCreateAccountScreen =
                                ScreenRegistry.get(SharedScreen.EvmCreateAccountScreen())
                            globalNavigator.push(evmCreateAccountScreen)
                        }

                        NetworkType.BITCOIN -> {
                            val bitcoinCreateAccountScreen =
                                ScreenRegistry.get(SharedScreen.BitcoinCreateAccountScreen())
                            globalNavigator.push(bitcoinCreateAccountScreen)
                        }

                        else -> throw NotImplementedError()
                    }
                },
                onToggleBalanceVisible = {
                    screenModel.toggleBalanceVisible(it)
                },
                onAccountChange = {
                    screenModel.onAccountChange(it)
                },
                onClickCopy = {
                    screenModel.onClickCopy()
                },
                onClickShowQrAntelope = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                            accountId = it.accountName,
                            address = null,
                            networkType = NetworkType.ANTELOPE,
                            initialBlockchainUid = uiState.value.selectedNetwork?.blockChainUid,
                        )
                    )
                    globalNavigator.push(screen)
                },
                onClickShowQrEvm = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                            accountId = it.account.id,
                            address = null,
                            networkType = NetworkType.EVM,
                            initialBlockchainUid = uiState.value.selectedNetwork?.blockChainUid,
                        )
                    )
                    globalNavigator.push(screen)
                },
                onClickPortfolio = { accountId, address, accountName ->
                    uiState.value.selectedNetwork?.blockchainType?.networkType?.let {
                        val screen = ScreenRegistry.get(
                            SharedScreen.PortfolioScreen(
                                accountId = accountId,
                                address = address,
                                networkType = it,
                                accountName = accountName
                            )
                        )
                        navigator.push(screen)
                    }
                },
                onClickPortfolioAntelope = { accountName, accountLabel ->
                    uiState.value.selectedNetwork?.blockchainType?.networkType?.let {
                        val screen = ScreenRegistry.get(
                            SharedScreen.PortfolioScreen(
                                accountId = accountName,
                                address = accountLabel,
                                networkType = NetworkType.ANTELOPE,
                                accountName = accountName
                            )
                        )
                        navigator.push(screen)
                    }
                },
                onClickSend = {
                    val networkType = uiState.value.selectedNetwork?.blockchainType?.networkType

                    val accountId = when (networkType) {
                        NetworkType.EVM -> {
                            screenModel.getCurrentAccountId()
                        }

                        NetworkType.ANTELOPE -> {
                            val antelopeData =
                                (uiState.value as? WalletMainScreenUiState.AntelopeData)
                                    ?: return@WalletMainScreen
                            antelopeData.selectedAccount?.account?.accountName.orEmpty()
                        }

                        NetworkType.BITCOIN -> {
                            screenModel.getCurrentAccountId()
                        }

                        else -> throw UnsupportedOperationException("Network $networkType does not handle send")
                    }

//                    val screen = ScreenRegistry.get(
//                        SharedScreen.SelectRecipientTypeScreen(
//                            accountId = accountId,
//                            networkType = networkType.name
//                        )
//                    )
//                    bottomNavigator.show(screen)
                    globalNavigator.push(ScreenRegistry.get(SharedScreen.ContactListScreen))
                },
                onClickReceive = {
                    uiState.value.selectedNetwork?.blockchainType?.networkType?.let {
                        val screen = ScreenRegistry.get(
                            SharedScreen.ReceiveTokenScreen(
                                accountId = screenModel.getCurrentAccountId(),
                                address = null,
                                networkType = it,
                                initialBlockchainUid = null
                            )
                        )
                        globalNavigator.push(screen)
                    }
                },
                onClickBuy = {
                    // TODO: Handle buy action
                },
                onClickManageAccounts = {
                    val manageAccountsScreen = ScreenRegistry.get(SharedScreen.ManageAccountsScreen)
                    globalNavigator.push(manageAccountsScreen)
                },
                isRefreshing = screenModel.isRefreshing.collectAsStateMultiplatform().value,
                onPullToRefresh = { screenModel.onPullToRefresh() },
                onClickAntelopeAsset = {
                    when (it) {
                        is AntelopeAssetsUiModel.RamBalanceUiModel -> {
                            val screen = ScreenRegistry.get(
                                SharedScreen.RamDetailScreen(
                                    accountName = it.accountName
                                )
                            )
                            navigator.push(screen)
                        }

                        is AntelopeAssetsUiModel.TokenBalanceUiModel -> {
                            // TODO: Handle tapping on Antelope token
                        }
                    }
                },
                onClickAntelopePowerUp = { accountName ->
                    val screen =
                        ScreenRegistry.get(SharedScreen.PowerUpScreen(accountName, isCpu = true))
                    globalNavigator.push(screen)
                },
                onClickReceiveTokenAntelope = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                            accountId = it.accountName,
                            address = null,
                            networkType = NetworkType.ANTELOPE,
                            initialBlockchainUid = null
                        )
                    )
                    globalNavigator.push(screen)
                },
                onClickAntelopeCpu = { isCPU, accountName ->
                    val screen = ScreenRegistry.get(
                        SharedScreen.NetAndCpuScreen(
                            isCpu = isCPU,
                            accountName = accountName
                        )
                    )
                    navigator.push(screen)
                },
                onClickAntelopeNet = { isCPU, accountName ->
                    val screen = ScreenRegistry.get(
                        SharedScreen.NetAndCpuScreen(
                            isCpu = isCPU,
                            accountName = accountName
                        )
                    )
                    navigator.push(screen)
                },
                onClickRecreateAccountAntelope = {
                    screenModel.getSelectedAccount()?.let { account ->
                        when (account.createAccountState) {
                            AntelopeAccount.CreateAccountState.IAP_PAYMENT_INITIALIZED,
                            AntelopeAccount.CreateAccountState.IAP_PAYMENT_PENDING,
                            AntelopeAccount.CreateAccountState.IAP_CREATE_ACCOUNT_PENDING,
                            AntelopeAccount.CreateAccountState.IAP_CREATE_ACCOUNT_FAILED_INVALID -> {
                                val screen = ScreenRegistry.get(
                                    SharedScreen.IapCreateAccountScreen(
                                        accountNameWithSuffix = account.accountName,
                                        accountNameType = AccountNameType.getAccountNameType(account.accountName).name,
                                        skipToCreateAccountStep = false,
                                        retryCreateAccountName = true,
                                        purchaseToken = null,
                                        purchaseId = null
                                    )
                                )

                                navigator.push(screen)
                            }

                            AntelopeAccount.CreateAccountState.FRIEND_CREATE_ACCOUNT_PENDING -> TODO()
                            AntelopeAccount.CreateAccountState.EVM_CREATE_ACCOUNT_INITIALIZED,
                            AntelopeAccount.CreateAccountState.EVM_CREATE_ACCOUNT_FAILED -> {
                                //TODO: Leonard will handle when EVM create account failed
                                println("Handle re-create account from evm")
                            }

                            else -> {
                                // Do nothing
                            }
                        }
                    }
                },
                onChangeAntelopeAccountBalanceUnit = screenModel::onChangeAntelopeBalanceUnit
            )
        }
    }

    @Composable
    fun WalletMainScreen(
        uiState: WalletMainScreenUiState,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: WalletMainScreenModel,
        onClickCreateWallet: () -> Unit,
        onClickImportWallet: (NetworkType) -> Unit,
        onClickCreateAccount: (networkType: NetworkType) -> Unit,
        onToggleBalanceVisible: (Boolean) -> Unit,
        onAccountChange: (index: Int) -> Unit,
        onClickCopy: () -> Unit,
        onClickShowQrAntelope: (AntelopeAccount) -> Unit,
        onClickShowQrEvm: (AccountBlockchainModel) -> Unit,
        onClickPortfolio: (accountId: String, address: String, accountName: String) -> Unit,
        onClickPortfolioAntelope: (accountName: String, accountLabel: String) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: (accountId: String) -> Unit,
        onClickBuy: () -> Unit,
        onClickManageAccounts: () -> Unit,
        onClickAntelopeAsset: (AntelopeAssetsUiModel) -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
        onClickAntelopePowerUp: (accountName: String) -> Unit,
        onClickReceiveTokenAntelope: (AntelopeAccount) -> Unit,
        onClickAntelopeNet: (isCpu: Boolean, accountName: String) -> Unit,
        onClickAntelopeCpu: (isCpu: Boolean, accountName: String) -> Unit,
        onClickRecreateAccountAntelope: () -> Unit,
        onChangeAntelopeAccountBalanceUnit: (AntelopeAccountBalanceUnit) -> Unit
    ) {
        BaseWalletMainScreenComposable(
            isRefreshing = isRefreshing,
            onPullToRefresh = onPullToRefresh,
            accountsPager = {
                AccountsPager(
                    uiState,
                    onClickCreateWallet = onClickCreateWallet,
                    onClickImportWallet = onClickImportWallet,
                    onClickCreateAccount = onClickCreateAccount,
                    onToggleBalanceVisible = onToggleBalanceVisible,
                    onAccountChange = onAccountChange,
                    onClickCopy = onClickCopy,
                    onClickShowQrAntelope = onClickShowQrAntelope,
                    onClickShowQrEvm = onClickShowQrEvm,
                    onClickPortfolio = onClickPortfolio,
                    onClickSend = onClickSend,
                    onClickReceive = onClickReceive,
                    onClickBuy = onClickBuy,
                    onClickManageAccounts = onClickManageAccounts,
                    onClickReceiveTokenEOS = onClickReceiveTokenAntelope,
                    onClickPortfolioAntelope = onClickPortfolioAntelope,
                    onClickRecreateAccountAntelope = onClickRecreateAccountAntelope,
                    onChangeBalanceUnit = onChangeAntelopeAccountBalanceUnit
                )
            },
            accountDetails = {
                AssetsList(
                    uiState,
                    onClickAntelopePowerUp = onClickAntelopePowerUp,
                    onClickAntelopeCpu = onClickAntelopeCpu,
                    onClickAntelopeNet = onClickAntelopeNet,
                    onClickAntelopeAsset = onClickAntelopeAsset,
                    onClickEvmAsset = {
                        // TODO: Handle clicking on Evm Asset
                    }
                )
            },
            customTopBar = {
                WalletMainScreenTopBar(
                    selectedNetwork = uiState.selectedNetwork,
                    onClickMenuIcon = {
//                        val menuScreen = ScreenRegistry.get(SharedScreen.MenuScreen)
//                        navigator.push(menuScreen)
                    },
                    rightIcon = {
                        if (isQrCodeScanningSupported()) {
                            val hapticFeedback = LocalHapticFeedback.current
                            MangalaWalletIconButton(
                                icon = MangalaWalletPack.Scan,
                                onClick = {
                                    val networkType =
                                        uiState.selectedNetwork?.blockchainType?.networkType

                                    networkType?.let {
                                        MangalaAnalytics.trackEvent(MangalaAnalytics.EventName.QR_SCANNER_OPENED)

                                        scanQRCode.scanQRCode(
                                            scanQRCodeListener = object : ScanQRCodeListener {
                                                override fun onScanQRCodeResult(result: String) {
                                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

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
                                }
                            )
                        }
                    },
                )
            }
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun AccountsPager(
        uiState: WalletMainScreenUiState,
        modifier: Modifier = Modifier,
        onClickCreateWallet: () -> Unit,
        onClickImportWallet: (NetworkType) -> Unit,
        onClickCreateAccount: (NetworkType) -> Unit,
        onToggleBalanceVisible: (Boolean) -> Unit,
        onAccountChange: (index: Int) -> Unit,
        onClickCopy: () -> Unit,
        onClickShowQrAntelope: (AntelopeAccount) -> Unit,
        onClickShowQrEvm: (AccountBlockchainModel) -> Unit,
        onClickPortfolio: (accountId: String, address: String, accountName: String) -> Unit,
        onClickPortfolioAntelope: (accountName: String, accountLabel: String) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: (accountId: String) -> Unit,
        onClickBuy: () -> Unit,
        onClickRecreateAccountAntelope: () -> Unit,
        onClickManageAccounts: () -> Unit,
        onClickReceiveTokenEOS: (AntelopeAccount) -> Unit,
        onChangeBalanceUnit: (AntelopeAccountBalanceUnit) -> Unit,
    ) {
        when (uiState) {
            WalletMainScreenUiState.Loading -> {

            }

            is WalletMainScreenUiState.NoWallet -> {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.SMALL)
                ) {
                    uiState.networkSelected?.blockchainType?.networkType?.let {
                        CreateWalletCard(
                            onClickCreateWallet = onClickCreateWallet,
                            onClickImportWallet = {
                                onClickImportWallet(it)
                            }
                        )
                    }
                }
            }

            is WalletMainScreenUiState.EvmData -> {
                EvmAccountsPagerDataState(
                    uiState = uiState,
                    modifier = modifier,
                    onAccountChange = onAccountChange,
                    onClickCopy = onClickCopy,
                    onClickShowQr = onClickShowQrEvm,
                    onClickManageAccounts = onClickManageAccounts,
                    accountInfoCardCornerButton = {
                        VisibilityToggleIconButton(
                            uiState.isBalanceVisible,
                            onClickBalanceVisible = onToggleBalanceVisible,
                            iconButtonModifier = Modifier.size(24.dp),
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    accountInfoCardBottomButtons = { accountModel ->
                        val uiModel = accountModel.account
                        AccountCardButton(
                            MR.strings.button_wallet_main_account_card_portfolio.desc().localized(),
                            MangalaWalletPack.Portfolio,
                            isLoading = accountModel.totalValuePlaceholderEnabled
                        ) {
                            onClickPortfolio(
                                uiModel.account.id,
                                uiModel.bip44Address,
                                uiModel.account.name
                            ) // TODO: Pass in correct type of address if needed
                        }
                        AccountCardButton(
                            MR.strings.all_send.desc().localized(),
                            MangalaWalletPack.HomeSend,
                            isLoading = accountModel.totalValuePlaceholderEnabled
                        ) {
                            onClickSend()
                        }
                        AccountCardButton(
                            MR.strings.all_receive.desc().localized(),
                            MangalaWalletPack.HomeReceive,
                            isLoading = accountModel.totalValuePlaceholderEnabled
                        ) {
                            onClickReceive(accountModel.account.account.id)
                        }
//                        AccountCardButton(
//                            MR.strings.button_wallet_main_account_card_buy.desc().localized(),
//                            MangalaWalletPack.Buy
//                        ) {
//                            onClickBuy()
//                        }
                    },
                    accountInfoCardCardInfo = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextTitle2_36(
                                it.totalValueFormatted,
                                Modifier
                                    .mangalaWalletPlaceholder(it.totalValuePlaceholderEnabled)
                                    .weight(1f)
                                    .basicMarquee(iterations = Int.MAX_VALUE)
                            )
                            Spacer(Modifier.width(Spacing.SMALL))
                            TextDescription2(
                                it.formattedPnl,
                                fontWeight = FontWeight.Medium,
                                color = if (it.isBalanceVisible.not()) MaterialTheme.mangalaColors.textSecondary else it.pnlColor,
                                modifier = Modifier.background(
                                    MaterialTheme.mangalaColors.bgInnerCard,
                                    shape = RoundedCornerShape(CornerRadius.Medium)
                                ).padding(
                                    horizontal = Dimensions.Padding.half,
                                    vertical = Dimensions.Padding.quarter
                                ).mangalaWalletPlaceholder(it.formattedPnlPlaceholderEnabled)
                            )
                        }
                    },
                    accountInfoCardSideButton = {},
                    addAccountCard = { modifier: Modifier ->
                        AddAccountCard(
                            modifier = modifier,
                            mainButton = {
                                MangalaGradientButton(
                                    label = MR.strings.all_add_new_account.desc().localized(),
                                    onClick = { onClickCreateAccount(NetworkType.EVM) },
                                    size = MangalaButtonSize.XMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MangalaTypography.Size14Medium()
                                )
                            },
                            secondButton = {
//                                MangalaTextButton(
//                                    MR.strings.button_wallet_main_add_watch_only_account.desc().localized(),
//                                    fontSize = FontType.REGULAR,
//                                    modifier = Modifier.fillMaxWidth(),
//                                    textAlign = TextAlign.Center,
//                                    fontWeight = FontWeight.SemiBold
//                                ) {
//                                    // TODO: Handle click
//                                }
                            },
                        )
                    }
                )
            }

            is WalletMainScreenUiState.AntelopeData -> {
                AntelopeAccountsPagerDataState(
                    uiState = uiState,
                    onAccountChange = onAccountChange,
                    addAccountCard = { modifier: Modifier ->
                        AddAccountCard(
                            modifier = modifier,
                            mainButton = {
                                Column {
                                    MangalaGradientButton(
                                        label = MR.strings.all_add_new_account.desc().localized(),
                                        onClick = { onClickCreateAccount(NetworkType.ANTELOPE) },
                                        size = MangalaButtonSize.XMedium,
                                        modifier = Modifier.fillMaxWidth(),
                                        style = MangalaTypography.Size14Medium()
                                    )
                                }
                            },
                            secondButton = {
                                MangalaTextButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    label = MR.strings.button_wallet_main_import_account.desc()
                                        .localized(),
                                    contentColor = MaterialTheme.mangalaColors.textPrimary,
                                    size = MangalaButtonSize.XMedium,
                                    style = MangalaTypography.Size14Medium(),
                                    onClick = {
                                        onClickImportWallet(NetworkType.ANTELOPE)
                                    }
                                )
//                                Temporary disabled
//                                MangalaTextButton(
//                                    MR.strings.button_wallet_main_add_watch_only_account.desc().localized(),
//                                    fontSize = FontType.REGULAR,
//                                    modifier = Modifier.fillMaxWidth(),
//                                    textAlign = TextAlign.Center,
//                                    fontWeight = FontWeight.SemiBold
//                                ) {
//                                    // TODO: Handle click
//                                }
                            },
                        )
                    },
                    onClickManageAccounts = {
                        TODO()
                    },
                    matchCardsHeight = true,
                    onClickCopy = onClickCopy,
                    onClickShowQr = { antelopeAccount ->
                        onClickShowQrAntelope(antelopeAccount.account)
                    },
                    onToggleBalanceVisible = onToggleBalanceVisible,
                    onClickPortfolio = onClickPortfolioAntelope,
                    onClickSend = onClickSend,
                    onClickReceive = { antelopeAccount ->
                        onClickReceiveTokenEOS(antelopeAccount.account)
                    },
                    onClickBuy = {
//                        TODO()
                    },
                    onClickRecreateAccount = onClickRecreateAccountAntelope,
                    onChangeBalanceUnit = onChangeBalanceUnit
                )
            }

            is WalletMainScreenUiState.BitcoinData -> {
                BitcoinAccountsPagerDataState(
                    uiState = uiState,
                    modifier = modifier,
                    onAccountChange = onAccountChange,
                    onClickCopy = onClickCopy,
                    onClickShowQr = { bitcoinAccount ->
//                        onClickShowQrBitcoin
                    },
                    onClickManageAccounts = onClickManageAccounts,
                    onToggleBalanceVisible = onToggleBalanceVisible,
                    onClickPortfolio = onClickPortfolio,
                    onClickSend = onClickSend,
                    onClickReceive = {
                        onClickReceive(it.account.accountId)
                    },
                    addAccountCard = {
                        AddAccountCard(
                            modifier = modifier,
                            mainButton = {
                                MangalaGradientButton(
                                    label = MR.strings.all_add_new_account.desc().localized(),
                                    onClick = { onClickCreateAccount(NetworkType.BITCOIN) },
                                    size = MangalaButtonSize.XMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    style = MangalaTypography.Size14Medium()
                                )
                            },
                            secondButton = {
//                                MangalaTextButton(
//                                    MR.strings.button_wallet_main_add_watch_only_account.desc().localized(),
//                                    fontSize = FontType.REGULAR,
//                                    modifier = Modifier.fillMaxWidth(),
//                                    textAlign = TextAlign.Center,
//                                    fontWeight = FontWeight.SemiBold
//                                ) {
//                                    // TODO: Handle click
//                                }
                            },
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ColumnScope.AssetsList(
    uiState: WalletMainScreenUiState,
    modifier: Modifier = Modifier,
    onClickAntelopePowerUp: (accountName: String) -> Unit,
    onClickAntelopeNet: (isCpu: Boolean, accountName: String) -> Unit,
    onClickAntelopeCpu: (isCpu: Boolean, accountName: String) -> Unit,
    onClickAntelopeAsset: (AntelopeAssetsUiModel) -> Unit,
    onClickEvmAsset: () -> Unit
) {
    when (uiState) {
        WalletMainScreenUiState.Loading -> {

        }

        is WalletMainScreenUiState.NoWallet -> {
            return
        }

        is WalletMainScreenUiState.EvmData -> {
            EvmAssetsList(uiState, onClickEvmAsset)
        }

        is WalletMainScreenUiState.AntelopeData -> {
            AntelopeAssetsList(
                uiState,
                onClickPowerUp = onClickAntelopePowerUp,
                onClickCpu = onClickAntelopeCpu,
                onClickNet = onClickAntelopeNet,
                onClickAsset = onClickAntelopeAsset
            )
        }

        is WalletMainScreenUiState.BitcoinData -> {
            BitcoinAssetsList(
                uiState = uiState,
                onClickAsset = onClickEvmAsset
            )
        }
    }
}

@Composable
private fun EvmAssetsList(
    uiState: WalletMainScreenUiState.EvmData,
    onClickAsset: () -> Unit
) {
    val balances =
        uiState.accounts.getOrNull(uiState.selectedAccountIndex)?.balances
    val balancesList = if (balances.isNullOrEmpty()) uiState.placeholderAssetsList else balances

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        contentPadding = PaddingValues(horizontal = Dimensions.Padding.default)
    ) {
        items(
            balancesList,
            key = {
                it.coinUid.ifBlank {
                    it.hashCode()
                }
            }
        ) {
            EvmAssetCard(
                it,
                fiatCurrencySymbol = uiState.fiatCurrencySymbol,
                uiState.isBalanceVisible,
                onClick = {
                    onClickAsset()
                },
                isLoading = it.coinUid.startsWith(KEY_PLACEHOLDER),
            )
        }
    }
}

@Composable
private fun AntelopeAssetsList(
    uiState: WalletMainScreenUiState.AntelopeData,
    onClickPowerUp: (accountName: String) -> Unit,
    onClickNet: (isCpu: Boolean, accountName: String) -> Unit,
    onClickCpu: (isCpu: Boolean, accountName: String) -> Unit,
    onClickAsset: (AntelopeAssetsUiModel) -> Unit
) {
    if (uiState.selectedAccount?.account?.isTemp == true) return // catch-all for empty list/ selected card not an account/ temp account

    val assets = uiState.accounts.getOrNull(uiState.selectedAccountIndex)?.assets
    val assetsList = assets ?: uiState.placeholderAssetList

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        contentPadding = PaddingValues(horizontal = Dimensions.Padding.default)
    ) {
        items(assetsList, key = {
            it.key.ifBlank {
                it.hashCode().toString()
            }
        }) {
            when (it) {
                is AntelopeAssetsUiModel.RamBalanceUiModel -> {
                    val isLoading = it.key.startsWith(KEY_PLACEHOLDER)
                    AntelopeRamAssetCard(
                        it,
                        uiState.isBalanceVisible,
                        onClick = {
                            onClickAsset(it)
                        },
                        isLoading = isLoading,
                        enabled = !isLoading
                    )
                }

                is AntelopeAssetsUiModel.TokenBalanceUiModel -> {
                    AntelopeAssetCard(
                        it,
                        uiState.isBalanceVisible,
                        onClick = {
                            onClickAsset(it)
                        },
                        isLoading = it.key.startsWith(KEY_PLACEHOLDER),
                    )
                }
            }
        }
    }
    VerticalSpacer(Spacing.BASE)

    if (uiState.accounts.isNotEmpty() && uiState.selectedAccount != null) {
        AntelopeResources(
            uiState,
            onClickPowerUp = onClickPowerUp,
            onClickResourceCard = onClickCpu
        )
    }
}

@Composable
private fun BitcoinAssetsList(
    uiState: WalletMainScreenUiState.BitcoinData,
    onClickAsset: () -> Unit = {}
) {
    val selectedAccount = uiState.accounts.getOrNull(uiState.selectedAccountIndex) ?: return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        contentPadding = PaddingValues(horizontal = Dimensions.Padding.default)
    ) {
        item {
            BitcoinAssetCard(
                balanceModel = selectedAccount.balanceModel,
                fiatCurrencySymbol = uiState.fiatCurrencySymbol,
                isBalanceVisible = uiState.isBalanceVisible,
                onClick = {
                    onClickAsset()
                },
                isLoading = selectedAccount.isLoading,
            )
        }
    }
}
