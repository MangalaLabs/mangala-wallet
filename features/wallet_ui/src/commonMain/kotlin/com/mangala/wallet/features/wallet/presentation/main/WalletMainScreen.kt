package com.mangala.wallet.features.wallet.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.features.wallet.presentation.AccountCardButton
import com.mangala.features.wallet.presentation.EvmAccountsPagerDataState
import com.mangala.features.wallet.presentation.AddAccountCard
import com.mangala.features.wallet.presentation.EvmAssetCard
import com.mangala.features.wallet.presentation.BaseWalletMainScreen
import com.mangala.features.wallet.presentation.BaseWalletMainScreenComposable
import com.mangala.features.wallet.presentation.CreateWalletCard
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack.HomeReceive
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack.HomeSend
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack.Portfolio
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack.Scan
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTitle2_36
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.VisibilityToggleIconButton
import com.mangala.wallet.ui.component.WalletMainScreenTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.isQrCodeScanningSupported
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent

internal class WalletMainScreen : BaseWalletMainScreen<WalletMainScreenModel>(), KoinComponent {

    @Composable
    override fun createScreenModel(): WalletMainScreenModel {
        return getScreenModel<WalletMainScreenModel>()
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: WalletMainScreenModel) {
        val globalNavigator = LocalGlobalNavigator.current

        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        val createWalletGuideScreen = rememberScreen(SharedScreen.CreateWalletGuideScreen)
        val importWalletGuideScreen = rememberScreen(SharedScreen.ImportWalletGuideScreen)
        val evmCreateAccountScreen = rememberScreen(SharedScreen.EvmCreateAccountScreen())
        val manageAccountsScreen = rememberScreen(SharedScreen.ManageAccountsScreen)
        val menuScreen = rememberScreen(SharedScreen.MenuScreen)

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            )
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            WalletMainScreen(
                uiState = uiState.value,
                navigator = navigator,
                globalNavigator = globalNavigator,
                screenModel = screenModel,
                onClickCreateWallet = {
                    globalNavigator.push(createWalletGuideScreen)
                },
                onClickImportWallet = {
                    globalNavigator.push(importWalletGuideScreen)
                },
                onClickCreateAccount = {
                    globalNavigator.push(evmCreateAccountScreen)
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
                onClickPortfolio = { accountId, address ->
                    val screen =
                        ScreenRegistry.get(SharedScreen.PortfolioScreen(accountId, address))
                    navigator.push(screen)
                },
                onClickSend = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.SelectRecipientTypeScreen(
                            accountId = screenModel.getCurrentAccountId(),
                        )
                    )
                    bottomNavigator.show(screen)
                },
                onClickReceive = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                        accountId = screenModel.getCurrentAccountId(),
                        address = null,
                        networkType = (uiState.value as? WalletMainScreenUiState.Data)?.networkSelected?.blockchainType?.networkType ?: NetworkType.EVM,
                        initialBlockchainUid = (uiState.value as? WalletMainScreenUiState.Data)?.networkSelected?.blockChainUid
                    )
                    )
                    globalNavigator.push(screen)
                },
                onClickBuy = {
                    // TODO: Handle buy action
                },
                onClickManageAccounts = {
                    globalNavigator.push(manageAccountsScreen)
                },
                isRefreshing = screenModel.isRefreshing.value,
            )
        }
    }

    override fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: WalletMainScreenModel
    ) {
        when (result) {
            QrCodeData.Login -> TODO("Not handled scan QR code Login")
            is QrCodeData.Payment -> {
                val step2Screen = ScreenRegistry.get(
                    SharedScreen.Step2SelectNetwork(
                        accountId = screenModel.getCurrentAccountId(),
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

            is QrCodeData.NotSignedTransaction -> {
                TODO("Handle show some kind of UI, as UI app is not supposed to handle this")
            }

            is QrCodeData.SignedTransaction -> {
                val signTransactionRequest = result.signedTransactionResponse.signTransactionRequest
                val signature = result.signedTransactionResponse.getSignature()
                val legacyGasPrice =
                    (signTransactionRequest.gasPrice as? GasPrice.Legacy)?.legacyGasPrice
                val eip1559GasPrice = (signTransactionRequest.gasPrice as? GasPrice.Eip1559)

                val sendSignedTransactionScreen = ScreenRegistry.get(
                    SharedScreen.SendSignedTransactionScreen(
                        walletId = signTransactionRequest.walletId,
                        accountId = signTransactionRequest.accountId,
                        nonce = signTransactionRequest.nonce,
                        fromAddress = signTransactionRequest.fromAddress,
                        blockchainUid = signTransactionRequest.blockchainType.uid,
                        toAddress = signTransactionRequest.transactionData.to?.hex.orEmpty(),
                        value = signTransactionRequest.transactionData.value,
                        input = signTransactionRequest.transactionData.input,
                        legacyGasPrice = legacyGasPrice,
                        maxFeePerGas = eip1559GasPrice?.maxFeePerGas,
                        maxPriorityFeePerGas = eip1559GasPrice?.maxPriorityFeePerGas,
                        baseFee = eip1559GasPrice?.baseFee,
                        gasLimit = signTransactionRequest.gasLimit,
                        gasFiatValue = signTransactionRequest.gasFiatValue,
                        transactionType = "", // TODO: Map data
                        contactName = signTransactionRequest.contactName,
                        contactAddress = signTransactionRequest.contactAddress,
                        v = signature.v,
                        r = signature.r,
                        s = signature.s
                    )
                )
                navigator.push(sendSignedTransactionScreen)
            }

            is QrCodeData.SyncAccount -> {
                val syncAccountScreen = ScreenRegistry.get(
                    SharedScreen.UiWalletSyncAccountScreen(
                        result.syncAccountRequest
                    )
                )
                navigator.push(syncAccountScreen)
            }

            QrCodeData.WalletConnect -> TODO("Not implemented scan QR code WalletConnect")
            null -> TODO("Not handled scan invalid QR code")
        }
    }

    @Composable
    fun WalletMainScreen(
        uiState: WalletMainScreenUiState,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: WalletMainScreenModel,
        onClickCreateWallet: () -> Unit,
        onClickImportWallet: () -> Unit,
        onClickCreateAccount: () -> Unit,
        onToggleBalanceVisible: (Boolean) -> Unit,
        onAccountChange: (index: Int) -> Unit,
        onClickCopy: () -> Unit,
        onClickPortfolio: (accountId: String, address: String) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: (accountId: String) -> Unit,
        onClickBuy: () -> Unit,
        onClickManageAccounts: () -> Unit,
        isRefreshing: Boolean
    ) {
        BaseWalletMainScreenComposable(
            isRefreshing = isRefreshing,
            onPullToRefresh = screenModel::onPullToRefresh,
            accountsPager = {
                AccountsPager(
                    uiState,
                    onClickCreateWallet = onClickCreateWallet,
                    onClickImportWallet = onClickImportWallet,
                    onClickCreateAccount = onClickCreateAccount,
                    onToggleBalanceVisible = onToggleBalanceVisible,
                    onAccountChange = onAccountChange,
                    onClickCopy = onClickCopy,
                    onClickPortfolio = onClickPortfolio,
                    onClickSend = onClickSend,
                    onClickReceive = onClickReceive,
                    onClickBuy = onClickBuy,
                    onClickManageAccounts = onClickManageAccounts
                )
            },
            accountDetails = {
                AssetsList(uiState)
            },
            customTopBar = {
                WalletMainScreenTopBar(
                    onClickMenuIcon = {
                        val menuScreen = ScreenRegistry.get(SharedScreen.MenuScreen)
                        navigator.push(menuScreen)
                    },
                    rightIcon = {
                        if (isQrCodeScanningSupported()) {
                            MangalaWalletIconButton(
                                icon = MangalaWalletPack.Scan,
                                onClick = {
                                    scanQRCode.scanQRCode(
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
                                    )
                                }
                            )
                        }
                    },
                )
            }
        )
    }

    @Composable
    fun AccountsPager(
        uiState: WalletMainScreenUiState,
        modifier: Modifier = Modifier,
        onClickCreateWallet: () -> Unit,
        onClickImportWallet: () -> Unit,
        onClickCreateAccount: () -> Unit,
        onToggleBalanceVisible: (Boolean) -> Unit,
        onAccountChange: (index: Int) -> Unit,
        onClickCopy: () -> Unit,
        onClickPortfolio: (accountId: String, address: String) -> Unit,
        onClickSend: () -> Unit,
        onClickReceive: (accountId: String) -> Unit,
        onClickBuy: () -> Unit,
        onClickManageAccounts: () -> Unit,
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
                    CreateWalletCard(
                        onClickCreateWallet = onClickCreateWallet,
                        onClickImportWallet = onClickImportWallet
                    )
                }
            }

            is WalletMainScreenUiState.Data -> {
                EvmAccountsPagerDataState(
                    uiState = uiState,
                    modifier = modifier,
                    onAccountChange = onAccountChange,
                    onClickCopy = onClickCopy,
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
                        AccountCardButton(
                            MR.strings.button_wallet_main_account_card_portfolio.desc().localized(),
                            MangalaWalletPack.Portfolio
                        ) {
                            onClickPortfolio(
                                accountModel.account.id,
                                accountModel.bip44Address
                            ) // TODO: Pass in correct type of address if needed
                        }
                        AccountCardButton(
                            MR.strings.all_send.desc().localized(),
                            MangalaWalletPack.HomeSend
                        ) {
                            onClickSend()
                        }
                        AccountCardButton(
                            MR.strings.all_receive.desc().localized(),
                            MangalaWalletPack.HomeReceive
                        ) {
                            onClickReceive(accountModel.account.id)
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
                            if (it.totalValueFormatted != null && it.formattedPnl != null) {
                                TextTitle2_36(if (it.isBalanceVisible) it.totalValueFormatted.orEmpty() else HIDDEN_BALANCE_STRING)
                                Spacer(Modifier.width(Spacing.SMALL))
                                TextDescription2(
                                    if (it.isBalanceVisible) it.formattedPnl.orEmpty() else HIDDEN_BALANCE_STRING,
                                    fontWeight = FontWeight.Medium,
                                    color = if (it.isBalanceVisible.not()) Colors.darkGray else it.pnlColor,
                                    modifier = Modifier.background(
                                        Color.White, shape = RoundedCornerShape(CornerRadius.Medium)
                                    ).padding(
                                        horizontal = Dimensions.Padding.half,
                                        vertical = Dimensions.Padding.quarter
                                    )
                                )
                            }
                        }
                    },
                    accountInfoCardSideButton = {

                    },
                    addAccountCard = { modifier: Modifier ->
                        AddAccountCard(
                            modifier = modifier,
                            mainButton = {
                                ButtonNormal(
                                    MR.strings.all_add_new_account.desc().localized(),
                                    modifier = Modifier.fillMaxWidth(),
                                    buttonModifier = Modifier.height(44.dp).fillMaxWidth(),
                                    fontSize = FontType.REGULAR
                                ) {
                                    onClickCreateAccount()
                                }
                            },
                            secondButton = {
                                MangalaTextButton(
                                    MR.strings.button_wallet_main_add_watch_only_account.desc()
                                        .localized(),
                                    fontSize = FontType.REGULAR,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold
                                ) {
                                    // TODO: Handle click
                                }
                            },
                        )
                    }
                )
            }
        }
    }
}

// TODO: Move this to BaseWalletMainScreen and reuse for both pro and ui variants
@Composable
fun ColumnScope.AssetsList(
    uiState: WalletMainScreenUiState,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        WalletMainScreenUiState.Loading -> {

        }

        is WalletMainScreenUiState.NoWallet -> {
            return
        }

        is WalletMainScreenUiState.Data -> {
            val balances =
                uiState.accounts.getOrNull(uiState.selectedAccountIndex)?.balances ?: return

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                contentPadding = PaddingValues(horizontal = Dimensions.Padding.default)
            ) {
                items(balances, key = {
                    it.coinUid.ifBlank {
                        it.hashCode()
                    }
                }) {
                    EvmAssetCard(
                        it,
                        fiatCurrencySymbol = uiState.fiatCurrencySymbol,
                        uiState.isBalanceVisible
                    )
                }
            }
        }
    }
}