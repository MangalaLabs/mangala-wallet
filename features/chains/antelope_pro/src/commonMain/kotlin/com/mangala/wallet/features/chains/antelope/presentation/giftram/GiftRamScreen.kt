package com.mangala.wallet.features.chains.antelope.presentation.giftram

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.InputSectionPercent
import com.mangala.wallet.ui.component.ItemInput
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaTopBarTitleInMiddle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.RamInformation
import com.mangala.wallet.ui.component.RamSuggestionInputUiModel
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class GiftRamScreen(
    private val accountName: String
) : BaseScreen<GiftRamScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.GIFT_RAM
    override val screenClassName: String = GiftRamScreen::class.simpleName.orEmpty()

    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun createScreenModel(): GiftRamScreenModel {
        return getScreenModel(parameters = {
            parametersOf(
                accountName
            )
        })
    }

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: GiftRamScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        MangalaBottomSheetNavigator(
            Modifier.background(MaterialTheme.mangalaColors.bg)
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            val pinScreen = remember {
                ScreenRegistry.get(
                    SharedScreen.UnlockPinScreen(
                        SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                        onUnlockSuccess = {
                            screenModel.onAuthenticationSuccess(accountName)
                            bottomSheetNavigator.hide()
                        },
                        antelopeAccountName = null
                    )
                )
            }

            val uiState = screenModel.uiState.collectAsStateMultiplatform().value

            LaunchedEffect((uiState as? GiftRamUiState.Success)?.uiModel?.showPinPrompt) {
                if ((uiState as? GiftRamUiState.Success)?.uiModel?.showPinPrompt == true) {
                    bottomSheetNavigator.show(pinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState as? GiftRamUiState.Success)?.uiModel?.resourceRequiredBreakdown != null) {
                val uiModel = (uiState as? GiftRamUiState.Success)?.uiModel

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

            when (uiState) {
                is GiftRamUiState.ExecuteRamTransferSuccess -> {
                    ExecuteTransactionSuccess(
                        onClickBack = { navigator.pop() },
                        textTitle = MR.strings.message_gift_ram_success.desc().localized()
                    ) {
                        MangalaGradientButton(
                            label = MR.strings.button_gift_ram_continue.desc().localized(),
                            onClick = { screenModel.continueTransaction() },
                            enabled = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        VerticalSpacer(Spacing.SMALL)
                        MangalaTextButton(
                            label = MR.strings.button_gift_ram_back_to_home.desc().localized(),
                            enabled = true,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = navigator::popUntilRoot
                        )
                    }
                }

                is GiftRamUiState.Success -> {
                    RamGiftScreen(
                        uiState,
                        onBackClicked = {
                            navigator.pop()
                        },
                        onRamAmountChange = {
                            screenModel.onRamAmountChange(it)
                        },
                        onRecipientAccountChange = {
                            screenModel.onRecipientAccountChange(it)
                        },
                        onMemoChange = {
                            screenModel.onMemoChange(it)
                        },
                        onClickTransfer = {
                            screenModel.onRequestTransaction()
                        },
                        onCLickSuggestionInput = {
                            screenModel.onSelectSuggestionInput(it)
                        },
                        isRefreshing = screenModel.isRefreshing.collectAsStateMultiplatform().value,
                        onPullToRefresh = {
                            screenModel.pullToRefresh()
                        }
                    )
                }

                is GiftRamUiState.Error -> {
                    TextNormal(
                        text = uiState.message,
                        fontSize = FontType.LARGE,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.mangalaColors.buttonDestructiveContainer
                    )
                }

                else -> {}
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun RamGiftScreen(
        uiState: GiftRamUiState,
        onBackClicked: () -> Unit,
        onRamAmountChange: (String) -> Unit,
        onRecipientAccountChange: (String) -> Unit,
        onMemoChange: (String) -> Unit,
        onClickTransfer: () -> Unit,
        onCLickSuggestionInput: (RamSuggestionInputUiModel) -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit,
    ) {

        val uiModel = remember(uiState) { (uiState as? GiftRamUiState.Success)?.uiModel }
        val isLoading = uiModel?.accountInfo == null

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = {
                onPullToRefresh()
            }
        )

        MaxSizeBox(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .pointerInput(Unit) {
                    detectTapGestures() {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            if (uiModel?.isLoading == true) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.mangalaColors.iconPrimary
                )
            }

            MaxSizeColumn {
                MangalaTopBarTitleInMiddle(
                    isLoading = false,
                    titleTopBar = MR.strings.title_gift_ram.desc().localized(),
                    onBackClicked = { onBackClicked() }
                )
                uiModel?.let {
                    MaxSizeColumn(
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
                    ) {
                        MaxWidthColumn(
                            Modifier.verticalScroll(rememberScrollState())
                                .pullRefresh(pullRefreshState, enabled = !isRefreshing)
                        ) {
                            VerticalSpacer(Dimensions.Height.xxxLarge)

                            RamInformation(
                                isLoading = isLoading,
                                colorIconRam = Colors.lightMintGreen,
                                colorTintIconRam = Colors.mintGreen,
                                totalRam = WrappedStringResource.StringRes(
                                    MR.strings.gift_total_ram,
                                    uiModel.ramAvailableString
                                ).resolve(),
                                percentageRamUsed = uiModel.percentRamUsageOnTotal,
                                percentString = uiModel.percentRamUsageOnTotalString
                            )

                            VerticalSpacer(Dimensions.Height.xxxxxxLarge)

                            InputSectionPercent(
                                isLoading = isLoading,
                                onValueChange = onRamAmountChange,
                                onClickSuggestionInput = { onCLickSuggestionInput(it) },
                                amountValue = uiModel.ramAmountText ?: "",
                                textTitleInput = MR.strings.gift_ram_input_selection_percent.desc()
                                    .localized(),
                                listSuggestionInputUiModels = uiModel.suggestionInputUiModels,
                                textNativeToken = "kb",
                                iconButtonSwap = {},
                                textNotify = {
                                    TextDescription2(
                                        text = uiModel.inputAmountError?.resolve().orEmpty(),
                                        fontSize = FontType.TINY,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                                    )
                                },
                                imeAction = ImeAction.Next
                            )

                            VerticalSpacer(Dimensions.Height.xLarge)

                            InputSectionGift(
                                uiModel = uiModel,
                                onRecipientAccountChange = onRecipientAccountChange,
                                onMemoChange = onMemoChange,
                            )
                        }

                        MangalaGradientButton(
                            label = MR.strings.button_gift_ram_content.desc().localized(),
                            onClick = onClickTransfer,
                            enabled = uiModel.isEnableExecuteButton,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = Dimensions.Padding.default)
                        )
                    }
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
    fun InputSectionGift(
        uiModel: GiftRamUiModel,
        onRecipientAccountChange: (String) -> Unit,
        onMemoChange: (String) -> Unit,
    ) {
        ItemInput(
            value = uiModel.recipientAccountText ?: "",
            onValueChange = onRecipientAccountChange,
            hint = MR.strings.hint_gift_ram_enter_account_name.desc().localized(),
            validationStatus = uiModel.recipientAccountNameValidationStatus,
            imeAction = ImeAction.Next,
            invalidContent = {
                TextDescription2(
                    text = MR.strings.label_gift_ram_invalid_account_name.desc().localized(),
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Normal,
                    color = Colors.main2,
                )
            },
            onClickScan = {
                scanQRCode.scanQRCode(object : ScanQRCodeListener {
                    override fun onScanQRCodeResult(result: String) {
                        onRecipientAccountChange(result)
                    }
                })
            },
            titleContent = {
                TextDescription2(
                    text = MR.strings.label_gift_ram_recipient.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary
                )
            }
        )

        VerticalSpacer(Dimensions.Height.medium)
        ItemInput(
            hint = MR.strings.all_memo.desc().localized(),
            value = uiModel.memoText ?: "",
            onValueChange = onMemoChange,
            imeAction = ImeAction.Done,
            onClickScan = {
                scanQRCode.scanQRCode(object : ScanQRCodeListener {
                    override fun onScanQRCodeResult(result: String) {
                        onMemoChange(result)
                    }
                })
            },
            titleContent = {
                TextDescription2(
                    text = MR.strings.label_gift_ram_memo.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textPrimary
                )
            }
        )
    }
}