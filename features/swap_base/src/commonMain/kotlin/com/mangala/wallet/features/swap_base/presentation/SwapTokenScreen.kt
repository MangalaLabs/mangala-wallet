package com.mangala.wallet.features.swap_base.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.swap_base.presentation.selecttoken.SelectTokenScreen
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Swap
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextDescription2WithInfoCircle
import com.mangala.wallet.ui.TextFieldState
import com.mangala.wallet.ui.TextNormal2
import com.mangala.wallet.ui.TextTitle2
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletDropDown
import com.mangala.wallet.ui.component.MangalaWalletDropDownWithAccountAddressImage
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.getSfProFamilyFont
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.navigation.args.Fraction
import com.mangala.wallet.ui.utils.navigation.args.Pair
import com.mangala.wallet.ui.utils.navigation.args.PreviewSwapTokenScreenArgs
import com.mangala.wallet.ui.utils.navigation.args.Route
import com.mangala.wallet.ui.utils.navigation.args.Token
import com.mangala.wallet.ui.utils.navigation.args.TokenAmount
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.uniswap.TradeError
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format

class SwapTokenScreen : BaseScreen<SwapTokenScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SWAP_TOKEN
    override val screenClassName: String = SwapTokenScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): SwapTokenScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: SwapTokenScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        MaxSizeColumn(
            Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            SwapTokenScreen(
                screenModel = screenModel,
                onClickPreviewSwap = { uiModel ->
                    screenModel.tradeData?.let {
                        val screen = ScreenRegistry.get(
                            SharedScreen.PreviewSwapTokenScreen(
                                constructPreviewSwapTokenScreenArgs(
                                    uiModel,
                                    it
                                )
                            )
                        )
                        navigator.push(screen)
                    }
                },
                onClickSelectToken = { uiModel, isFromToken ->
                    navigator.push(
                        SelectTokenScreen(
                            accountAddress = uiModel.selectedAccount.bip44Address,
                            accountId = uiModel.selectedAccount.account.id,
                            blockChainUid = screenModel.selectedNetwork.blockChainUid,
                            isFromToken = isFromToken,
                            selectedTokenAddress = if (isFromToken) uiModel.selectedFromToken.address else uiModel.selectedToToken.address,
                            onSelectToken = if (isFromToken) screenModel::onSelectFromToken else screenModel::onSelectToToken
                        )
                    )
                }
            )
        }
    }

    @Composable
    private fun SwapTokenScreen(
        screenModel: SwapTokenScreenModel,
        onClickPreviewSwap: (SwapTokenUiModel) -> Unit,
        onClickSelectToken: (SwapTokenUiModel, Boolean) -> Unit
    ) {
        when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
            is SwapTokenScreenUiState.Loading -> {
//                Temp loading UI
                MangalaCircularProgressIndicatorFullScreen()
            }

            is SwapTokenScreenUiState.Success -> {
                SwapTokenScreenSuccessState(
                    screenModel = screenModel,
                    uiState = uiState,
                    onClickPreviewSwap = onClickPreviewSwap,
                    onClickSelectToken = onClickSelectToken
                )
            }

            is SwapTokenScreenUiState.Error -> {
//                Temp error UI
                TextTitle4(text = uiState.message.resolve())
            }
        }
    }

    @Composable
    private fun SwapTokenScreenSuccessState(
        screenModel: SwapTokenScreenModel,
        uiState: SwapTokenScreenUiState.Success,
        onClickPreviewSwap: (SwapTokenUiModel) -> Unit,
        onClickSelectToken: (SwapTokenUiModel, Boolean) -> Unit
    ) {
        val listAccount = screenModel.listAccount.collectAsStateMultiplatform().value
        val fromTokenValue = screenModel.fromTokenValue.collectAsStateMultiplatform()
        val toTokenValue = screenModel.toTokenValue.collectAsStateMultiplatform()
        val listDex = screenModel.listDex
        val uiModel = uiState.swapTokenScreenUiModel

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.cloudGray)
                .padding(Dimensions.Padding.default)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Dimensions.Padding.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextTitle4(text = MR.strings.label_swap.desc().localized())

                MangalaWalletDropDown(
                    chosenOptionName = uiModel.selectedDex.name,
                    chosenOptionImageUrl = uiModel.selectedDex.urlImage,
                    listOptionImagesUrl = listDex.map { it.urlImage },
                    listOptionName = listDex.map { it.name },
                    optionImageModifier = Modifier.size(Dimensions.IconButtonSize),
                    dropdownMenuBoxModifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(CornerRadius.Medium))
                        .padding(
                            vertical = Dimensions.Padding.half,
                            horizontal = Dimensions.Padding.small
                        ),
                    onClickOption = { screenModel.onSelectDex(listDex[it]) },
                )
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            MangalaWalletDropDownWithAccountAddressImage(
                chosenOptionName = uiModel.selectedAccount.account.name,
                chosenOptionAddress = uiModel.selectedAccount.bip44Address,
                listOptionAddress = listAccount.map { it.bip44Address },
                listOptionName = listAccount.map { it.account.name },
                textColor = Color.Black,
                onClickOption = { screenModel.onSelectAccount(listAccount[it]) },
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            Box {
                Column {
                    SwapItem(
                        isFromToken = true,
                        value = fromTokenValue.value,
                        inputState = if (uiModel.isInsufficientAmount) TextFieldState.Wrong else TextFieldState.Correct,
                        uiModel = uiModel,
                        onValueChange = screenModel::onFromTokenValueChange,
                        onClickSelectToken = {
                            onClickSelectToken(
                                uiModel,
                                it
                            )
                        }

                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    SwapItem(
                        isFromToken = false,
                        value = toTokenValue.value,
                        uiModel = uiModel,
                        onValueChange = screenModel::onToTokenValueChange,
                        onClickSelectToken = {
                            onClickSelectToken(
                                uiModel,
                                it
                            )
                        }
                    )
                }

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Swap,
                        tint = Colors.second,
                        onClick = screenModel::reverseFromAndToToken,
                        modifier = Modifier
                            .size(Dimensions.IconNormalSize)
                            .background(Color.White, shape = CircleShape)
                            .border(1.dp, Colors.cloudGray, shape = CircleShape)
                    )

                    Spacer(modifier = Modifier.width(Spacing.XBASE))
                }
            }

            Spacer(modifier = Modifier.height(Spacing.XBASE))

            val buttonText = when (uiModel.tradeError) {
                is TradeError.TradeNotFound -> MR.strings.button_swap_error_low_liquidity.desc()
                    .localized()

                else -> MR.strings.button_swap_preview_swap.desc().localized()
            }

            ButtonNormal(
                text = buttonText,
                onClick = {
                    onClickPreviewSwap(uiModel)
                },
                fontSize = FontType.REGULAR,
                buttonModifier = Modifier.fillMaxWidth(),
                enabled = screenModel.tradeData != null && uiModel.isSwapEnabled
            )

            Spacer(modifier = Modifier.height(Spacing.BASE))

            if (uiModel.price.isNotEmpty()) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextDescription2WithInfoCircle(
                        text = MR.strings.message_swap_price.desc().localized(),
                        color = Colors.caption,
                    )

                    TextDescription2(
                        text = uiModel.price,
                        color = Colors.caption
                    )
                }
            }
        }
    }

    @Composable
    private fun SwapItem(
        isFromToken: Boolean,
        value: String,
        inputState: TextFieldState = TextFieldState.Correct,
        uiModel: SwapTokenUiModel,
        onValueChange: (String) -> Unit,
        onClickSelectToken: (Boolean) -> Unit
    ) {
        val shape =
            if (isFromToken) RoundedCornerShape(
                topStart = CornerRadius.Medium,
                topEnd = CornerRadius.Medium
            )
            else RoundedCornerShape(
                bottomStart = CornerRadius.Medium,
                bottomEnd = CornerRadius.Medium
            )
        val selectedToken = if (isFromToken) uiModel.selectedFromToken else uiModel.selectedToToken
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White, shape = shape)
                .padding(Dimensions.Padding.default)
        ) {
            TextDescription2(
                text = if (isFromToken) MR.strings.title_swap_from_token.desc()
                    .localized() else MR.strings.title_swap_to_token.desc().localized(),
                color = Colors.caption
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = value,
                    placeholder = {
                        TextTitle2(
                            text = "0",
                            color = Colors.caption,
                            fontWeight = FontWeight.Medium,
                        )
                    },
                    textStyle = TextStyle.Companion.Default.copy(
                        color = if (inputState == TextFieldState.Wrong) Colors.main2 else Color.Black,
                        fontWeight = FontWeight.Medium,
                        fontSize = FontType.TITLE_2,
                        fontFamily = getSfProFamilyFont(FontWeight.Medium)
                    ),
                    singleLine = true,
                    onValueChange = onValueChange,
                    trailingIcon = {
                        MangalaWalletIconButton(
                            icon = MangalaWalletPack.Clear,
                            tint = Colors.caption,
                            onClick = { onValueChange("") },
                            modifier = Modifier.size(Dimensions.IconSize)
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = Color.Black,
                        errorCursorColor = Colors.main2
                    ),
                    isError = inputState == TextFieldState.Wrong,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.weight(1f)
                )

                LocalImage(
                    imageResource = BlockchainType.Ethereum.localImage,
                    modifier = Modifier.size(Dimensions.IconButtonSize)
                )

                Spacer(modifier = Modifier.width(Spacing.XTINY))

                TextNormal2(
                    text = selectedToken.tokenCode,
                    color = Colors.caption
                )

                Spacer(modifier = Modifier.width(Spacing.XSMALL))

                MangalaWalletIconButton(
                    icon = MangalaWalletPack.ArrowRight,
                    onClick = { onClickSelectToken(isFromToken) },
                )
            }

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            TextDescription2(
                text = MR.strings.message_swap_balance.format(selectedToken.balance).localized(),
                color = if (inputState == TextFieldState.Wrong) Colors.main2 else Colors.caption,
            )
        }
    }

    private fun constructPreviewSwapTokenScreenArgs(
        uiModel: SwapTokenUiModel,
        tradeData: TradeData
    ): PreviewSwapTokenScreenArgs {
        val selectedAccount = uiModel.selectedAccount
        val selectedFromToken = uiModel.selectedFromToken
        val selectedToToken = uiModel.selectedToToken


        return PreviewSwapTokenScreenArgs(
            accountAddress = selectedAccount.bip44Address,
            accountName = selectedAccount.account.name,
            accountId = selectedAccount.account.id,
            tokenFromSymbol = selectedFromToken.tokenCode,
            tokenFromLogoUrl = selectedFromToken.logoUrl,
            tokenToSymbol = selectedToToken.tokenCode,
            tokenToLogoUrl = selectedToToken.logoUrl,
            dex = uiModel.selectedDex.name,
            allowedSlippagePercent = tradeData.options.allowedSlippagePercent,
            feeOnTransfer = tradeData.options.feeOnTransfer,
            route = tradeData.trade.route.toRouteNavArg(),
            tokenAmountIn = tradeData.trade.tokenAmountIn.toTokenAmountNavArg(),
            tokenAmountOut = tradeData.trade.tokenAmountOut.toTokenAmountNavArg(),
            ttl = tradeData.options.ttl,
            type = tradeData.trade.type.name,
            recipient = tradeData.options.recipient?.hex
        )
    }

    private fun com.mangala.wallet.uniswap.domain.models.Route.toRouteNavArg(): Route {
        return Route(
            pairs = pairs.toPairNavArgList(),
            tokenIn = tokenIn.toTokenNavArg(),
            tokenOut = tokenOut.toTokenNavArg()
        )
    }

    private fun List<com.mangala.wallet.uniswap.domain.models.Pair>.toPairNavArgList(): List<Pair> {
        return map { it.toPairNavArg() }
    }

    private fun com.mangala.wallet.uniswap.domain.models.Pair.toPairNavArg(): Pair {
        return Pair(
            reserve0 = reserve0.toTokenAmountNavArg(),
            reserve1 = reserve1.toTokenAmountNavArg()
        )
    }

    private fun com.mangala.wallet.uniswap.domain.models.TokenAmount.toTokenAmountNavArg(): TokenAmount {
        return TokenAmount(
            token = token.toTokenNavArg(),
            amount = amount.toFractionArg()
        )
    }

    private fun com.mangala.wallet.uniswap.domain.models.Token.toTokenNavArg(): Token {
        return when (this) {
            is com.mangala.wallet.uniswap.domain.models.Token.Ether -> Token.Ether(
                this.address.hex
            )

            is com.mangala.wallet.uniswap.domain.models.Token.Erc20 -> Token.Erc20(
                this.address.hex,
                this.decimals
            )
        }
    }

    private fun com.mangala.wallet.uniswap.domain.models.Fraction.toFractionArg(): Fraction {
        return Fraction(numerator, denominator)
    }
}