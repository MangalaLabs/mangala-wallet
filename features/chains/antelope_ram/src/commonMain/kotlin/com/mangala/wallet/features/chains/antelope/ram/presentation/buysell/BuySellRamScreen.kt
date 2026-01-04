package com.mangala.wallet.features.chains.antelope.ram.presentation.buysell

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Detective
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Swap
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.RecipientValidationStatus
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.InputSectionPercent
import com.mangala.wallet.ui.component.ItemInput
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.RamInformation
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.navigation.navigationResult
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class BuySellRamScreen(
    private val accountName: String,
    private val isBuyRam: Boolean
) : BaseScreen<BuySellRamScreenModel>(), KoinComponent {

    override val screenName: String =
        if (isBuyRam) MangalaAnalytics.Screens.ANTELOPE_BUY_RAM else MangalaAnalytics.Screens.ANTELOPE_SELL_RAM
    override val screenClassName: String = BuySellRamScreen::class.simpleName.orEmpty()

    override val key: ScreenKey
        get() = SharedScreen.BuySellRamScreen::class.simpleName.toString()

    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun createScreenModel(): BuySellRamScreenModel = getScreenModel(parameters = {
        parametersOf(
            accountName,
            isBuyRam
        )
    })

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: BuySellRamScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        BuySellRamScreen(
            screenModel = screenModel,
            navigator = navigator
        )
    }

    @Composable
    private fun BuySellRamScreen(
        screenModel: BuySellRamScreenModel,
        navigator: Navigator,
    ) {
        val navigationResult = navigator.navigationResult
        val uiState by screenModel.uiState.collectAsStateMultiplatform(Dispatchers.Main.immediate)
        LaunchedEffect(true) {
            onBackPressedCallback = {
                if (uiState is BuySellRamUiState.ExecuteBuySellSuccess) {
                    navigationResult.popWithResult(true)
                } else {
                    navigator.pop()
                }
                false
            }
        }

        when (uiState) {
            is BuySellRamUiState.ExecuteBuySellSuccess -> {
                BuySellRamExecuteSuccessState(
                    { screenModel.continueTransaction() },
                    { navigator.popUntilRoot() }
                )
            }

            is BuySellRamUiState.Success -> {
                BuySellRamScreenSuccessState(
                    screenModel = screenModel,
                    isRefreshing = screenModel.isRefreshing.value,
                    navigator = navigator,
                    uiState = uiState,
                    onPullToRefresh = {
                        screenModel.pullToRefresh()
                    }
                )
            }

            else -> {}
        }
    }

    @Composable
    private fun BuySellRamExecuteSuccessState(
        continueTransaction: () -> Unit,
        onBackHome: () -> Unit
    ) {
        ExecuteTransactionSuccess(
            onClickBack = {},
            textTitle = if (isBuyRam) MR.strings.buy_ram_success.desc()
                .localized() else MR.strings.sell_ram_success.desc().localized(),
            modifier = Modifier.background(MaterialTheme.mangalaColors.bg),
        ) {
            MangalaGradientButton(
                label = if (isBuyRam) MR.strings.continue_buy_ram.desc()
                    .localized() else MR.strings.continue_sell_ram.desc().localized(),
                onClick = continueTransaction,
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            )

            VerticalSpacer(Spacing.SMALL)

            MangalaTextButton(
                label = MR.strings.button_transfer_ram_back_to_home.desc().localized(),
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                onClick = onBackHome
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun BuySellRamScreenSuccessState(
        screenModel: BuySellRamScreenModel,
        uiState: BuySellRamUiState,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
        navigator: Navigator
    ) {
        val uiModel = remember(uiState) { (uiState as? BuySellRamUiState.Success)?.uiModel }
        val pinScreen = remember {
            ScreenRegistry.get(
                SharedScreen.UnlockPinScreen(
                    SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                    onUnlockSuccess = {
                        screenModel.onAuthenticationSuccess(accountName)
                        navigator.pop()
                    },
                    antelopeAccountName = null
                )
            )
        }

        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = {
                onPullToRefresh()
            }
        )

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        if ((uiState as? BuySellRamUiState.Success)?.uiModel?.resourceRequiredBreakdown != null) {
            val uiModel = (uiState as? BuySellRamUiState.Success)?.uiModel

            AntelopeResourceProviderFeeDialog(
                feeBreakdown = uiModel?.resourceRequiredBreakdown,
                resourceRequiredTotal = uiModel?.resourceRequiredTotal,
                onClick = {
                    screenModel.onConfirmResourceProviderFee()
                },
                onDismiss = {
                    screenModel.onDismissTransactionFeeBreakdown()
                }
            )
        }

        LaunchedEffect((uiState as? BuySellRamUiState.Success)?.uiModel?.showPinPrompt) {
            if ((uiState as? BuySellRamUiState.Success)?.uiModel?.showPinPrompt == true) {
                navigator.push(pinScreen)
                screenModel.onPinPromptShown()
            }
        }

        MaxSizeBox(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (uiModel?.isLoading == true) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.mangalaColors.iconPrimary
                )
            }

            MaxSizeColumn(
                Modifier
                    .background(MaterialTheme.mangalaColors.bg)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    },
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = if (isBuyRam) MR.strings.title_buy_ram.desc().localized()
                    else MR.strings.title_sell_ram.desc().localized(),
                    onBackClicked = { navigator.pop() },
                )

                MaxWidthColumn(verticalArrangement = Arrangement.SpaceBetween) {
                    MaxWidthColumn(
                        modifier = Modifier
                            .weight(1f)
                            .pullRefresh(pullRefreshState, enabled = !isRefreshing)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        uiModel?.let {
                            RamInformation(
                                isLoading = uiModel.inputEnabled.not(),
                                modifier = Modifier.padding(Dimensions.Padding.default),
                                colorIconRam = if (isBuyRam) Colors.lightMintGreen else Colors.lightRed,
                                colorTintIconRam = if (isBuyRam) Colors.mintGreen else Colors.red,
                                totalRam = if (isBuyRam) {
                                    uiModel.totalRamString?.let { "$it kb" }
                                } else {
                                    uiModel.ramAvailableString?.let { "$it kb" }
                                },
                                percentageRamUsed = uiModel.percentRamUsageOnTotal?.floatValue(
                                    exactRequired = false
                                ),
                                percentString = uiModel.percentRamUsageOnTotalString,
                                ram24hPnl = uiModel.ram24hPnlFormatted,
                                eosBalance = uiModel.formattedEosBalance,
                                ramPrice = if (uiModel.ramPriceFormatted == null) null else "${uiModel.ramPriceFormatted} " + MR.strings.native_token_divided_to_kb.format(
                                    uiModel.nativeToken
                                ).localized()
                            )
                            InputSectionPercent(
                                isLoading = uiModel.inputEnabled.not(),
                                onValueChange = if (uiModel.isInputtingEos) screenModel::onEosValueChange else screenModel::onRamValueChange,
                                onClickSuggestionInput = screenModel::onSelectSuggestionInput,
                                amountValue = if (uiModel.isInputtingEos) uiModel.formattedInputNativeCoinAmount else uiModel.formattedInputRamAmount,
                                textTitleInput =
                                    if (isBuyRam) {
                                        if (uiModel.isInputtingEos) {
                                            MR.strings.amount_of_native_token_want_to_buy.format(
                                                uiModel.nativeToken
                                            )
                                                .localized()
                                        } else {
                                            MR.strings.amount_of_ram_want_to_buy.desc().localized()
                                        }
                                    } else {
                                        MR.strings.amount_of_ram_want_to_sell.desc().localized()
                                    },
                                listSuggestionInputUiModels = uiModel.suggestionInputUiModels,
                                textNativeToken = if (uiModel.isInputtingEos) uiModel.nativeToken else "kb",
                                iconButtonSwap = {
                                    if (isBuyRam) {
                                        IconButton(
                                            onClick = { screenModel.reverseRamEosInput() },
                                            enabled = uiModel.inputEnabled
                                        ) {
                                            Icon(
                                                imageVector = MangalaWalletPack.Swap,
                                                contentDescription = null,
                                                tint = Colors.white,
                                                modifier = Modifier
                                                    .size(Dimensions.IconButtonSize)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isBuyRam) Colors.mintGreen else Colors.red,
                                                        shape = CircleShape
                                                    )
                                                    .background(
                                                        color = if (isBuyRam) Colors.mintGreen else Colors.red,
                                                        shape = CircleShape
                                                    )
                                                    .padding(Dimensions.Padding.quarter)
                                            )
                                        }
                                    }
                                },
                                textNotify = {
                                    TextDescription2(
                                        text = if (uiModel.inputAmountError != null) uiModel.inputAmountError.resolve()
                                        else if (uiModel.formattedInputNativeCoinAmount.isBlank() && uiModel.formattedInputRamAmount.isBlank()) ""
                                        else if (isBuyRam) {
                                            MR.strings.buy_kb_for_native_token.format(
                                                if (uiModel.isInputtingEos) "~${uiModel.formattedRamAmount}" else uiModel.formattedRamAmount,
                                                if (uiModel.isInputtingEos.not()) "~${uiModel.formattedNativeCoinAmount}" else uiModel.formattedNativeCoinAmount,
                                                uiModel.nativeToken
                                            ).localized()
                                        } else {
                                            MR.strings.sell_kb_for_native_token.format(
                                                uiModel.formattedRamAmount,
                                                uiModel.formattedNativeCoinAmount,
                                                uiModel.nativeToken
                                            ).localized()
                                        },
                                        fontSize = FontType.TINY,
                                        fontWeight = FontWeight.Normal,
                                        color = if (uiModel.inputAmountError != null) MaterialTheme.mangalaColors.buttonDestructiveContainer else MaterialTheme.mangalaColors.textPrimary,
                                        modifier = Modifier.mangalaWalletPlaceholder(uiModel.inputEnabled.not())
                                    )
                                },
                                modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                            )
                            Spacer(modifier = Modifier.height(Spacing.TINY))

                            if (isBuyRam) {
                                BuyForOtherSection(
                                    onIsBuyForOtherChange = screenModel::onIsBuyForOtherChange,
                                    onOtherAccountNameChange = screenModel::onReceiveAccountNameChange,
                                    isBuyForOther = uiModel.isBuyForOther,
                                    otherAccountName = uiModel.receiveAccountName,
                                    isLoading = uiModel.inputEnabled.not(),
                                    receiverAccountNameValidationStatus = uiModel.receiverAccountNameValidationStatus
                                )
                            }
                        }
                    }

                    MangalaGradientButton(
                        label = if (isBuyRam) MR.strings.title_buy_ram.desc()
                            .localized() else MR.strings.title_sell_ram.desc().localized(),
                        onClick = {
                            screenModel.onRequestTransaction()
                        },
                        enabled = uiModel?.isEnableExecuteButton ?: false,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Dimensions.Padding.default,
                                vertical = Dimensions.Padding.default
                            )
                    )
                }
            }

            PullRefreshIndicator(
                isRefreshing,
                pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
                    .padding(top = Dimensions.paddingRefreshingOffsetDefaultTop)
            )
        }
    }

    @Composable
    private fun BuyForOtherSection(
        onIsBuyForOtherChange: (Boolean) -> Unit,
        onOtherAccountNameChange: (String) -> Unit,
        otherAccountName: String,
        isBuyForOther: Boolean,
        isLoading: Boolean,
        receiverAccountNameValidationStatus: RecipientValidationStatus
    ) {
        MaxWidthColumn(
            modifier = Modifier.padding(horizontal = Dimensions.Padding.default),
        ) {
            MaxWidthRow(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.Detective,
                        tint = MaterialTheme.mangalaColors.iconSecondary,
                        contentDescription = null,
                        modifier = Modifier
                            .size(Dimensions.IconSizeNextToText)
                    )
                    Spacer(modifier = Modifier.width(Spacing.XTINY))
                    TextDescription2(
                        text = MR.strings.buy_for_others.desc().localized(),
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                    )
                }
                Switch(
                    enabled = !isLoading,
                    checked = isBuyForOther,
                    onCheckedChange = { onIsBuyForOtherChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.mangalaColors.bgInnerCard,
                        checkedTrackColor = MaterialTheme.mangalaColors.iconPrimary,
                        uncheckedThumbColor = MaterialTheme.mangalaColors.bgInnerCard,
                        uncheckedTrackColor = MaterialTheme.mangalaColors.iconSecondary,
                    )
                )
            }

            if (isBuyForOther) {
                ItemInput(
                    value = otherAccountName,
                    hint = MR.strings.hint_buy_sell_ram_buy_for_other_account_name.desc()
                        .localized(),
                    onValueChange = onOtherAccountNameChange,
                    validationStatus = receiverAccountNameValidationStatus,
                    imeAction = ImeAction.Done,
                    invalidContent = {
                        TextDescription2(
                            text = MR.strings.label_transfer_ram_invalid_account_name.desc()
                                .localized(),
                            fontSize = FontType.TINY,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                        )
                    },
                    onClickScan = {
                        scanQRCode.scanQRCode(object : ScanQRCodeListener {
                            override fun onScanQRCodeResult(result: String) {
                                onOtherAccountNameChange(result)
                            }
                        })
                    }
                )
            }
        }
    }
}

