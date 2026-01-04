package com.mangala.wallet.features.swap_base.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowDown
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextDescription2WithInfoCircle
import com.mangala.wallet.ui.TextFieldState
import com.mangala.wallet.ui.TextNormal2
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.modifier.noRippleClickable
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.utils.ext.removeTrailingZeroes
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

abstract class BasePreviewSwapTokenScreen(
    private val accountAddress: String,
    private val accountName: String,
    private val accountId: String,
    private val tokenFromSymbol: String,
    private val tokenFromLogoUrl: String,
    private val tokenToSymbol: String,
    private val tokenToLogoUrl: String,
    private val tradeData: TradeData,
    private val dex: Dex
) : BaseScreen<PreviewSwapTokenScreenModel>() {

    // TODO: onUnlockSuccess should only be for the specific pro variant implementation
    // TODO: SignTransactionRequest should only be for the specific ui variant implementation
    // Since we share transactionData building logic for both approve and swap transaction, we can use the same function
    abstract fun onClickExecuteTransaction(navigator: Navigator, signTransactionRequest: SignTransactionRequest, onUnlockSuccess: () -> Unit)

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): PreviewSwapTokenScreenModel = getScreenModel(parameters = {
        parametersOf(
            tradeData,
            accountAddress,
            accountId,
            dex
        )
    })

    @Composable
    override fun ScreenContent(screenModel: PreviewSwapTokenScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        MaxSizeColumn(Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (uiState) {
                is PreviewSwapUiState.Success -> {
                    val uiModel = uiState.previewSwapUiModel
                    val txHash = uiModel.txHash
                    if (txHash?.isEmpty() != false) {
                        PreviewSwapTokenScreen(
                            screenModel = screenModel,
                            uiModel = uiModel,
                            onBackClicked = {
                                navigator.pop()
                            },
                            onExecuteSwapClick = {
                                val signTransactionRequest = screenModel.getSignTransactionRequest()
                                signTransactionRequest?.let {
                                    onClickExecuteTransaction(navigator, it, onUnlockSuccess = {
                                        screenModel.executeSwap()
                                    })
                                }
                            }
                        )
                    } else {
                        SwapSuccessScreen(txHash) {
                            navigator.popUntilRoot()
                        }
                    }
                }

                is PreviewSwapUiState.NeedApprove -> {
                    val uiModel = uiState.previewSwapApproveUiModel
                    ApproveTokenScreen(
                        screenModel = screenModel,
                        uiModel = uiModel,
                        onBackClicked = {
                            navigator.pop()
                        },
                        onApproveClicked = {
                            val signTransactionRequest = screenModel.getApproveSignTransactionRequest()
                            signTransactionRequest?.let {
                                onClickExecuteTransaction(navigator, it, onUnlockSuccess = {
                                    screenModel.approve()
                                })
                            }
                        }
                    )
                }

                else -> MangalaCircularProgressIndicatorFullScreen()
            }
        }
    }

    @Composable
    private fun SwapSuccessScreen(txHash: String, onDismiss: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.cloudGray)
                .padding(Dimensions.Padding.default),
        ) {
            MangalaWalletTopBar(
                text = "",
                navigationIcon = {
                    IconButton(onClick = {
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = MangalaWalletPack.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(Dimensions.IconButtonSize)
                        )
                    }
                }
            )
            Text("Transaction sent! $txHash") // TODO: Extract string resource when UI is finalized
        }
    }

    @Composable
    private fun ApproveTokenScreen(
        screenModel: PreviewSwapTokenScreenModel,
        uiModel: PreviewSwapApproveUiModel,
        onBackClicked: () -> Unit,
        onApproveClicked: () -> Unit,
    ) {
        val focusManager = LocalFocusManager.current
        val spendingCap = screenModel.spendingCap.collectAsStateMultiplatform().value
        val transactionFee = uiModel.selectedTransactionFeeOption?.transactionFeeValueString ?: ""
        val transactionFeeFiat = uiModel.selectedTransactionFeeOption?.transactionFeeFiatValueString ?: ""
        val borderColor = when (uiModel.spendingCapTextFieldState) {
            TextFieldState.Empty -> MaterialTheme.colors.secondary
            TextFieldState.Correct -> MaterialTheme.colors.secondary
            TextFieldState.Wrong -> MaterialTheme.colors.error
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.cloudGray)
                .noRippleClickable {
                    focusManager.clearFocus()
                }
        ) {
            MangalaWalletTopBar(
                text = MR.strings.label_swap.desc().localized(),
                onBackClicked = onBackClicked,
            )

            Column(
                modifier = Modifier
                    .padding(Dimensions.Padding.default),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TextNormal2(
                        text = MR.strings.title_preview_swap_approve.desc().localized(),
                        color = Colors.main1Text,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    MaxWidthRow(horizontalArrangement = Arrangement.Center) {
                        TokenItem(
                            amount = "",
                            tokenSymbol = tokenFromSymbol,
                            tokenLogoUrl = tokenFromLogoUrl,
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.XSMALL))

                    OutlinedTextField(
                        value = spendingCap,
                        onValueChange = screenModel::onSpendingCapChanged,
                        trailingIcon = {
                            TextDescription2(
                                text = MR.strings.message_send_token_max.desc().localized(),
                                color = Colors.teal,
                                modifier = Modifier
                                    .clickable {
                                        screenModel.setMaxSpendingCap()
                                    }
                            )
                        },
                        modifier = Modifier
                            .onFocusChanged {
                                screenModel.onFocusSpendingCapChanged(it.isFocused)
                            }
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.Medium),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = borderColor,
                            unfocusedIndicatorColor = MaterialTheme.colors.secondary,
                            cursorColor = Color.Black
                        ),
                        textStyle = TextStyle.Default.copy(
                            color = if (uiModel.spendingCapTextFieldState == TextFieldState.Wrong) Colors.main2 else Colors.main1Text,
                            fontWeight = FontWeight.Medium,
                            fontSize = FontType.SMALL,
                            fontFamily = getSfProFamilyFont(FontWeight.Medium)
                        ),
                        placeholder = {
                            TextDescription2(
                                text = "0",
                                color = Colors.caption,
                                fontWeight = FontWeight.Medium,
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            autoCorrect = false,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        isError = uiModel.spendingCapTextFieldState == TextFieldState.Wrong,
                    )

                    if (uiModel.spendingCapTextFieldState == TextFieldState.Wrong){
                        Spacer(modifier = Modifier.height(Spacing.XTINY))

                        TextDescription2(
                            text = MR.strings.message_preview_swap_approve_error_approve_less_than_swap.desc()
                                .localized(),
                            color = Colors.main2
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.MEDIUM))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(CornerRadius.Tiny)
                            )
                            .padding(
                                horizontal = Dimensions.Padding.default,
                                vertical = Dimensions.Padding.half
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextDescription2(
                            text = MR.strings.title_preview_swap_selected_account.desc()
                                .localized(),
                            color = Colors.main1Text
                        )

                        Spacer(modifier = Modifier.width(Spacing.SMALL))

                        TextDescription2(
                            text = "$accountName (${getFormattedAccountId()})",
                            color = Colors.caption,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.SMALL))

                    if (!uiModel.isFocus){
                        Column(
                            modifier = Modifier.background(
                                color = Color.White,
                                shape = RoundedCornerShape(CornerRadius.Tiny)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = Dimensions.Padding.default,
                                        vertical = Dimensions.Padding.half
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextDescription2WithInfoCircle(
                                    text = MR.strings.title_preview_swap_selected_dex.desc()
                                        .localized()
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RemoteImage(
                                        url = dex.urlImage,
                                        modifier = Modifier.size(Dimensions.IconSize)
                                    )

                                    Spacer(modifier = Modifier.width(Spacing.TINY))

                                    TextDescription2(
                                        dex.name,
                                        color = Colors.caption
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(1.dp).background(Colors.cloudGray))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = Dimensions.Padding.default,
                                        vertical = Dimensions.Padding.half
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextDescription2WithInfoCircle(
                                    text = MR.strings.title_preview_swap_gas_fee.desc().localized(),
                                    color = if (uiModel.isInsufficientCoinToPayForGas) Colors.main2
                                    else Colors.main1Text
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    TextDescription2(
                                        transactionFee,
                                        color = if (uiModel.isInsufficientCoinToPayForGas) Colors.main2
                                        else Colors.caption
                                    )

                                    Spacer(modifier = Modifier.height(Spacing.XTINY))

                                    TextDescription2(
                                        transactionFeeFiat,
                                        color = if (uiModel.isInsufficientCoinToPayForGas) Colors.main2
                                        else Colors.caption
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.XSMALL))

                TextDescription2(
                    text = MR.strings.message_preview_swap_approve.desc().localized(),
                    color = Colors.main1Text
                )

                Spacer(Modifier.weight(1f))

                ButtonNormal(
                    text = when{
                        uiModel.isFocus -> MR.strings.all_continue.desc().localized()
                        uiModel.isInsufficientCoinToPayForGas -> MR.strings.button_preview_swap_error_not_enough_pay_gas.desc().localized()
                        else -> MR.strings.button_preview_swap_approve.desc().localized()
                    },
                    onClick = {
                        if (uiModel.isFocus) focusManager.clearFocus()
                        else onApproveClicked()
                    },
                    fontSize = FontType.REGULAR,
                    buttonModifier = Modifier.fillMaxWidth(),
                    enabled = uiModel.isEnableApproveButton
                )
            }
        }
    }

    @Composable
    private fun PreviewSwapTokenScreen(
        screenModel: PreviewSwapTokenScreenModel,
        uiModel: PreviewSwapUiModel,
        onBackClicked: () -> Unit,
        onExecuteSwapClick: () -> Unit,
    ) {
        val amountIn = screenModel.amountIn.collectAsStateMultiplatform().value
        val amountOut = screenModel.amountOut.collectAsStateMultiplatform().value
        val transactionFee = uiModel.selectedTransactionFeeOption?.transactionFeeValueString ?: ""
        val transactionFeeFiat = uiModel.selectedTransactionFeeOption?.transactionFeeFiatValueString ?: ""

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.cloudGray)
        ) {
            MangalaWalletTopBar(
                text = MR.strings.label_swap.desc().localized(),
                onBackClicked = onBackClicked,
            )

            Column(
                modifier = Modifier
                    .padding(Dimensions.Padding.default),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    TokenItem(
                        amount = amountIn?.scale(4)?.removeTrailingZeroes()?.toPlainString()
                            ?: "Error",
                        tokenSymbol = tokenFromSymbol,
                        tokenLogoUrl = tokenFromLogoUrl
                    )

                    Spacer(modifier = Modifier.height(Spacing.XSMALL))

                    Icon(
                        imageVector = MangalaWalletPack.ArrowDown,
                        contentDescription = null,
                        tint = Colors.main1Text,
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    )

                    Spacer(modifier = Modifier.height(Spacing.XSMALL))

                    TokenItem(
                        amount = amountOut?.scale(4)?.removeTrailingZeroes()?.toPlainString()
                            ?: "Error",
                        tokenSymbol = tokenToSymbol,
                        tokenLogoUrl = tokenToLogoUrl
                    )

                    Spacer(modifier = Modifier.height(Spacing.XBASE))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(CornerRadius.Tiny)
                            )
                            .padding(
                                horizontal = Dimensions.Padding.default,
                                vertical = Dimensions.Padding.half
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextDescription2(
                            text = MR.strings.title_preview_swap_selected_account.desc()
                                .localized(),
                            color = Colors.main1Text
                        )

                        Spacer(modifier = Modifier.width(Spacing.SMALL))

                        TextDescription2(
                            text = "$accountName (${getFormattedAccountId()})",
                            color = Colors.caption,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.SMALL))

                    Column(
                        modifier = Modifier.background(
                            color = Color.White,
                            shape = RoundedCornerShape(CornerRadius.Tiny)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Dimensions.Padding.default,
                                    vertical = Dimensions.Padding.half
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextDescription2WithInfoCircle(
                                text = MR.strings.title_preview_swap_selected_dex.desc().localized()
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RemoteImage(
                                    url = dex.urlImage,
                                    modifier = Modifier.size(Dimensions.IconSize)
                                )

                                Spacer(modifier = Modifier.width(Spacing.TINY))

                                TextDescription2(
                                    dex.name,
                                    color = Colors.caption
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(1.dp).background(Colors.cloudGray))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Dimensions.Padding.default,
                                    vertical = Dimensions.Padding.half
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextDescription2WithInfoCircle(
                                text = MR.strings.title_preview_swap_max_slippage.desc().localized()
                            )

                            TextDescription2(
                                tradeData.options.allowedSlippagePercent.scale(1)
                                    .toPlainString() + "%",
                                color = Colors.caption
                            )
                        }

                        Spacer(modifier = Modifier.height(1.dp).background(Colors.cloudGray))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Dimensions.Padding.default,
                                    vertical = Dimensions.Padding.half
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextDescription2WithInfoCircle(
                                text = MR.strings.title_preview_swap_gas_fee.desc().localized(),
                                color = if (uiModel.isInsufficientCoinToPayForGas) Colors.main2
                                else Colors.main1Text
                            )

                            Column(horizontalAlignment = Alignment.End) {
                                TextDescription2(
                                    transactionFee,
                                    color = if (uiModel.isInsufficientCoinToPayForGas) Colors.main2
                                    else Colors.caption
                                )

                                Spacer(modifier = Modifier.height(Spacing.XTINY))

                                TextDescription2(
                                    transactionFeeFiat,
                                    color = if (uiModel.isInsufficientCoinToPayForGas) Colors.main2
                                    else Colors.caption
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                ButtonNormal(
                    text = if (uiModel.isInsufficientCoinToPayForGas) {
                        MR.strings.button_preview_swap_error_not_enough_pay_gas.desc().localized()
                    } else {
                        MR.strings.button_preview_swap_confirm_swap.desc().localized()
                    },
                    onClick = onExecuteSwapClick,
                    fontSize = FontType.REGULAR,
                    buttonModifier = Modifier.fillMaxWidth(),
                    enabled = uiModel.isEnableSwapConfirmButton
                )
            }
        }
    }

    @Composable
    private fun TokenItem(amount: String, tokenSymbol: String, tokenLogoUrl: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RemoteImage(
                url = tokenLogoUrl,
                modifier = Modifier.size(Dimensions.IconNormalSize)
            )

            Spacer(modifier = Modifier.width(Spacing.XSMALL))

            TextTitle4(
                text = amount,
                color = Colors.main1Text
            )

            Spacer(modifier = Modifier.width(Spacing.TINY))

            TextTitle4(
                text = tokenSymbol,
                color = Colors.main1Text
            )
        }
    }

    private fun getFormattedAccountId(): String =
        Address(accountAddress).eip55.take(8) + "..." + Address(accountAddress).eip55.takeLast(8)
}