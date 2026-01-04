package com.mangala.wallet.features.wallet.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.features.wallet.presentation.EvmAccountInfoCard
import com.mangala.features.wallet.presentation.EvmAccountItemUiModel
import com.mangala.features.wallet.presentation.AddAccountCard
import com.mangala.features.wallet.presentation.BaseWalletMainScreen
import com.mangala.features.wallet.presentation.BaseWalletMainScreenComposable
import com.mangala.features.wallet.presentation.BaseWalletMainScreenDataUiState
import com.mangala.features.wallet.presentation.CreateWalletCard
import com.mangala.features.wallet.presentation.colorAccount
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeReceive
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextInButtonTiny
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletIconButtonCold
import com.mangala.wallet.ui.component.WalletMainScreenTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent

internal class WalletMainScreen : BaseWalletMainScreen<WalletMainScreenModel>(), KoinComponent {

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
                val signTransactionRequest = result.signTransactionRequest
                val legacyGasPrice =
                    (signTransactionRequest.gasPrice as? GasPrice.Legacy)?.legacyGasPrice
                val eip1559GasPrice = (signTransactionRequest.gasPrice as? GasPrice.Eip1559)

                val signedTransactionQrScreen = ScreenRegistry.get(
                    SharedScreen.SignedTransactionQrScreen(
                        requestId = signTransactionRequest.requestId,
                        walletId = signTransactionRequest.walletId,
                        accountId = signTransactionRequest.accountId,
                        nonce = signTransactionRequest.nonce,
                        blockchainUid = signTransactionRequest.blockchainType.uid,
                        fromAddress = signTransactionRequest.fromAddress,
                        toAddress = signTransactionRequest.transactionData.to?.hex.orEmpty(),
                        value = signTransactionRequest.transactionData.value,
                        input = signTransactionRequest.transactionData.input,
                        legacyGasPrice = legacyGasPrice,
                        maxFeePerGas = eip1559GasPrice?.maxFeePerGas,
                        maxPriorityFeePerGas = eip1559GasPrice?.maxPriorityFeePerGas,
                        baseFee = eip1559GasPrice?.baseFee,
                        gasLimit = signTransactionRequest.gasLimit,
                        gasFiatValue = signTransactionRequest.gasFiatValue,
                        transactionType = signTransactionRequest.transactionType.toString(), // TODO: Check type of transaction and forward to the correct confirmation screen
                        contactName = signTransactionRequest.contactName,
                        contactAddress = signTransactionRequest.contactAddress
                    )
                )
                navigator.push(signedTransactionQrScreen)
            }

            is QrCodeData.SyncAccount -> TODO("Show error since Cold wallet doesn't handle a sync data request")
            is QrCodeData.SignedTransaction -> TODO("Show error since Cold wallet don't have send transaction capability")
            QrCodeData.WalletConnect -> TODO("Not implemented scan QR code WalletConnect")
            null -> TODO("Not handled scan invalid QR code")
        }
    }

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

        val observer = remember {
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (!screenModel.isDeviceSecure()) {
                        val lockScreen = ScreenRegistry.get(SharedScreen.LockScreenV2)
                        navigator.push(lockScreen)
                        return@LifecycleEventObserver
                    }
                }
            }
        }
        LocalLifecycleOwner.current.lifecycle.addObserver(observer)

        val createWalletGuideScreen = rememberScreen(SharedScreen.CreateWalletGuideScreen)
        val importWalletGuideScreen = rememberScreen(SharedScreen.ImportWalletGuideScreen)
        val evmCreateAccountScreen = rememberScreen(SharedScreen.EvmCreateAccountScreen())

        val observer = remember {
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (!screenModel.isDeviceSecure()) {
                        val lockScreen = ScreenRegistry.get(SharedScreen.LockScreenV2)
                        navigator.push(lockScreen)
                        return@LifecycleEventObserver
                    }
                }
            }
        }
        LocalLifecycleOwner.current.lifecycle.addObserver(observer)

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            )
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            WalletMainScreenComposable(
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
                onAccountChange = {
                    screenModel.onAccountChange(it)
                },
                onClickCopy = {
                    screenModel.onClickCopy()
                },
                onClickReceive = { accountId ->
                    val screen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenScreen(
                        accountId = accountId,
                        address = null,
                        networkType = (uiState.value as? WalletMainScreenUiState.Data)?.networkSelected?.blockchainType?.networkType ?: NetworkType.ANTELOPE,
                        initialBlockchainUid = (uiState.value as? WalletMainScreenUiState.Data)?.networkSelected?.blockChainUid
                    )
                    )
                    globalNavigator.push(screen)
                },
                onClickManageAccounts = {
                    globalNavigator.push(manageAccountsScreen)
                },
                isRefreshing = screenModel.isRefreshing.value,
                onPullToRefresh = { screenModel.onPullToRefresh() },
                onClickSync = { accountId ->
                    val screen =
                        ScreenRegistry.get(SharedScreen.ColdWalletSyncAccountScreen(accountId = accountId))
                    navigator.push(screen)
                }
            )
        }
    }

    @Composable
    fun WalletMainScreenComposable(
        uiState: WalletMainScreenUiState,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: WalletMainScreenModel,
        onClickCreateWallet: () -> Unit,
        onClickImportWallet: () -> Unit,
        onClickCreateAccount: () -> Unit,
        onAccountChange: (index: Int) -> Unit,
        onClickCopy: () -> Unit,
        onClickReceive: (accountId: String) -> Unit,
        onClickManageAccounts: () -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
        onClickSync: (accountId: String) -> Unit
    ) {
        BaseWalletMainScreenComposable(
            isRefreshing,
            onPullToRefresh = onPullToRefresh,
            networkName = uiState.selectedNetworkName,
            accountSummary = {
                // Cold wallet doesn't have account summary since it doesn't have balance
            },
            accountsPager = {
                AccountsPager(
                    uiState,
                    onClickCreateWallet = onClickCreateWallet,
                    onClickImportWallet = onClickImportWallet,
                    onClickCreateAccount = onClickCreateAccount,
                    onAccountChange = onAccountChange,
                    onClickCopy = onClickCopy,
                    onClickReceive = onClickReceive,
                    onClickManageAccounts = onClickManageAccounts,
                    onClickSync = onClickSync
                )
            },
            accountDetails = {
                // Cold wallet doesn't have account details since it doesn't have balance
            },
            customTopBar = {
                WalletMainScreenTopBar(
                    onClickMenuIcon = {
                        val menuScreen = ScreenRegistry.get(SharedScreen.MenuScreen)
                        navigator.push(menuScreen)
                    }
                )
            },
        )
    }

    @Composable
    fun AccountsPager(
        uiState: WalletMainScreenUiState,
        modifier: Modifier = Modifier,
        onClickCreateWallet: () -> Unit,
        onClickImportWallet: () -> Unit,
        onClickCreateAccount: () -> Unit,
        onAccountChange: (index: Int) -> Unit,
        onClickCopy: () -> Unit,
        onClickReceive: (accountId: String) -> Unit,
        onClickManageAccounts: () -> Unit,
        onClickSync: (accountId: String) -> Unit
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
                AccountsColdWalletDataState(
                    uiState = uiState,
                    modifier = modifier,
                    onClickCopy = onClickCopy,
                    accountInfoCardCornerButton = {
                    },
                    accountInfoCardSideButton = { accountModel ->
                        Row(
                            modifier.weight(1f),
                            horizontalArrangement = Arrangement.End
                        ) {
                            AccountCardButtonCold(
                                MR.strings.all_receive.desc().localized(),
                                MangalaWalletPack.HomeReceive
                            ) {
                                onClickReceive(accountModel.account.id)
                            }
                            Spacer(Modifier.width(Spacing.SMALL))

                            AccountCardButtonCold(
                                MR.strings.sync.desc().localized(),
                                MangalaWalletPack.Sync
                            ) {
                                onClickSync(accountModel.account.id)
                            }

                        }
                    },
                    accountInfoCardBottomButtons = {
                    },
                    accountInfoCardCardInfo = {
                    },
                    addAccountCard = { modifier: Modifier ->
                        AddAccountCard(
                            modifier = modifier,
                            mainButton = {
                                ButtonNormal(
                                    MR.strings.all_add_new_account.desc().localized(),
                                    modifier = Modifier.fillMaxWidth()
                                        .background(Color(0x26484848)),
                                    buttonModifier = Modifier.height(44.dp).fillMaxWidth(),
                                    fontSize = FontType.REGULAR
                                ) {
                                    onClickCreateAccount()
                                }
                            },
                            secondButton = {
                            },
                        )
                    },
                )
            }
        }
    }

    @Composable
    fun AccountsColdWalletDataState(
        modifier: Modifier = Modifier,
        uiState: BaseWalletMainScreenDataUiState,
        accountInfoCardCornerButton: @Composable RowScope.(account: EvmAccountItemUiModel) -> Unit,
        accountInfoCardBottomButtons: @Composable RowScope.(account: AccountBlockchainModel) -> Unit,
        accountInfoCardCardInfo: @Composable ColumnScope.(account: EvmAccountItemUiModel) -> Unit,
        accountInfoCardSideButton: @Composable RowScope.(account: AccountBlockchainModel) -> Unit,
        addAccountCard: @Composable (Modifier) -> Unit,
        onClickCopy: () -> Unit
    ) {
        Column(modifier = modifier.padding(Dimensions.Padding.default)) {
            addAccountCard(modifier)

            Spacer(modifier = Modifier.height(Spacing.BASE))

            when(uiState) {
                is BaseWalletMainScreenDataUiState.BaseEvmDataState -> {
                    uiState.accounts.forEach { account ->
                        EvmAccountInfoCard(
                            accountModel = account.account,
                            cornerButton = { accountInfoCardCornerButton(account) },
                            cardInfo = { accountInfoCardCardInfo(account) },
                            bottomButtons = { accountInfoCardBottomButtons(account.account) },
                            sideButton = { accountInfoCardSideButton(account.account) },
                            modifier = Modifier.background(
                                colorAccount(
                                    uiState,
                                    account
                                )
                            ),
                            onClickCopy = onClickCopy
                        )
                        Spacer(modifier = Modifier.height(Spacing.SMALL))
                    }
                }
                is BaseWalletMainScreenDataUiState.BaseAntelopeDataState -> {

                }

                else -> {}
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))
        }
    }

    @Composable
    fun AccountCardButtonCold(
        label: String,
        icon: ImageVector,
        onClick: () -> Unit
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(Dimensions.Padding.half)
        ) {
            MangalaWalletIconButtonCold(
                icon = icon,
                tint = Colors.darkGray,
            ) {
                onClick()
            }
            Spacer(modifier = Modifier.height(2.dp))
            TextInButtonTiny(
                label,
                fontWeight = FontWeight.Normal,
                lineHeight = 16.8.sp
            )
        }
    }
}
