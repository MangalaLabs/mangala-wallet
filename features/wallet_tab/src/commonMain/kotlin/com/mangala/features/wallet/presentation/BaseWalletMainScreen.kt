package com.mangala.features.wallet.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import cafe.adriel.voyager.navigator.Navigator
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.chart.Chart
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Elevation
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Cpu
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Exclamation
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Group
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeReceive
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeSend
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Net
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Plus
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Portfolio
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Powerup
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Ram
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AntelopeAccountBalanceUnit
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.TextDescription1
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.TextTitle2_36
import com.mangala.wallet.ui.TextTitle3
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.PagerIndicator
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.component.VisibilityToggleIconButton
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.imageloader.MultiImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.formattedCompactBalance
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.DecimalFormat
import com.mangala.wallet.utils.ext.formatCompact
import com.mangala.wallet.utils.onClickIfNotLoading
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseWalletMainScreen<T : BaseWalletMainScreenModel> : BaseScreen<T>(),
    KoinComponent {

    val scanQRCode: ScanQRCode by inject()

    abstract fun onHandleQrCodeResult(
        result: QrCodeData?,
        navigator: Navigator,
        globalNavigator: Navigator,
        screenModel: T
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BaseWalletMainScreenComposable(
    isRefreshing: Boolean,
    onPullToRefresh: () -> Unit,
    accountsPager: @Composable () -> Unit,
    accountDetails: @Composable() (ColumnScope.() -> Unit),
    customTopBar: @Composable () -> Unit = {}
) {
    val pullRefreshState = PullRefreshState(
        isRefreshing = isRefreshing,
        onRefresh = {
            onPullToRefresh()
        }
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.mangalaColors.bg)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        customTopBar()
        Box(
            modifier = Modifier.pullRefresh(pullRefreshState, !isRefreshing)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                Modifier.fillMaxSize()
            ) {
                Spacer(modifier = Modifier.size(Spacing.SMALL))
                accountsPager()
                Spacer(modifier = Modifier.size(Spacing.BASE))
                accountDetails()
                Spacer(modifier = Modifier.size(Spacing.SMALL))
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun EvmAccountInfoCard(
    accountModel: AccountBlockchainModel,
    cornerButton: @Composable RowScope.() -> Unit = {},
    sideButton: @Composable RowScope.() -> Unit = {},
    cardInfo: @Composable ColumnScope.() -> Unit = {},
    modifier: Modifier = Modifier,
    onClickCopy: () -> Unit,
    onClickShowQr: (AccountBlockchainModel) -> Unit,
    bottomButtons: @Composable RowScope.() -> Unit
) {
    AccountInfoCard(
        accountBasicInfo = {
            EvmAccountAndAddress(
                accountName = accountModel.account.name,
                address = accountModel.formattedBip44Address(),
                cornerButton = cornerButton,
                onClickCopy = onClickCopy,
                onClickShowQr = {
                    onClickShowQr(accountModel)
                }
            )
        },
        sideButton = sideButton,
        cardInfo = cardInfo,
        modifier = modifier,
        bottomButtons = bottomButtons
    )
}

@Composable
fun AntelopeAccountInfoCard(
    uiModel: AntelopeAccountItemUiModel,
    modifier: Modifier = Modifier,
    onToggleBalanceVisible: (Boolean) -> Unit,
    onClickCopy: () -> Unit,
    onClickShowQr: (AntelopeAccountItemUiModel) -> Unit,
    onClickSend: () -> Unit,
    onClickReceive: (AntelopeAccountItemUiModel) -> Unit,
    onClickBuy: () -> Unit,
    onClickPortfolio: (accountName: String, accountLabel: String) -> Unit,
    onClickRecreateAccount: () -> Unit,
    onChangeBalanceUnit: (AntelopeAccountBalanceUnit) -> Unit
) {
    AccountInfoCard(
        accountBasicInfo = {
            AntelopeAccountName(
                accountName = uiModel.account.accountName,
                onClickCopy = onClickCopy,
                additionalButtons = {
                    if (uiModel.account.isTemp.not()) {
                        MangalaWalletIconButton(
                            MangalaWalletPack.Group,
                            onClick = {
                                onClickShowQr(uiModel)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        MangalaWalletIconButton(
                            MangalaWalletPack.Exclamation,
                            onClick = {
                                onClickRecreateAccount()
                            },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
            )
        },
        sideButton = {
            VisibilityToggleIconButton(
                uiModel.isBalanceVisible,
                onClickBalanceVisible = { onToggleBalanceVisible((uiModel.isBalanceVisible).not()) },
                iconButtonModifier = Modifier.size(24.dp),
                modifier = Modifier.size(24.dp)
            )
        },
        cardInfo = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TextTitle2_36(
                            text = uiModel.totalValueFormatted,
                            modifier = Modifier
                                .mangalaWalletPlaceholder(uiModel.totalValueSkeletonVisible)
                                .wrapContentWidth(align = Alignment.Start)
                                .basicMarquee(iterations = Int.MAX_VALUE)
                                .weight(1f, fill = false),
                            color = MaterialTheme.mangalaColors.textPrimary,
                        )
                        Spacer(Modifier.width(Spacing.TINY))
                        TextDescription2(
                            text = uiModel.formattedCoreBalanceSymbol,
                            fontSize = FontType.REGULAR,
                            modifier = Modifier
                                .mangalaWalletPlaceholder(uiModel.coreBalanceSymbolSkeletonVisible)
                                .wrapContentWidth(align = Alignment.Start),
                            color = MaterialTheme.mangalaColors.textSecondary
                        )
                        AntelopeAccountBalanceUnitDropDownMenu(
                            uiModel.coreBalanceSymbol.orEmpty(),
                            onSelectUnit = onChangeBalanceUnit,
                            isEnabled = !uiModel.totalValueSkeletonVisible
                        )
                    }

                    TextDescription2(
                        text = uiModel.fiatValueFormatted,
                        fontSize = FontType.LARGE,
                        modifier = Modifier.mangalaWalletPlaceholder(uiModel.fiatValueSkeletonVisible),
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                }
            }
        },
        modifier = modifier,
        bottomButtons = {
            if (uiModel.account.isTemp) {
                AccountCardButton(
                    MR.strings.button_wallet_main_retry_create.desc().localized(),
                    MangalaWalletPack.Wallet
                ) {
                    onClickRecreateAccount()
                }
            } else {
                // TODO: Use these same buttons for EVM card
                AccountCardButton(
                    MR.strings.button_wallet_main_account_card_portfolio.desc().localized(),
                    MangalaWalletPack.Portfolio
                ) {
                    onClickIfNotLoading(
                        uiModel.totalValueSkeletonVisible
                    ) {
                        onClickPortfolio(
                            uiModel.account.accountName,
                            uiModel.account.accountName,
                        ) // TODO: Pass in correct type of address if needed
                    }
                }
                AccountCardButton(
                    MR.strings.all_send.desc().localized(),
                    MangalaWalletPack.HomeSend
                ) {
                    onClickIfNotLoading(
                        uiModel.totalValueSkeletonVisible
                    ) {
                        onClickSend()
                    }
                }
                AccountCardButton(
                    MR.strings.all_receive.desc().localized(),
                    MangalaWalletPack.HomeReceive
                ) {
                    onClickIfNotLoading(
                        uiModel.totalValueSkeletonVisible
                    ) {
                        onClickReceive(uiModel)
                    }
                }
//            AccountCardButton(
//                MR.strings.button_wallet_main_account_card_buy.desc().localized(),
//                MangalaWalletPack.Buy
//            ) {
//                onClickBuy()
//            }
            }
        }
    )
}

@Composable
private fun AntelopeAccountBalanceUnitDropDownMenu(
    nativeCoinSymbol: String,
    onSelectUnit: (AntelopeAccountBalanceUnit) -> Unit,
    isEnabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .wrapContentSize(Alignment.TopEnd)
    ) {
        IconButton(
            modifier = Modifier.padding(1.dp)
                .width(Dimensions.IconButtonSizeSmall)
                .height(Dimensions.IconButtonSizeSmall),
            onClick = { expanded = !expanded },
            enabled = isEnabled
        ) {
            Icon(
                modifier = Modifier,
                imageVector = MangalaWalletPack.Dropdown,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconSecondary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(color = MaterialTheme.mangalaColors.bgInnerCard)
        ) {
            AntelopeAccountBalanceUnit.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        onSelectUnit(it)
                        expanded = false
                    }
                ) {
                    TextDescription2(
                        text = if (it == AntelopeAccountBalanceUnit.NativeCoin) nativeCoinSymbol
                        else it.symbol,
                        color = MaterialTheme.mangalaColors.textPrimary
                    )
                }
            }
        }
    }
}


@Composable
fun AccountInfoCard(
    accountBasicInfo: @Composable() (ColumnScope.() -> Unit),
    sideButton: @Composable() (RowScope.() -> Unit) = {},
    cardInfo: @Composable() (ColumnScope.() -> Unit) = {},
    modifier: Modifier = Modifier,
    bottomButtons: @Composable() (RowScope.() -> Unit)
) {
    AccountCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                accountBasicInfo()
            }
            sideButton()
        }

        cardInfo()
        Spacer(Modifier.height(Spacing.SMALL))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.BASE),
            modifier = Modifier.fillMaxWidth()
        ) {
            bottomButtons()
        }
    }
}

@Composable
private fun AntelopeAccountName(
    accountName: String,
    additionalButtons: @Composable RowScope.() -> Unit = {},
    onClickCopy: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
    ) {
        TextDescription2(
            accountName,
            color = MaterialTheme.mangalaColors.textPrimary
        )
        MangalaWalletIconButton(
            MangalaWalletPack.Copy,
            onClick = {
                onClickCopy()
            },
            modifier = Modifier.size(16.dp)
        )
        additionalButtons()
    }
}

@Composable
private fun EvmAccountAndAddress(
    accountName: String,
    address: String,
    cornerButton: @Composable() (RowScope.() -> Unit),
    onClickCopy: () -> Unit,
    onClickShowQr: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextDescription1(
            accountName,
            color = MaterialTheme.mangalaColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        cornerButton()
    }
    Spacer(Modifier.height(Spacing.XTINY))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextDescription2(
            address,
            color = MaterialTheme.mangalaColors.textSecondary
        )
        Spacer(Modifier.width(Spacing.TINY))
        MangalaWalletIconButton(
            MangalaWalletPack.Copy,
            onClick = {
                onClickCopy()
            },
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(Spacing.TINY))
        MangalaWalletIconButton(
            MangalaWalletPack.Group,
            onClick = {
                onClickShowQr()
            },
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun AccountCard(
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
    cardVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().then(cardModifier),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.Medium,
        ),
        shape = RoundedCornerShape(CornerRadius.Medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard,
        ),
    ) {
        Column(
            Modifier.then(modifier).padding(Dimensions.Padding.default),
            verticalArrangement = cardVerticalArrangement,
            content = content
        )
    }
}

@Composable
fun CreateWalletCard(
    modifier: Modifier = Modifier,
    onClickCreateWallet: () -> Unit,
    onClickImportWallet: () -> Unit
) {
    AccountCard(modifier) {
        Column {
            // TODO: Handle UI based on network type (for Antelope needs to have extra buttons)
            TextTitle3(MR.strings.title_create_wallet_guide.desc().localized())
            Spacer(Modifier.height(Spacing.BASE))
            Column {
                MangalaGradientButton(
                    label = MR.strings.button_wallet_main_create_wallet.desc().localized(),
                    onClick = onClickCreateWallet,
                    size = MangalaButtonSize.XMedium,
                    modifier = Modifier.fillMaxWidth(),
                    style = MangalaTypography.Size14Medium()
                )
                Spacer(Modifier.height(Spacing.TINY))
                MangalaTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    label = MR.strings.button_wallet_main_import_wallet.desc().localized(),
                    contentColor = MaterialTheme.mangalaColors.textPrimary,
                    size = MangalaButtonSize.XMedium,
                    fontWeight = MangalaTypography.Size14Medium().fontWeight!!,
                    fontSize = MangalaTypography.Size14Medium().fontSize,
                    onClick = onClickImportWallet
                )
            }
        }
    }
}

@Composable
fun AddAccountCard(
    modifier: Modifier = Modifier,
    mainButton: @Composable () -> Unit,
    secondButton: @Composable () -> Unit
) {
    AccountCard(
        modifier = Modifier.background(MaterialTheme.mangalaColors.bgInnerCard),
        cardModifier = modifier,
        cardVerticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            TextTitle4(
                MR.strings.title_wallet_main_add_account.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
            Spacer(Modifier.height(Spacing.XTINY))
            TextDescription2(
                MR.strings.message_wallet_main_add_account.desc().localized(),
                color = MaterialTheme.mangalaColors.textSecondary,
            )
        }
        Spacer(Modifier.height(Spacing.BASE))
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.TINY)) {
            mainButton()
            secondButton()
        }
    }
}

@Composable
fun AccountCardButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(modifier)
            .clickable(
                enabled = !isLoading,
                indication = if (isLoading) LocalIndication.current else null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
    ) {
        Box(
            Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.mangalaColors.bgSwipeAction),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        VerticalSpacer(Spacing.XTINY)

        Text(
            text = label,
            style = MangalaTypography.Size12Regular(),
            color = MaterialTheme.mangalaColors.textPrimary,
        )
    }
}

@Composable
fun EvmAccountsPagerDataState(
    modifier: Modifier = Modifier,
    uiState: BaseWalletMainScreenDataUiState.BaseEvmDataState,
    onAccountChange: (Int) -> Unit,
    accountInfoCardCornerButton: @Composable RowScope.(account: EvmAccountItemUiModel) -> Unit,
    accountInfoCardBottomButtons: @Composable RowScope.(account: EvmAccountItemUiModel) -> Unit,
    accountInfoCardCardInfo: @Composable ColumnScope.(account: EvmAccountItemUiModel) -> Unit,
    accountInfoCardSideButton: @Composable RowScope.(account: EvmAccountItemUiModel) -> Unit,
    addAccountCard: @Composable (Modifier) -> Unit,
    onClickManageAccounts: () -> Unit,
    onClickCopy: () -> Unit,
    onClickShowQr: (AccountBlockchainModel) -> Unit,
    matchCardsHeight: Boolean = false
) {
    val pageCount =
        if (uiState.accounts.isEmpty()) 1 else uiState.accounts.size + 1 // TODO: Set to 20 to test case where we have too many accounts
//                val pagerState = rememberPagerState()
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedAccountIndex, // Can load from state for initial page
        initialPageOffsetFraction = 0f
    ) {
        // provide pageCount
        pageCount
    }
    val localDensity = LocalDensity.current

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            onAccountChange(page)
        }
    }

    var cardMaxHeightDp by remember {
        mutableStateOf(0.dp)
    }

    // Logic to ensure AccountInfoCard and AddAccountCard will have same height regardless of font size change of AccountInfoCard
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = Spacing.TINY,
        userScrollEnabled = true,
        reverseLayout = false,
        contentPadding = PaddingValues(
            start = Dimensions.Padding.default,
            end = Dimensions.Padding.default
        ),
        beyondViewportPageCount = 1,
        pageSize = PageSize.Fill,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = {
            if (it == pageCount - 1) {
                "AddAccountCard"
            } else {
                val id = if (it >= uiState.accounts.size) {
                    it
                } else {
                    uiState.accounts[it].account.account.id
                }
                "AccountInfoCard$id"
            }
        },
        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
            pagerState,
            Orientation.Horizontal
        ),
        pageContent = { pageIndex ->
//                        val pageIndex = it
            if (pageIndex < pageCount - 1) {
                uiState.accounts.getOrNull(pageIndex)?.let {
                    EvmAccountInfoCard(
                        accountModel = it.account,
                        cornerButton = {
                            accountInfoCardCornerButton(it)
                        },
                        cardInfo = {
                            accountInfoCardCardInfo(it)
                        },
                        bottomButtons = {
                            accountInfoCardBottomButtons(it)
                        },
                        sideButton = {
                            accountInfoCardSideButton(it)
                        },
                        modifier = Modifier
//                            .background(
//                                colorAccount(
//                                    uiState,
//                                    it
//                                )
//                            )
                            .onGloballyPositioned {
                                val temp = with(localDensity) { it.size.height.toDp() }
                                cardMaxHeightDp = max(
                                    cardMaxHeightDp,
                                    temp
                                ) // Logic to ensure AccountInfoCard and AddAccountCard will have same height regardless of font size change of AccountInfoCard
                            },
                        onClickCopy = onClickCopy,
                        onClickShowQr = onClickShowQr
                    )
                }
            } else {
                addAccountCard(if (!matchCardsHeight) Modifier.height(cardMaxHeightDp) else Modifier)
            }
        }
    )
    Spacer(modifier = Modifier.height(Spacing.TINY))
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.default),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val coroutineScope = rememberCoroutineScope()
        PagerIndicator(
            pageCount = pageCount,
            modifier = Modifier.weight(1f).padding(start = Spacing.SMALL),
            pagerState = pagerState,
            indicatorCount = 5,
            indicatorSize = 8.dp,
            activeColor = MaterialTheme.mangalaColors.iconPrimary,
            inActiveColor = MaterialTheme.mangalaColors.iconSecondary,
            space = 6.dp,
            lastItemComposable = { isSelected, modifier ->
                Icon(
                    MangalaWalletPack.Plus,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.mangalaColors.iconPrimary else MaterialTheme.mangalaColors.iconSecondary,
                    modifier = modifier
                )
            },
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(it)
                }
            },
        )

        MangalaTextButton(
            label = MR.strings.all_manage_accounts.desc().localized(),
            enabled = uiState.manageAccountButtonEnabled,
            contentColor = MaterialTheme.mangalaColors.textPrimary,
            modifier = Modifier,
            size = MangalaButtonSize.Small,
            style = MangalaTypography.Size12Medium(),
            onClick = {
                onClickManageAccounts()
            }
        )
    }
}

@Composable
fun AntelopeAccountsPagerDataState(
    modifier: Modifier = Modifier,
    uiState: BaseWalletMainScreenDataUiState.BaseAntelopeDataState,
    onToggleBalanceVisible: (Boolean) -> Unit,
    onAccountChange: (Int) -> Unit,
    addAccountCard: @Composable (Modifier) -> Unit,
    onClickManageAccounts: () -> Unit,
    onClickCopy: () -> Unit,
    onClickShowQr: (AntelopeAccountItemUiModel) -> Unit,
    onClickSend: () -> Unit,
    onClickReceive: (AntelopeAccountItemUiModel) -> Unit,
    onClickBuy: () -> Unit,
    onClickPortfolio: (accountName: String, accountLabel: String) -> Unit,
    onChangeBalanceUnit: (AntelopeAccountBalanceUnit) -> Unit,
    onClickRecreateAccount: () -> Unit,
    matchCardsHeight: Boolean = false
) {
    val pageCount =
        if (uiState.accounts.isEmpty()) 1 else uiState.accounts.size + 1 // TODO: Set to 20 to test case where we have too many accounts
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedAccountIndex,
        initialPageOffsetFraction = 0f
    ) {
        // provide pageCount
        pageCount
    }
    val localDensity = LocalDensity.current

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.distinctUntilChanged().collect { page ->
            onAccountChange(page)
        }
    }

    var cardMaxHeightDp by remember {
        mutableStateOf(0.dp)
    }

    // Logic to ensure AccountInfoCard and AddAccountCard will have same height regardless of font size change of AccountInfoCard
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = Spacing.TINY,
        userScrollEnabled = true,
        reverseLayout = false,
        contentPadding = PaddingValues(
            start = Dimensions.Padding.default,
            end = Dimensions.Padding.default
        ),
        beyondViewportPageCount = 1,
        pageSize = PageSize.Fill,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = {
            if (it == pageCount - 1) {
                "AddAccountCard"
            } else {
                val id = if (it >= uiState.accounts.size) {
                    it
                } else {
                    uiState.accounts[it].account.accountName
                }
                "AccountInfoCard$id"
            }
        },
        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
            pagerState,
            Orientation.Horizontal
        ),
        pageContent = { pageIndex ->
//                        val pageIndex = it
            if (pageIndex < pageCount - 1) {
                uiState.accounts.getOrNull(pageIndex)?.let {
                    AntelopeAccountInfoCard(
                        uiModel = it,
                        onToggleBalanceVisible = onToggleBalanceVisible,
                        onClickCopy = onClickCopy,
                        onClickShowQr = onClickShowQr,
                        onClickSend = onClickSend,
                        onClickReceive = onClickReceive,
                        onClickBuy = onClickBuy,
                        onClickPortfolio = onClickPortfolio,
                        onClickRecreateAccount = onClickRecreateAccount,
                        onChangeBalanceUnit = onChangeBalanceUnit,
                        modifier = Modifier
//                            .background(
//                                colorAccount(
//                                    uiState,
//                                    it
//                                )
//                            )
                            .onGloballyPositioned {
                                val temp = with(localDensity) { it.size.height.toDp() }
                                cardMaxHeightDp = max(
                                    cardMaxHeightDp,
                                    temp
                                ) // Logic to ensure AccountInfoCard and AddAccountCard will have same height regardless of font size change of AccountInfoCard
                            },
                    )
                }
            } else {
                addAccountCard(if (!matchCardsHeight) Modifier.height(cardMaxHeightDp) else Modifier)
            }
        }
    )
    Spacer(modifier = Modifier.height(Spacing.SMALL))
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.default),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val coroutineScope = rememberCoroutineScope()
        PagerIndicator(
            pageCount = pageCount,
            modifier = Modifier.weight(1f).padding(start = Spacing.SMALL),
            pagerState = pagerState,
            indicatorCount = 5,
            indicatorSize = 8.dp,
            activeColor = MaterialTheme.mangalaColors.iconPrimary,
            inActiveColor = MaterialTheme.mangalaColors.iconSecondary,
            space = 6.dp,
            lastItemComposable = { isSelected, modifier ->
                Icon(
                    MangalaWalletPack.Plus,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.mangalaColors.iconPrimary else MaterialTheme.mangalaColors.iconSecondary,
                    modifier = modifier
                )
            },
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(it)
                }
            },
        )
        //   Temporarily hide the "Manage accounts" button for Antelope
//        MangalaTextButton(
//            MR.strings.all_manage_accounts.desc().localized(),
//            isEnabled = uiState.manageAccountButtonEnabled
//        ) {
//            onClickManageAccounts()
//        }
    }
}

@Composable
fun EvmAssetCard(
    balanceModel: TokenBalanceModel,
    fiatCurrencySymbol: String,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    enabled: Boolean = false
) {
    AssetCard(
        symbol = balanceModel.contractSymbol,
        imageSource = ImageSource.Resource(balanceModel.localImage),
        name = balanceModel.contractName,
        formattedPrice = fiatCurrencySymbol + balanceModel.currentPrice.formatCompact(),
        isBalanceVisible = isBalanceVisible,
        onClick = { onClickIfNotLoading(isLoading, onClick) },
        sparklineData = balanceModel.sparklineIn7d?.price,
        priceChangePercentage24h = try {
            balanceModel.priceChangePercentage24h?.let { BigDecimal.parseString(it) }
        } catch (e: Exception) {
            null
        },
        formattedCompactBalance = balanceModel.formattedCompactBalance(),
        isLoading = isLoading,
        enabled = enabled
    )
}

@Composable
fun AntelopeRamAssetCard(
    uiModel: AntelopeAssetsUiModel.RamBalanceUiModel,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean = false
) {
    ProgressBorderCard(
        symbol = uiModel.symbol,
        imageSource = ImageSource.Vector(MangalaWalletPack.Ram),
        name = uiModel.name,
        formattedPrice = uiModel.formattedPrice,
        isBalanceVisible = isBalanceVisible,
        sparklineData = uiModel.sparkline,
        priceChangePercentage24h = uiModel.priceChangePercentage24h?.scale(2),
        formattedCompactBalance = if (uiModel.convertedBalance == null || uiModel.formattedPrice == null) null else "${uiModel.convertedBalance} ${uiModel.unitFormatted}",
        borderWidth = 2.dp,
        progress = uiModel.ramUsedPercentage?.let { it / 100 },
        progressColor = Colors.third,
        noProgressColor = MaterialTheme.mangalaColors.border,
        onClick = { onClickIfNotLoading(isLoading, onClick) },
        isLoading = isLoading,
        enabled = enabled
    )
}

@Composable
fun AntelopeAssetCard(
    uiModel: AntelopeAssetsUiModel.TokenBalanceUiModel,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    enabled: Boolean = false
) {
    AssetCard(
        symbol = uiModel.symbol,
        imageSource = uiModel.iconResource ?: ImageSource.Resource(null),
        name = uiModel.name,
        formattedPrice = uiModel.formattedPrice,
        isBalanceVisible = isBalanceVisible,
        onClick = { onClickIfNotLoading(isLoading, onClick) },
        sparklineData = uiModel.sparkline,
        priceChangePercentage24h = uiModel.priceChangePercentage24h?.scale(2),
        formattedCompactBalance = if (uiModel.balance == null || uiModel.symbol.isEmpty()) null else "${uiModel.balance.formatCompact()} ${uiModel.symbol}",
        isLoading = isLoading,
        enabled = enabled
    )
}

@Composable
fun AntelopeResources(
    uiState: BaseWalletMainScreenDataUiState.BaseAntelopeDataState,
    onClickPowerUp: (accountName: String) -> Unit,
    onClickResourceCard: (isCpu: Boolean, accountName: String) -> Unit,
) {
    val decimalFormat = remember { DecimalFormat("#.##") }
    val currentAccount = uiState.accounts.getOrNull(uiState.selectedAccountIndex)
    val netUsage = currentAccount?.netUsagePercentage
    val cpuUsage = currentAccount?.cpuUsagePercentage
    val isBalanceVisible = uiState.isBalanceVisible
    val isLoading =
        uiState.accounts.getOrNull(uiState.selectedAccountIndex)?.totalValueSkeletonVisible ?: false

    MaxWidthColumn(Modifier.padding(horizontal = Dimensions.Padding.default)) {
        TextNormal(
            MR.strings.title_wallet_main_antelope_resources.desc().localized(),
            color = MaterialTheme.mangalaColors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        VerticalSpacer(Spacing.SMALL)
        MaxWidthRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PowerUpCard(
                isLoading,
            ) {
                onClickIfNotLoading(isLoading) {
                    onClickPowerUp(currentAccount?.account?.accountName ?: "")
                }
            }
            ResourceCard(
                MR.strings.all_antelope_cpu.desc().localized(),
                cpuUsage?.toFloat() ?: 0f,
                MangalaWalletPack.Cpu,
                decimalFormat,
                isBalanceVisible,
                isLoading,
            ) {
                onClickIfNotLoading(isLoading) {
                    onClickResourceCard(true, currentAccount?.account?.accountName.orEmpty())
                }
            }
            ResourceCard(
                MR.strings.all_antelope_net.desc().localized(),
                netUsage?.toFloat() ?: 0f,
                MangalaWalletPack.Net,
                decimalFormat,
                isBalanceVisible,
                isLoading,
            ) {
                onClickIfNotLoading(isLoading) {
                    onClickResourceCard(
                        false,
                        currentAccount?.account?.accountName.orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
fun PowerUpCard(
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(CornerRadius.Small),
        modifier = Modifier.defaultMinSize(108.dp),
        enabled = !isLoading,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.Medium,
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(Dimensions.Padding.default)
        ) {
            Image(
                MangalaWalletPack.Powerup,
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            VerticalSpacer(Spacing.TINY)
            TextTiny(
                text = MR.strings.title_wallet_main_antelope_card_powerup.desc().localized(),
                color = MaterialTheme.mangalaColors.textSecondary
            )
            TextTiny(
                MR.strings.label_wallet_main_antelope_card_powerup.desc().localized(),
                color = MaterialTheme.mangalaColors.textSecondary
            )
        }
    }
}

@Composable
fun ResourceCard(
    resourceName: String,
    percentageUsed: Float,
    icon: ImageVector,
    decimalFormat: DecimalFormat,
    isBalanceVisible: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    // UX Improvement: Animate the percentage change for a smooth visual effect.
    val animatedPercentage by animateFloatAsState(
        targetValue = if (isBalanceVisible)percentageUsed else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "percentageAnimation"
    )

    // UI/UX Fix: Determine the content color dynamically based on the fill percentage.
    // When the wave gets high enough to interfere with the text (e.g., > 35%),
    // we switch to a high-contrast color. Using your theme's dark background color is a great choice.
    val uncoveredColor = MaterialTheme.mangalaColors.textSecondary
    val coveredColor = MaterialTheme.mangalaColors.bgInnerCard

    val resourceNameColor = if (animatedPercentage > 45) coveredColor else uncoveredColor

    val percentageTextColor = if (animatedPercentage > 30) coveredColor else uncoveredColor

    val iconColor = if (animatedPercentage > 55) {
        MaterialTheme.mangalaColors.bgInnerCard // Use the same color for the icon
    } else {
        Color.Unspecified
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(CornerRadius.Small),
        enabled = !isLoading,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.Medium,
        )
    ) {
        val waveBackground = Modifier.drawBehind {
            val fillHeight = size.height * animatedPercentage / 100

            // Only draw the wave if the percentage is greater than 0
            if (fillHeight > 0) {
                // Background wave (lighter)
                drawBackgroundWave(
                    size, fillHeight, 0.05f, 0f,
                    listOf(Color(0xFFD3FFFC), Color(0xFF7ED8D1))
                )
                // Foreground wave (darker)
                drawForegroundWave(
                    size, fillHeight, 0.05f, size.width * 0.05f,
                    listOf(Color(0xFF54CDC2), Color(0xFF7ED8D1))
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center, // Ensures content is centered
            modifier = waveBackground
                .defaultMinSize(minWidth = 108.dp, minHeight = 108.dp) // Ensure a consistent size
                .padding(Dimensions.Padding.default)
        ) {
            // Apply the dynamic color to the icon using a ColorFilter.
            Icon(
                imageVector = icon,
                contentDescription = "$resourceName usage", // Accessibility improvement
                modifier = Modifier.size(32.dp),
                tint = iconColor
            )

            VerticalSpacer(Spacing.TINY)

            // Apply the dynamic color to the text.
            TextTiny(
                text = resourceName,
                color = resourceNameColor,
                fontWeight = FontWeight.Medium
            )

            TextTiny(
                text = StringDesc.ResourceFormatted(
                    MR.strings.all_used,
                    if (isBalanceVisible) decimalFormat.format(animatedPercentage.toDouble()) + "%" else HIDDEN_BALANCE_STRING,
                ).localized(),
                color = percentageTextColor
            )
        }
    }
}

private val CardBorderRadius = Dimensions.Padding.small

@Composable
fun ProgressBorderCard(
    symbol: String,
    imageSource: ImageSource,
    name: String,
    formattedPrice: String?,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    sparklineData: List<Double>?,
    priceChangePercentage24h: BigDecimal?,
    formattedCompactBalance: String?,
    // progress border card
    progressColor: Color,
    noProgressColor: Color,
    progress: Float?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadius.Medium, // Should match AssetCard
    borderWidth: Dp = 2.dp,
    isLoading: Boolean,
    enabled: Boolean = false
) {
    val progressToAngle = remember(progress) { 360 * (progress ?: 0.0f) }
    AssetCard(
        symbol = symbol,
        imageSource = imageSource,
        name = name,
        formattedPrice = formattedPrice,
        isBalanceVisible = isBalanceVisible,
        onClick = onClick,
        sparklineData = sparklineData,
        priceChangePercentage24h = priceChangePercentage24h,
        formattedCompactBalance = formattedCompactBalance,
        isLoading = isLoading,
        enabled = enabled
    ) {
        Card(
            modifier = Modifier
//                .background(Color.Green)
                .fillMaxWidth()
                .padding(borderWidth)
                .drawWithContent {
                    val diameter = size.width * 2
                    drawPieSlice(
                        color = progressColor,
                        startAngle = -90f,
                        sweepAngle = progressToAngle,
                        useCenter = true,
                        diameter = diameter
                    )
                    drawPieSlice(
                        color = noProgressColor,
                        startAngle = -90f + progressToAngle,
                        sweepAngle = 360 - progressToAngle,
                        useCenter = true,
                        diameter = diameter
                    )
                    drawContent()
                },
            shape = RoundedCornerShape(cornerRadius - borderWidth),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            ),
        ) {
            AssetCardContent(
                Modifier.padding(CardBorderRadius - borderWidth), // so that this and the standard AssetCard has the same height with same content
                symbol,
                imageSource,
                name,
                formattedPrice,
                isBalanceVisible,
                sparklineData,
                priceChangePercentage24h,
                formattedCompactBalance,
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun AssetCard(
    symbol: String,
    imageSource: ImageSource,
    name: String,
    formattedPrice: String?,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
    sparklineData: List<Double>?,
    priceChangePercentage24h: BigDecimal?,
    formattedCompactBalance: String?,
    isLoading: Boolean = false,
    enabled: Boolean = false,
    content: @Composable () -> Unit = {
        AssetCardContent(
            symbol = symbol,
            imageSource = imageSource,
            name = name,
            formattedPrice = formattedPrice,
            isBalanceVisible = isBalanceVisible,
            sparklineData = sparklineData,
            priceChangePercentage24h = priceChangePercentage24h,
            formattedCompactBalance = formattedCompactBalance,
            isLoading = isLoading
        )
    },
) {
    Card(
        shape = RoundedCornerShape(CornerRadius.Medium),
        elevation = CardDefaults.cardElevation(
            defaultElevation = Elevation.Medium,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            disabledContainerColor = MaterialTheme.mangalaColors.bgInnerCard
        ),
        modifier = Modifier.width(152.dp),
        onClick = onClick,
        enabled = enabled
    ) {
        content()
    }
}

@Composable
private fun AssetCardContent(
    modifier: Modifier = Modifier.padding(CardBorderRadius),
    symbol: String,
    imageSource: ImageSource,
    name: String,
    formattedPrice: String?,
    isBalanceVisible: Boolean,
    sparklineData: List<Double>?,
    priceChangePercentage24h: BigDecimal?,
    formattedCompactBalance: String?,
    isLoading: Boolean
) {
    Column(modifier = modifier) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextDescription1(
                if (isLoading) "ABC" else symbol,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier
                    .weight(1f)
                    .mangalaWalletPlaceholder(visible = isLoading),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                if (isLoading) {
                    Box(Modifier.size(20.dp).clip(CircleShape).mangalaWalletPlaceholder(isLoading))
                } else {
                    MultiImage(
                        imageSource,
                        modifier = Modifier.size(20.dp).clip(CircleShape)
                    )
                }
            }
        }
        TextDescription2(
            if (isLoading) "Placeholder" else name,
            color = MaterialTheme.mangalaColors.textSecondary,
            fontSize = FontType.MICRO_10,
            modifier = Modifier.mangalaWalletPlaceholder(isLoading)
        )
        Spacer(Modifier.height(Spacing.TINY))
        if (sparklineData.isNullOrEmpty() || isLoading) {
            Spacer(Modifier.height(40.dp))
        } else {
            Chart(
                chartModifier = Modifier.fillMaxWidth().height(40.dp),
                items = sparklineData,
                priceChangePercentage = priceChangePercentage24h,
                showPriceChangePercentage = false
            )
        }
        Spacer(Modifier.height(Spacing.MEDIUM))
        TextDescription2(
            if (isLoading || formattedCompactBalance == null) "0.00 EOS" else if (isBalanceVisible) formattedCompactBalance else HIDDEN_BALANCE_STRING,
            color = MaterialTheme.mangalaColors.textPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.mangalaWalletPlaceholder(isLoading || formattedCompactBalance == null),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        TextDescription2(
            if (isLoading || formattedPrice == null) "$0.00" else if (isBalanceVisible) formattedPrice else HIDDEN_BALANCE_STRING,
            color = MaterialTheme.mangalaColors.textSecondary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.mangalaWalletPlaceholder(isLoading || formattedPrice == null),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(Spacing.XTINY))
        val value = priceChangePercentage24h?.formatCompact()
        val priceChange = value?.let {
            val sign = if (priceChangePercentage24h >= 0) "+" else ""
            "$sign$value%"
        }
        Row {
            TextDescription2(
                if (isLoading) "+0.00%" else if (isBalanceVisible) priceChange.orEmpty() else HIDDEN_BALANCE_STRING,
                color = if (isBalanceVisible.not()) MaterialTheme.mangalaColors.textSecondary else if (priceChangePercentage24h != null && priceChangePercentage24h > 0) Colors.green else Colors.coral,
                fontWeight = FontWeight.Medium,
                fontSize = FontType.MICRO_10,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
            TextDescription2(
                if (isLoading) "+0.00%" else if (priceChange.isNullOrEmpty()
                        .not() && isBalanceVisible
                ) " " + MR.strings.message_wallet_main_card_pnl_description.desc()
                    .localized() else "",
                fontWeight = FontWeight.Medium,
                fontSize = FontType.MICRO_10,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
        }
    }
}

private fun DrawScope.drawBackgroundWave(
    size: Size,
    fillHeight: Float,
    phaseShift: Float,
    offsetX: Float,
    colors: List<Color>
) {
    val path = Path().apply {
        moveTo(0f, size.height - fillHeight)
        var x = -offsetX
        while (x < size.width) {
            quadraticBezierTo(
                x + size.width * 0.1f, size.height - fillHeight - size.height * phaseShift,
                x + size.width * 0.2f, size.height - fillHeight
            )
            x += size.width * 0.2f
        }
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, size.height - fillHeight),
        end = Offset(size.width, size.height)
    )
    drawPath(path, gradient)
}

fun DrawScope.drawForegroundWave(
    size: Size,
    fillHeight: Float,
    phaseShift: Float,
    offsetX: Float,
    colors: List<Color>
) {
    val path = Path().apply {
        moveTo(0f - offsetX, size.height - fillHeight)
        var x = -offsetX
        while (x < size.width) {
            quadraticBezierTo(
                x + size.width * 0.1f, size.height - fillHeight + size.height * phaseShift,
                x + size.width * 0.2f, size.height - fillHeight
            )
            x += size.width * 0.2f
        }
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    val gradient = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, size.height - fillHeight),
        end = Offset(size.width, size.height)
    )
    drawPath(path, gradient)
}

fun DrawScope.drawPieSlice(
    color: Color,
    startAngle: Float,
    sweepAngle: Float,
    useCenter: Boolean,
    diameter: Float
) {
    val radius = diameter / 2
    val circleTopLeft =
        center - Offset(radius, radius) // Correctly places the top left of the bounding box

    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = useCenter,
        size = size.copy(
            width = diameter,
            height = diameter
        ), // Uses the minimum dimension to maintain aspect ratio
        topLeft = circleTopLeft, // Properly centers the circle in the Canvas
        blendMode = BlendMode.SrcIn
    )
}

fun colorAccount(
    uiState: BaseWalletMainScreenDataUiState<*>,
    account: BaseAccountItemUiModel
): Color {
    return if (uiState.accounts.indexOf(account) % 2 == 0) Color(0xFFF6F6F6) else Color(
        0xFFC4D0FD
    )
}

private const val PAGER_INITIAL_PAGE = 0